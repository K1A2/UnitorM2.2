package unitor.uni.k1a2.unitor2.views.Dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

import unitor.uni.k1a2.unitor2.File.FileIO;
import unitor.uni.k1a2.unitor2.R;
import unitor.uni.k1a2.unitor2.views.adapters.list.SelectUnipackListAdapter;
import unitor.uni.k1a2.unitor2.views.adapters.list.SelectUnipackListItem;

/**
 * Created by jckim on 2017-12-04.
 */

public class MultiDialog extends DialogFragment {

    private TextView text_Title;
    private TextView text_Path;
    private Button button_selectAll;
    private ListView list_File;

    private Bundle bundle = null;
    private FileIO fileIO;
    private SelectUnipackListItem selectUnipackListItem = null;
    private SelectUnipackListAdapter selectUnipackListAdapter = null;
    private File[] file_list = null;
    private String s_path;
    private int i_type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.dialog_file, container, false);

        text_Title = (TextView)root.findViewById(R.id.Text_title);
        text_Path = (TextView)root.findViewById(R.id.Text_path);
        button_selectAll = (Button)root.findViewById(R.id.Button_select_All);
        list_File = (ListView)root.findViewById(R.id.List_files);

        fileIO = new FileIO(root.getContext());

        bundle = getArguments();

        i_type = bundle.getInt(DialogKey.KEY_BUNDLE_TYPE);
        s_path = fileIO.getDefaultPath();
        selectUnipackListAdapter = new SelectUnipackListAdapter(i_type);

        if (i_type == DialogKey.KEY_BUNDLE_TYPE_UNIPACK) {
            text_Title.setText(getString(R.string.dialog_title_unipack));
            setFileList(i_type, s_path);
            button_selectAll.setVisibility(View.GONE);
        } else if (i_type == DialogKey.KEY_BUNDEL_TYPE_FILES) {
            text_Title.setText(getString(R.string.dialog_title_file));
            setFileList(i_type, s_path);

        }

        list_File.setOnItemClickListener(onItemClickListener);

        return root;
    }

    private void setFileList(int type, String path) {
        File[] list = null;

        text_Path.setText(path);
        selectUnipackListAdapter.clear();

        if (type == DialogKey.KEY_BUNDLE_TYPE_UNIPACK) {
            list = new File(path).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory()||(file.isFile()&&file.getName().endsWith(".zip"));
                }
            });
        } else if (type == DialogKey.KEY_BUNDEL_TYPE_FILES) {
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
            selectUnipackListAdapter.addItem(".../", "", getContext().getResources().getDrawable(android.support.design.R.drawable.navigation_empty_icon));
        }

        for (File file:list) {
            if (file.isDirectory()) {
                selectUnipackListAdapter.addItem(file.getName(), file.getAbsolutePath() + "/", getContext().getResources().getDrawable(R.drawable.ic_folder_white_48dp));
            } else {
                if (file.getName().endsWith(".zip")) {
                    selectUnipackListAdapter.addItem(file.getName(), file.getAbsolutePath() + "/", getContext().getResources().getDrawable(R.drawable.ic_insert_drive_file_white_48dp));
                } else if (file.getName().endsWith(".wav")||file.getName().endsWith(".mp3")) {
                    selectUnipackListAdapter.addItem(file.getName(), file.getAbsolutePath() + "/", getContext().getResources().getDrawable(R.drawable.ic_music_note_white_48dp));
                }
            }
        }

        list_File.setAdapter(selectUnipackListAdapter);
    }

    OnUnipackSelectListener onUnipackSelectListener;

    public interface OnUnipackSelectListener {
        void onUnipackSelect(String name, String path);
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            selectUnipackListItem = (SelectUnipackListItem)selectUnipackListAdapter.getItem(i);
            String name = selectUnipackListItem.getTitle();
            String path = selectUnipackListItem.getPath();

            if (name.equals(".../")) {
                setFileList(i_type, new File(text_Path.getText().toString()).getParentFile().getAbsolutePath() + "/");
            } else if (new File(path).isDirectory()) {
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
        onUnipackSelectListener = (OnUnipackSelectListener)activity;
    }
}
