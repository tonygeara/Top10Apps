package com.example.tony.top10apps;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FeefAdapter<T extends FeedEntry> extends ArrayAdapter {
    private static final String TAG = "FeefAdapter";

    private final int layoutResources;
    private final LayoutInflater layoutinflater;
    private List<T> applications;

    public FeefAdapter(@NonNull Context context, int resource, List<T> applications) {
        super(context, resource);
        this.layoutResources = resource;
        this.layoutinflater = LayoutInflater.from(context);
        this.applications = applications;
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewholder;


        if (convertView == null) {

            Log.d(TAG, "getView: called with null convertView");
            convertView = layoutinflater.inflate(layoutResources, parent, false);
            viewholder = new ViewHolder(convertView);
            convertView.setTag(viewholder);
        } else {
            Log.d(TAG, "getView: provided a Converted View");
            viewholder = (ViewHolder) convertView.getTag();
        }
        View view = layoutinflater.inflate(layoutResources, parent, false);
//        TextView tvName = (TextView) convertView.findViewById(R.id.TVName);
//        TextView tvArtist = (TextView) convertView.findViewById(R.id.TVArtist);
//        TextView tvSummary = (TextView) convertView.findViewById(R.id.TVSummary);

        T currentApp = applications.get(position);

        viewholder.tvName.setText(currentApp.getName());
        viewholder.tvArtist.setText(currentApp.getArtist());
        viewholder.tvSummary.setText(currentApp.getSummary());

        return convertView;
    }

    private class ViewHolder {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        ViewHolder(View v) {
            this.tvName = v.findViewById(R.id.TVName);
            this.tvArtist = v.findViewById(R.id.TVArtist);
            this.tvSummary = v.findViewById(R.id.TVSummary);
        }

    }
}
