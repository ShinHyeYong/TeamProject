package utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import psj.hahaha.R;
import sub.listpage.Chatting;

/**
 * Created by HY on 2016-12-08.
 */

public class ChatLogAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Chatting> arrayList;

    public ChatLogAdapter(Context context, ArrayList<Chatting> arrayList) {
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

        convertView = inflater.inflate(chatting.getRes(), null);

        convertView.setTag(holder);

        holder.id = (TextView) convertView.findViewById(R.id.userID);
        holder.comment = (TextView) convertView.findViewById(R.id.comment);

        holder.id.setText(chatting.getId());
        holder.comment.setText(chatting.getComment());

        return convertView;
    }
    private class ViewHolder{
        public TextView id;
        public TextView comment;
    }

}
