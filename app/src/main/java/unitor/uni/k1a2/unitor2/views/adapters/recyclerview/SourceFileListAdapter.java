package unitor.uni.k1a2.unitor2.views.adapters.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import unitor.uni.k1a2.unitor2.R;

/**
 * Created by jckim on 2018-02-17.
 */

public class SourceFileListAdapter extends RecyclerView.Adapter<SourceFileListAdapter.ViewHolder> {

    private List<SourceFileListItem> sourceFileListItems;
    private int itemLayout;

    public SourceFileListAdapter(int itemLayout) {
        this.sourceFileListItems = new ArrayList<SourceFileListItem>();
        this.itemLayout = itemLayout;
    }

    @Override
    public SourceFileListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SourceFileListAdapter.ViewHolder holder, int position) {
        SourceFileListItem item = sourceFileListItems.get(position);
        holder.textTitle.setText(item.getTitle());
        holder.textPath.setText(item.getPath());
    }

    @Override
    public int getItemCount() {
        return sourceFileListItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textTitle;
        public TextView textPath;

        public ViewHolder(View itemView){
            super(itemView);

            textTitle = (TextView) itemView.findViewById(R.id.Text_file_title);
            textPath = (TextView) itemView.findViewById(R.id.Text_file_path);
        }

    }

    public void removeItem(int position) {
        try {
            sourceFileListItems.remove(position);
            notifyItemRemoved(position);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void addItem(SourceFileListItem item) {
        sourceFileListItems.add(item);
        notifyItemInserted(sourceFileListItems.size());
    }

    public void addItem(SourceFileListItem item, int position) {
        sourceFileListItems.add(position, item);
        notifyItemInserted(position);
    }

    public SourceFileListItem getItem(int position) {
        return sourceFileListItems.get(position);
    }

    public void clearItem() {
        final int count = sourceFileListItems.size();
        sourceFileListItems.clear();
        notifyItemRangeRemoved(0, count);
    }
}
