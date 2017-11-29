package unitor.uni.k1a2.unitor2.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import unitor.uni.k1a2.unitor2.R;

/**
 * Created by jckim on 2017-11-29.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();
    }
}
