package com.minotour.minotour.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.minotour.minotour.R;
import com.minotour.minotour.models.TestModel;

import java.util.ArrayList;

/**
 * Created by Marcel O'Neil on 2016-04-10.
 * Pushes search queries into list
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private ArrayList<TestModel> mDataset;

    public interface IZoneClick{
        void zoneClick(TestModel model);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public TextView txtTitle, txtDesc, txtDist;

        public ViewHolder(View v) {
            super(v);

            root = v;
            txtTitle = (TextView) v.findViewById(R.id.item_search_txtTitle);
            txtDesc = (TextView) v.findViewById(R.id.item_search_txtDesc);
            txtDist = (TextView) v.findViewById(R.id.item_search_txtDistance);
        }
    }

    IZoneClick callback;

    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchAdapter(ArrayList<TestModel> data, IZoneClick callback) {
        mDataset = data;
        this.callback = callback;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_outline, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final TestModel item = mDataset.get(position);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.zoneClick(item);
            }
        });
        holder.txtTitle.setText(item.name);
        holder.txtDesc.setText(item.desc);
        holder.txtDist.setText(" - "+item.distance + "km");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
