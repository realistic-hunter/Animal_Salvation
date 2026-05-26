package com.animalsalvation.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 领养申请实体类。
 *
 * <p>记录某个申请人对某只动物提交的领养申请及审核结果。</p>
 */
public class AdoptionApplication {
    /** 申请编号。 */
    private int applyId;
    /** 被申请领养的动物编号。 */
    private int animalId;
    /** 申请人姓名。 */
    private String applicantName;
    /** 申请人联系电话。 */
    private String phone;
    /** 申请理由。 */
    private String reason;
    /** 审核状态，例如待审核、审核通过、审核拒绝。 */
    private String status;
    /** 申请提交时间。 */
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
