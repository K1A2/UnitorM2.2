package unitor.uni.k1a2.unitor2.views.Dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import unitor.uni.k1a2.unitor2.R;

/**
 * Created by jckim on 2017-12-04.
 */

public class MultiDialog extends DialogFragment {

    private TextView text_Title;
    private TextView text_Path;
    private Button button_selectAll;
    private ListView list_File;

    private Bundle bundle = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.dialog_file, container, false);

        text_Title = (TextView)root.findViewById(R.id.Text_title);
        text_Path = (TextView)root.findViewById(R.id.Text_path);
        button_selectAll = (Button)root.findViewById(R.id.Button_select_All);
        list_File = (ListView)root.findViewById(R.id.List_files);

        bundle = getArguments();

        

        return root;
    }
}
