package com.example.garnet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.garnet.utils.LogUtils;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LogInActivity extends AppCompatActivity {
    private EditText passWordEditText, userNameEditText;
    private Button logInButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        passWordEditText = findViewById(R.id.password_et);
        userNameEditText = findViewById(R.id.user_name_et);
        logInButton = findViewById(R.id.log_in_button_activity);
        registerButton = findViewById(R.id.register_button_activity);

        logInButton.setOnClickListener(v -> {
            // 两个字符串必须在listener内部获取
            String userName = userNameEditText.getText().toString();
            String passWord = passWordEditText.getText().toString();
            LogUtils.logWeb("userName:  " + userName);
            LogUtils.logWeb("passWord:  " + passWord);
            if (!userName.isEmpty() && !passWord.isEmpty()){
                sendLoginDetail(userName, passWord, "login");
            }else {
                Toast.makeText(this, "信息不完整！", Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(v -> {
            // 两个字符串必须在listener内部获取
            String userName = userNameEditText.getText().toString();
            String passWord = passWordEditText.getText().toString();
            if (!userName.isEmpty() && !passWord.isEmpty()){
                sendLoginDetail(userName, passWord, "register");
            }else {
                Toast.makeText(this, "信息不完整！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendLoginDetail(String userName, String passWord, String endpoint) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        try {
            json.put("username", userName);
            json.put("password", passWord);
            LogUtils.logWeb("json sent is:" + (json));
        }catch (Exception e){
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, json.toString());

        //这里使用本机的ip
        String URL = "http://10.0.2.2:3001/" + endpoint;

        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();

        // 在后台线程发送请求
        new Thread(() -> {
            try(Response response = client.newCall(request).execute()) {
                if (endpoint.equals("register")){
                    if (response.isSuccessful()){
                        String message ="注册成功，为您自动登录";
                        runOnUiThread(() -> Toast.makeText(LogInActivity.this, message, Toast.LENGTH_SHORT).show());
                        sendLoginDetail(userName, passWord, "login");
                    }else {
                        String message = "Error: " + response.code();
                        runOnUiThread(() -> Toast.makeText(LogInActivity.this, message, Toast.LENGTH_SHORT).show());
                    }
                } else if (endpoint.equals("login")) {
                    String message = response.isSuccessful() ? "登录成功" : "Error: " + response.code();
                    runOnUiThread(() -> Toast.makeText(LogInActivity.this, message, Toast.LENGTH_SHORT).show());

                    if (response.isSuccessful()){
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("username", userName);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.logWeb("An error occurred.");
                runOnUiThread(() -> Toast.makeText(LogInActivity.this, "An error occurred", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}