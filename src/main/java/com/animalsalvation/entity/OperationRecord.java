package com.animalsalvation.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 操作记录实体类。
 *
 * <p>每条记录会被压入操作栈，用于显示历史和撤销最近一次操作。</p>
 */
public class OperationRecord {
    /** 操作类型，例如新增动物、新增救助任务、提交领养申请。 */
    private String operationType;
    /** 被操作对象的编号。 */
    private int targetId;
    /** 操作说明。 */
    private String description;
    /** 操作发生时间。 */
    private LocalDateTime operationTime;

    public OperationRecord(String operationType, int targetId, String description, LocalDateTime operationTime) {
        this.operationType = operationType;
        this.targetId = targetId;
        this.description = description;
        this.operationTime = operationTime;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }

    public String getOperationTimeText() {
        return operationTime == null ? "" : operationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
