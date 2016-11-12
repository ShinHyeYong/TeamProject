package main.fragment.drawer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import psj.hahaha.R;
import utils.Element;
import utils.adapter.ListViewAdapter;

public class NoticeFragment extends Fragment implements AdapterView.OnItemClickListener{

    private ListView listView;
    ArrayList<Element> elements;

    public NoticeFragment() {
        // Required empty public constructor
    }

    public static NoticeFragment newInstance() {
        NoticeFragment fragment = new NoticeFragment();
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
        View view = inflater.inflate(R.layout.fragment_gather, container, false);

        // 리스트뷰에 대한 세팅?선언
        listView = (ListView) view.findViewById(R.id.listView);

        // 예시로 요소가 두개인 경우로 할게용
        setArrayList();

        setListView();

        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), position + " 번째 글 클릭 하엿읍니다", Toast.LENGTH_SHORT).show();
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
        elements.add(new Element("공지사항1", "1995-07-19"));
        elements.add(new Element("공지사항2", "1995-07-19"));
        elements.add(new Element("공지사항3", "1995-07-19"));
        elements.add(new Element("공지사항4", "1995-07-19"));
        elements.add(new Element("공지사항5", "1995-07-19"));
        elements.add(new Element("공지사항6", "1995-07-19"));
        elements.add(new Element("공지사항7", "1995-07-19"));
        elements.add(new Element("공지사항8", "1995-07-19"));
        elements.add(new Element("공지사항9", "1995-07-19"));
        elements.add(new Element("공지사항10", "1995-07-19"));
        elements.add(new Element("공지사항11", "1995-07-19"));
    }
}
