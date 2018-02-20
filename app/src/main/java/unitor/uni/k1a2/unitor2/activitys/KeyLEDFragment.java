package unitor.uni.k1a2.unitor2.activitys;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import unitor.uni.k1a2.unitor2.File.FileIO;
import unitor.uni.k1a2.unitor2.File.SharedPreference.PreferenceKey;
import unitor.uni.k1a2.unitor2.File.SharedPreference.SharedPreferenceIO;
import unitor.uni.k1a2.unitor2.R;
import unitor.uni.k1a2.unitor2.views.adapters.recyclerview.SourceFileListAdapter;
import unitor.uni.k1a2.unitor2.views.adapters.recyclerview.SourceFileListItem;
import unitor.uni.k1a2.unitor2.views.listener.RecyclerItemClickListener;

/**
 * Created by ITX on 2018-01-18.
 */

public class KeyLEDFragment extends Fragment {

    private LinearLayout linear_buttons;
    private Spinner spinner_Chain;
    private RecyclerView list_LED;

    private String path;
    private ArrayList<String[]> array_ledfiles;

    private FileIO fileIO;
    private SharedPreferenceIO sharedPreferenceIO;
    private SourceFileListItem sourceFileListItem;
    private SourceFileListAdapter sourceFileListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_keyled, container, false);

        linear_buttons = (LinearLayout) root.findViewById(R.id.Layout_Btns);
        spinner_Chain = (Spinner) root.findViewById(R.id.Spinner_chain_L);
        list_LED = (RecyclerView) root.findViewById(R.id.Recycle_led_list);

        list_LED.setLayoutManager(new LinearLayoutManager(getContext()));
        list_LED.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), new LinearLayoutManager(getContext()).getOrientation());
        list_LED.addItemDecoration(dividerItemDecoration);

        sourceFileListAdapter = new SourceFileListAdapter(R.layout.view_list_files);

        fileIO = new FileIO(getContext());
        sharedPreferenceIO = new SharedPreferenceIO(getContext(), PreferenceKey.KEY_REPOSITORY_INFO);

        path = sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "");
        String chain_S = sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_CHAIN, "1");
        ArrayAdapter arrayAdapter = null;
        try {
            int chain_I = Integer.valueOf(chain_S);
            String[] chainlist = new String[chain_I];
            for (int i = 0;i < chain_I;i++) {
                chainlist[i] = String.format(getString(R.string.spinner_chain), String.valueOf(i + 1));
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

        try {
            array_ledfiles = fileIO.getLEDFile(path + "keyLED/");
        } catch (Exception e) {
            e.printStackTrace();
            array_ledfiles = null;
            fileIO.showErr(e.getMessage());
        }

        if (array_ledfiles != null) {

            for (String[] s:array_ledfiles) {
                sourceFileListItem = new SourceFileListItem();
                sourceFileListItem.setTitle(s[0]);
                sourceFileListItem.setPath(s[1]);
                sourceFileListAdapter.addItem(sourceFileListItem);
            }
            list_LED.setAdapter(sourceFileListAdapter);
        }

        list_LED.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), list_LED, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                Toast.makeText(getContext(), position + "클릭", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongItemClicked(View view, int position) {
                Toast.makeText(getContext(), position + "롱클릭", Toast.LENGTH_SHORT).show();
            }
        }));

        return root;
    }

    View.OnClickListener playbtnClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

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
