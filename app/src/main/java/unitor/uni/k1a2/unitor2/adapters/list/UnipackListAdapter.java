package unitor.uni.k1a2.unitor2.adapters.list;

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

public class UnipackListAdapter extends BaseAdapter {

    private ArrayList<UnipackListItem> listViewList = new ArrayList<UnipackListItem>();
    private String title;
    private String producer;
    private String path;
    private String chain;

    public UnipackListAdapter () {

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
            convertView = inflater.inflate(R.layout.view_list_unipack, parent, false);
        }

        TextView titleView = (TextView)convertView.findViewById(R.id.list_unipack_title);
        TextView produceView = (TextView)convertView.findViewById(R.id.list_unipack_producer);
        TextView pathView= (TextView)convertView.findViewById(R.id.list_unipack_path);
        TextView chainView = (TextView)convertView.findViewById(R.id.list_unipack_chain);

        UnipackListItem listItem = listViewList.get(pos);

        title = listItem.getTitle();
        producer = listItem.getProducer();
        path = listItem.getPath();
        chain = listItem.getChain();

        titleView.setText(title);
        produceView.setText(producer);
        pathView.setText(path);
        chainView.setText(chain);

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

    public void addItem (String title, String producer, String path, String chain) {
        UnipackListItem listItem = new UnipackListItem();

        listItem.setTitle(title);
        listItem.setProducer(producer);
        listItem.setPath(path);
        listItem.setChain(chain);

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