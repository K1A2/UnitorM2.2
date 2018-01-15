package unitor.uni.k1a2.unitor2.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

import unitor.uni.k1a2.unitor2.R;

/**
 * Created by jckim on 2018-01-14.
 */

public class CheckView extends LinearLayout implements Checkable {

    private CheckBox checkBox;

    public CheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean b) {
        checkBox = (CheckBox)findViewById(R.id.Fragdial_file_isSelect);
        if (checkBox.isChecked() != b) {
            checkBox.setChecked((b));
        }
    }

    @Override
    public boolean isChecked() {
        checkBox = (CheckBox)findViewById(R.id.Fragdial_file_isSelect);
        return checkBox.isChecked();
    }

    @Override
    public void toggle() {
        checkBox = (CheckBox)findViewById(R.id.Fragdial_file_isSelect);
        setChecked(checkBox.isChecked() ? false : true);
    }
}
