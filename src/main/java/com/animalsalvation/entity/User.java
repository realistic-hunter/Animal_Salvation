package com.animalsalvation.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 用户实体类。
 *
 * <p>用于登录验证、用户管理、角色控制和个人信息维护。</p>
 */
public class User {
    /** 用户编号。 */
    private int id;
    /** 登录用户名。 */
    private String username;
    /** 加密后的密码哈希值，不保存明文密码。 */
    private String passwordHash;
    /** 用户真实姓名。 */
    private String realName;
    /** 联系电话。 */
    private String phone;
    /** 邮箱。 */
    private String email;
    /** 角色：ADMIN、STAFF、ADOPTER。 */
    private String role;
    /** 是否启用账号。 */
    private boolean enabled;
    /** 创建时间。 */
    private LocalDateTime createTime;
    /** 最近登录时间。 */
    private LocalDateTime lastLoginTime;

    public User(int id, String username, String passwordHash, String realName, String phone,
                String email, String role, boolean enabled, LocalDateTime createTime,
                LocalDateTime lastLoginTime) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.realName = realName;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
        this.createTime = createTime;
        this.lastLoginTime = lastLoginTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getCreateTimeText() {
        return format(createTime);
    }

    public String getLastLoginTimeText() {
        return format(lastLoginTime);
    }

    private String format(LocalDateTime time) {
        return time == null ? "" : time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
