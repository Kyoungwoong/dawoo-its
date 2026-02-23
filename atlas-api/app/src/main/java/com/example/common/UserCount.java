package com.example.common;

public class UserCount {
    private String name;
    private int age;
    private int count;

    public UserCount() {
    }

    public UserCount(String name, int age, int count) {
        this.name = name;
        this.age = age;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
