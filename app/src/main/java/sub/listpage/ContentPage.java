package sub.listpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import psj.hahaha.R;

/**
 * Created by user on 2016-11-11.
 */

public class ContentPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);
        Intent intent = getIntent();

        String titleValue = intent.getStringExtra("title");
        String mainValue = intent.getStringExtra("main");

        TextView titleTv = (TextView) findViewById(R.id.titleTV);
        TextView mainTv = (TextView) findViewById(R.id.mainTV);

        titleTv.setText(titleValue);
        mainTv.setText(mainValue);

    }
}
