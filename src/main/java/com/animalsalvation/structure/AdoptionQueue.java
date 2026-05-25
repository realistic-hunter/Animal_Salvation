package com.animalsalvation.structure;

import com.animalsalvation.entity.AdoptionApplication;

import java.util.ArrayList;
import java.util.List;

public class AdoptionQueue {
    private Node front;
    private Node rear;
    private int size;

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

    public List<AdoptionApplication> toList() {
        List<AdoptionApplication> applications = new ArrayList<>();
        Node current = front;
        while (current != null) {
            applications.add(current.data);
            current = current.next;
        }
        return applications;
    }

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

    private static class Node {
        private final AdoptionApplication data;
        private Node next;

        private Node(AdoptionApplication data) {
            this.data = data;
        }
    }
}
