package unitor.uni.k1a2.unitor2.activitys;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import unitor.uni.k1a2.unitor2.R;

/**
 * Created by ITX on 2018-01-18.
 */

public class KeyLEDFragment extends Fragment {

    private LinearLayout linear_buttons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_keyled, container, false);

        linear_buttons = (LinearLayout) root.findViewById(R.id.Layout_Btns);

        return root;
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
