package com.animalsalvation.structure;

import com.animalsalvation.entity.Animal;

import java.util.ArrayList;
import java.util.List;

public class AnimalBinarySearchTree {
    private Node root;

    public void insert(Animal animal) {
        root = insert(root, animal);
    }

    public Animal search(int id) {
        Node current = root;
        while (current != null) {
            if (id == current.data.getId()) {
                return current.data;
            }
            current = id < current.data.getId() ? current.left : current.right;
        }
        return null;
    }

    public List<Animal> inOrder() {
        List<Animal> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    public void rebuild(List<Animal> animals) {
        root = null;
        for (Animal animal : animals) {
            insert(animal);
        }
    }

    private Node insert(Node node, Animal animal) {
        if (node == null) {
            return new Node(animal);
        }
        if (animal.getId() < node.data.getId()) {
            node.left = insert(node.left, animal);
        } else if (animal.getId() > node.data.getId()) {
            node.right = insert(node.right, animal);
        } else {
            node.data = animal;
        }
        return node;
    }

    private void inOrder(Node node, List<Animal> result) {
        if (node == null) {
            return;
        }
        inOrder(node.left, result);
        result.add(node.data);
        inOrder(node.right, result);
    }

    private static class Node {
        private Animal data;
        private Node left;
        private Node right;

        private Node(Animal data) {
            this.data = data;
        }
    }
}
