package unitor.uni.k1a2.unitor2.activitys;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import unitor.uni.k1a2.unitor2.File.FileIO;
import unitor.uni.k1a2.unitor2.File.SharedPreference.PreferenceKey;
import unitor.uni.k1a2.unitor2.File.SharedPreference.SharedPreferenceIO;
import unitor.uni.k1a2.unitor2.R;

/**
 * Created by jckim on 2017-12-07.
 */

public class InfoFragment extends Fragment {

    private EditText edit_Title = null;
    private EditText edit_Producer = null;
    private EditText edit_Chain = null;
    private Button button_Save = null;
    private TextView text_Info = null;

    private SharedPreferenceIO sharedPreferenceIO = null;
    private FileIO fileIO;
    private ArrayList<String> listInfo;
    private String title;
    private String producer;
    private String chain;
    private String path;
    private int chainInt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_info, container, false);

        edit_Title = (EditText) root.findViewById(R.id.Edit_Title);
        edit_Producer = (EditText) root.findViewById(R.id.Edit_Producer);
        edit_Chain = (EditText) root.findViewById(R.id.Edit_Chain);
        button_Save = (Button) root.findViewById(R.id.Button_Save);
        text_Info = (TextView) root.findViewById(R.id.Text_info);

        sharedPreferenceIO = new SharedPreferenceIO(getContext(), PreferenceKey.KEY_REPOSITORY_INFO);
        listInfo = new ArrayList<String>();
        fileIO = new FileIO(getContext());

        title = sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_TITLE, "");
        producer = sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PRODUCER, "");
        chain = sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_CHAIN, "");
        path = sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "");

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
                edit_Title.setText(title);
            }
            path = fileIO.getDefaultPath() + title + "/";
            sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_PATH, path);
        }

        setText_Info();

        edit_Title.setText(title);
        edit_Producer.setText(producer);
        edit_Chain.setText(chain);

        //에딧 택스트 편집
        edit_Title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                title = editable.toString();
                sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_TITLE, title);
            }
        });
        edit_Producer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                producer = editable.toString();
                sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_PRODUCER, producer);
            }
        });
        edit_Chain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                chain = editable.toString();
                sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_CHAIN, chain);
            }
        });

        button_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.equals("")||producer.equals("")||chain.equals("")) {
                    Toast.makeText(getContext(), getString(R.string.toast_newUnipack_null), Toast.LENGTH_LONG).show();
                } else {
                    try {
                        fileIO.mkInfo(title, producer, chain, path + "info");
                        Toast.makeText(getContext(), getString(R.string.toast_save_succeed), Toast.LENGTH_SHORT).show();
                        setText_Info();
                    } catch (Exception e) {
                        e.printStackTrace();
                        fileIO.showErr(e.getMessage());
                        Toast.makeText(getContext(), getString(R.string.toast_save_fail), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return root;
    }

    private void setText_Info() {
        //info내용 가져옴
        try {
            listInfo = fileIO.getInfo(path);
        } catch (Exception e) {
            e.printStackTrace();
            listInfo = null;
            fileIO.showErr(e.getMessage());
        }

        //info내용 출력
        if (listInfo != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String content:listInfo) {
                stringBuilder.append(content + "\n");
            }
            text_Info.setText(stringBuilder);
        }
    }
}
