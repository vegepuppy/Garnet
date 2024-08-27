package com.example.garnet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_INFO_ITEM = "NOTE_INFO_ITEM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        TextView titleTv = findViewById(R.id.note_title_tv);
        TextView contentTv = findViewById(R.id.note_content_tv);

        Intent intent = getIntent();
        NoteInfoItem noteInfoItem = (NoteInfoItem) intent.getSerializableExtra(NOTE_INFO_ITEM);

        titleTv.setText(noteInfoItem.getDisplayString());
        contentTv.setText(noteInfoItem.getContent());
    }
}