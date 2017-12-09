package unitor.uni.k1a2.unitor2.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import unitor.uni.k1a2.unitor2.File.SharedPreference.PreferenceKey;
import unitor.uni.k1a2.unitor2.File.SharedPreference.SharedPreferenceIO;
import unitor.uni.k1a2.unitor2.R;

/**
 * Created by jckim on 2017-12-03.
 */

public class TabHostActivity extends AppCompatActivity {

    private long backKeyPress;
    private SharedPreferenceIO sharedPKill;
    private boolean isKeppOn = false;
    private boolean isKilledSelf = false;
    private PowerManager.WakeLock wakeLock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tabhost);

        //ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

        //Info Tab
        ActionBar.Tab infoTab = actionBar.newTab();
        infoTab.setText("Info");
        TabsListener<InfoFragment> a = new TabsListener<InfoFragment>(this, "Info", InfoFragment.class);
        infoTab.setTabListener(a);
        actionBar.addTab(infoTab);

        ActionBar.Tab keySoundTab = actionBar.newTab();
        keySoundTab.setText("KeySound");
        TabsListener<KeySoundFragment> b = new TabsListener<KeySoundFragment>(this, "KeySound", KeySoundFragment.class);
        keySoundTab.setTabListener(b);
        actionBar.addTab(keySoundTab);

        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tabId"));
        }
    }

    @Override
    public void onBackPressed() {
        Toast toast = new Toast(TabHostActivity.this);
        if (System.currentTimeMillis() - backKeyPress < 2000) {
            isKilledSelf = true;//스스로 종료시
            sharedPKill.setBoolean(PreferenceKey.KEY_KILL_DIED, PreferenceKey.KEY_KILL_SELF);
            startActivity(new Intent(TabHostActivity.this, MainActivity.class));
            finish();
        } else {
            toast.makeText(TabHostActivity.this, getString(R.string.toast_back), Toast.LENGTH_SHORT).show();
            backKeyPress = System.currentTimeMillis();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tabId", getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    protected void onResume() {
        isKilledSelf = false;
        if (wakeLock != null) {
            wakeLock.acquire();
        } else {
            setScreenOn();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (!isKilledSelf) {//강제종료시
            sharedPKill.setBoolean(PreferenceKey.KEY_KILL_DIED, PreferenceKey.KEY_KILL_FORCE);
        }
        if (wakeLock != null) {
            wakeLock.release();
        }
        super.onPause();
    }

    private void setScreenOn() {
        sharedPKill = new SharedPreferenceIO(this);
        isKeppOn = sharedPKill.getBoolean("setting_screen", false);
        if (isKeppOn) {
            PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "ScreenWake");
            wakeLock.acquire();
        }
    }

    //탭클릭리스너
    private class TabsListener<T extends Fragment> implements ActionBar.TabListener {

        private Fragment mfragment;
        private final Activity mactivity;
        private final String mtag;
        private final Class<T> mclass;

        public TabsListener (Activity activity, String tag, Class<T> clz) {
            mactivity = activity;
            mtag = tag;
            mclass = clz;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mfragment == null) {
                mfragment = Fragment.instantiate(mactivity, mclass.getName());
                ft.replace(android.R.id.content, mfragment, mtag);
            } else {
                ft.attach(mfragment);
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mfragment != null) {
                ft.detach(mfragment);
            }
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }
    }
}
