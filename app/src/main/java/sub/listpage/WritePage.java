package sub.listpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import main.MainActivity;
import psj.hahaha.R;

/**
 * Created by user on 2016-11-11.
 */

public class WritePage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write);
    }

    public void goMain(View v){

        Intent mainIntent = new Intent(WritePage.this, MainActivity.class);
        mainIntent.putExtra("write","ex");
        startActivity(mainIntent);
        finish();
    }


    public void goContent(View v){

        Intent contentIntent = new Intent(WritePage.this, ContentPage.class);
        //제목
        EditText eTitle = (EditText) findViewById(R.id.editTitle);
        //본문
        EditText eMain = (EditText) findViewById(R.id.editMain);
        //이미지
        ImageView eImage = (ImageView) findViewById(R.id.imageWrite);
        contentIntent.putExtra("title",eTitle.getText().toString());
        contentIntent.putExtra("main",eMain.getText().toString());
        startActivity(contentIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent Mainintent = new Intent(WritePage.this, MainActivity.class);
        Mainintent.putExtra("write","ex");
        startActivity(Mainintent);
    }

    //이미지 첨부(사진 or 앨범)
    public void attachImage(View v){

    }
}