package sub.listpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import psj.hahaha.R;

/**
 * Created by user on 2016-11-11.
 */

public class ContentPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);
        Intent intent = getIntent();

        String titleValue = intent.getStringExtra("title");
        String mainValue = intent.getStringExtra("main");

        //제목
        TextView titleTv = (TextView) findViewById(R.id.titleTV);
        //본문
        TextView mainTv = (TextView) findViewById(R.id.mainTV);

        titleTv.setText(titleValue);
        mainTv.setText(mainValue);

        //본문 이미지
        ImageView cImage = (ImageView) findViewById(R.id.imageContent);

    }

    //코멘터리 전송
    public void submitComment(View v){

    }
}
