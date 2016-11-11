package utils.adapter;

/**
 * Created by PSJ on 2016. 11. 1..
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import utils.Element;
import psj.hahaha.R;

public class ListViewAdapter extends BaseAdapter { // BaseAdapter가 커스텀 어댑터가 아니라, BaseAdapter를 응용하여 커스텀화 시킨것

    private Context context;
    private ArrayList<Element> elements;

    // 생성자를 통해서 Context와 어레이리스트를 받아온다.
    // Context는 해당되는 페이지의 뷰의 주소를 갖는다.(현재는 Page1Fragment의 주소를 가짐)
    public ListViewAdapter(Context context, ArrayList<Element> elements) {
        this.context = context;
        this.elements = elements;
    }

    // 리스트뷰의 크기, 크기가 0이면 한개도 안보이고 사이즈보다 작아지면 다 보여줄수없음
    @Override
    public int getCount() {
        return elements.size();
    }


    // 리스트뷰의 해당되는 오브젝트(아이템) 이게 널이면 아이템이 하나도안찍힘
    @Override
    public Object getItem(int position) {
        return elements.get(position);
    }

    // 현재 보여지는 페이지의 포지션?(인덱스라고 이해하면 편할듯) 예를 들면 한페이지에 10개가 보인다 -> 그럼 0부터 9까지의 포지션을 가지고있다
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 리스트뷰의 뷰를 담당하는 부분 현재 약간 어려울수도 있지만 뷰홀더를 이용하여 재사용성을 높임( 문제점이 나중에 발생함 무조건발생함)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        // 현재 뷰에 대한 세팅이 없으면 널이 나오게된다.
        if (convertView == null) {
            viewHolder = new ViewHolder();

            // Page1Fragment의 주소값을 통해서 레이아웃을 구상한다.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 근데 그 레이아웃은 현재 아래에 적힌 list_item의 레이아웃에 맞게 작성한다. null은 자세히몰라양
            convertView = inflater.inflate(R.layout.list_item, null);

            // 그 레이아웃에서 요소들(id)에 해당되는 텍스트뷰, 이미지뷰 등을 뷰홀더의 변수로 넣어준다.
            viewHolder.title = (TextView) convertView.findViewById(R.id.list_title);
            viewHolder.timeStamp = (TextView) convertView.findViewById(R.id.list_timestamp);

            // 태그를 이용하여 현재 사용한 뷰홀더를 넣어준다.
            convertView.setTag(viewHolder);
        } else {
            // 이미 세팅이 되어있으므로 사용했던 뷰를 가져온다.
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 생성자에서 받아온 어레이리스트의 포지션에 맞게끔 값을 배분해준다` 끝
        viewHolder.title.setText(elements.get(position).getTitle());
        viewHolder.timeStamp.setText(elements.get(position).getTimeStamp());

        return convertView;
    }

    // 뷰홀더를 이용해서 리스트뷰를 재사용
    class ViewHolder {
        TextView title;
        TextView timeStamp;
    }
}
