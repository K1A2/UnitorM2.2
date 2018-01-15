package unitor.uni.k1a2.unitor2.views.adapters.list;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import unitor.uni.k1a2.unitor2.R;
import unitor.uni.k1a2.unitor2.views.Dialogs.DialogKey;

/**
 * Created by jckim on 2017-12-02.
 */

public class SelectFileListAdapter extends BaseAdapter {

    private ArrayList<SelectFileListItem> listViewList = new ArrayList<SelectFileListItem>();

    private int type;
    private Drawable icon;
    private String title;
    private String path;

    public SelectFileListAdapter(int Type) {
        this.type = Type;
    }

    @Override
    public int getCount () {
        return listViewList.size();
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            if (type == DialogKey.KEY_BUNDLE_TYPE_UNIPACK) {
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_list_select_u, parent, false);
            } else if (type == DialogKey.KEY_BUNDEL_TYPE_FILES) {
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_list_select_f, parent, false);
            }
        }

        TextView titleView = (TextView)convertView.findViewById(R.id.FragDial_file_name_select);
        TextView pathView= (TextView)convertView.findViewById(R.id.FragDial_file_path_select);
        ImageView iconView = (ImageView)convertView.findViewById(R.id.FragDial_file_Image_select);

        SelectFileListItem listItem = listViewList.get(pos);

        title = listItem.getTitle();
        path = listItem.getPath();
        icon = listItem.getIcon();

        titleView.setText(title);
        pathView.setText(path);
        iconView.setImageDrawable(icon);

        return convertView;
    }

    @Override
    public long getItemId (int position) {
        return position;
    }

    @Override
    public Object getItem (int position) {
        return listViewList.get(position);
    }

    public void addItem (String title, String path, Drawable icon) {
        SelectFileListItem listItem = new SelectFileListItem();

        listItem.setTitle(title);
        listItem.setPath(path);
        listItem.setIconDrw(icon);

        listViewList.add(listItem);
    }

    public void remove (int position) {
        listViewList.remove(position);
        DataChange();
    }

    public void clear () {
        listViewList.clear();
        DataChange();
    }

    public void DataChange () {
        this.notifyDataSetChanged();
    }
}