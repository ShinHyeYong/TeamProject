package intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import fragment.drawer.NoticeFragment;
import psj.hahaha.R;
import utils.Constants;

public class SubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Intent intent = new Intent();

        int count = getIntent().getExtras().getInt(Constants.FRAGMENT_KEY);
        if(count == Constants.DRAWER_1_PAGE){
            Fragment fragment = NoticeFragment.newInstance();
            fragment.setArguments(savedInstanceState);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment).addToBackStack(null).commit();
            /*
        }else if(count == Constants.DRAWER_2_PAGE){
            Fragment fragment = NoticeFragment.newInstance();
            fragment.setArguments(savedInstanceState);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_help, fragment).addToBackStack(null).commit();
        }else if(count == Constants.DRAWER_3_PAGE){
            Fragment fragment = NoticeFragment.newInstance();
            fragment.setArguments(savedInstanceState);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_qna, fragment).addToBackStack(null).commit();
                    */
        }
    }
}
