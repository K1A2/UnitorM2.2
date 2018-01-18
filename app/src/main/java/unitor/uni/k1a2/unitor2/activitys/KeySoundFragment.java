package unitor.uni.k1a2.unitor2.activitys;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import unitor.uni.k1a2.unitor2.R;
import unitor.uni.k1a2.unitor2.views.Dialogs.DialogKey;
import unitor.uni.k1a2.unitor2.views.Dialogs.MultiDialog;

/**
 * Created by jckim on 2017-12-09.
 */

public class KeySoundFragment extends Fragment {

    private LinearLayout linear_buttons;
    private Button button_addSound;
    private ListView list_sounds;
    private RadioGroup radioG_mode;
    private MultiDialog multiDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_keysound, container, false);

        linear_buttons = (LinearLayout) root.findViewById(R.id.Layout_Btns);
        button_addSound = (Button) root.findViewById(R.id.Button_AddSound);
        list_sounds = (ListView) root.findViewById(R.id.List_KeySound);
        radioG_mode = (RadioGroup) root.findViewById(R.id.RadioG_mode);

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
                    case R.id.Radio_Edit:
                        break;

                    case R.id.Radio_Test:
                        break;
                }
            }
        });

        return root;
    }



    //사운드추가
    public void addSound() {

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
}
