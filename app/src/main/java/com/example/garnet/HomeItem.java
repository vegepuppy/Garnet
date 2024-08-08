package com.example.garnet;

import java.util.ArrayList;
import java.util.List;

public class HomeItem {
    private final String hometask;
    private List<String> linklist = new ArrayList<>();
    private boolean Done;
    private boolean isRead;

    public HomeItem(String task,List<String> link,boolean done,boolean read){
        this.hometask = task;
        this.linklist = link;
        this.Done = done;
        this.isRead = read;
    }

    public String getHomeTask(){return hometask;}

    public List<String> getLinkList(){return linklist;}

    public boolean getDone(){return Done;}

    public boolean getRead(){return isRead;}

    public void changeRead(){isRead = true;}
}
