package com.example.garnet;

import android.os.Handler;
import android.os.Message;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class FetchLinkRunnable implements Runnable {
    private final Handler mHandler;
    private final String mUri;

    public FetchLinkRunnable(Handler mHandler, String uri) {
        this.mHandler = mHandler;
        this.mUri = uri;
    }

    @Override
    public void run() {
        FetchLinkMessageObject object = fetchLinkTitle(mUri);
        Message msg = new Message();
        msg.what = 1;  // TODO: 2024-07-21 因为这里只有一个thread，所以设为1无妨，正常应该是常量
        msg.obj = object;
        mHandler.sendMessage(msg);
    }

    private FetchLinkMessageObject fetchLinkTitle(String uriString) {
        FetchLinkMessageObject object = new FetchLinkMessageObject();
        try {
            Connection connection = Jsoup.connect(uriString).timeout(3000);
            Document document = connection.get();
            object.linkTitle = document.title();
            object.isValid = true;
            object.isSuccess = true;
        } catch (IOException e) {
            object.isSuccess = false;
        } catch (java.lang.IllegalArgumentException e) {
            object.isValid = false;
        }
        return object;
    }

}
