package com.example.garnet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_INFO_ITEM = "NOTE_INFO_ITEM";
    private EditText contentEt;
    private EditText titleEt;
    private NoteInfoItem noteInfoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        titleEt = findViewById(R.id.note_title_tv);
        contentEt = findViewById(R.id.note_content_tv);

        Intent intent = getIntent();

        noteInfoItem = (NoteInfoItem) intent.getSerializableExtra(NOTE_INFO_ITEM);

        titleEt.setText(noteInfoItem.getDisplayString());
        contentEt.setText(noteInfoItem.getContent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO: 2024-08-27 退出时保存
        GarnetDatabaseHelper helper = new GarnetDatabaseHelper(NoteActivity.this);
        String displayString = titleEt.getText().toString();
        String content = contentEt.getText().toString();

        if(displayString.isEmpty()){
            if(content.isEmpty()){
                return;
            }else {
                displayString = content;
            }
        }
        noteInfoItem.setContent(content);
        noteInfoItem.setDisplayString(displayString);

        if (noteInfoItem.getId() != InfoItem.LACK_ID) {
            helper.updateInfoItem(noteInfoItem, displayString, content);
        }else{
            helper.insertInfoItem(noteInfoItem);
        }

    }

}