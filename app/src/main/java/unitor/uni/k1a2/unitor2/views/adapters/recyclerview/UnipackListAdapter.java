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

public class UnipackListAdapter extends RecyclerView.Adapter<UnipackListAdapter.ViewHolder> {

    private List<UnipackListItem> UnipackListItems;
    private int itemLayout;

    public UnipackListAdapter(int itemLayout) {
        this.UnipackListItems = new ArrayList<UnipackListItem>();
        this.itemLayout = itemLayout;
    }

    @Override
    public UnipackListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UnipackListAdapter.ViewHolder holder, int position) {
        UnipackListItem item = UnipackListItems.get(position);
        holder.textTitle.setText(item.getTitle());
        holder.textPath.setText(item.getPath());
        holder.textProducer.setText(item.getProducer());
        holder.textChain.setText(item.getChain());
    }

    @Override
    public int getItemCount() {
        return UnipackListItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textTitle;
        public TextView textPath;
        public TextView textProducer;
        public TextView textChain;

        public ViewHolder(View itemView){
            super(itemView);

            textTitle = (TextView) itemView.findViewById(R.id.list_unipack_title);
            textPath = (TextView) itemView.findViewById(R.id.list_unipack_path);
            textProducer = (TextView) itemView.findViewById(R.id.list_unipack_producer);
            textChain = (TextView) itemView.findViewById(R.id.list_unipack_chain);
        }

    }

    public void removeItem(int position) {
        try {
            UnipackListItems.remove(position);
            notifyItemRemoved(position);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void addItem(UnipackListItem item) {
        UnipackListItems.add(item);
        notifyItemInserted(UnipackListItems.size());
    }

    public void addItem(UnipackListItem item, int position) {
        UnipackListItems.add(position, item);
        notifyItemInserted(position);
    }

    public UnipackListItem getItem(int position) {
        return UnipackListItems.get(position);
    }

    public void clearItem() {
        final int count = UnipackListItems.size();
        UnipackListItems.clear();
        notifyItemRangeRemoved(0, count);
    }
}
