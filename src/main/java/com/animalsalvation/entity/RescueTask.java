package com.animalsalvation.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 救助任务实体类。
 *
 * <p>记录一条需要志愿者处理的动物救助任务。</p>
 */
public class RescueTask {
    /** 任务编号。 */
    private int taskId;
    /** 关联动物编号。 */
    private int animalId;
    /** 任务标题。 */
    private String title;
    /** 任务地点。 */
    private String location;
    /** 紧急程度：非常紧急、紧急、普通。 */
    private String urgencyLevel;
    /** 任务状态，例如待处理、已派发。 */
    private String status;
    /** 负责志愿者。 */
    private String volunteerName;
    /** 任务创建时间。 */
    private LocalDateTime createTime;

    public RescueTask(int taskId, int animalId, String title, String location, String urgencyLevel,
                      String status, String volunteerName, LocalDateTime createTime) {
        this.taskId = taskId;
        this.animalId = animalId;
        this.title = title;
        this.location = location;
        this.urgencyLevel = urgencyLevel;
        this.status = status;
        this.volunteerName = volunteerName;
        this.createTime = createTime;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getAnimalId() {
        return animalId;
    }

    public void setAnimalId(int animalId) {
        this.animalId = animalId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVolunteerName() {
        return volunteerName;
    }

    public void setVolunteerName(String volunteerName) {
        this.volunteerName = volunteerName;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getCreateTimeText() {
        return createTime == null ? "" : createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
