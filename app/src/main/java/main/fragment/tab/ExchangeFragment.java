package main.fragment.tab;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import utils.dbconnected.LogInActivity;
import sub.listpage.WritePage;
import utils.model.UserInfo;
import utils.Element;
import utils.adapter.ListViewAdapter;
import psj.hahaha.R;

public class ExchangeFragment extends Fragment implements View.OnClickListener{

    private ListView listView;
    ArrayList<Element> elements;

    public ExchangeFragment() {
        // Required empty public constructor
    }

    public static ExchangeFragment newInstance() {
        ExchangeFragment fragment = new ExchangeFragment();
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
        View view = inflater.inflate(R.layout.fragment_exchange, container, false);

        // 리스트뷰에 대한 세팅?선언
        listView = (ListView) view.findViewById(R.id.listView);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        // 예시로 요소가 두개인 경우로 할게용
        setArrayList();

        setListView();
        return view;
    }

    private void setListView(){
        // 리스트 뷰 세팅( 커스텀 리스트뷰어댑터 )
        ListViewAdapter adapter = new ListViewAdapter(getActivity(), elements);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {

                Toast.makeText(getActivity(), "Item clicked : " + position, Toast.LENGTH_SHORT).show();

            }

        });
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
            Intent intent = new Intent(getActivity(), WritePage.class);
            startActivity(intent);
            getActivity().finish();
        }else{
            Intent intent = new Intent(getActivity(), LogInActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        
    }
}

