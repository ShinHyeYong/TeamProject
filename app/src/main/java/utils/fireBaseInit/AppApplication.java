package utils.fireBaseInit;

import android.app.Application;

import com.google.firebase.FirebaseApp;

/**
 * Created by user on 2016-12-06.
 */

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Firebase 초기화
        FirebaseApp.initializeApp(this);
    }
}

