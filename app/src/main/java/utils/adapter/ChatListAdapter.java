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

    // 리스트뷰의 크기, 크기가 0이면 한개도 안보이고 사이즈보다 작아지면 다 보여줄수없음
    @Override
    public int getCount() {
        return arrayList.size();
    }

    // 리스트뷰의 해당되는 오브젝트(아이템) 이게 널이면 아이템이 하나도안찍힘
    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    // 현재 보여지는 페이지의 포지션?(인덱스라고 이해하면 편할듯) 예를 들면 한페이지에 10개가 보인다 -> 그럼 0부터 9까지의 포지션을 가지고있다
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 리스트뷰의 뷰를 담당하는 부분 현재 약간 어려울수도 있지만 뷰홀더를 이용하여 재사용성을 높임( 문제점이 나중에 발생함 무조건발생함)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final int pos = position;

        // 캐시된 뷰가 없을 경우 새로 생성하고 뷰홀더를 생성
        if (convertView == null) {
            holder = new ViewHolder();

            // Fragment의 주소값을 통해서 레이아웃을 구상한다.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 그 레이아웃은 현재 아래에 적힌 list_gather_item 레이아웃에 맞게 작성한다.
            convertView = inflater.inflate(R.layout.list_gather_item, parent, false);

            // 그 레이아웃에서 요소들(id)에 해당되는 텍스트뷰, 이미지뷰 등을 뷰홀더의 변수로 넣어준다.
            holder.name_text = (TextView) convertView.findViewById(R.id.name);

            // 태그를 이용하여 현재 사용한 뷰홀더를 넣어줌
            convertView.setTag(holder);

            // 캐시된 뷰가 있을 경우 저장된 뷰홀더를 사용
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 생성자에서 받아온 어레이리스트의 포지션에 맞게끔 값을 배분해준다
        holder.name_text.setText(arrayList.get(pos));
        return convertView;
    }

    // 뷰홀더를 이용해서 리스트뷰를 재사용
    private class ViewHolder {
        TextView name_text;
        TextView chat_text;
    }
}
