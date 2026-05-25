package com.animalsalvation.structure;

import com.animalsalvation.entity.Animal;

import java.util.ArrayList;
import java.util.List;

public class AnimalLinkedList {
    private Node head;
    private int size;

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

    private static class Node {
        private Animal data;
        private Node next;

        private Node(Animal data) {
            this.data = data;
        }
    }
}
