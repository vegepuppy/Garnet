package com.example.garnet;

import java.util.ArrayList;
import java.util.List;

public class HomeItem {
    private final String hometask;
    private List<String> linklist = new ArrayList<>();

    public HomeItem(String task,List<String> link){
        this.hometask = task;
        this.linklist = link;
    }

    public String getHomeTask(){return hometask;}

    public List<String> getLinkList(){return linklist;}
}
