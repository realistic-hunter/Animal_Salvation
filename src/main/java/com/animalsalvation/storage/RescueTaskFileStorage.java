package com.animalsalvation.storage;

import com.animalsalvation.entity.RescueTask;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 救助任务列表文件存储类。
 *
 * <p>普通队列、紧急优先队列和已处理任务都可以复用这套 CSV 读写逻辑。</p>
 */
public final class RescueTaskFileStorage {
    private RescueTaskFileStorage() {
    }

    /** 保存救助任务列表。 */
    public static void save(List<RescueTask> tasks, Path file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"taskId", "animalId", "title", "location", "urgencyLevel",
                "status", "volunteerName", "createTime"});
        for (RescueTask task : tasks) {
            rows.add(new String[]{
                    String.valueOf(task.getTaskId()),
                    String.valueOf(task.getAnimalId()),
                    task.getTitle(),
                    task.getLocation(),
                    task.getUrgencyLevel(),
                    task.getStatus(),
                    task.getVolunteerName(),
                    CsvFiles.formatTime(task.getCreateTime())
            });
        }
        CsvFiles.writeRows(file, rows);
    }

    /** 读取救助任务列表。 */
    public static List<RescueTask> load(Path file) throws IOException {
        List<RescueTask> tasks = new ArrayList<>();
        List<String[]> rows = CsvFiles.readRows(file);
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            tasks.add(new RescueTask(
                    Integer.parseInt(row[0]),
                    Integer.parseInt(row[1]),
                    row[2],
                    row[3],
                    row[4],
                    row[5],
                    row[6],
                    CsvFiles.parseTime(row[7])
            ));
        }
        return tasks;
    }
}
