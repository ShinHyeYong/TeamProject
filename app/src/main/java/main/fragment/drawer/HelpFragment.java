package main.fragment.drawer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import psj.hahaha.R;

public class HelpFragment extends Fragment {

    public HelpFragment() {
        // Required empty public constructor
    }

    public static HelpFragment newInstance() {
        HelpFragment fragment = new HelpFragment();
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
        
        View fragment2View = inflater.inflate(R.layout.fragment_help, container, false);
        
        LinearLayout layout = (LinearLayout) fragment2View.findViewById(R.id.fragment_help);
        VideoView videoView = new VideoView(getActivity());
        videoView.setVideoPath("http://180.229.55.20:30334/helpvid.mp4");
        videoView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                               ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(videoView);
        
        videoView.start();
        
        return fragment2View;

    }

}
