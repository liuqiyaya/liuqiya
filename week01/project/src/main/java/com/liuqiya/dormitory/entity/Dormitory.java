package com.liuqiya.dormitory.entity;

public class Dormitory
{private String building;
   private String room;

    public Dormitory() {
    }

    public Dormitory(String building, String room) {
        this.building = building;
        this.room = room;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
    @Override
    public String toString() {
        return building + room;
    }
}
