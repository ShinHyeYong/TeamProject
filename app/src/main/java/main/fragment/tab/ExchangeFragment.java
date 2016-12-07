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

public class ExchangeFragment extends Fragment implements View.OnClickListener{
    String URL="http://210.91.76.33:8080/context/getexchangelist.php";
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
        elements = new ArrayList<>();
        GetEListThread thread = new GetEListThread();
        Constants.tpexecutor.execute(thread);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this main.fragment
        View view = inflater.inflate(R.layout.fragment_exchange, container, false);

        // 리스트뷰에 대한 세팅?선언
        listView = (ListView) view.findViewById(R.id.elistView);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.efab);
        fab.setOnClickListener(this);

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
                Intent i = new Intent(getActivity(),ContentPage.class);
                i.putExtra("fragmentType","exchange");
                i.putExtra("contentnum",String.valueOf(elements.size()-position-1));
                startActivity(i);
                getActivity().finish();
            }

        });
    }

    @Override
    public void onClick(View v) {
        if(UserInfo.UserEntry.IS_LOGIN == true) {
            Intent intent = new Intent(getActivity(), WritePage.class);
            intent.putExtra("fragmentType","exchange");
            startActivity(intent);
            getActivity().finish();
        }else{
            Intent intent = new Intent(getActivity(), LogInActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }


    class GetEListThread extends Thread{
        @Override
        public void run(){
            try {
                java.net.URL url = new URL(URL);

                URLConnection conn = url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                String result =  sb.toString();
                if (!result.equalsIgnoreCase("failure")) {
                    try {
                        JSONObject root = new JSONObject(result);

                        JSONArray ja = root.getJSONArray("result");
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

