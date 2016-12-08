package utils.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import psj.hahaha.R;

/**
 * Created by HY on 2016-12-08.
 */

public class ChatListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> arrayList;

    public ChatListAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final int pos = position;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_gather_item, parent, false);

            holder.name_text = (TextView) convertView.findViewById(R.id.name);
          //  holder.chat_text = (TextView) convertView.findViewById(R.id.chat);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Log.i("Title", arrayList.get(pos));
        Log.i("Message", arrayList.get(pos));
        holder.name_text.setText(arrayList.get(pos));
//        holder.chat_text.setText("ㄴㅁㅇㄹㄴㅁㅇㄹㄴㅁㅇㄹ");

        return convertView;
    }

    private class ViewHolder {
        TextView name_text;
        TextView chat_text;
    }
}
