package com.animalsalvation.structure;

import com.animalsalvation.entity.AdoptionApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * 领养申请队列。
 *
 * <p>使用先进先出规则：先提交的申请先审核。</p>
 */
public class AdoptionQueue {
    /** 队头节点。 */
    private Node front;
    /** 队尾节点。 */
    private Node rear;
    /** 队列元素数量。 */
    private int size;

    /** 入队：把申请放到队尾。 */
    public void enqueue(AdoptionApplication application) {
        Node node = new Node(application);
        if (rear == null) {
            front = node;
            rear = node;
        } else {
            rear.next = node;
            rear = node;
        }
        size++;
    }

    /** 出队：取出队头申请。 */
    public AdoptionApplication dequeue() {
        if (front == null) {
            return null;
        }
        AdoptionApplication application = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return application;
    }

    /** 转成列表，保持从队头到队尾的顺序。 */
    public List<AdoptionApplication> toList() {
        List<AdoptionApplication> applications = new ArrayList<>();
        Node current = front;
        while (current != null) {
            applications.add(current.data);
            current = current.next;
        }
        return applications;
    }

    /** 按申请编号删除队列中的申请。 */
    public boolean removeById(int applyId) {
        if (front == null) {
            return false;
        }
        if (front.data.getApplyId() == applyId) {
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
            if (current.data.getApplyId() == applyId) {
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
        private final AdoptionApplication data;
        private Node next;

        private Node(AdoptionApplication data) {
            this.data = data;
        }
    }
}
