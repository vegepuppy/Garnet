package com.example.garnet;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class TextLengthLimiter implements TextWatcher {
    // 用于限制输入字符的长度

    private final TextView limiterTextView;
    private final int MAX_LENGTH = 20;

    TextLengthLimiter(TextView limiterTextView){
        this.limiterTextView = limiterTextView;
    }

    TextLengthLimiter(TextView limiterTextView, int length){
        this.limiterTextView = limiterTextView;
        limiterTextView.setText(Integer.toString(length) + "/" + MAX_LENGTH);
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        limiterTextView.setText(s.length() + "/" + MAX_LENGTH);
    }

    public static void bindTextLimiter(EditText et, TextView limiterTextView){
        et.addTextChangedListener(new TextLengthLimiter(limiterTextView));
    }
}
