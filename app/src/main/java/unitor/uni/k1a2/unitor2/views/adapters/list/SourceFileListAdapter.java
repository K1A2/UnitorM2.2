package unitor.uni.k1a2.unitor2.views.adapters.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import unitor.uni.k1a2.unitor2.R;

/**
 * Created by jckim on 2017-12-02.
 */

public class SourceFileListAdapter extends BaseAdapter {

    private ArrayList<SourceFileListItem> listViewList = new ArrayList<SourceFileListItem>();

    private String title;
    private String path;

    public SourceFileListAdapter() {

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
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_list_files, parent, false);
        }

        TextView titleView = (TextView)convertView.findViewById(R.id.Text_file_title);
        TextView pathView= (TextView)convertView.findViewById(R.id.Text_file_path);

        SourceFileListItem listItem = listViewList.get(pos);

        title = listItem.getTitle();
        path = listItem.getPath();

        titleView.setText(title);
        pathView.setText(path);

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

    public void addItem (String title, String path) {
        SourceFileListItem listItem = new SourceFileListItem();

        listItem.setTitle(title);
        listItem.setPath(path);

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