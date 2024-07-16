package com.example.garnet;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

public class AddTodoAlertDialogHelper {
    private Button dateButton;
    private EditText editText;
    private TextLengthLimiter textLengthLimiter;
    private AlertDialog.Builder builder; //要传入getActivity
    private DatePickerDialog datePickerDialog;
    private FragmentActivity fragmentActivity;
    private TodoItem ret;
    private String task;

    public AddTodoAlertDialogHelper(FragmentActivity fragmentActivity, View view) {

        this.fragmentActivity = fragmentActivity;

        //builder 的初始化
        builder = new AlertDialog.Builder(fragmentActivity);
        builder.setTitle("创建待办");
        builder.setView(view);
        builder.setPositiveButton("确定", new PositiveButtonListener());
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        //字符输入组件初始化
        editText = view.findViewById(R.id.task_et);
        final TextView countTv = view.findViewById(R.id.word_count_tv);
        editText.addTextChangedListener(new TextLengthLimiter(countTv));

        //日期按钮初始化
        dateButton = view.findViewById(R.id.choose_date_btn);
        dateButton.setOnClickListener(new DateButtonListener());
    }


    private class PositiveButtonListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String text = editText.getText().toString();

            //判断标题不能为空
            if(text.trim().isEmpty()){
                Toast.makeText(fragmentActivity, "事项不能为空!", Toast.LENGTH_SHORT).show();
            }else{
                task = text;
            }
        }
    }

    public void show(){
        builder.show();
    }
}
