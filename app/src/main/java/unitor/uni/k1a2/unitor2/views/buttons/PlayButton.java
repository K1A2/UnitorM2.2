package unitor.uni.k1a2.unitor2.views.buttons;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import unitor.uni.k1a2.unitor2.R;

/**
 * Created by jckim on 2017-12-09.
 */

public class PlayButton extends LinearLayout {

    public TextView textView;

    public PlayButton(Context context) {
        super(context);
        iniView(context);
    }

    public PlayButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        iniView(context);
        getAttrs(attrs);
    }

    public PlayButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniView(context);
        getAttrs(attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        iniView(context);
        getAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void iniView(Context context) {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(infService);
        View view = layoutInflater.inflate(R.layout.view_button_play, PlayButton.this, false);
        textView = (TextView) view.findViewById(R.id.View_Play_text);

        this.setClickable(true);
        textView.setClickable(true);

        addView(view);
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PlayButton);
        setTypeArray(typedArray);
    }


    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PlayButton, defStyle, 0);
        setTypeArray(typedArray);
    }

    private void getAttrs(AttributeSet attrs, int defStyle, int defStyleAttr) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PlayButton, defStyle, defStyleAttr);
        setTypeArray(typedArray);
    }

    private void setTypeArray(TypedArray typeArray) {
        String text_string = typeArray.getString(R.styleable.PlayButton_name);
        textView.setText(text_string);
        typeArray.recycle();
    }
}
