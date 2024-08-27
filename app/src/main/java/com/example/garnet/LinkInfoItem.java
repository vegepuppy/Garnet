package com.example.garnet;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class LinkInfoItem extends InfoItem{

    private boolean isLinkFetched;
    // 以上两个变量不能是final，用户可能会改，忽视IDE提示

    public static final long LACK_ID = -1;


    public void setDisplayString(String displayString) {
        this.displayString = displayString;
    }

    @Override
    void show(Context context) {
        String uriString = getContent();

        Uri webpage = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        try {
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(context, "无效链接！", Toast.LENGTH_SHORT).show();
        }
    }

    public LinkInfoItem(String display, String uri, long belong, long id) {
        super(display, uri, belong, id);
    }

    public boolean isLinkFetched() {
        return isLinkFetched;
    }

    public void setLinkFetched(boolean linkFetched) {
        isLinkFetched = linkFetched;
    }
}
