package com.example.jeremysun.mywebviewtest;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int pid = Process.myPid();
        int tid = Process.myTid();
        Log.d(TAG,"pid = " + pid);
        Log.d(TAG,"tid = " + tid);

        editText = findViewById(R.id.et_input);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(MainActivity.this, WebViewActivity.class));
                String url;
                String content = editText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    url = "https://www.baidu.com";
                } else {
                    if (content.startsWith("http://") || content.startsWith("https://")) {
                        url = content;
                    } else {
                        StringBuffer sb = new StringBuffer(content);
                        sb.insert(0, "http://");
                        url = sb.toString();
                    }
                }
                intent.putExtra("web_url", url);
                intent.putExtra("type","remote");
                startActivity(intent);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(MainActivity.this, WebViewActivity.class));
                intent.putExtra("type","local");
                startActivity(intent);
            }
        });


    }

}
