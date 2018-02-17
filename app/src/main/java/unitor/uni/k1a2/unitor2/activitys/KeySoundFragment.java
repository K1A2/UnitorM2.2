package unitor.uni.k1a2.unitor2.activitys;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import unitor.uni.k1a2.unitor2.File.FileIO;
import unitor.uni.k1a2.unitor2.File.SharedPreference.PreferenceKey;
import unitor.uni.k1a2.unitor2.File.SharedPreference.SharedPreferenceIO;
import unitor.uni.k1a2.unitor2.R;
import unitor.uni.k1a2.unitor2.views.Dialogs.DialogKey;
import unitor.uni.k1a2.unitor2.views.Dialogs.MultiDialog;
import unitor.uni.k1a2.unitor2.views.adapters.list.SourceFileListAdapter;
import unitor.uni.k1a2.unitor2.views.adapters.list.SourceFileListItem;
import unitor.uni.k1a2.unitor2.views.buttons.PlayButton;

/**
 * Created by jckim on 2017-12-09.
 */

public class KeySoundFragment extends Fragment {

    private LinearLayout linear_buttons;
    private LinearLayout linear_edit;
    private LinearLayout linear_play;
    private RadioGroup radioG_chain;
    private Button button_addSound;
    private TextView text_current;
    private TextView text_content;
    private ListView list_sounds;
    private RadioButton radio_chain_1;
    private RadioGroup radioG_mode;
    private Spinner spinner_Chain;
    private CheckBox check_delete;
    private MultiDialog multiDialog;

    private FileIO fileIO;
    private String path;
    private String current;
    private ArrayList<String> array_content;
    private ArrayList<String[]> array_sounds;
    private ArrayList<String[]> array_multi;
    private SharedPreferenceIO sharedPreferenceIO = null;
    private SourceFileListItem sourceFileListItem = null;
    private SourceFileListAdapter sourceFileListAdapter = null;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_keysound, container, false);

        linear_buttons = (LinearLayout) root.findViewById(R.id.Layout_Btns);
        button_addSound = (Button) root.findViewById(R.id.Button_AddSound);
        list_sounds = (ListView) root.findViewById(R.id.List_KeySound);
        radioG_mode = (RadioGroup) root.findViewById(R.id.RadioG_mode);
        text_current = (TextView) root.findViewById(R.id.Text_current_sound);
        spinner_Chain = (Spinner) root.findViewById(R.id.Spinner_chain);
        text_content = (TextView) root.findViewById(R.id.text_content);
        check_delete = (CheckBox) root.findViewById(R.id.Check_deleteSound);
        linear_edit = (LinearLayout) root.findViewById(R.id.Layout_Edit);
        linear_play = (LinearLayout) root.findViewById(R.id.Layout_Play);
        radioG_chain = (RadioGroup) root.findViewById(R.id.RadioG_chain_S);

        text_content.setMovementMethod(new ScrollingMovementMethod());

        fileIO = new FileIO(getContext());
        sourceFileListAdapter = new SourceFileListAdapter();
        sharedPreferenceIO = new SharedPreferenceIO(getContext(), PreferenceKey.KEY_REPOSITORY_INFO);

        path = sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "");
        String chain_S = sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_CHAIN, "1");
        ArrayAdapter arrayAdapter = null;
        try {
            int chain_I = Integer.valueOf(chain_S);
            String[] chainlist = new String[chain_I];
            for (int i = 0;i < chain_I;i++) {
                chainlist[i] = String.format(getString(R.string.spinner_chain), String.valueOf(i + 1));
                RadioButton radioButton = new RadioButton(getContext());
                radioButton.setText(String.format(getString(R.string.spinner_chain), String.valueOf(i + 1)));
                radioButton.setId(i + 1);
                radioG_chain.addView(radioButton);
                if (i == 0) {
                    radio_chain_1 = radioButton;
                }
            }
            arrayAdapter = new ArrayAdapter(getContext(), android.support.design.R.layout.support_simple_spinner_dropdown_item, chainlist);
        } catch (NumberFormatException e) {
            fileIO.showErr(e.getMessage());
            String[] s = new String[1];
            s[0] = String.format(getString(R.string.spinner_chain), "1");
            arrayAdapter = new ArrayAdapter(getContext(), android.support.design.R.layout.support_simple_spinner_dropdown_item, s);
        } catch (Exception e) {
            fileIO.showErr(e.getMessage());
            String[] s = new String[1];
            s[0] = String.format(getString(R.string.spinner_chain), "1");
            arrayAdapter = new ArrayAdapter(getContext(), android.support.design.R.layout.support_simple_spinner_dropdown_item, s);
        } finally {
            if (arrayAdapter != null) {
                spinner_Chain.setAdapter(arrayAdapter);
            }
        }

        if (new File(fileIO.getDefaultPath() + "unipackProject/work/keySound.txt").exists()) {
            try {
                array_content = fileIO.getKeySoundWork();
            } catch (Exception e) {
                array_content = null;
                fileIO.showErr(e.getMessage());
            }
        } else {
            try {
                array_content = fileIO.getKeySound(path);
                fileIO.mkKeySoundWork(array_content);
            } catch (Exception e) {
                array_content = null;
                fileIO.showErr(e.getMessage());
            }
        }

        setSound();

        //버튼처리
        for (int vertical = 1;vertical < 9;vertical++) {
            for (int horizontal = 1;horizontal < 9;horizontal++) {
                PlayButton playButton = (PlayButton) root.findViewWithTag(String.valueOf(vertical) + " " + String.valueOf(horizontal));
                playButton.textView.setOnClickListener(playClick);
                playButton.textView.setClickable(true);
                playButton.textView.setFocusable(true);
            }
        }

        //사운드 불러오기
        button_addSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putInt(DialogKey.KEY_BUNDLE_TYPE, DialogKey.KEY_BUNDEL_TYPE_FILES);
                multiDialog = new MultiDialog();
                multiDialog.setArguments(args);
                multiDialog.show(getFragmentManager(), DialogKey.KEY_MAIN_FILE);
            }
        });

        //모드변경
        radioG_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.Radio_Edit://편집
                        linear_play.setVisibility(View.INVISIBLE);
                        linear_edit.setVisibility(View.VISIBLE);
                        spinner_Chain.setSelection(0);
                        setButtonSound("1");
                        break;

                    case R.id.Radio_Test://테스트
                        linear_play.setVisibility(View.VISIBLE);
                        linear_edit.setVisibility(View.INVISIBLE);
                        if (radio_chain_1 != null) {
                            if (!radio_chain_1.isSelected()) {
                                radioG_chain.check(radio_chain_1.getId());
                            }
                        }
                        setButtonSound("1");
                        array_multi = new ArrayList<String[]>();
                    int c = 0;
                        for (String s:array_content) {
                            String[] line = s.split("\\s+");
                            int count = 0;
                            boolean isFind = false;
                            String multi_count = "0";

                            for (;count < array_multi.size();count++) {//chain, x, y, multi, now
                                String[] m = array_multi.get(count);
                                if (m[0].equals(line[0])&&m[1].equals(line[1])&&m[2].equals(line[2])) {
                                    multi_count = m[3];
                                    isFind = true;
                                    break;
                                }
                            }
                            if (isFind) {
                                try {
                                    array_multi.set(count, new String[] {line[0], line[1], line[2], String.valueOf(Integer.valueOf(multi_count) + 1), "0"});
                                } catch (NumberFormatException e) {

                                } catch (Exception e) {

                                }
                            } else {
                                array_multi.add(new String[] {line[0], line[1], line[2], "0", "0"});
                                c++;
                            }
                        }
                        break;
                }
            }
        });

        //체인변경
        spinner_Chain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String chain_selected = ((String) spinner_Chain.getItemAtPosition(i)).split("\\s+")[0];
                    setButtonSound(chain_selected);
                } catch (Exception e) {
                    fileIO.showErr(e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //체인 처리
        radioG_chain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                try {
                    String chain_selected = String.valueOf(i);
                    setButtonSound(chain_selected);
                } catch (Exception e) {
                    fileIO.showErr(e.getMessage());
                }
            }
        });

        //리스트 아이템
        list_sounds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sourceFileListItem = (SourceFileListItem) sourceFileListAdapter.getItem(i);

                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_sound, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_sound_select:
                                current = sourceFileListItem.getTitle();
                                text_current.setText("Selected: " +  current);
                                break;

                            case R.id.menu_sound_play:
                                soundPlay.onPlay(sourceFileListItem.getTitle());
                                break;

                            case R.id.menu_sound_delete:
                                new File(sourceFileListItem.getPath()).delete();
                                addSound();//리스트
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        return root;
    }



    //사운드추가
    public void addSound() {
        setSound();
        fileChange.onFileChange();
    }

    //
    private void setSound() {
        sourceFileListAdapter.clear();
        try {
            array_sounds = fileIO.getSoundFile(path + "sounds/");
            for (String[] s:array_sounds) {
                sourceFileListAdapter.addItem(s[0], s[1]);
            }
            list_sounds.setAdapter(sourceFileListAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            array_sounds = null;
            fileIO.showErr(e.getMessage());
        }
    }

    private void setButtonSound(String chain) {
        if (array_content != null) {
            for (int vertical = 1;vertical < 9;vertical++) {
                for (int horizontal = 1;horizontal < 9;horizontal++) {
                    PlayButton playButton = (PlayButton) root.findViewWithTag(String.valueOf(vertical) + " " + String.valueOf(horizontal));
                    playButton.setBackgroundResource(R.drawable.playbutton);
                }
            }
            StringBuilder a = new StringBuilder();
            for (String s:array_content) {
                if (s.startsWith(chain)) {
                    a.append(s + "\n");
                    String[] xyz = s.split("\\s+");
                    PlayButton playButton = (PlayButton) root.findViewWithTag(xyz[1] + " " + xyz[2]);
                    playButton.setBackgroundResource(R.drawable.background_playbutton_used);
                }
            }
            text_content.setText(a.toString());
        }
    }

    //버튼 크기조절
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewTreeObserver viewTreeObserver = linear_buttons.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    linear_buttons.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    linear_buttons.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                final int height = linear_buttons.getHeight();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(height, height);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                linear_buttons.setLayoutParams(params);
            }
        });
    }

    //플레이버튼
    View.OnClickListener playClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String chain = "1";
            switch (radioG_mode.getCheckedRadioButtonId()) {
                case R.id.Radio_Edit:
                    chain = ((String) spinner_Chain.getItemAtPosition(spinner_Chain.getSelectedItemPosition())).split("\\s+")[0];
                    break;

                case R.id.Radio_Test:
                    chain = String.valueOf(radioG_chain.getCheckedRadioButtonId());
                    break;
            }
            String xy = ((TextView) view.findViewById(R.id.View_Play_text)).getText().toString();

            switch (radioG_mode.getCheckedRadioButtonId()) {
                case R.id.Radio_Edit:
                    if (check_delete.isChecked()) {//삭제
                        String[] content = text_content.getText().toString().split("\n");
                        if (array_content != null) {
                            if (array_content.size() > 0) {

                                for (int i = content.length - 1;i >= 0;i--) {
                                    if (content[i].startsWith(chain + " " + xy)) {
                                        Collections.reverse(array_content);
                                        array_content.remove(array_content.indexOf(content[i]));
                                        Collections.reverse(array_content);
                                        setButtonSound(chain);
                                        try {
                                            fileIO.mkKeySoundWork(array_content);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            fileIO.showErr(e.getMessage());
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    } else {//추가
                        if (!text_current.getText().toString().isEmpty()) {
                            String name_Sound = text_current.getText().toString().replaceFirst("Selected: ", "");
                            String sounds = chain + " " + xy + " " + name_Sound;

                            StringBuilder stringBuilder = new StringBuilder(text_content.getText().toString());
                            stringBuilder.append(sounds + "\n");
                            text_content.setText(stringBuilder.toString());
                            if (array_content == null) {
                                array_content = new ArrayList<String>();
                            }
                            array_content.add(sounds);
                            ((PlayButton) root.findViewWithTag(((TextView) view.findViewById(R.id.View_Play_text)).getText().toString())).setBackgroundResource(R.drawable.background_playbutton_used);
                            try {
                                fileIO.mkKeySoundWork(array_content);
                            } catch (Exception e) {
                                e.printStackTrace();
                                fileIO.showErr(e.getMessage());
                            }
                        }
                    }
                    break;

                case R.id.Radio_Test://테스트모드
                    String[] clicked = xy.split("\\s+");
                    for (int count = 0;count < array_multi.size();count++) {
                        String[] s = array_multi.get(count);
                        if (s[0].equals(chain)&&s[1].equals(clicked[0])&&s[2].equals(clicked[1])) {
                            int multi =Integer.parseInt(s[3]);
                            int now_E = Integer.parseInt(s[4]);
                            int now = Integer.parseInt(s[4]);
                            for (String m:array_content) {
                                if (m.startsWith(chain + " " + xy)) {
                                    if (now > 0) {
                                        now--;
                                        continue;
                                    } else {
                                        soundPlay.onPlay(m.split("\\s+")[3]);
                                        if (now_E == multi) {
                                            now_E = -1;
                                        }
                                        array_multi.set(count, new String[] {s[0], s[1], s[2], String.valueOf(multi), String.valueOf(now_E + 1)});
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                    break;
            }
        }
    };

    public void saveKeySound() {
        try {
            fileIO.mkKeySound(path + "keySound");
            Toast.makeText(getContext(), getString(R.string.toast_save_succeed), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), getString(R.string.toast_save_fail), Toast.LENGTH_SHORT).show();
            fileIO.showErr(e.getMessage());
        }
    }

    private SoundPlay soundPlay;
    private FileChange fileChange;

    public interface SoundPlay {
        public void onPlay(String title);
    }

    public interface FileChange {
        public void onFileChange();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        soundPlay = (SoundPlay)activity;
        fileChange = (FileChange)activity;
    }
}
