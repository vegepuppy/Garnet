package com.example.garnet;

import java.util.ArrayList;
import java.util.List;

public class HomeItem {
    private final String hometask;
    private List<InfoItem> home_info_list;
    private boolean Done;
    private long id;

    public HomeItem(String task,List<InfoItem> infolist,boolean done,long id){
        this.hometask = task;
        this.home_info_list = infolist;
        this.Done = done;
        this.id = id;
    }

    public String getHomeTask(){return hometask;}

    public boolean getDone(){return Done;}

    public List<InfoItem> getinfolist() {
        return home_info_list;
    }

    public long getId() {return id;}

    public List<String> getLink(){
        List<String> link = new ArrayList<>();
        if (home_info_list == null||home_info_list.get(0)==null){
            link.add("无链接");
            return link;
        }
        else {
            for (int i = 0; i < home_info_list.size(); i++) {
                link.add(home_info_list.get(i).getDisplayString());
            }
            return link;
        }
    }
}
