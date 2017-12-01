package unitor.uni.k1a2.unitor2.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ListView;

import unitor.uni.k1a2.unitor2.R;
import unitor.uni.k1a2.unitor2.adapters.list.UnipackListAdapter;

/**
 * Created by jckim on 2017-11-29.
 */

public class MainActivity extends AppCompatActivity {

    private ListView list_unipack;
    private UnipackListAdapter unipackListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        list_unipack = (ListView)findViewById(R.id.List_Unipack);//유니팩 리스트

        getSupportActionBar().hide();

        unipackListAdapter = new UnipackListAdapter();
        unipackListAdapter.addItem("Title", "Producer", "Path", "Chain", "Land");

        list_unipack.setAdapter(unipackListAdapter);
    }
}
