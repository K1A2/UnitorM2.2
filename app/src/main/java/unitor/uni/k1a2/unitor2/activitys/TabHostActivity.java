package unitor.uni.k1a2.unitor2.activitys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Random;

import unitor.uni.k1a2.unitor2.File.AsyncTask.CopyFile;
import unitor.uni.k1a2.unitor2.File.FileIO;
import unitor.uni.k1a2.unitor2.File.FileKey;
import unitor.uni.k1a2.unitor2.File.SharedPreference.PreferenceKey;
import unitor.uni.k1a2.unitor2.File.SharedPreference.SharedPreferenceIO;
import unitor.uni.k1a2.unitor2.R;
import unitor.uni.k1a2.unitor2.views.Dialogs.MultiDialog;

/**
 * Created by jckim on 2017-12-03.
 */

public class TabHostActivity extends AppCompatActivity implements MultiDialog.OnFileSelectListener, KeySoundFragment.SoundPlay, KeySoundFragment.FileChange {

    private String path;
    private String title;
    private FileIO fileIO;
    private SharedPreferenceIO sharedPreferenceIO = null;
    private long backKeyPress;
    private SharedPreferenceIO sharedPKill;
    private ArrayList<Object[]> array_sounds = new ArrayList<Object[]>();
    private SoundPool soundPool;
    private MenuItem menu_save;
    private GestureDetector gestureDetector;
    private PowerManager.WakeLock wakeLock = null;

    private Toolbar toolbar;
    private LinearLayout linear_Toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private boolean isKeppOn = false;
    private boolean isUnload = false;
    private boolean isKilledSelf = false;
    private boolean isShowing = true;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tabhost);

        fileIO = new FileIO(this);

        sharedPKill = new SharedPreferenceIO(this, PreferenceKey.KEY_REPOSITORY_KILL);
        sharedPreferenceIO = new SharedPreferenceIO(this, PreferenceKey.KEY_REPOSITORY_INFO);
        path = sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "");
        title = sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_TITLE, "");

        if (!getIntent().getBooleanExtra("KILL", false)) {
            new File(fileIO.getDefaultPath() + "unipackProject/work/keySound.txt").delete();
        }

        if (path.equals("")) {
            if (title.equals("")) {
                //타이틀 없으면 랜덤으로 조합
                Random random = new Random();
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0;i < 7;i++) {
                    if (random.nextBoolean()) {
                        stringBuffer.append((char)((int)(random.nextInt(26))+97));
                    } else {
                        stringBuffer.append((random.nextInt(10)));
                    }
                }
                title = stringBuffer.toString();
                sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_TITLE, title);
            }
            path = fileIO.getDefaultPath() + title + "/";
            sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_PATH, path);
        }

        soundLoad(path + "sounds/");

        //탭설정
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.container);
        linear_Toolbar = (LinearLayout) findViewById(R.id.Layout_Toolbar);

        //Info Tab
        TabLayout.Tab infoTab = tabLayout.newTab();
        infoTab.setText("Info");
        tabLayout.addTab(infoTab);

        TabLayout.Tab keySoundTab = tabLayout.newTab();
        keySoundTab.setText("KeySound");
        tabLayout.addTab(keySoundTab);

        TabLayout.Tab keyLEDTab = tabLayout.newTab();
        keyLEDTab.setText("KeyLED");
        tabLayout.addTab(keyLEDTab);

        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (menu_save != null) {
                    switch (position) {
                        case 0:
                            menu_save.setTitle(getString(R.string.save_info));
                            break;

                        case 1:
                            menu_save.setTitle(getString(R.string.save_keySound));
                            break;

                        case 2:
                            menu_save.setTitle(getString(R.string.save_KeyLED));
                            break;
                    }
                }
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //제스쳐 인식 툴바 보이기/숨기기 TODO:제스쳐
        gestureDetector = new GestureDetector(onGestureListener);

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //gestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });
    }

    private int getSlope (float x1, float y1, float x2, float y2) {
        Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
        if (angle > 45 && angle <= 135)
            // top
            return 1;
        if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
            // left
            return 2;
        if (angle < -45 && angle>= -135)
            // down
            return 3;
        if (angle > -45 && angle <= 45)
            // right
            return 4;
        return 0;
    }

    //TODO: 제스쳐
    GestureDetector.OnGestureListener onGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
//                try {
//                    if (Math.abs(motionEvent.getY() - motionEvent1.getY()) > SWIPE_MAX_OFF_PATH)
//                        return false;
//                    // down to up swipe
//                    if (motionEvent.getY() - motionEvent1.getY() > SWIPE_MIN_DISTANCE && Math.abs(v1) > SWIPE_THRESHOLD_VELOCITY) {
//                        Toast.makeText(TabHostActivity.this, "Swipe up", Toast.LENGTH_SHORT).show();
//                        if (isShowing) {
//                            linear_Toolbar.setVisibility(View.GONE);
//                            isShowing = false;
//                        }
//                    }
//                    // up to down swipe
//                    else if (motionEvent1.getY() - motionEvent.getY() > SWIPE_MIN_DISTANCE && Math.abs(v1) > SWIPE_THRESHOLD_VELOCITY) {
//                        Toast.makeText(TabHostActivity.this, "Swipe down", Toast.LENGTH_SHORT).show();
//                        if (!isShowing) {
//                            linear_Toolbar.setVisibility(View.VISIBLE);
//                            isShowing = true;
//                        }
//                    }
//                } catch (Exception e) {
//
//                }
            switch (getSlope(motionEvent.getX(), motionEvent.getY(), motionEvent1.getX(), motionEvent1.getY())) {
                case 1:
                    Log.d("Swipe", "top");
                    Toast.makeText(TabHostActivity.this, "Swipe up", Toast.LENGTH_SHORT).show();
                    if (isShowing) {
                        linear_Toolbar.setVisibility(View.GONE);
                        isShowing = false;
                    }
                    return true;
                case 2:
                    Log.d("Swipe", "left");
                    return true;
                case 3:
                    Log.d("Swipe", "down");
                    Toast.makeText(TabHostActivity.this, "Swipe down", Toast.LENGTH_SHORT).show();
                    if (!isShowing) {
                        linear_Toolbar.setVisibility(View.VISIBLE);
                        isShowing = true;
                    }
                    return true;
                case 4:
                    Log.d("Swipe", "right");
                    return true;
            }
            return true;
        }


    };

    @Override
    public void onBackPressed() {
        Toast toast = new Toast(TabHostActivity.this);
        if (System.currentTimeMillis() - backKeyPress < 2000) {
            soundUnLoad();
            isKilledSelf = true;//스스로 종료시
            sharedPKill.setBoolean(PreferenceKey.KEY_KILL_DIED, PreferenceKey.KEY_KILL_SELF);
            startActivity(new Intent(TabHostActivity.this, MainActivity.class));
            finish();
        } else {
            toast.makeText(TabHostActivity.this, getString(R.string.toast_back), Toast.LENGTH_SHORT).show();
            backKeyPress = System.currentTimeMillis();
        }
    }

    //메뉴생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actionbar, menu);
        menu_save = menu.findItem(R.id.menu_save);
        return super.onCreateOptionsMenu(menu);
    }
    //메뉴리스너
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                if (tabLayout != null) {
                    int index = tabLayout.getSelectedTabPosition();
                    switch (index) {
                        case 0:
                            InfoFragment infoFragment = (InfoFragment)getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + index);
                            infoFragment.saveInfo();
                            break;

                        case 1:
                            KeySoundFragment keySoundFragment = (KeySoundFragment)getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + index);
                            keySoundFragment.saveKeySound();
                            break;

                        case 2:
                            break;
                    }
                }
                return true;

            case R.id.menu_saveall:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
        isKeppOn = sharedPKill.getBoolean("setting_screen", false);
        if (isKeppOn) {
            PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "ScreenWake");
            wakeLock.acquire();
        }
    }

    //사운드파일
    @Override
    public void onFileSelect(ArrayList<String[]> files) {
        new CopyFile(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, FileKey.KEY_COPY_SOUND, files, this, (KeySoundFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + 1));
    }

    //파일 로딩
    public void soundLoad(String file) {
        soundUnLoad();
        try {
            File[] sound_list = new File(file).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isFile()&&(file.getName().endsWith(".wav")||file.getName().endsWith(".mp3"));
                }
            });
            if (sound_list == null) {
                soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            } else {
                soundPool = new SoundPool(sound_list.length, AudioManager.STREAM_MUSIC, 0);
            }
            if (sound_list != null) {
                new LoadSound().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sound_list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fileIO.showErr(e.getMessage());
        }
    }

    //사운드 언로딘
    public void soundUnLoad() {
        if (soundPool != null) {
            isUnload = true;
            for (Object[] o:array_sounds) {
                soundPool.unload((int) o[1]);
            }
            soundPool.release();
            array_sounds = new ArrayList<Object[]>();
            isUnload = false;
        }
    }

    @Override
    public void onPlay(String title) {
        for (Object[] o:array_sounds) {
            if (o[0].toString().equalsIgnoreCase(title)) {
                soundPool.play((int) o[1], 1, 1, 0, 0, 1f);
            }
        }
    }

    @Override
    public void onFileChange() {
        soundLoad(path + "sounds/");
    }

    private class TabPagerAdapter extends FragmentPagerAdapter {

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    InfoFragment infoFragment = new InfoFragment();
                    return infoFragment;

                case 1:
                    KeySoundFragment keySoundFragment = new KeySoundFragment();
                    return keySoundFragment;

                case 2:
                    KeyLEDFragment keyLEDFragment = new KeyLEDFragment();
                    return keyLEDFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    //사운드 로딩
    private class LoadSound extends AsyncTask<File, Object, String> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(TabHostActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(getString(R.string.async_load_sound_title));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(File... files) {
            while (isUnload) {
                synchronized (this) {
                    try {
                        wait(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            progressDialog.setMax(files.length);

            for (int i = 0;i < files.length;i++) {
                int load = soundPool.load(files[i].getAbsolutePath(), 0);
                String name = files[i].getName();
                array_sounds.add(new Object[] {name, load});
                publishProgress(i, files[i].getAbsolutePath());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            progressDialog.setProgress((int) values[0]);
            progressDialog.setMessage((String) values[1]);
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
        }
    }
}
