package com.animalsalvation.storage;

import com.animalsalvation.entity.RescueTask;
import com.animalsalvation.structure.RescueTaskQueue;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 普通救助任务队列文件存储类。
 *
 * <p>读取时按文件顺序 enqueue，保证 FIFO 顺序不变。</p>
 */
public final class RescueTaskQueueFileStorage {
    private RescueTaskQueueFileStorage() {
    }

    /** 保存普通任务队列。 */
    public static void save(RescueTaskQueue queue, Path file) throws IOException {
        RescueTaskFileStorage.save(queue.toList(), file);
    }

    /** 读取普通任务队列。 */
    public static RescueTaskQueue load(Path file) throws IOException {
        RescueTaskQueue queue = new RescueTaskQueue();
        for (RescueTask task : RescueTaskFileStorage.load(file)) {
            queue.enqueue(task);
        }
        return queue;
    }
}
