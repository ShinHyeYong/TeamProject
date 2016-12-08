package sub.listpage;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.Random;

import psj.hahaha.R;
import utils.adapter.ChatLogAdapter;
import utils.model.ChatInfo;

/**
 * Created by user on 2016-12-06.
 */
/*

public class ChattingActivity extends BaseAdapter {
    private Context mContext;
    private ArrayList<Chatting> arrayList;

    public ChattingActivity(Context context, ArrayList<Chatting> arrayList) {
        this.mContext = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        Chatting chatting = arrayList.get(position);

        holder = new ViewHolder();

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(chatting.getRes(), parent, false);

        convertView.setTag(holder);

        holder.id = (TextView) convertView.findViewById(R.id.userID);
        holder.comment = (TextView) convertView.findViewById(R.id.comment);

        holder.id.setText(chatting.getId());
        holder.comment.setText(chatting.getComment());

        return convertView;
    }
    public class ViewHolder{
        public TextView id;
        public TextView comment;
    }

}
*/

public class ChattingActivity extends AppCompatActivity {

    private Button btn_send_msg;
    private EditText input_msg;
    private TextView append_chat_id, append_chat_msg, append_chat_time;
    private String chat_msg, chat_user_name, chat_time_stamp;

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

        /*append_chat_id = (TextView) findViewById(R.id.chat_id);
        append_chat_msg = (TextView) findViewById(R.id.chat_msg);
        append_chat_time = (TextView) findViewById(R.id.chat_time);*/

        user_name = getIntent().getExtras().get("user_name").toString();
        room_name = getIntent().getExtras().get("room_name").toString();

        arrayList = new ArrayList<>();

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

    private void setListener() {
        adapter = new ChatLogAdapter(this, arrayList);

        listView.setAdapter(adapter);
    }

    private void append_chat_id(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();
        Chatting info = new Chatting(null, null, null, R.layout.chat_leftrow);
        int x = 0;
        while (i.hasNext()) {
//            chat_user_name = (String) ((DataSnapshot) i.next()).getValue();
            String serverToText = (String) ((DataSnapshot) i.next()).getValue();
            Log.i("Test0", "" + x + " " + serverToText);
            switch (x) {
                case 0:
                    info.setComment(serverToText);
                    break;
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
            x++;
//            append_chat_id.append(chat_user_name + "\n\n");
        }
        arrayList.add(info);
        Log.i("Test", arrayList.toString());
    }

}