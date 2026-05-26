package com.animalsalvation.structure;

import com.animalsalvation.entity.Animal;

import java.util.ArrayList;
import java.util.List;

/**
 * 动物二叉搜索树。
 *
 * <p>按动物编号建立树结构，左子树编号更小，右子树编号更大。</p>
 */
public class AnimalBinarySearchTree {
    /** 根节点。 */
    private Node root;

    /** 插入动物；如果编号已存在，则覆盖原数据。 */
    public void insert(Animal animal) {
        root = insert(root, animal);
    }

    /** 按编号在树中查找动物。 */
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

    /** 中序遍历，结果会按动物编号升序排列。 */
    public List<Animal> inOrder() {
        List<Animal> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    /** 根据动物列表重建整棵树。 */
    public void rebuild(List<Animal> animals) {
        root = null;
        for (Animal animal : animals) {
            insert(animal);
        }
    }

    /** 清空树。 */
    public void clear() {
        root = null;
    }

    /** 递归插入节点。 */
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

    /** 递归中序遍历：左子树 -> 当前节点 -> 右子树。 */
    private void inOrder(Node node, List<Animal> result) {
        if (node == null) {
            return;
        }
        inOrder(node.left, result);
        result.add(node.data);
        inOrder(node.right, result);
    }

    /** 二叉搜索树节点。 */
    private static class Node {
        private Animal data;
        private Node left;
        private Node right;

        private Node(Animal data) {
            this.data = data;
        }
    }
}
