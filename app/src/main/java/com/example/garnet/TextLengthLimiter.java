package com.example.garnet;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class TextLengthLimiter implements TextWatcher {
    // 用于限制输入字符的长度
    private CharSequence wordNum;//记录输入的字数

    private TextView tv;
    private final int MAX_LENGTH = 20;

    TextLengthLimiter(TextView tv){
        this.tv = tv;
    }

    TextLengthLimiter(TextView tv, int length){
        this.tv = tv;
        tv.setText(Integer.toString(length)+"/" + MAX_LENGTH);
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        wordNum = s;//实时记录输入的字数
    }

    @Override
    public void afterTextChanged(Editable s) {
        tv.setText(s.length() + "/" + MAX_LENGTH);
    }//
}
