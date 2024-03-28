package com.example.garnet;

import java.util.ArrayList;
import java.util.List;

public class DataBase {
    private static List<InfoItem> infoItemList = new ArrayList<>();
    public DataBase() {
        infoItemList.add(new InfoItem("高等数学"));
        infoItemList.add(new InfoItem("学校通知"));
        for (int i = 0; i < 5; i++) {
            infoItemList.add(new InfoItem(Integer.toString(i)+" infoitem"));
        }
    }

    public static List<InfoItem> getInfoItemList() {
        return infoItemList;
    }

    // This is a test comment.
}
