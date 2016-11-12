package intro;

import android.content.Intent;
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

import dbconnected.ChangeInfoActivity;
import dbconnected.LogInActivity;
import psj.hahaha.R;
import utils.model.UserInfo;
import utils.Constants;

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
    }

    protected void setNavigationDrawer(Bundle args) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowHomeEnabled(false);

        View layout = getLayoutInflater().inflate(R.layout.fragment_navigation_drawer, null);

        Button login_btn = (Button) layout.findViewById(R.id.login_btn);
        Button change_btn = (Button) layout.findViewById(R.id.userinfo_btn);
        if(UserInfo.UserEntry.IS_LOGIN==true){
            login_btn.setText("로그아웃");
            change_btn.setVisibility(View.VISIBLE);
        }else if(UserInfo.UserEntry.IS_LOGIN==false){
            login_btn.setText("로그인");
            change_btn.setVisibility(View.GONE);
        }

        headerLayout = layout;

        drawer = new DrawerBuilder().withActivity(this)
                .withTranslucentNavigationBar(false)
                .withToolbar(toolbar)
                .withHeader(layout)
                .addDrawerItems(
                        new SecondaryDrawerItem().withName("공지사항").withIcon(R.drawable.ic_action_profile).withIdentifier(Constants.DRAWER_1_PAGE),
                        new SecondaryDrawerItem().withName("설정").withIcon(R.drawable.ic_action_profile).withIdentifier(Constants.DRAWER_2_PAGE),
                        new SecondaryDrawerItem().withName("도움말").withIcon(R.drawable.ic_action_profile).withIdentifier(Constants.DRAWER_3_PAGE)
                ).withOnDrawerItemClickListener(this).withSelectedItem(-1)
                .withSavedInstance(args).withShowDrawerOnFirstLaunch(true).build();
    }

    public void goToLogin(View view){
        if(UserInfo.UserEntry.IS_LOGIN==true){
            UserInfo.UserEntry.USER_ID = null;
            UserInfo.UserEntry.USER_NAME = null;
            UserInfo.UserEntry.USER_PWD = null;
            UserInfo.UserEntry.IS_LOGIN = false;
            Toast.makeText(this,"로그아웃 되었습니다.",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }else if(UserInfo.UserEntry.IS_LOGIN==false){
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void changeUserInfo(View view){
        Intent intent = new Intent(this, ChangeInfoActivity.class);
        startActivity(intent);
        finish();
    }
}
