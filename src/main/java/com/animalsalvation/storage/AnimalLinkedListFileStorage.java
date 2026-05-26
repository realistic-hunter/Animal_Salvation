package com.animalsalvation.storage;

import com.animalsalvation.entity.Animal;
import com.animalsalvation.structure.AnimalLinkedList;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 动物链表文件存储类。
 *
 * <p>负责把 AnimalLinkedList 保存到 animals.csv，并从文件恢复链表。</p>
 */
public final class AnimalLinkedListFileStorage {
    private AnimalLinkedListFileStorage() {
    }

    /** 保存动物链表。 */
    public static void save(AnimalLinkedList linkedList, Path file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"id", "name", "type", "gender", "age", "healthStatus",
                "rescueStatus", "adoptStatus", "foundLocation", "foundTime"});
        for (Animal animal : linkedList.toList()) {
            rows.add(new String[]{
                    String.valueOf(animal.getId()),
                    animal.getName(),
                    animal.getType(),
                    animal.getGender(),
                    String.valueOf(animal.getAge()),
                    animal.getHealthStatus(),
                    animal.getRescueStatus(),
                    animal.getAdoptStatus(),
                    animal.getFoundLocation(),
                    CsvFiles.formatTime(animal.getFoundTime())
            });
        }
        CsvFiles.writeRows(file, rows);
    }

    /** 读取动物链表。 */
    public static AnimalLinkedList load(Path file) throws IOException {
        AnimalLinkedList linkedList = new AnimalLinkedList();
        List<String[]> rows = CsvFiles.readRows(file);
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            linkedList.add(new Animal(
                    Integer.parseInt(row[0]),
                    row[1],
                    row[2],
                    row[3],
                    Integer.parseInt(row[4]),
                    row[5],
                    row[6],
                    row[7],
                    row[8],
                    CsvFiles.parseTime(row[9])
            ));
        }
        return linkedList;
    }
}
