package com.animalsalvation.algorithm;

import com.animalsalvation.entity.Animal;

import java.util.List;

/**
 * 查询算法工具类。
 *
 * <p>这个类只放静态方法，不保存业务数据，所以构造方法设为私有。</p>
 */
public final class SearchAlgorithms {
    private SearchAlgorithms() {
    }

    /**
     * 按动物编号进行二分查找。
     *
     * @param sortedAnimals 已经按 id 升序排好的动物列表
     * @param id 要查找的动物编号
     * @return 找到时返回动物对象；找不到时返回 null
     */
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
