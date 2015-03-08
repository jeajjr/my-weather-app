package com.homespotter.weatherinternshipproject.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.SettingsProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AdapterDrawerMenuRecyclerView extends RecyclerView.Adapter<AdapterDrawerMenuRecyclerView.ViewHolder> {
    public static final int ITEM_WITH_ICON = 0;
    public static final int ITEM_SIMPLE = 1;
    public static final int ITEM_SECONDARY = 2;
    public static final int LINE_SEPARATOR = 4;
    public static final int SPACER = 5;

    private static String TAG = "DrawerRecyclerViewAdapter";

    private List<Map<String, ?>> itemsSet;
    private ArrayList<String> cityList;
    private OnDrawerItemClickListener onDrawerItemClickListener;
    private int currentItem = 3;
    private Context context;
    private SettingsProfile settingsProfile;

    private CompoundButton.OnCheckedChangeListener switchChangeListener;


    public AdapterDrawerMenuRecyclerView(Context context, ArrayList<String> cityList, SettingsProfile settingsProfile) {
        this.context = context;
        this.cityList = cityList;
        this.itemsSet = DrawerItemsLister.createDrawerList(context, cityList);
        this.settingsProfile = settingsProfile;
    }

    public interface OnDrawerItemClickListener {
        public void onItemClick(int uniqueID);
        public void onItemLongClick(int uniqueID);
    }

    public void setOnDrawerItemClickListener(final OnDrawerItemClickListener mItemClickListener) {
        this.onDrawerItemClickListener = mItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView name;
        View currentView;

        public ViewHolder(View v) {
            super(v);

            currentView = v;

            icon = (ImageView) v.findViewById(R.id.itemIcon);
            name = (TextView) v.findViewById(R.id.itemName);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = (Integer) itemsSet.get(getPosition()).get("id");
                    Log.d(TAG, "adapter received click on city item " + id);

                    if (onDrawerItemClickListener != null)
                        onDrawerItemClickListener.onItemClick(id);

                    if ((id & DrawerItemsLister.ITEM_CITY_MASK) == DrawerItemsLister.ITEM_CITY_MASK) {
                        currentItem = getPosition();
                        notifyDataSetChanged();
                    }
                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int id = (Integer) itemsSet.get(getPosition()).get("id");
                    Log.d(TAG, "adapter received long click on city item " + id);

                    if (onDrawerItemClickListener != null && id != -1)
                        onDrawerItemClickListener.onItemLongClick(id);

                    return true;
                }
            });
        }

        public void bindData (Map<String, ?> item, int position) {
            switch ((Integer) item.get("type")) {
                case ITEM_SIMPLE:
                    name.setText((String) item.get("name"));
                    break;
                case ITEM_WITH_ICON:
                    name.setText((String) item.get("name"));
                    icon.setImageResource((Integer) item.get("icon"));
                    break;
                case ITEM_SECONDARY:
                    name.setText((String) item.get("name"));
                    break;
            }
            int itemID = (Integer) item.get("id");
            if (((itemID & DrawerItemsLister.ITEM_CITY_MASK) == DrawerItemsLister.ITEM_CITY_MASK) && (name != null)) {
                if (currentItem == position) {
                    name.setTypeface(null, Typeface.BOLD);
                }
                else {
                    name.setTypeface(null, Typeface.NORMAL);
                }
            }
        }
    }

    public void dataSetChanged(ArrayList<String> cityList) {
        this.cityList = cityList;
        this.itemsSet = DrawerItemsLister.createDrawerList(context, cityList);
        this.notifyDataSetChanged();
    }

    public void setCurrentCity(int currentCityPosition) {
        currentItem = 3 + currentCityPosition;
    }

    @Override
    public int getItemViewType(int position) {
        return (Integer) itemsSet.get(position).get("type");
    }

    @Override
    public AdapterDrawerMenuRecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View v = null;

        switch (viewType) {
            case ITEM_WITH_ICON:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_icon, parent, false);
                break;
            case ITEM_SIMPLE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_simple, parent, false);
                break;
            case ITEM_SECONDARY:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_secondary, parent, false);
            break;
            case LINE_SEPARATOR:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_separator, parent, false);
                break;
            case SPACER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_8dp_space, parent, false);
                break;
        }

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, int position) {
        Map<String, ?> item = itemsSet.get(position);
        holder.bindData(item, position);
    }

    @Override
    public int getItemCount() {
        return itemsSet.size();
    }


}