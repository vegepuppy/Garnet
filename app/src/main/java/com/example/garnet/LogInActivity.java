package com.example.garnet;

import android.os.Bundle;
import android.view.View;
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
    private EditText passWordEditText;
    private EditText userNameEditText;
    private Button logInButton;
    private final String URL = "http://10.0.2.2:3001/api";//这里要使用本机的ip

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        passWordEditText = findViewById(R.id.password_et);
        userNameEditText = findViewById(R.id.user_name_et);
        logInButton = findViewById(R.id.log_in_button_activity);

        logInButton.setOnClickListener(v -> {
            String userName = userNameEditText.getText().toString();
            if (!userName.isEmpty()){
                sendToServer("username",userName);
            }else {
                Toast.makeText(LogInActivity.this, "UserName is Empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendToServer(String key, String message) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        try {
            json.put(key, message);
            LogUtils.logWeb("json sent is:" + (json));
        }catch (Exception e){
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, json.toString());

        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();

        // 在后台线程发送请求
        new Thread(() -> {
            try(Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    LogUtils.logWeb("Message sent successfully");
                } else {
                    LogUtils.logWeb("Message send failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.logWeb("An error occurred.");
            }
        }).start();
    }
}