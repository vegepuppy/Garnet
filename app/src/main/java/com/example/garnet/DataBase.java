package com.example.garnet;

import java.util.ArrayList;
import java.util.List;

public class DataBase {
    private static List<InfoItem> database = new ArrayList<>();

    public static List<InfoItem> getDatabase() {
        return database;
    }
}
