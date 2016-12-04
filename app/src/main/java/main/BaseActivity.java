package main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import psj.hahaha.R;
import utils.Constants;
import utils.dbconnected.ChangeInfoActivity;
import utils.dbconnected.LogInActivity;
import utils.dbconnected.SignInActivity;
import utils.model.UserInfo;

/**
 * Created by HY on 2016-11-09.
 */
public class BaseActivity extends AppCompatActivity implements Drawer.OnDrawerItemClickListener {
    private static String TAG = BaseActivity.class.getSimpleName();
    /* Drawer */
    private AccountHeader header;
    private Drawer drawer;
    /* ViewPager */
    private ViewPager viewPager;
    private FragmentPagerAdapter viewPagerAdapter;

    private View headerLayout;

    public BaseActivity() {

    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        Bundle args = new Bundle();
        Intent intent = new Intent(BaseActivity.this, SubActivity.class);
        if (drawerItem != null) {
            Fragment fragment = null;
            int index = (int) drawerItem.getIdentifier();
            if (position == index) return false;
            position = index;
            switch (position) {
                case Constants.DRAWER_1_PAGE:
                    args.putInt(Constants.FRAGMENT_KEY, Constants.DRAWER_1_PAGE);
                    intent.putExtras(args);
                    startActivity(intent, args);
                    break;
                case Constants.DRAWER_2_PAGE:
                    args.putInt(Constants.FRAGMENT_KEY, Constants.DRAWER_2_PAGE);
                    intent.putExtras(args);
                    startActivity(intent, args);
                    break;
                case Constants.DRAWER_3_PAGE:
                    args.putInt(Constants.FRAGMENT_KEY, Constants.DRAWER_3_PAGE);
                    intent.putExtras(args);
                    startActivity(intent, args);
                    break;
            }
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_navigation_drawer);

        SharedPreferences sp = getSharedPreferences("autologin", MODE_PRIVATE);
        UserInfo.UserEntry.USER_ID = sp.getString("id", UserInfo.UserEntry.USER_ID);
        UserInfo.UserEntry.USER_NAME = sp.getString("name", UserInfo.UserEntry.USER_NAME);
        UserInfo.UserEntry.USER_PWD = sp.getString("pwd", UserInfo.UserEntry.USER_PWD);
        if(UserInfo.UserEntry.USER_ID!=null)
            UserInfo.UserEntry.IS_LOGIN = true;
        else
            UserInfo.UserEntry.IS_LOGIN = false;
    }

    protected void setNavigationDrawer(Bundle args) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowHomeEnabled(false);

        View layout = getLayoutInflater().inflate(R.layout.fragment_navigation_drawer, null);

        Button signin_btn = (Button) layout.findViewById(R.id.signin_btn);
        Button login_btn = (Button) layout.findViewById(R.id.login_btn);
        Button change_btn = (Button) layout.findViewById(R.id.changeuserinfo_btn);
        if (UserInfo.UserEntry.IS_LOGIN) {
            login_btn.setText("로그아웃");
            change_btn.setVisibility(View.VISIBLE);
            signin_btn.setVisibility(View.GONE);
        } else {
            login_btn.setText("로그인");
            change_btn.setVisibility(View.GONE);
            signin_btn.setVisibility(View.VISIBLE);
        }

        headerLayout = layout;

        drawer = new DrawerBuilder().withActivity(this)
                .withTranslucentNavigationBar(false)
                .withToolbar(toolbar)
                .withHeader(layout)
                .addDrawerItems(
                        new SecondaryDrawerItem().withName("공지사항").withIdentifier(Constants.DRAWER_1_PAGE),
                        new SecondaryDrawerItem().withName("도움말").withIdentifier(Constants.DRAWER_2_PAGE),
                        new SecondaryDrawerItem().withName("문의하기").withIdentifier(Constants.DRAWER_3_PAGE)
                        //new SecondaryDrawerItem().withName("문의하기").withIcon(R.drawable.ic_action_profile).withIdentifier(Constants.DRAWER_3_PAGE)
                ).withOnDrawerItemClickListener(this).withSelectedItem(-1)
                .withSavedInstance(args).withShowDrawerOnFirstLaunch(true).build();

    }

    public void goToLogin(View view) {
        if (UserInfo.UserEntry.IS_LOGIN) {
            UserInfo.UserEntry.USER_ID = null;
            UserInfo.UserEntry.USER_NAME = null;
            UserInfo.UserEntry.USER_PWD = null;
            UserInfo.UserEntry.IS_LOGIN = false;

            SharedPreferences sp = getSharedPreferences("autologin", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("id", UserInfo.UserEntry.USER_ID);
            editor.putString("name", UserInfo.UserEntry.USER_NAME);
            editor.putString("pwd", UserInfo.UserEntry.USER_PWD);
            editor.putBoolean("islogin",UserInfo.UserEntry.IS_LOGIN);
            editor.commit();

            Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
            finish();
        }
    }
    public void signin(View view){
        Intent intent = new Intent(this,SignInActivity.class);
        startActivity(intent);
        finish();
    }

    public void changeUserInfo(View view) {
        Intent intent = new Intent(this, ChangeInfoActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
