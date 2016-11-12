package intro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import fragment.tab.GatherFragment;
import fragment.tab.ExchangeFragment;
import fragment.tab.MarketFragment;
import fragment.tab.DonateFragment;
import psj.hahaha.R;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private SmartTabLayout smartTabLayout;
//    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        mToolbar=(Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
*/
        this.setNavigationDrawer(savedInstanceState);
        setFragment();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        // 현재 탭의 레이아웃 전체를 읽어옴
        final LinearLayout linearLayout = (LinearLayout) smartTabLayout.getChildAt(0);

        // 프래그먼트 형태로 선택된 포지션의 프래그먼트를 불러온다. 만약 널이면 재실행
        Fragment fragment = ((FragmentPagerAdapter) viewPager.getAdapter()).getItem(position);
        if (fragment != null) onResume();

        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            TextView tabTextView = (TextView) linearLayout.getChildAt(i);
            // 현재 선택된 부분의 글씨색
            if (i == position) {
                tabTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                tabTextView.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    // 페이지 세팅해 주는 부분
    private void setFragment() {

        // 각 페이지에 대한 프레그먼트(아이템 세팅 해 주는 부분)
        FragmentPagerAdapter viewAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("모여라", GatherFragment.class)
                .add("교환", ExchangeFragment.class)
                .add("마켓", MarketFragment.class)
                .add("보물찾기", DonateFragment.class)
                .create()
        );

        // 뷰 페이저에 대한 세팅 부분
        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        // 현재 임플리먼트로 온페이지체인지리스너를 가져오기 때문에, this로 처리가능
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(viewAdapter);

        smartTabLayout = (SmartTabLayout) findViewById(R.id.main_viewpagertab);
        smartTabLayout.setDistributeEvenly(true);
        smartTabLayout.setViewPager(viewPager);

        final LinearLayout linearLayout = (LinearLayout) smartTabLayout.getChildAt(0);
        TextView tab_title = (TextView) linearLayout.getChildAt(0);
        tab_title.setTextColor(getResources().getColor(R.color.colorPrimary));
    }
}