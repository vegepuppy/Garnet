package com.example.garnet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;

public class NoteInfoItem extends InfoItem  {


    @Override
    void show(Context context) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(NoteActivity.NOTE_INFO_ITEM, this);
        context.startActivity(intent);

    }

    public NoteInfoItem(String displayString, String content, long belong, long id) {
        super(displayString, content, belong, id);
    }
}