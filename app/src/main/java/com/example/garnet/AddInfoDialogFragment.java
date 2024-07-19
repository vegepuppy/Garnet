package com.example.garnet;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class AddInfoDialogFragment extends DialogFragment {
    private EditText et;
    private StateListener stateListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 可能要改，这里可以设置样式

    }

    @Override
    public void onStart() {
        // 修改样式
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.dialog_fragment_add_info,
                container,false);

        et = v.findViewById(R.id.add_info_dialog_edit);

        Button confirmButton = v.findViewById(R.id.add_info_confirm_button);
        confirmButton.setOnClickListener(new ConfirmedListener());

        return v;
    }

    public void setStateListener(StateListener stateListener){
        this.stateListener = stateListener;
    }

    private class ConfirmedListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String uri = et.getText().toString();
            if(uri.trim().isEmpty()){
                Toast.makeText(requireActivity(),"事项不能为空!",Toast.LENGTH_SHORT).show();
                et.getText().clear();
            }else {
                stateListener.onConfirmed(uri);
                dismiss();
            }
        }
    }

    public interface StateListener{
        void onConfirmed(String uri);
    }
}
