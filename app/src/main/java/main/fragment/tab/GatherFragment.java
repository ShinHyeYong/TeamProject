package main.fragment.tab;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.iconics.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import sub.listpage.ChatRoom;
import sub.listpage.WritePage;
import utils.Element;
import utils.adapter.ListViewAdapter;
import psj.hahaha.R;
import utils.dbconnected.LogInActivity;
import utils.model.UserInfo;

public class GatherFragment extends Fragment implements View.OnClickListener {

    private ListView listView;
    ArrayList<Element> elements;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot(); //FCM DataBase root를 불러옴.
    private String timeStamp;

    public GatherFragment() {
        // Required empty public constructor
    }

    public static GatherFragment newInstance() {
        GatherFragment fragment = new GatherFragment();
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
        listView = (ListView) view.findViewById(R.id.glistView);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.gfab);
        fab.setOnClickListener(this);
//        arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,list_of_rooms);
        arrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.my_text_view,list_of_rooms);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(UserInfo.UserEntry.IS_LOGIN == true) {
                    Intent intent = new Intent(getActivity(), ChatRoom.class);
                    intent.putExtra("room_name", ((TextView) view).getText().toString());
                    intent.putExtra("user_name", UserInfo.UserEntry.USER_NAME);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(), LogInActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }

            }
        });


        //FCM value Listener
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();        //Temp값 만듬.
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }
                list_of_rooms.clear();  // clear current room
                list_of_rooms.addAll(set);  //add temp list
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }


    @Override
    public void onClick(View view) {
        if(UserInfo.UserEntry.IS_LOGIN == true) {

            Dialog dialog = new Dialog(getActivity());
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());

            builder.setTitle("채팅방을 만듭니다.");
            final EditText input = new EditText(getActivity());
            builder.setView(input);

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    Map<String,Object> map = new HashMap<String, Object>();
                    map.put("채팅방 :  " + input.getText().toString(),"");
                    root.updateChildren(map);
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            builder.show();


        }else{
            Intent intent = new Intent(getActivity(), LogInActivity.class);
            startActivity(intent);
            getActivity().finish();
        }


    }
}
