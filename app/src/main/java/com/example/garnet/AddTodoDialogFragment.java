package com.example.garnet;

import android.app.DatePickerDialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Objects;

public class AddTodoDialogFragment extends DialogFragment {

    private EditText et;
    private Button dateButton;
    private StateListener stateListener;
    private String dateSelected = TodoItem.LACK_DATE;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 可能要改，这里可以设置样式
    }

    @Override
    public void onStart() {
        //修改样式
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        assert window != null;
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
        View v = getLayoutInflater().inflate(R.layout.dialog_fragment_add_todo, container, false);

        et = v.findViewById(R.id.add_todo_dialog_edit);
        TextView limiterTv = v.findViewById(R.id.add_todo_limiter);
        TextLengthLimiter.bindTextLimiter(et,limiterTv);

        dateButton = v.findViewById(R.id.add_todo_date_button);
        Button confrimButton = v.findViewById(R.id.add_todo_confirm_button);

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
            if(task.trim().isEmpty()){
                Toast.makeText(requireActivity(),"事项不能为空!",Toast.LENGTH_SHORT).show();
                et.getText().clear();
            }else{
                stateListener.onConfirmed(dateSelected,task);
                dismiss();
            }
        }
    }

    private class DateButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireActivity(), // 替换getActivity
                    new MyOnDateSetListener(),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        }
    }

    public interface StateListener{
        void onConfirmed(String date, String task);
    }


    private class MyOnDateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateSelected = DateFormatter.formatDate(year,month,dayOfMonth);
            dateButton.setText(dateSelected);
        }
    }
}
