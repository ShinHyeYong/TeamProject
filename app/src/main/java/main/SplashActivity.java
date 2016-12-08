package main;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import psj.hahaha.R;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_TIME_OUT=1500; //스플래쉬 액티비티를 보여주는 시간

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //스레드를 이용해 스플래쉬 시간이 지나면 자동으로 액티비티 종료시키고
        //메인 액티비티로 이동
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        },SPLASH_TIME_OUT);
    }
}
