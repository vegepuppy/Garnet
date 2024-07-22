package com.example.garnet;

public class InfoGroup {
    private String name;
    private long id;
    public static long LACK_ID = -1;

    public InfoGroup(String name, long id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }


}
