package main.fragment.drawer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import psj.hahaha.R;

public class QnaFragment extends Fragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this main.fragment
        return inflater.inflate(R.layout.fragment_qna, container, false);
    }

}