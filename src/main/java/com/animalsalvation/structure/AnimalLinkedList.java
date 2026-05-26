package com.animalsalvation.structure;

import com.animalsalvation.entity.Animal;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义单链表。
 *
 * <p>用于保存动物信息，展示“链表”这种数据结构在项目中的应用。</p>
 */
public class AnimalLinkedList {
    /** 链表头节点。 */
    private Node head;
    /** 当前链表元素数量。 */
    private int size;

    /** 在链表尾部追加一只动物。 */
    public void add(Animal animal) {
        Node node = new Node(animal);
        if (head == null) {
            head = node;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = node;
        }
        size++;
    }

    /** 按动物编号顺序遍历链表并查找。 */
    public Animal findById(int id) {
        Node current = head;
        while (current != null) {
            if (current.data.getId() == id) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    /** 按动物编号更新链表中的节点数据。 */
    public boolean update(Animal animal) {
        Node current = head;
        while (current != null) {
            if (current.data.getId() == animal.getId()) {
                current.data = animal;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /** 按动物编号删除节点。 */
    public boolean removeById(int id) {
        if (head == null) {
            return false;
        }
        if (head.data.getId() == id) {
            head = head.next;
            size--;
            return true;
        }

        Node previous = head;
        Node current = head.next;
        while (current != null) {
            if (current.data.getId() == id) {
                previous.next = current.next;
                size--;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    /** 把链表转换成 List，方便表格显示、排序和文件保存。 */
    public List<Animal> toList() {
        List<Animal> animals = new ArrayList<>();
        Node current = head;
        while (current != null) {
            animals.add(current.data);
            current = current.next;
        }
        return animals;
    }

    public int size() {
        return size;
    }

    /** 清空链表。 */
    public void clear() {
        head = null;
        size = 0;
    }

    /** 链表节点：data 保存数据，next 指向下一个节点。 */
    private static class Node {
        private Animal data;
        private Node next;

        private Node(Animal data) {
            this.data = data;
        }
    }
}
