# Animal_Salvation

基于 Java 与数据结构的流浪动物救助调度管理系统

桌面端 JavaFX 课程设计项目，重点展示链表、栈、队列、二叉排序树、查找与排序算法在流浪动物救助业务中的应用。

## 已完成功能

- JavaFX 主界面可启动
- 左侧菜单 + 右侧表格布局
- `Animal` 实体类
- `AnimalLinkedList` 自定义单链表
- 动物信息表格展示
- 新增动物并实时刷新表格
- 救助任务管理：普通任务进入 FIFO 队列，紧急任务进入优先队列
- 领养申请管理：申请按提交顺序进入 FIFO 审核队列
- 操作历史：使用栈保存操作记录，并支持撤销最近一次新增动物、救助任务或领养申请操作
- 二叉排序树：按动物编号插入、查找，并支持中序遍历
- 哈希查找：使用 `HashMap<Integer, Animal>` 快速定位动物
- 二分查找：对按编号快速排序后的动物列表进行查询
- 排序算法：冒泡排序、选择排序、快速排序

## 运行方式

在 IDEA 中打开本目录，等待 Maven 依赖同步完成后运行：

```bash
mvn javafx:run
```

或者在 IDEA Maven 面板中执行 `Plugins -> javafx -> javafx:run`。

## 数据结构规划

- 链表：`AnimalLinkedList` 动态管理动物信息
- 栈：`OperationStack` 保存操作历史，支持撤销
- 队列：`RescueTaskQueue`、`AdoptionQueue` 先进先出处理
- 优先队列：`PriorityQueue<RescueTask>` 按非常紧急、紧急、普通调度
- 二叉排序树：`AnimalBinarySearchTree` 按动物编号组织并中序遍历
- 哈希查找：`HashMap<Integer, Animal>` 按动物编号快速查询
- 二分查找：`SearchAlgorithms.binarySearchById`
- 排序算法：
  - 冒泡排序：按年龄升序
  - 选择排序：按发现时间升序、任务紧急程度排序
  - 快速排序：按动物编号升序

## 简历描述参考

基于 JavaFX 开发流浪动物救助调度管理系统，围绕动物登记、救助任务派发、领养申请审核、查询排序与操作撤销等真实业务流程，设计并实现链表、栈、队列、优先队列、二叉排序树、哈希查找、二分查找及多种排序算法，突出数据结构在业务调度和信息管理中的应用。
