package sub.listpage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import psj.hahaha.R;
import utils.adapter.ChatLogAdapter;

/**
 * Created by user on 2016-12-06.
 */
public class ChattingActivity extends AppCompatActivity {

    private Button btn_send_msg;
    private EditText input_msg;

    private String user_name, room_name;
    private DatabaseReference root;
    private String temp_key;
    String time_stamp;

    private ArrayList<Chatting> arrayList;
    private ChatLogAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);
        btn_send_msg = (Button) findViewById(R.id.btn_send);
        input_msg = (EditText) findViewById(R.id.msg_input);

        listView = (ListView) findViewById(R.id.chatting);

        user_name = getIntent().getExtras().get("user_name").toString();
        room_name = getIntent().getExtras().get("room_name").toString();

        // arrayList 생성
        arrayList = new ArrayList<>();

        // 채팅방 이름 받아옴
        setTitle(room_name);

        root = FirebaseDatabase.getInstance().getReference().child(room_name);

        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> map = new HashMap<String, Object>();
                //채팅방 고유키 설정
                temp_key = root.push().getKey();
                root.updateChildren(map);
                time_stamp = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());

                //고유키를 바탕으로 데이터베이스 설정
                DatabaseReference message_root = root.child(temp_key);
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("name", user_name);
                map2.put("msg", input_msg.getText().toString());
                map2.put("time", time_stamp);

                input_msg.setText("");
                message_root.updateChildren(map2);
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_id(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_id(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        setListener();
    }

    // adapter를 가져와 arrayList에 뿌려줌
    private void setListener() {
        adapter = new ChatLogAdapter(this, arrayList);

        listView.setAdapter(adapter);
    }

    private void append_chat_id(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();
        // 상대방이면 채팅을 왼쪽에
        Chatting info = new Chatting(null, null, null, R.layout.chat_leftrow);

        // x 값으로 msg, id, time 확인
        int x = 0;
        while (i.hasNext()) {
            String serverToText = (String) ((DataSnapshot) i.next()).getValue();
            switch (x) {
                case 0:
                    info.setComment(serverToText);
                    break;
                // id가 사용자와 일치하면 채팅을 오른쪽에
                case 1:
                    info.setId(serverToText);
                    if (user_name.equals(serverToText)) {
                        info.setRes(R.layout.chat_rightrow);
                    }
                    break;
                case 2:
                    info.setTime(serverToText);
                    break;
            }
            // 리스트 증가
            x++;
        }
        arrayList.add(info);
    }

}