package com.animalsalvation.service;

import com.animalsalvation.algorithm.SearchAlgorithms;
import com.animalsalvation.algorithm.SortAlgorithms;
import com.animalsalvation.entity.AdoptionApplication;
import com.animalsalvation.entity.Animal;
import com.animalsalvation.entity.OperationRecord;
import com.animalsalvation.entity.RescueTask;
import com.animalsalvation.storage.AdoptionApplicationFileStorage;
import com.animalsalvation.storage.AdoptionQueueFileStorage;
import com.animalsalvation.storage.AnimalLinkedListFileStorage;
import com.animalsalvation.storage.OperationStackFileStorage;
import com.animalsalvation.storage.RescueTaskFileStorage;
import com.animalsalvation.storage.RescueTaskQueueFileStorage;
import com.animalsalvation.structure.AdoptionQueue;
import com.animalsalvation.structure.AnimalBinarySearchTree;
import com.animalsalvation.structure.AnimalLinkedList;
import com.animalsalvation.structure.OperationStack;
import com.animalsalvation.structure.RescueTaskQueue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * 动物救助业务层。
 *
 * <p>控制器只负责界面交互，真正的动物登记、任务派发、领养审核、撤销和文件保存逻辑都放在这里。</p>
 */
public class AnimalService {
    private static final Path DEFAULT_DATA_DIR = Path.of("data");

    /** 用自定义链表保存动物基础信息。 */
    private final AnimalLinkedList animals = new AnimalLinkedList();
    /** 普通救助任务使用 FIFO 队列。 */
    private final RescueTaskQueue normalTaskQueue = new RescueTaskQueue();
    /** 待审核领养申请使用 FIFO 队列。 */
    private final AdoptionQueue adoptionQueue = new AdoptionQueue();
    /** 操作记录使用栈，便于撤销最近一次新增操作。 */
    private final OperationStack operationStack = new OperationStack();
    /** 二叉搜索树按动物编号组织数据，用于树查找和中序遍历。 */
    private final AnimalBinarySearchTree animalTree = new AnimalBinarySearchTree();
    /** 哈希表作为动物编号到动物对象的快速索引。 */
    private final Map<Integer, Animal> animalIndex = new HashMap<>();
    /** 紧急任务使用优先队列，紧急程度越高越先派发。 */
    private final PriorityQueue<RescueTask> urgentTaskQueue = new PriorityQueue<>(
            Comparator.<RescueTask>comparingInt(task -> SortAlgorithms.urgencyWeight(task.getUrgencyLevel()))
                    .thenComparing(RescueTask::getCreateTime)
    );
    private final List<RescueTask> handledTasks = new ArrayList<>();
    private final List<AdoptionApplication> reviewedApplications = new ArrayList<>();

    public AnimalService() {
        addSampleData();
    }

    /** 保存所有动物救助业务数据到默认 data 目录。 */
    public void saveAllData() throws IOException {
        saveAllData(DEFAULT_DATA_DIR);
    }

    /** 保存所有动物救助业务数据到指定目录。 */
    public void saveAllData(Path dataDir) throws IOException {
        Files.createDirectories(dataDir);
        AnimalLinkedListFileStorage.save(animals, dataDir.resolve("animals.csv"));
        RescueTaskQueueFileStorage.save(normalTaskQueue, dataDir.resolve("normal_tasks.csv"));
        RescueTaskFileStorage.save(new ArrayList<>(urgentTaskQueue), dataDir.resolve("urgent_tasks.csv"));
        RescueTaskFileStorage.save(handledTasks, dataDir.resolve("handled_tasks.csv"));
        AdoptionQueueFileStorage.save(adoptionQueue, dataDir.resolve("pending_adoptions.csv"));
        AdoptionApplicationFileStorage.save(reviewedApplications, dataDir.resolve("reviewed_adoptions.csv"));
        OperationStackFileStorage.save(operationStack, dataDir.resolve("operations.csv"));
    }

    /** 从默认 data 目录读取全部业务数据。 */
    public void loadAllData() throws IOException {
        loadAllData(DEFAULT_DATA_DIR);
    }

    /** 从指定目录读取全部业务数据，并重建哈希表和二叉搜索树索引。 */
    public void loadAllData(Path dataDir) throws IOException {
        resetAllData();
        AnimalLinkedList loadedAnimals = AnimalLinkedListFileStorage.load(dataDir.resolve("animals.csv"));
        for (Animal animal : loadedAnimals.toList()) {
            addAnimalWithoutHistory(animal);
        }

        RescueTaskQueue loadedNormalTasks = RescueTaskQueueFileStorage.load(dataDir.resolve("normal_tasks.csv"));
        for (RescueTask task : loadedNormalTasks.toList()) {
            normalTaskQueue.enqueue(task);
        }

        urgentTaskQueue.addAll(RescueTaskFileStorage.load(dataDir.resolve("urgent_tasks.csv")));
        handledTasks.addAll(RescueTaskFileStorage.load(dataDir.resolve("handled_tasks.csv")));

        AdoptionQueue loadedPendingApplications = AdoptionQueueFileStorage.load(dataDir.resolve("pending_adoptions.csv"));
        for (AdoptionApplication application : loadedPendingApplications.toList()) {
            adoptionQueue.enqueue(application);
        }
        reviewedApplications.addAll(AdoptionApplicationFileStorage.load(dataDir.resolve("reviewed_adoptions.csv")));

        restoreOperationStack(OperationStackFileStorage.load(dataDir.resolve("operations.csv")));
    }

    /** 新增动物，同时维护链表、哈希索引和二叉搜索树。 */
    public void addAnimal(Animal animal) {
        if (animals.findById(animal.getId()) != null) {
            throw new IllegalArgumentException("动物编号已存在。");
        }
        animals.add(animal);
        animalIndex.put(animal.getId(), animal);
        animalTree.insert(animal);
        record("新增动物", animal.getId(), "新增动物：" + animal.getName());
    }

    public List<Animal> findAll() {
        return animals.toList();
    }

    public Animal findById(int id) {
        return animals.findById(id);
    }

    public Animal hashFindById(int id) {
        return animalIndex.get(id);
    }

    public Animal binaryFindById(int id) {
        return SearchAlgorithms.binarySearchById(SortAlgorithms.quickSortById(findAll()), id);
    }

    public Animal treeFindById(int id) {
        return animalTree.search(id);
    }

    public List<Animal> treeInOrderAnimals() {
        return animalTree.inOrder();
    }

    /** 根据界面选择调用不同排序算法。 */
    public List<Animal> sortAnimals(String sortMode) {
        return switch (sortMode) {
            case "冒泡排序：按年龄升序" -> SortAlgorithms.bubbleSortByAge(findAll());
            case "选择排序：按发现时间升序" -> SortAlgorithms.selectionSortByFoundTime(findAll());
            default -> SortAlgorithms.quickSortById(findAll());
        };
    }

    /** 新增救助任务：普通任务进入普通队列，紧急任务进入优先队列。 */
    public void addRescueTask(RescueTask task) {
        ensureAnimalExists(task.getAnimalId());
        if ("普通".equals(task.getUrgencyLevel())) {
            normalTaskQueue.enqueue(task);
        } else {
            urgentTaskQueue.offer(task);
        }
        record("新增救助任务", task.getTaskId(), "新增救助任务：" + task.getTitle());
    }

    /** 派发下一条任务：优先队列为空时才处理普通队列。 */
    public RescueTask dispatchNextTask() {
        RescueTask task = urgentTaskQueue.poll();
        if (task == null) {
            task = normalTaskQueue.dequeue();
        }
        if (task == null) {
            return null;
        }
        task.setStatus("已派发");
        handledTasks.add(task);
        record("派发救助任务", task.getTaskId(), "派发任务：" + task.getTitle());
        return task;
    }

    public List<RescueTask> pendingTasks() {
        List<RescueTask> tasks = new ArrayList<>();
        tasks.addAll(urgentTaskQueue);
        tasks.addAll(normalTaskQueue.toList());
        return SortAlgorithms.selectionSortTasksByUrgency(tasks);
    }

    public List<RescueTask> handledTasks() {
        return new ArrayList<>(handledTasks);
    }

    /** 提交领养申请，申请按提交顺序进入审核队列。 */
    public void addAdoptionApplication(AdoptionApplication application) {
        ensureAnimalExists(application.getAnimalId());
        adoptionQueue.enqueue(application);
        record("提交领养申请", application.getApplyId(), "提交领养申请：" + application.getApplicantName());
    }

    /** 审核下一条领养申请。 */
    public AdoptionApplication reviewNextApplication(String resultStatus) {
        AdoptionApplication application = adoptionQueue.dequeue();
        if (application == null) {
            return null;
        }
        application.setStatus(resultStatus);
        reviewedApplications.add(application);
        record("审核领养申请", application.getApplyId(),
                "审核申请：" + application.getApplicantName() + "，结果：" + resultStatus);
        return application;
    }

    public List<AdoptionApplication> pendingApplications() {
        return adoptionQueue.toList();
    }

    public List<AdoptionApplication> reviewedApplications() {
        return new ArrayList<>(reviewedApplications);
    }

    public List<OperationRecord> operationHistory() {
        return operationStack.toList();
    }

    /** 从操作栈弹出最近一次记录，并撤销对应新增操作。 */
    public OperationRecord undoLastOperation() {
        OperationRecord record = operationStack.pop();
        if (record == null) {
            return null;
        }
        if ("新增动物".equals(record.getOperationType())) {
            animals.removeById(record.getTargetId());
            rebuildAnimalIndexes();
        } else if ("新增救助任务".equals(record.getOperationType())) {
            normalTaskQueue.removeById(record.getTargetId());
            urgentTaskQueue.removeIf(task -> task.getTaskId() == record.getTargetId());
        } else if ("提交领养申请".equals(record.getOperationType())) {
            adoptionQueue.removeById(record.getTargetId());
        }
        return record;
    }

    /** 生成下一个动物编号。 */
    public int nextId() {
        int maxId = 0;
        for (Animal animal : animals.toList()) {
            maxId = Math.max(maxId, animal.getId());
        }
        return maxId + 1;
    }

    /** 生成下一个任务编号。 */
    public int nextTaskId() {
        int maxId = 2000;
        for (RescueTask task : pendingTasks()) {
            maxId = Math.max(maxId, task.getTaskId());
        }
        for (RescueTask task : handledTasks) {
            maxId = Math.max(maxId, task.getTaskId());
        }
        return maxId + 1;
    }

    /** 生成下一个领养申请编号。 */
    public int nextApplyId() {
        int maxId = 3000;
        for (AdoptionApplication application : pendingApplications()) {
            maxId = Math.max(maxId, application.getApplyId());
        }
        for (AdoptionApplication application : reviewedApplications) {
            maxId = Math.max(maxId, application.getApplyId());
        }
        return maxId + 1;
    }

    /** 系统启动时放入示例数据，方便打开界面后直接演示。 */
    private void addSampleData() {
        addAnimalWithoutHistory(new Animal(1001, "小橘", "猫", "雌", 2, "轻微皮肤病",
                "已救助", "待领养", "城南公园", LocalDateTime.now().minusDays(3)));
        addAnimalWithoutHistory(new Animal(1002, "黑豆", "狗", "雄", 4, "营养不良",
                "治疗中", "暂不可领养", "地铁站B口", LocalDateTime.now().minusDays(1)));
        addAnimalWithoutHistory(new Animal(1003, "雪球", "猫", "未知", 1, "健康",
                "观察中", "待领养", "大学东门", LocalDateTime.now().minusHours(6)));

        addRescueTaskWithoutHistory(new RescueTask(2001, 1002, "地铁站受伤犬转运",
                "地铁站B口", "非常紧急", "待处理", "王志愿者", LocalDateTime.now().minusHours(4)));
        addRescueTaskWithoutHistory(new RescueTask(2002, 1001, "公园猫咪复查",
                "城南公园", "普通", "待处理", "李志愿者", LocalDateTime.now().minusHours(2)));
        addRescueTaskWithoutHistory(new RescueTask(2003, 1003, "大学东门幼猫观察",
                "大学东门", "紧急", "待处理", "赵志愿者", LocalDateTime.now().minusHours(1)));

        adoptionQueue.enqueue(new AdoptionApplication(3001, 1001, "陈同学", "13800000001",
                "有养猫经验，宿舍外租可养宠。", "待审核", LocalDateTime.now().minusHours(3)));
        adoptionQueue.enqueue(new AdoptionApplication(3002, 1003, "林女士", "13800000002",
                "家庭稳定，希望领养幼猫。", "待审核", LocalDateTime.now().minusHours(1)));
    }

    private void addAnimalWithoutHistory(Animal animal) {
        animals.add(animal);
        animalIndex.put(animal.getId(), animal);
        animalTree.insert(animal);
    }

    private void addRescueTaskWithoutHistory(RescueTask task) {
        if ("普通".equals(task.getUrgencyLevel())) {
            normalTaskQueue.enqueue(task);
        } else {
            urgentTaskQueue.offer(task);
        }
    }

    private void ensureAnimalExists(int animalId) {
        if (!animalIndex.containsKey(animalId)) {
            throw new IllegalArgumentException("动物编号不存在。");
        }
    }

    /** 动物链表变更后，重新生成哈希索引和二叉搜索树。 */
    private void rebuildAnimalIndexes() {
        animalIndex.clear();
        animalTree.rebuild(animals.toList());
        for (Animal animal : animals.toList()) {
            animalIndex.put(animal.getId(), animal);
        }
    }

    private void resetAllData() {
        animals.clear();
        normalTaskQueue.clear();
        adoptionQueue.clear();
        operationStack.clear();
        animalTree.clear();
        animalIndex.clear();
        urgentTaskQueue.clear();
        handledTasks.clear();
        reviewedApplications.clear();
    }

    private void restoreOperationStack(OperationStack loadedStack) {
        List<OperationRecord> records = new ArrayList<>(loadedStack.toList());
        Collections.reverse(records);
        for (OperationRecord record : records) {
            operationStack.push(record);
        }
    }

    private void record(String operationType, int targetId, String description) {
        operationStack.push(new OperationRecord(operationType, targetId, description, LocalDateTime.now()));
    }
}
