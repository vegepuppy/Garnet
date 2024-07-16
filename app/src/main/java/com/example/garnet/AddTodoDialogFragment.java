package com.example.garnet;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class AddTodoDialogFragment extends DialogFragment {

    private View v;
    private TextView titleTv;
    private EditText et;
    private TextView limiterTv;
    private Button dateButton;
    private Button confrimButton;
    private StateListener stateListener;
    private String dateSelected = "无日期";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 2024-07-16 可能要改，这里可以设置样式
    }

    @Override
    public void onStart() {
        //修改样式
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 填充布局，获取组件
        v = getLayoutInflater().inflate(R.layout.dialog_fragment_add_todo, container, false);
        titleTv = v.findViewById(R.id.add_todo_dialog_title);
        et = v.findViewById(R.id.add_todo_dialog_edit);
        limiterTv = v.findViewById(R.id.add_todo_limiter);
        dateButton = v.findViewById(R.id.add_todo_date_button);
        confrimButton = v.findViewById(R.id.add_todo_confirm_button);

        // 给日期按钮设置listener
        dateButton.setOnClickListener(new DateButtonListener());

        //给confirm设置listener
        confrimButton.setOnClickListener(new ConfirmedListener());
        return v;


    }

    public void setStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }

    private class ConfirmedListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String task = et.getText().toString();
            stateListener.onConfirmed(dateSelected,task);
            dismiss();
        }
    }

    private class DateButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getActivity(),
                    new MyOnDateSetListener(),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        }
    }

    public interface StateListener{
        TodoItem onConfirmed(String date, String task);
    }


    private class MyOnDateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateSelected = DateFormatter.formatDate(year,month,dayOfMonth);
            dateButton.setText(dateSelected);
        }
    }
}
