package com.animalsalvation.storage;

import com.animalsalvation.entity.User;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户文件存储类。
 *
 * <p>用户数据保存到 data/users.csv，密码字段保存的是哈希值。</p>
 */
public final class UserFileStorage {
    private UserFileStorage() {
    }

    /** 保存用户列表。 */
    public static void save(List<User> users, Path file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"id", "username", "passwordHash", "realName", "phone", "email",
                "role", "enabled", "createTime", "lastLoginTime"});
        for (User user : users) {
            rows.add(new String[]{
                    String.valueOf(user.getId()),
                    user.getUsername(),
                    user.getPasswordHash(),
                    user.getRealName(),
                    user.getPhone(),
                    user.getEmail(),
                    user.getRole(),
                    String.valueOf(user.isEnabled()),
                    CsvFiles.formatTime(user.getCreateTime()),
                    CsvFiles.formatTime(user.getLastLoginTime())
            });
        }
        CsvFiles.writeRows(file, rows);
    }

    /** 读取用户列表。 */
    public static List<User> load(Path file) throws IOException {
        List<User> users = new ArrayList<>();
        List<String[]> rows = CsvFiles.readRows(file);
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            users.add(new User(
                    Integer.parseInt(row[0]),
                    row[1],
                    row[2],
                    row[3],
                    row[4],
                    row[5],
                    row[6],
                    Boolean.parseBoolean(row[7]),
                    CsvFiles.parseTime(row[8]),
                    CsvFiles.parseTime(row[9])
            ));
        }
        return users;
    }
}
