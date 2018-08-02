package com.androidtask.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidtask.R;
import com.androidtask.model.Locations;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    private ArrayList<Locations> locationList;
    public LocationAdapter(Context context, ArrayList<Locations> restaurants) {
        locationList = restaurants;
    }
    private static ClickListener clickListener;

    public interface ClickListener {
        void onItemClick(int position, View v,Locations locations);
        void onItemLongClick(int position, View v);
    }

    @Override
    public LocationAdapter.LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_location_list, parent, false);
        LocationViewHolder viewHolder = new LocationViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LocationAdapter.LocationViewHolder holder, int position) {
        holder.bindRestaurant(locationList.get(position));
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.img_location)
        ImageView img_location;
        @BindView(R.id.txt_location)
        TextView txt_location;
        @BindView(R.id.txt_rating_key)
        TextView txt_rating_value;
        @BindView(R.id.txt_distance_value)
        TextView txt_distance_value;

        private Context mContext;

        public LocationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        v.setBackgroundColor(Color.parseColor("#f0f0f0"));
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                    {
                        v.setBackgroundColor(Color.TRANSPARENT);
                    }
                    return false;
                }
            });
        }

        public void bindRestaurant(Locations location) {
            txt_location.setText(location.getName());
            txt_rating_value.setText(location.getDistance());
            txt_distance_value.setText(location.getRating());
        }
    }

}
