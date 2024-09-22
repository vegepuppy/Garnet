package com.example.garnet;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_INFO_ITEM = "NOTE_INFO_ITEM";
    private EditText contentEt;
    private EditText titleEt;
    private NoteInfoItem noteInfoItem;
    public static final int RESULT_NOTE_INSERT_CODE = 1111;// FIXME: 2024-09-22 暂时用1111表示，后面再改
    public static final int RESULT_NOTE_UPDATE_CODE = 2222;

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

        FloatingActionButton saveFab = findViewById(R.id.save_note_fab);
        saveFab.setOnClickListener(new NoteSaveFabOnClickListener());
    }

    private class NoteSaveFabOnClickListener implements View.OnClickListener {
        public void onClick(View v) {
            GarnetDatabaseHelper helper = new GarnetDatabaseHelper(NoteActivity.this);
            String displayString = titleEt.getText().toString();
            String content = contentEt.getText().toString();

            setResult(333);
            if (displayString.isEmpty()) {
                if (content.isEmpty()) {
                    finish();
                } else {
                    if (content.length() <= 15) {
                        displayString = content;
                    } else {
                        displayString = content.substring(0, 15) + "...";
                    }
                }
            }

            noteInfoItem.setContent(content);
            noteInfoItem.setDisplayString(displayString);

            if (noteInfoItem.getId() != InfoItem.LACK_ID) {
                setResult(RESULT_NOTE_UPDATE_CODE);
                helper.updateInfoItem(noteInfoItem, displayString, content);
            } else {
                setResult(RESULT_NOTE_INSERT_CODE);
                helper.insertInfoItem(noteInfoItem);
            }

            finish();
        }
    }
}
