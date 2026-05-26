package com.animalsalvation.storage;

import com.animalsalvation.entity.AdoptionApplication;
import com.animalsalvation.structure.AdoptionQueue;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 领养申请队列文件存储类。
 *
 * <p>读取时按文件顺序 enqueue，保证先提交的申请仍然先审核。</p>
 */
public final class AdoptionQueueFileStorage {
    private AdoptionQueueFileStorage() {
    }

    /** 保存待审核领养队列。 */
    public static void save(AdoptionQueue queue, Path file) throws IOException {
        AdoptionApplicationFileStorage.save(queue.toList(), file);
    }

    /** 读取待审核领养队列。 */
    public static AdoptionQueue load(Path file) throws IOException {
        AdoptionQueue queue = new AdoptionQueue();
        for (AdoptionApplication application : AdoptionApplicationFileStorage.load(file)) {
            queue.enqueue(application);
        }
        return queue;
    }
}
