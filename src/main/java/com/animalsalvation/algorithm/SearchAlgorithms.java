package com.animalsalvation.algorithm;

import com.animalsalvation.entity.Animal;

import java.util.List;

public final class SearchAlgorithms {
    private SearchAlgorithms() {
    }

    public static Animal binarySearchById(List<Animal> sortedAnimals, int id) {
        int left = 0;
        int right = sortedAnimals.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            Animal animal = sortedAnimals.get(mid);
            if (animal.getId() == id) {
                return animal;
            }
            if (animal.getId() < id) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null;
    }
}
