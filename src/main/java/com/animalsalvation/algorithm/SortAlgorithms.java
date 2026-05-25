package com.animalsalvation.algorithm;

import com.animalsalvation.entity.Animal;
import com.animalsalvation.entity.RescueTask;

import java.util.ArrayList;
import java.util.List;

public final class SortAlgorithms {
    private SortAlgorithms() {
    }

    public static List<Animal> bubbleSortByAge(List<Animal> source) {
        List<Animal> animals = new ArrayList<>(source);
        for (int i = 0; i < animals.size() - 1; i++) {
            for (int j = 0; j < animals.size() - 1 - i; j++) {
                if (animals.get(j).getAge() > animals.get(j + 1).getAge()) {
                    swap(animals, j, j + 1);
                }
            }
        }
        return animals;
    }

    public static List<Animal> selectionSortByFoundTime(List<Animal> source) {
        List<Animal> animals = new ArrayList<>(source);
        for (int i = 0; i < animals.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < animals.size(); j++) {
                if (animals.get(j).getFoundTime().isBefore(animals.get(minIndex).getFoundTime())) {
                    minIndex = j;
                }
            }
            swap(animals, i, minIndex);
        }
        return animals;
    }

    public static List<Animal> quickSortById(List<Animal> source) {
        List<Animal> animals = new ArrayList<>(source);
        quickSort(animals, 0, animals.size() - 1);
        return animals;
    }

    public static List<RescueTask> selectionSortTasksByUrgency(List<RescueTask> source) {
        List<RescueTask> tasks = new ArrayList<>(source);
        for (int i = 0; i < tasks.size() - 1; i++) {
            int bestIndex = i;
            for (int j = i + 1; j < tasks.size(); j++) {
                if (urgencyWeight(tasks.get(j).getUrgencyLevel()) < urgencyWeight(tasks.get(bestIndex).getUrgencyLevel())) {
                    bestIndex = j;
                }
            }
            RescueTask temp = tasks.get(i);
            tasks.set(i, tasks.get(bestIndex));
            tasks.set(bestIndex, temp);
        }
        return tasks;
    }

    public static int urgencyWeight(String urgencyLevel) {
        if ("非常紧急".equals(urgencyLevel)) {
            return 1;
        }
        if ("紧急".equals(urgencyLevel)) {
            return 2;
        }
        return 3;
    }

    private static void quickSort(List<Animal> animals, int left, int right) {
        if (left >= right) {
            return;
        }
        int pivot = animals.get(left + (right - left) / 2).getId();
        int i = left;
        int j = right;
        while (i <= j) {
            while (animals.get(i).getId() < pivot) {
                i++;
            }
            while (animals.get(j).getId() > pivot) {
                j--;
            }
            if (i <= j) {
                swap(animals, i, j);
                i++;
                j--;
            }
        }
        quickSort(animals, left, j);
        quickSort(animals, i, right);
    }

    private static void swap(List<Animal> animals, int i, int j) {
        Animal temp = animals.get(i);
        animals.set(i, animals.get(j));
        animals.set(j, temp);
    }
}
