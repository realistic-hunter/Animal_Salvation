package com.animalsalvation.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdoptionApplication {
    private int applyId;
    private int animalId;
    private String applicantName;
    private String phone;
    private String reason;
    private String status;
    private LocalDateTime applyTime;

    public AdoptionApplication(int applyId, int animalId, String applicantName, String phone,
                               String reason, String status, LocalDateTime applyTime) {
        this.applyId = applyId;
        this.animalId = animalId;
        this.applicantName = applicantName;
        this.phone = phone;
        this.reason = reason;
        this.status = status;
        this.applyTime = applyTime;
    }

    public int getApplyId() {
        return applyId;
    }

    public void setApplyId(int applyId) {
        this.applyId = applyId;
    }

    public int getAnimalId() {
        return animalId;
    }

    public void setAnimalId(int animalId) {
        this.animalId = animalId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(LocalDateTime applyTime) {
        this.applyTime = applyTime;
    }

    public String getApplyTimeText() {
        return applyTime == null ? "" : applyTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
