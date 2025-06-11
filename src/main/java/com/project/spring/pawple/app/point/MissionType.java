package com.project.spring.pawple.app.point;

public enum MissionType {
    WALK("산책하기"),
    WATER("물 충분히 마시기"),
    FOOD("제시간에 밥 먹기"),
    TOOTH("양치하기기"),
    PLAY("놀이시간 갖기");

    private final String description;

    MissionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
