package com.animalsalvation.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Animal {
    private int id;
    private String name;
    private String type;
    private String gender;
    private int age;
    private String healthStatus;
    private String rescueStatus;
    private String adoptStatus;
    private String foundLocation;
    private LocalDateTime foundTime;

    public Animal(int id, String name, String type, String gender, int age, String healthStatus,
                  String rescueStatus, String adoptStatus, String foundLocation, LocalDateTime foundTime) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.gender = gender;
        this.age = age;
        this.healthStatus = healthStatus;
        this.rescueStatus = rescueStatus;
        this.adoptStatus = adoptStatus;
        this.foundLocation = foundLocation;
        this.foundTime = foundTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getRescueStatus() {
        return rescueStatus;
    }

    public void setRescueStatus(String rescueStatus) {
        this.rescueStatus = rescueStatus;
    }

    public String getAdoptStatus() {
        return adoptStatus;
    }

    public void setAdoptStatus(String adoptStatus) {
        this.adoptStatus = adoptStatus;
    }

    public String getFoundLocation() {
        return foundLocation;
    }

    public void setFoundLocation(String foundLocation) {
        this.foundLocation = foundLocation;
    }

    public LocalDateTime getFoundTime() {
        return foundTime;
    }

    public void setFoundTime(LocalDateTime foundTime) {
        this.foundTime = foundTime;
    }

    public String getFoundTimeText() {
        return foundTime == null ? "" : foundTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
