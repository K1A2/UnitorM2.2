package unitor.uni.k1a2.unitor2.activitys;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
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
    private boolean isKeppOn = false;
    private boolean isUnload = false;
    private boolean isKilledSelf = false;
    private ArrayList<Object[]> array_sounds = new ArrayList<Object[]>();
    private SoundPool soundPool;
    private MenuItem menu_save;
    private ActionBar actionBar;
    private PowerManager.WakeLock wakeLock = null;

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

        //ActionBar
        actionBar = getSupportActionBar();
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

        ActionBar.Tab keyLEDTab = actionBar.newTab();
        keyLEDTab.setText("KeyLED");
        TabsListener<KeyLEDFragment> c = new TabsListener<KeyLEDFragment>(this, "KeyLED", KeyLEDFragment.class);
        keyLEDTab.setTabListener(c);
        actionBar.addTab(keyLEDTab);

        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tabId"));
        }

        if (!getIntent().getBooleanExtra("KILL", false)) {
            new File(fileIO.getDefaultPath() + "unipackProject/work/keySound.txt").delete();
        }
    }

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tabId", getSupportActionBar().getSelectedNavigationIndex());
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
                if (actionBar != null) {
                    int index = actionBar.getSelectedNavigationIndex();
                    switch (index) {
                        case 0:
                            InfoFragment infoFragment = (InfoFragment)getSupportFragmentManager().findFragmentByTag("Info");
                            infoFragment.saveInfo();
                            break;

                        case 1:
                            KeySoundFragment keySoundFragment = (KeySoundFragment)getSupportFragmentManager().findFragmentByTag("KeySound");
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
        new CopyFile(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, FileKey.KEY_COPY_SOUND, files, this, (KeySoundFragment) getSupportFragmentManager().findFragmentByTag("KeySound"));
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
            new LoadSound().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sound_list);
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
            if (menu_save != null) {
                switch (tab.getPosition()) {
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
