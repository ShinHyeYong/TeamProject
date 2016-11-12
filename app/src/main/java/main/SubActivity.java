package main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import main.fragment.drawer.HelpFragment;
import main.fragment.drawer.NoticeFragment;
import main.fragment.drawer.QnaFragment;
import psj.hahaha.R;
import utils.Constants;

public class SubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

//        Intent intent = new Intent();
        TextView title = (TextView) findViewById(R.id.toolbar_title);

        int count = getIntent().getExtras().getInt(Constants.FRAGMENT_KEY);

        if(count == Constants.DRAWER_1_PAGE){
            title.setText("공지사항");
            Fragment fragment = NoticeFragment.newInstance();
            fragment.setArguments(savedInstanceState);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment).addToBackStack(null).commit();
        }else if(count == Constants.DRAWER_2_PAGE){
            title.setText("도움말");
            Fragment fragment = HelpFragment.newInstance();
            fragment.setArguments(savedInstanceState);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment).addToBackStack(null).commit();
        }else if(count == Constants.DRAWER_3_PAGE){
            title.setText("문의하기");
            Fragment fragment = QnaFragment.newInstance();
            fragment.setArguments(savedInstanceState);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getSupportFragmentManager().getBackStackEntryCount() > 1){
            getSupportFragmentManager().popBackStack();
        }else{
            finish();
        }
    }
}
