package com.example.garnet;

import java.util.ArrayList;
import java.util.List;

public class HomeItem {
    private final String hometask;
    private List<String> linklist = new ArrayList<>();
    private List<String> uriList = new ArrayList<>();
    private boolean Done;
    private boolean isRead;
    private long id;

    public HomeItem(String task,List<String> link,List<String> uri,boolean done,boolean read,long id){
        this.hometask = task;
        this.linklist = link;
        this.Done = done;
        this.uriList = uri;
        this.isRead = read;
        this.id = id;
    }

    public String getHomeTask(){return hometask;}

    public List<String> getLinkList(){return linklist;}

    public boolean getDone(){return Done;}

    public boolean getRead(){return isRead;}

    public void changeRead(){isRead = true;}

    public void refreshRead(){isRead = false;}

    public List<String> getUriList() {
        return uriList;
    }

    public long getId() {return id;}
}
