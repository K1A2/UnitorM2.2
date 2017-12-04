package unitor.uni.k1a2.unitor2.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import unitor.uni.k1a2.unitor2.R;

/**
 * Created by jckim on 2017-12-03.
 */

public class TabHostActivity extends AppCompatActivity {

    private long backKeyPress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tabhost);
    }

    @Override
    public void onBackPressed() {
        Toast toast = new Toast(TabHostActivity.this);
        if (System.currentTimeMillis() - backKeyPress < 2000) {
            startActivity(new Intent(TabHostActivity.this, MainActivity.class));
            finish();
        } else {
            toast.makeText(TabHostActivity.this, getString(R.string.toast_back), Toast.LENGTH_SHORT).show();
            backKeyPress = System.currentTimeMillis();
        }
    }
}
