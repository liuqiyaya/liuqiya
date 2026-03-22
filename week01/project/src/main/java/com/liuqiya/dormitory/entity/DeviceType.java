package com.liuqiya.dormitory.entity;

public class DeviceType {
    private Integer id;
    private String typeName;

    public DeviceType() {
    }

    public DeviceType(String typeName, Integer id) {
        this.typeName = typeName;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    @Override
    public String toString() {
        return id + ". " + typeName;
    }
}

