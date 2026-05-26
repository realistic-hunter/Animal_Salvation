package com.animalsalvation.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV 文件读写工具类。
 *
 * <p>所有文件存储类都通过它读写 UTF-8 CSV，避免每个类重复写转义逻辑。</p>
 */
final class CsvFiles {
    private CsvFiles() {
    }

    /** 把多行字符串数组写入 CSV 文件。 */
    static void writeRows(Path file, List<String[]> rows) throws IOException {
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (String[] row : rows) {
                writer.write(toLine(row));
                writer.newLine();
            }
        }
    }

    /** 从 CSV 文件读取多行字符串数组；文件不存在时返回空列表。 */
    static List<String[]> readRows(Path file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        if (!Files.exists(file)) {
            return rows;
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    rows.add(parseLine(line));
                }
            }
        }
        return rows;
    }

    /** 把时间转成字符串保存。 */
    static String formatTime(LocalDateTime time) {
        return time == null ? "" : time.toString();
    }

    /** 把文件中的时间字符串还原成 LocalDateTime。 */
    static LocalDateTime parseTime(String text) {
        return text == null || text.isBlank() ? null : LocalDateTime.parse(text);
    }

    /** 把一行数据拼成 CSV 文本。 */
    private static String toLine(String[] row) {
        List<String> cells = new ArrayList<>();
        for (String cell : row) {
            cells.add(escape(cell));
        }
        return String.join(",", cells);
    }

    /** 对逗号、双引号、换行进行 CSV 转义。 */
    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        boolean needQuotes = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        return needQuotes ? "\"" + escaped + "\"" : escaped;
    }

    /** 解析单行 CSV 文本。 */
    private static String[] parseLine(String line) {
        List<String> cells = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (quoted) {
                if (ch == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cell.append('"');
                        i++;
                    } else {
                        quoted = false;
                    }
                } else {
                    cell.append(ch);
                }
            } else if (ch == '"') {
                quoted = true;
            } else if (ch == ',') {
                cells.add(cell.toString());
                cell.setLength(0);
            } else {
                cell.append(ch);
            }
        }
        cells.add(cell.toString());
        return cells.toArray(String[]::new);
    }
}
