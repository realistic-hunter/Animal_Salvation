package com.animalsalvation.service;

import com.animalsalvation.entity.User;
import com.animalsalvation.storage.UserFileStorage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户业务层。
 *
 * <p>负责登录验证、用户管理、修改个人信息、修改密码，以及用户数据文件保存。</p>
 */
public class UserService {
    private static final Path DEFAULT_USER_FILE = Path.of("data", "users.csv");
    private static final String PASSWORD_SALT = "AnimalSalvation";

    /** 按用户编号索引用户。 */
    private final Map<Integer, User> usersById = new HashMap<>();
    /** 按用户名索引用户，用于登录和查重。 */
    private final Map<String, User> usersByUsername = new HashMap<>();

    public UserService() {
        addDefaultAdmin();
    }

    /**
     * 登录验证。
     *
     * <p>用户名存在、账号启用、密码哈希匹配时才允许登录。</p>
     */
    public User login(String username, String password) {
        String normalizedUsername = normalizeUsername(username);
        User user = usersByUsername.get(normalizedUsername);
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误。");
        }
        if (!user.isEnabled()) {
            throw new IllegalStateException("该用户账号已被禁用。");
        }
        if (!user.getPasswordHash().equals(hashPassword(password))) {
            throw new IllegalArgumentException("用户名或密码错误。");
        }
        user.setLastLoginTime(LocalDateTime.now());
        return user;
    }

    /** 新增用户。 */
    public User addUser(String username, String password, String realName, String phone,
                        String email, String role) {
        String normalizedUsername = normalizeUsername(username);
        if (usersByUsername.containsKey(normalizedUsername)) {
            throw new IllegalArgumentException("用户名已存在。");
        }
        User user = new User(nextId(), normalizedUsername, hashPassword(password), required(realName, "姓名"),
                trim(phone), trim(email), required(role, "角色"), true, LocalDateTime.now(), null);
        putUser(user);
        return user;
    }

    /** 删除用户。 */
    public boolean removeUser(int userId) {
        User user = usersById.remove(userId);
        if (user == null) {
            return false;
        }
        usersByUsername.remove(user.getUsername());
        return true;
    }

    /** 管理员更新用户资料、角色和启用状态。 */
    public User updateUser(int userId, String realName, String phone, String email,
                           String role, boolean enabled) {
        User user = requireUser(userId);
        user.setRealName(required(realName, "姓名"));
        user.setPhone(trim(phone));
        user.setEmail(trim(email));
        user.setRole(required(role, "角色"));
        user.setEnabled(enabled);
        return user;
    }

    /** 用户修改自己的个人资料。 */
    public User updatePersonalInfo(int userId, String realName, String phone, String email) {
        User user = requireUser(userId);
        user.setRealName(required(realName, "姓名"));
        user.setPhone(trim(phone));
        user.setEmail(trim(email));
        return user;
    }

    /** 用户通过旧密码修改自己的密码。 */
    public void changePassword(int userId, String oldPassword, String newPassword) {
        User user = requireUser(userId);
        if (!user.getPasswordHash().equals(hashPassword(oldPassword))) {
            throw new IllegalArgumentException("旧密码错误。");
        }
        user.setPasswordHash(hashPassword(newPassword));
    }

    /** 管理员直接重置指定用户密码。 */
    public void resetPassword(int userId, String newPassword) {
        requireUser(userId).setPasswordHash(hashPassword(newPassword));
    }

    public User findById(int userId) {
        return usersById.get(userId);
    }

    /** 查询所有用户，并按编号升序返回。 */
    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>(usersById.values());
        users.sort(Comparator.comparingInt(User::getId));
        return users;
    }

    /** 检查用户名是否已经存在。 */
    public boolean usernameExists(String username) {
        return usersByUsername.containsKey(normalizeUsername(username));
    }

    public void saveUsers() throws IOException {
        saveUsers(DEFAULT_USER_FILE);
    }

    /** 保存用户数据到指定文件。 */
    public void saveUsers(Path file) throws IOException {
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        UserFileStorage.save(findAllUsers(), file);
    }

    public void loadUsers() throws IOException {
        loadUsers(DEFAULT_USER_FILE);
    }

    /** 从指定文件读取用户数据；读取后确保默认管理员存在。 */
    public void loadUsers(Path file) throws IOException {
        usersById.clear();
        usersByUsername.clear();
        for (User user : UserFileStorage.load(file)) {
            putUser(user);
        }
        addDefaultAdmin();
    }

    /** 默认管理员账号：op / 666666。 */
    private void addDefaultAdmin() {
        if (!usersByUsername.containsKey("op")) {
            int adminId = usersById.containsKey(1) ? nextId() : 1;
            putUser(new User(adminId, "op", hashPassword("666666"), "系统管理员",
                    "", "", "ADMIN", true, LocalDateTime.now(), null));
        }
    }

    private void putUser(User user) {
        usersById.put(user.getId(), user);
        usersByUsername.put(user.getUsername(), user);
    }

    private User requireUser(int userId) {
        User user = usersById.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在。");
        }
        return user;
    }

    private int nextId() {
        int maxId = 0;
        for (int id : usersById.keySet()) {
            maxId = Math.max(maxId, id);
        }
        return maxId + 1;
    }

    /** 用户名统一转成小写，避免 OP 和 op 被当成两个账号。 */
    private String normalizeUsername(String username) {
        String value = required(username, "用户名").toLowerCase();
        if (value.length() < 2) {
            throw new IllegalArgumentException("用户名至少需要 2 个字符。");
        }
        return value;
    }

    private String required(String value, String fieldName) {
        String text = trim(value);
        if (text.isEmpty()) {
            throw new IllegalArgumentException(fieldName + "不能为空。");
        }
        return text;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    /** 使用 SHA-256 对密码加盐哈希，避免明文保存密码。 */
    private String hashPassword(String password) {
        String value = required(password, "密码");
        if (value.length() < 6) {
            throw new IllegalArgumentException("密码至少需要 6 个字符。");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((PASSWORD_SALT + value).getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("当前 Java 环境不支持 SHA-256。", exception);
        }
    }
}
