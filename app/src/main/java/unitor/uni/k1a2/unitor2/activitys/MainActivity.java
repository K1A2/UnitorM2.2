package unitor.uni.k1a2.unitor2.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import unitor.uni.k1a2.unitor2.File.AsyncTask.DeleteFile;
import unitor.uni.k1a2.unitor2.File.AsyncTask.UnzipFile;
import unitor.uni.k1a2.unitor2.File.FileIO;
import unitor.uni.k1a2.unitor2.File.FileKey;
import unitor.uni.k1a2.unitor2.File.SharedPreference.PreferenceKey;
import unitor.uni.k1a2.unitor2.File.SharedPreference.SharedPreferenceIO;
import unitor.uni.k1a2.unitor2.R;
import unitor.uni.k1a2.unitor2.activitys.SharedPreference.SettingActivity;
import unitor.uni.k1a2.unitor2.views.Dialogs.DialogKey;
import unitor.uni.k1a2.unitor2.views.Dialogs.MultiDialog;
import unitor.uni.k1a2.unitor2.views.adapters.recyclerview.UnipackListAdapter;
import unitor.uni.k1a2.unitor2.views.adapters.recyclerview.UnipackListItem;
import unitor.uni.k1a2.unitor2.views.listener.RecyclerItemClickListener;

/**
 * Created by jckim on 2017-11-29.
 */

public class MainActivity extends AppCompatActivity implements MultiDialog.OnUnipackSelectListener {

    private RecyclerView list_unipack = null;
    private FloatingActionButton fab_new = null;
    private FloatingActionButton fab_import = null;
    private FloatingActionButton fab_setting = null;

    private FileIO fileIO = null;
    private MultiDialog multiDialog;
    private UnipackListItem unipackListItem = null;
    private UnipackListAdapter unipackListAdapter = null;
    private SharedPreferenceIO sharedPreferenceIO = null, sharedKill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        list_unipack = (RecyclerView) findViewById(R.id.List_Unipack);//유니팩 리스트
        fab_new = (FloatingActionButton)findViewById(R.id.fab_new);//새로운 유니팩
        fab_import = (FloatingActionButton)findViewById(R.id.fab_import);//유니팩 불러오기
        fab_setting = (FloatingActionButton)findViewById(R.id.fab_setting);//설정

        fileIO = new FileIO(this);

        getSupportActionBar().hide();

        list_unipack.setLayoutManager(new LinearLayoutManager(this));
        list_unipack.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, new LinearLayoutManager(this).getOrientation());
        list_unipack.addItemDecoration(dividerItemDecoration);

        unipackListAdapter = new UnipackListAdapter(R.layout.view_list_unipack);
        showUnipacks();

        list_unipack.addOnItemTouchListener(new RecyclerItemClickListener(this, list_unipack, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                unipackListItem = (UnipackListItem)unipackListAdapter.getItem(position);
                final String string_title = unipackListItem.getTitle();
                final String string_producer = unipackListItem.getProducer();
                final String string_chain = unipackListItem.getChain();
                final String string_Path = unipackListItem.getPath();
                startEdit(string_title, string_producer, string_chain, string_Path);
            }

            @Override
            public void onLongItemClicked(View view, final int position) {
                unipackListItem = (UnipackListItem)unipackListAdapter.getItem(position);
                AlertDialog.Builder delete = new AlertDialog.Builder(MainActivity.this);
                delete.setTitle(String.format(getString(R.string.alert_title_dunipack), unipackListItem.getTitle()));
                delete.setMessage(String.format(getString(R.string.alert_message_dunipack), unipackListItem.getTitle()));
                delete.setNegativeButton(getString(R.string.alert_button_dcancel), null);
                delete.setPositiveButton(getString(R.string.alert_button_dok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DeleteFile deleteFile = new DeleteFile(MainActivity.this);
                        deleteFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, FileKey.KEY_DELETE_UNIPACK, unipackListItem.getPath(), String.format(getString(R.string.asynk_delete_title), unipackListItem.getTitle()), list_unipack, unipackListAdapter, position);
                    }
                });
                delete.show();
            }
        }));

//        //유니팩 리스트 클릭시
//        list_unipack.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                unipackListItem = (UnipackListItem)unipackListAdapter.getItem(i);
//                final String string_title = unipackListItem.getTitle();
//                final String string_producer = unipackListItem.getProducer();
//                final String string_chain = unipackListItem.getChain();
//                final String string_Path = unipackListItem.getPath();
//                startEdit(string_title, string_producer, string_chain, string_Path);
//            }
//        });
//
//        //유니팩 리스트 롱클릭시(삭제)
//        list_unipack.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                unipackListItem = (UnipackListItem)unipackListAdapter.getItem(i);
//                AlertDialog.Builder delete = new AlertDialog.Builder(MainActivity.this);
//                delete.setTitle(String.format(getString(R.string.alert_title_dunipack), unipackListItem.getTitle()));
//                delete.setMessage(String.format(getString(R.string.alert_message_dunipack), unipackListItem.getTitle()));
//                delete.setNegativeButton(getString(R.string.alert_button_dcancel), null);
//                delete.setPositiveButton(getString(R.string.alert_button_dok), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        DeleteFile deleteFile = new DeleteFile(MainActivity.this);
//                        deleteFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, FileKey.KEY_DELETE_UNIPACK, unipackListItem.getPath(), String.format(getString(R.string.asynk_delete_title), unipackListItem.getTitle()), list_unipack, unipackListAdapter);
//                    }
//                });
//                delete.show();
//                return true;
//            }
//        });

        View.OnClickListener OnFloatingButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.fab_new:
                        final LinearLayout layout = (LinearLayout)View.inflate(MainActivity.this, R.layout.dialog_newunpack, null);
                        AlertDialog.Builder newUnipack = new AlertDialog.Builder(MainActivity.this);
                        newUnipack.setView(layout);
                        newUnipack.setTitle(getString(R.string.dialog_title_newUnipack));
                        newUnipack.setPositiveButton(getString(R.string.dialog_Make), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final EditText edit_title = (EditText)layout.findViewById(R.id.Edit_Title_new);
                                final EditText edit_producer = (EditText)layout.findViewById(R.id.Edit_Producer_new);
                                final EditText edit_chain = (EditText)layout.findViewById(R.id.Edit_Chain_new);

                                final String string_title = edit_title.getText().toString();
                                final String string_producer = edit_producer.getText().toString();
                                final String string_chain = edit_chain.getText().toString();
                                final String string_path = fileIO.getDefaultPath() + "unipackProject/" + string_title + "/";

                                if ((string_title.length() == 0||string_title.equals(""))||(string_producer.length() == 0||string_producer.equals(""))||(string_chain.length() == 0||string_chain.equals(""))) {
                                    Toast.makeText(MainActivity.this, getString(R.string.toast_newUnipack_null), Toast.LENGTH_LONG).show();
                                } else {
                                    try {
                                        fileIO.mkNewUnipack(string_title, string_producer, string_chain, string_path);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        fileIO.showErr(e.getMessage());
                                    }
                                    startEdit(string_title, string_producer, string_chain, string_path);
                                }
                            }
                        });
                        newUnipack.setNegativeButton(getString(R.string.cancel), null);
                        newUnipack.show();
                        break;

                    case R.id.fab_import:
                        Bundle args = new Bundle();
                        args.putInt(DialogKey.KEY_BUNDLE_TYPE, DialogKey.KEY_BUNDLE_TYPE_UNIPACK);
                        multiDialog = new MultiDialog();
                        multiDialog.setArguments(args);
                        multiDialog.show(getSupportFragmentManager(), DialogKey.KEY_MAIN_FILE);
                        break;

                    case R.id.fab_setting:
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
                        finish();
                        break;
                }
            }
        };

        fab_new.setOnClickListener(OnFloatingButtonClick);
        fab_import.setOnClickListener(OnFloatingButtonClick);
        fab_setting.setOnClickListener(OnFloatingButtonClick);

        sharedKill = new SharedPreferenceIO(this, PreferenceKey.KEY_REPOSITORY_KILL);
        final boolean killed = sharedKill.getBoolean(PreferenceKey.KEY_KILL_DIED, false);
        if (killed) {
            sharedPreferenceIO = new SharedPreferenceIO(this, PreferenceKey.KEY_REPOSITORY_INFO);
            String title = sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_TITLE, "");

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(String.format(getString(R.string.alert_recover), title));
            alertDialog.setMessage(String.format(getString(R.string.alert_die_M), title));
            alertDialog.setPositiveButton(getString(R.string.alert_die_OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(MainActivity.this, TabHostActivity.class);
                    intent.putExtra("KILL", true);
                    startActivity(intent);
                    finish();
                }
            });
            alertDialog.setNegativeButton(getString(R.string.alert_die_NO), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    sharedKill.setBoolean(PreferenceKey.KEY_KILL_DIED, false);
                }
            });
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showUnipacks();
    }

    private void startEdit(String string_title, String string_producer, String string_chain, String string_path) {
        sharedPreferenceIO = new SharedPreferenceIO(MainActivity.this, PreferenceKey.KEY_REPOSITORY_INFO);
        sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_TITLE, string_title);
        sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_PRODUCER, string_producer);
        sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_CHAIN, string_chain);
        sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_PATH, string_path);
        Intent intent = new Intent(MainActivity.this, TabHostActivity.class);
        intent.putExtra("KILL", false);
        startActivity(intent);
        finish();
    }

    private void showUnipacks() {
        unipackListAdapter.clearItem();
        ArrayList<String[]> arrayUnipack = fileIO.getUnipacks();
        if (arrayUnipack != null) {
            for (String[] unipackInfo:arrayUnipack) {
                if (unipackInfo != null) {
                    unipackListItem = new UnipackListItem();
                    unipackListItem.setTitle(unipackInfo[0]);
                    unipackListItem.setProducer(unipackInfo[1]);
                    unipackListItem.setChain(unipackInfo[3]);
                    unipackListItem.setPath(unipackInfo[2]);
                    if (unipackInfo != null) unipackListAdapter.addItem(unipackListItem);
                }
            }
        }
        list_unipack.setAdapter(unipackListAdapter);
    }

    @Override
    public void onUnipackSelect(String name, String path) {
        if (multiDialog != null) {
            multiDialog.dismiss();
            UnzipFile unzipFile = new UnzipFile(this);
            unzipFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, name, path, fileIO.getDefaultPath() + "unipackProject/" + name +"/", list_unipack, unipackListAdapter);
        }
    }
}
