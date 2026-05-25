package com.animalsalvation.structure;

import com.animalsalvation.entity.RescueTask;

import java.util.ArrayList;
import java.util.List;

public class RescueTaskQueue {
    private Node front;
    private Node rear;
    private int size;

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

    public List<RescueTask> toList() {
        List<RescueTask> tasks = new ArrayList<>();
        Node current = front;
        while (current != null) {
            tasks.add(current.data);
            current = current.next;
        }
        return tasks;
    }

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

    private static class Node {
        private final RescueTask data;
        private Node next;

        private Node(RescueTask data) {
            this.data = data;
        }
    }
}
