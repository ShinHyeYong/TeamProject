package main.fragment.tab;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import sub.listpage.WritePage;
import utils.Element;
import utils.adapter.ListViewAdapter;
import psj.hahaha.R;
import utils.dbconnected.LogInActivity;
import utils.model.UserInfo;

public class MarketFragment extends Fragment implements View.OnClickListener{

    private ListView listView;
    ArrayList<Element> elements;

    public MarketFragment() {
        // Required empty public constructor
    }

    public static MarketFragment newInstance() {
        MarketFragment fragment = new MarketFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this main.fragment
        View view = inflater.inflate(R.layout.fragment_market, container, false);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        // 리스트뷰에 대한 세팅?선언
        listView = (ListView) view.findViewById(R.id.listView);

        // 예시로 요소가 두개인 경우로 할게용
        setArrayList();

        setListView();
        return view;
    }

    private void setListView(){
        // 리스트 뷰 세팅( 커스텀 리스트뷰어댑터 )
        ListViewAdapter adapter = new ListViewAdapter(getActivity(), elements);

        listView.setAdapter(adapter);
    }

    private void setArrayList() {
        // 어레이 리스트 초기화
        elements = new ArrayList<>();

        // 해당되는 어레이리스트에 요소 추가해주기
        elements.add(new Element("ㅁㅈㄷㄹ", "1995-07-19"));
        elements.add(new Element("ㄹㄷㄴㅁ", "1995-07-19"));
        elements.add(new Element("ㄹㄷㄴㅇ", "1995-07-19"));
        elements.add(new Element("ㅁㅈㄷㄹ", "1995-07-19"));
        elements.add(new Element("ㅁㅈㄷㄹ", "1995-07-19"));
        elements.add(new Element("ㄹㄷㄴㅁ", "1995-07-19"));
        elements.add(new Element("ㄹㄷㄴㅇ", "1995-07-19"));
        elements.add(new Element("ㅁㅈㄷㄹ", "1995-07-19"));
    }

    @Override
    public void onClick(View v) {
        if(UserInfo.UserEntry.IS_LOGIN == true) {
            Toast.makeText(getActivity(),"Market Fragment",Toast.LENGTH_LONG).show();
        }else{

        }
    }
}