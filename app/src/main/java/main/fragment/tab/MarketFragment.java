package main.fragment.tab;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import psj.hahaha.R;
import sub.listpage.ContentPage;
import sub.listpage.WritePage;
import utils.Constants;
import utils.Element;
import utils.adapter.ListViewAdapter;
import utils.dbconnected.LogInActivity;
import utils.model.UserInfo;

public class MarketFragment extends Fragment implements View.OnClickListener{
    String URL="http://210.91.76.33:8080/context/getmarketlist.php";
    private ListView listView;
    ArrayList<Element> elements;

    public MarketFragment() {}

    //프래그먼트 인스턴스 생성
    public static MarketFragment newInstance() {
        MarketFragment fragment = new MarketFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        elements = new ArrayList<>();//element 객체 생성
        GetMListThread thread = new GetMListThread(); //리스트 불러오는 쓰레드 생성
        Constants.tpexecutor.execute(thread); //쓰레드 풀로 쓰레드 실행
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //프래그먼트의 레이아웃
        View view = inflater.inflate(R.layout.fragment_market, container, false);

        listView = (ListView) view.findViewById(R.id.mlistView);
        //글 작성 버튼에 리스너 세팅
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.mfab);
        fab.setOnClickListener(this);

        setListView();

        return view;
    }

    private void setListView(){
        // 리스트 뷰 세팅( 커스텀 리스트뷰어댑터 ), elemets에 있는 값을 리스트에 세팅
        ListViewAdapter adapter = new ListViewAdapter(getActivity(), elements);
        listView.setAdapter(adapter);
        //리스트 아이템 클릭 시 게시글을 확인하는 contentpage로 이동하는 리스너 세팅
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Intent i = new Intent(getActivity(),ContentPage.class);
                i.putExtra("fragmentType","market");
                i.putExtra("contentnum",String.valueOf(elements.size()-position-1));
                startActivity(i);
                getActivity().finish();
            }

        });
    }

    //글 작성 버튼의 onclick 함수
    @Override
    public void onClick(View v) {
        if(UserInfo.UserEntry.IS_LOGIN == true) { //로그인 상태면 글 작성
            Intent intent = new Intent(getActivity(), WritePage.class);
            intent.putExtra("fragmentType","market");
            startActivity(intent);
            getActivity().finish();
        }else{ //비로그인 상태면 로그인 액티비티로 이동
            Intent intent = new Intent(getActivity(), LogInActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    //게시글 리스트 서버에서 받아오는 쓰레드
    class GetMListThread extends Thread{
        @Override
        public void run(){
            try {
                java.net.URL url = new URL(URL);

                URLConnection conn = url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                //php서버에서 echo하는 데이터를 받음 (php서버에서 json형태로 parsing)
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                String result = sb.toString();
                if (!result.equalsIgnoreCase("failure")) {
                    try {
                        JSONObject root = new JSONObject(result);
                        JSONArray ja = root.getJSONArray("result");

                        //서버에서 받아온 결과를 ArrayList<Element>에 저장
                        if(ja.length()!=0) {
                            for(int i=ja.length()-1;i>=0;i--){
                                elements.add(new Element(ja.getJSONObject(i).getString("title"), ja.getJSONObject(i).getString("time")));
                            }
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
               e.printStackTrace();
            }

        }
    }
}