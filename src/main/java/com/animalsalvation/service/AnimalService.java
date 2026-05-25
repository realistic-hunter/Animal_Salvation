package com.animalsalvation.service;

import com.animalsalvation.algorithm.SearchAlgorithms;
import com.animalsalvation.algorithm.SortAlgorithms;
import com.animalsalvation.entity.AdoptionApplication;
import com.animalsalvation.entity.Animal;
import com.animalsalvation.entity.OperationRecord;
import com.animalsalvation.entity.RescueTask;
import com.animalsalvation.structure.AdoptionQueue;
import com.animalsalvation.structure.AnimalBinarySearchTree;
import com.animalsalvation.structure.AnimalLinkedList;
import com.animalsalvation.structure.OperationStack;
import com.animalsalvation.structure.RescueTaskQueue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class AnimalService {
    private final AnimalLinkedList animals = new AnimalLinkedList();
    private final RescueTaskQueue normalTaskQueue = new RescueTaskQueue();
    private final AdoptionQueue adoptionQueue = new AdoptionQueue();
    private final OperationStack operationStack = new OperationStack();
    private final AnimalBinarySearchTree animalTree = new AnimalBinarySearchTree();
    private final Map<Integer, Animal> animalIndex = new HashMap<>();
    private final PriorityQueue<RescueTask> urgentTaskQueue = new PriorityQueue<>(
            Comparator.<RescueTask>comparingInt(task -> SortAlgorithms.urgencyWeight(task.getUrgencyLevel()))
                    .thenComparing(RescueTask::getCreateTime)
    );
    private final List<RescueTask> handledTasks = new ArrayList<>();
    private final List<AdoptionApplication> reviewedApplications = new ArrayList<>();

    public AnimalService() {
        addSampleData();
    }

    public void addAnimal(Animal animal) {
        if (animals.findById(animal.getId()) != null) {
            throw new IllegalArgumentException("动物编号已存在");
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

    public List<Animal> sortAnimals(String sortMode) {
        return switch (sortMode) {
            case "冒泡排序：按年龄升序" -> SortAlgorithms.bubbleSortByAge(findAll());
            case "选择排序：按发现时间升序" -> SortAlgorithms.selectionSortByFoundTime(findAll());
            default -> SortAlgorithms.quickSortById(findAll());
        };
    }

    public void addRescueTask(RescueTask task) {
        ensureAnimalExists(task.getAnimalId());
        if ("普通".equals(task.getUrgencyLevel())) {
            normalTaskQueue.enqueue(task);
        } else {
            urgentTaskQueue.offer(task);
        }
        record("新增救助任务", task.getTaskId(), "新增救助任务：" + task.getTitle());
    }

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

    public void addAdoptionApplication(AdoptionApplication application) {
        ensureAnimalExists(application.getAnimalId());
        adoptionQueue.enqueue(application);
        record("提交领养申请", application.getApplyId(), "提交领养申请：" + application.getApplicantName());
    }

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

    public int nextId() {
        int maxId = 0;
        for (Animal animal : animals.toList()) {
            maxId = Math.max(maxId, animal.getId());
        }
        return maxId + 1;
    }

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
                "有养猫经验，宿舍外租可养宠", "待审核", LocalDateTime.now().minusHours(3)));
        adoptionQueue.enqueue(new AdoptionApplication(3002, 1003, "林女士", "13800000002",
                "家庭稳定，希望领养幼猫", "待审核", LocalDateTime.now().minusHours(1)));
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
            throw new IllegalArgumentException("动物编号不存在");
        }
    }

    private void rebuildAnimalIndexes() {
        animalIndex.clear();
        animalTree.rebuild(animals.toList());
        for (Animal animal : animals.toList()) {
            animalIndex.put(animal.getId(), animal);
        }
    }

    private void record(String operationType, int targetId, String description) {
        operationStack.push(new OperationRecord(operationType, targetId, description, LocalDateTime.now()));
    }
}
