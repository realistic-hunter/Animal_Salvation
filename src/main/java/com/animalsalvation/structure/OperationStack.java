package com.animalsalvation.structure;

import com.animalsalvation.entity.OperationRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作记录栈。
 *
 * <p>后发生的操作先撤销，符合栈的后进先出特性。</p>
 */
public class OperationStack {
    /** 栈顶节点。 */
    private Node top;
    /** 栈中元素数量。 */
    private int size;

    /** 入栈：新增操作记录放到栈顶。 */
    public void push(OperationRecord record) {
        Node node = new Node(record);
        node.next = top;
        top = node;
        size++;
    }

    /** 出栈：取出并删除栈顶记录。 */
    public OperationRecord pop() {
        if (top == null) {
            return null;
        }
        OperationRecord record = top.data;
        top = top.next;
        size--;
        return record;
    }

    /** 查看栈顶记录但不删除。 */
    public OperationRecord peek() {
        return top == null ? null : top.data;
    }

    /** 转成列表，顺序为栈顶到栈底。 */
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

    /** 清空栈。 */
    public void clear() {
        top = null;
        size = 0;
    }

    /** 栈节点。 */
    private static class Node {
        private final OperationRecord data;
        private Node next;

        private Node(OperationRecord data) {
            this.data = data;
        }
    }
}
