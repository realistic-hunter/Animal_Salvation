package com.animalsalvation.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OperationRecord {
    private String operationType;
    private int targetId;
    private String description;
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
