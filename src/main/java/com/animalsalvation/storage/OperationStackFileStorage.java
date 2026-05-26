package com.animalsalvation.storage;

import com.animalsalvation.entity.OperationRecord;
import com.animalsalvation.structure.OperationStack;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 操作栈文件存储类。
 *
 * <p>保存时从栈顶到栈底写入，读取时反向压栈，恢复原来的栈顶顺序。</p>
 */
public final class OperationStackFileStorage {
    private OperationStackFileStorage() {
    }

    /** 保存操作栈。 */
    public static void save(OperationStack stack, Path file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"operationType", "targetId", "description", "operationTime"});
        for (OperationRecord record : stack.toList()) {
            rows.add(new String[]{
                    record.getOperationType(),
                    String.valueOf(record.getTargetId()),
                    record.getDescription(),
                    CsvFiles.formatTime(record.getOperationTime())
            });
        }
        CsvFiles.writeRows(file, rows);
    }

    /** 读取操作栈。 */
    public static OperationStack load(Path file) throws IOException {
        OperationStack stack = new OperationStack();
        List<String[]> rows = CsvFiles.readRows(file);
        rows = new ArrayList<>(rows.subList(Math.min(1, rows.size()), rows.size()));
        Collections.reverse(rows);
        for (String[] row : rows) {
            stack.push(new OperationRecord(
                    row[0],
                    Integer.parseInt(row[1]),
                    row[2],
                    CsvFiles.parseTime(row[3])
            ));
        }
        return stack;
    }
}
