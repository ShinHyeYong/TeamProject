package main.fragment.drawer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import psj.hahaha.R;

public class QnaFragment extends Fragment implements View.OnClickListener {

    EditText et;

    private final String MAIL_TO = "shy954006@gmail.com";
    private final String MAIL_TITLE = "~~에 관해 문의 드립니다.";

    public QnaFragment() {
        // Required empty public constructor
    }

    public static QnaFragment newInstance() {
        QnaFragment fragment = new QnaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View fragment1View = inflater.inflate(R.layout.fragment_qna, container, false);
        button = (Button) fragment1View.findViewById(R.id.bt_help);
        et = (EditText) fragment1View.findViewById(R.id.et);

        button.setOnClickListener(this);
        return fragment1View;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_help) {
            // 메일내용
            String text = et.getText().toString();
            String mail = "mailto:" + MAIL_TO;
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(mail));
            intent.putExtra(Intent.EXTRA_SUBJECT, MAIL_TITLE);
            intent.putExtra(Intent.EXTRA_TEXT, text);

            this.startActivity(intent);
        }
    }
}
