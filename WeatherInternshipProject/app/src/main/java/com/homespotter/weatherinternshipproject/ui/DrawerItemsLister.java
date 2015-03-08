package com.homespotter.weatherinternshipproject.ui;

import android.content.Context;

import com.homespotter.weatherinternshipproject.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ernesto on 07/03/2015.
 */
public class DrawerItemsLister {
    public static final int ADD_NEW_CITY = 1;
    public static final int SETTINGS = 2;
    public static final int ABOUT_THIS_APP = 3;

    public static final int ITEM_CITY_MASK = 0x100;

    private static HashMap createOrganizingItem(int type) {
        HashMap item = new HashMap();

        item.put("id", -1);
        item.put("type", type);

        return item;
    }

    private static HashMap createSimpleMenuItem(int uniqueID, String name) {
        HashMap item = new HashMap();

        item.put("id", uniqueID);
        item.put("name", name);
        item.put("type", AdapterDrawerMenuRecyclerView.ITEM_SIMPLE);

        return item;
    }

    private static HashMap createSecondaryMenuItem(int uniqueID, String name) {
        HashMap item = new HashMap();

        item.put("id", uniqueID);
        item.put("name", name);
        item.put("type", AdapterDrawerMenuRecyclerView.ITEM_SECONDARY);

        return item;
    }

    private static HashMap createMenuItemWithIcon(int uniqueID, String name, int drawableID) {
        HashMap item = new HashMap();

        item.put("id", uniqueID);
        item.put("name", name);
        item.put("icon", drawableID);
        item.put("type", AdapterDrawerMenuRecyclerView.ITEM_WITH_ICON);

        return item;
    }

    public static List<Map<String, ?>> createDrawerList(Context context, ArrayList<String> cityList) {
        ArrayList<Map<String, ?>> list = new ArrayList<Map<String, ?>>();

        list.add(createOrganizingItem(AdapterDrawerMenuRecyclerView.SPACER));
        list.add(createOrganizingItem(AdapterDrawerMenuRecyclerView.SPACER));

        list.add(createSimpleMenuItem(-1, context.getString(R.string.my_cities) + ":"));

        for (int i = 0; i < cityList.size(); i++)
            list.add(createSecondaryMenuItem(ITEM_CITY_MASK | i, cityList.get(i)));

        list.add(createSecondaryMenuItem(ADD_NEW_CITY, context.getString(R.string.add_new_city)));

        list.add(createOrganizingItem(AdapterDrawerMenuRecyclerView.LINE_SEPARATOR));

        list.add(createMenuItemWithIcon(SETTINGS, context.getString(R.string.settings), R.drawable.gear_dark));

        list.add(createOrganizingItem(AdapterDrawerMenuRecyclerView.LINE_SEPARATOR));

        list.add(createSimpleMenuItem(ABOUT_THIS_APP, context.getString(R.string.about_this_app)));

        return list;
    }
}