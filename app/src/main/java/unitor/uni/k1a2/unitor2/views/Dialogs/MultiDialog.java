package unitor.uni.k1a2.unitor2.views.Dialogs;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import unitor.uni.k1a2.unitor2.File.FileIO;
import unitor.uni.k1a2.unitor2.R;
import unitor.uni.k1a2.unitor2.views.adapters.list.SelectFileListAdapter;
import unitor.uni.k1a2.unitor2.views.adapters.list.SelectFileListItem;

/**
 * Created by jckim on 2017-12-04.
 */

public class MultiDialog extends DialogFragment {

    private TextView text_Title;
    private TextView text_Path;
    private Button button_selectAll;
    private Button button_add;
    private ListView list_File;

    private Bundle bundle = null;
    private FileIO fileIO;
    private SoundPool soundPool;
    private SelectFileListItem selectFileListItem = null;
    private SelectFileListAdapter selectFileListAdapter = null;
    private File[] file_list = null;
    private String s_path;
    private Activity activityA;
    private int i_type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.dialog_file, container, false);

        text_Title = (TextView)root.findViewById(R.id.Text_title);
        text_Path = (TextView)root.findViewById(R.id.Text_path);
        button_selectAll = (Button)root.findViewById(R.id.Button_select_All);
        list_File = (ListView)root.findViewById(R.id.List_files);
        button_add = (Button)root.findViewById(R.id.Button_dial_add);

        fileIO = new FileIO(root.getContext());

        bundle = getArguments();

        i_type = bundle.getInt(DialogKey.KEY_BUNDLE_TYPE);
        s_path = fileIO.getDefaultPath();
        selectFileListAdapter = new SelectFileListAdapter(i_type);

        if (i_type == DialogKey.KEY_BUNDLE_TYPE_UNIPACK) {
            onUnipackSelectListener = (OnUnipackSelectListener)activityA;
            root.findViewById(R.id.text_dial_sound).setVisibility(View.GONE);
            button_add.setVisibility(View.GONE);
            text_Title.setText(getString(R.string.dialog_title_unipack));
            setFileList(i_type, s_path);
            button_selectAll.setVisibility(View.GONE);
        } else if (i_type == DialogKey.KEY_BUNDEL_TYPE_FILES) {
            onFileSelectListener = (OnFileSelectListener)activityA;
            text_Title.setText(getString(R.string.dialog_title_file));
            setFileList(i_type, s_path);
        }

        //리스트클릭시
        list_File.setOnItemClickListener(onItemClickListener);

        //사운드재생
        list_File.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectFileListItem = (SelectFileListItem) selectFileListAdapter.getItem(i);

                String name = selectFileListItem.getTitle();
                if (new File(selectFileListItem.getPath()).isFile()&&(name.endsWith(".wav")||name.endsWith(".mp3"))) {
                    if (soundPool != null) {
                        soundPool.release();
                    }
                    soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
                    soundPool.load(selectFileListItem.getPath(), 1);
                    soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                        @Override
                        public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                            soundPool.play(i, 1, 1, 0, 0, 1);
                            soundPool.unload(i);
                        }
                    });
                }
                return true;
            }
        });

        //체크 선택
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray checkId = list_File.getCheckedItemPositions();
                int count = selectFileListAdapter.getCount();
                ArrayList<String[]> selected = new ArrayList<String[]>();
                for (int i = 0;i < count;i++) {
                    if (checkId.get(i)) {
                        selectFileListItem = (SelectFileListItem)selectFileListAdapter.getItem(i);
                        selected.add(new String[] {selectFileListItem.getTitle(), selectFileListItem.getPath()});
                    }
                }
                onFileSelectListener.onFileSelect(selected);
            }
        });

        //전체선택
        button_selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int count = selectFileListAdapter.getCount();

                for (int i = 0;i < count;i++) {
                    selectFileListItem = (SelectFileListItem) selectFileListAdapter.getItem(i);
                    if (selectFileListItem.getTitle().endsWith(".wav")||selectFileListItem.getTitle().endsWith(".mp3")) {
                        list_File.setItemChecked(i, true);
                    }
                }
            }
        });

        return root;
    }

    private void setFileList(int type, String path) {
        File[] list = null;

        text_Path.setText(path);
        selectFileListAdapter.clear();

        if (type == DialogKey.KEY_BUNDLE_TYPE_UNIPACK) {
            list_File.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            list = new File(path).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory()||(file.isFile()&&file.getName().endsWith(".zip"));
                }
            });
        } else if (type == DialogKey.KEY_BUNDEL_TYPE_FILES) {
            list_File.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            list = new File(path).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory()||(file.isFile()&&(file.getName().endsWith(".wav")||file.getName().endsWith(".mp3")));
                }
            });
        }
        Arrays.sort(list, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                return file.getName().compareToIgnoreCase(t1.getName());
            }
        });

        if (!path.equals(fileIO.getDefaultPath())) {
            selectFileListAdapter.addItem(".../", "", getContext().getResources().getDrawable(android.support.design.R.drawable.navigation_empty_icon));
        }

        for (File file:list) {
            if (file.isDirectory()) {
                selectFileListAdapter.addItem(file.getName(), file.getAbsolutePath() + "/", getContext().getResources().getDrawable(R.drawable.ic_folder_white_48dp));
            } else {
                if (file.getName().endsWith(".zip")) {
                    selectFileListAdapter.addItem(file.getName(), file.getAbsolutePath() + "/", getContext().getResources().getDrawable(R.drawable.ic_insert_drive_file_white_48dp));
                } else if (file.getName().endsWith(".wav")||file.getName().endsWith(".mp3")) {
                    selectFileListAdapter.addItem(file.getName(), file.getAbsolutePath() + "/", getContext().getResources().getDrawable(R.drawable.ic_music_note_white_48dp));
                }
            }
        }

        list_File.setAdapter(selectFileListAdapter);
    }

    OnUnipackSelectListener onUnipackSelectListener;
    OnFileSelectListener onFileSelectListener;

    public interface OnUnipackSelectListener {
        void onUnipackSelect(String name, String path);
    }

    public interface OnFileSelectListener {
        void onFileSelect(ArrayList<String[]> files);
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            selectFileListItem = (SelectFileListItem)selectFileListAdapter.getItem(i);
            String name = selectFileListItem.getTitle();
            String path = selectFileListItem.getPath();

            if (name.equals(".../")) {
                list_File.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                setFileList(i_type, new File(text_Path.getText().toString()).getParentFile().getAbsolutePath() + "/");
            } else if (new File(path).isDirectory()) {
                list_File.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                setFileList(i_type, path);
            } else {
                if (name.endsWith(".zip")) {
                    onUnipackSelectListener.onUnipackSelect(name, path);
                } else if (name.endsWith(".wav")||name.endsWith(".mp3")) {

                }
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityA = activity;
    }
}
