package com.animalsalvation.structure;

import com.animalsalvation.entity.OperationRecord;

import java.util.ArrayList;
import java.util.List;

public class OperationStack {
    private Node top;
    private int size;

    public void push(OperationRecord record) {
        Node node = new Node(record);
        node.next = top;
        top = node;
        size++;
    }

    public OperationRecord pop() {
        if (top == null) {
            return null;
        }
        OperationRecord record = top.data;
        top = top.next;
        size--;
        return record;
    }

    public OperationRecord peek() {
        return top == null ? null : top.data;
    }

    public List<OperationRecord> toList() {
        List<OperationRecord> records = new ArrayList<>();
        Node current = top;
        while (current != null) {
            records.add(current.data);
            current = current.next;
        }
        return records;
    }

    public int size() {
        return size;
    }

    private static class Node {
        private final OperationRecord data;
        private Node next;

        private Node(OperationRecord data) {
            this.data = data;
        }
    }
}
