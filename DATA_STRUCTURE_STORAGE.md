# 数据结构文件存储说明

项目现在已经补齐“所有业务数据”的文件存储代码，统一放在 `com.animalsalvation.storage` 包中，并且在 `AnimalService` 里提供了总入口。

## 统一保存和读取

```java
animalService.saveAllData();
animalService.loadAllData();
```

默认保存目录是项目根目录下的 `data` 文件夹。也可以指定目录：

```java
animalService.saveAllData(Path.of("data"));
animalService.loadAllData(Path.of("data"));
```

## 保存的全部数据文件

| 文件 | 数据 | 对应数据结构 |
| --- | --- | --- |
| `animals.csv` | 动物信息 | `AnimalLinkedList` 链表 |
| `normal_tasks.csv` | 普通待处理救助任务 | `RescueTaskQueue` 队列 |
| `urgent_tasks.csv` | 紧急待处理救助任务 | `PriorityQueue<RescueTask>` 优先队列 |
| `handled_tasks.csv` | 已派发救助任务 | `List<RescueTask>` 线性表 |
| `pending_adoptions.csv` | 待审核领养申请 | `AdoptionQueue` 队列 |
| `reviewed_adoptions.csv` | 已审核领养申请 | `List<AdoptionApplication>` 线性表 |
| `operations.csv` | 操作历史 | `OperationStack` 栈 |

## 存储类

- `AnimalLinkedListFileStorage`：保存和读取动物链表。
- `RescueTaskQueueFileStorage`：保存和读取普通救助任务队列。
- `RescueTaskFileStorage`：保存和读取救助任务列表，也用于优先队列和已处理任务。
- `AdoptionQueueFileStorage`：保存和读取待审核领养申请队列。
- `AdoptionApplicationFileStorage`：保存和读取领养申请列表。
- `OperationStackFileStorage`：保存和读取操作记录栈。
- `CsvFiles`：CSV 工具类，负责 UTF-8 文件读写、逗号/双引号/换行转义、时间转换。

## 说明

`HashMap<Integer, Animal>` 和 `AnimalBinarySearchTree` 不单独保存，因为它们是查询索引结构，可以由 `animals.csv` 中的动物信息重新构建。读取全部数据时，`AnimalService.loadAllData(...)` 会自动重建哈希索引和二叉搜索树。
