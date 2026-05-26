package com.animalsalvation.structure;

import com.animalsalvation.entity.RescueTask;

import java.util.ArrayList;
import java.util.List;

/**
 * 普通救助任务队列。
 * <p>普通任务按先进先出顺序派发。</p>
 */
public class RescueTaskQueue {
    /** 队头节点。 */
    private Node front;
    /** 队尾节点。 */
    private Node rear;
    /** 队列元素数量。 */
    private int size;

    /** 入队：新增任务放到队尾。 */
    public void enqueue(RescueTask task) {
        Node node = new Node(task);
        if (rear == null) {
            front = node;
            rear = node;
        } else {
            rear.next = node;
            rear = node;
        }
        size++;
    }

    /** 出队：取出队头任务。 */
    public RescueTask dequeue() {
        if (front == null) {
            return null;
        }
        RescueTask task = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return task;
    }

    /** 转成列表，保持从队头到队尾的顺序。 */
    public List<RescueTask> toList() {
        List<RescueTask> tasks = new ArrayList<>();
        Node current = front;
        while (current != null) {
            tasks.add(current.data);
            current = current.next;
        }
        return tasks;
    }

    /** 按任务编号删除队列中的任务。 */
    public boolean removeById(int taskId) {
        if (front == null) {
            return false;
        }
        if (front.data.getTaskId() == taskId) {
            front = front.next;
            if (front == null) {
                rear = null;
            }
            size--;
            return true;
        }

        Node previous = front;
        Node current = front.next;
        while (current != null) {
            if (current.data.getTaskId() == taskId) {
                previous.next = current.next;
                if (current == rear) {
                    rear = previous;
                }
                size--;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    public int size() {
        return size;
    }

    /** 清空队列。 */
    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }

    /** 队列节点。 */
    private static class Node {
        private final RescueTask data;
        private Node next;

        private Node(RescueTask data) {
            this.data = data;
        }
    }
}
