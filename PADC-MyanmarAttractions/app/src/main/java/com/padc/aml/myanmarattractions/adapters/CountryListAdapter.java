package com.padc.aml.myanmarattractions.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import com.padc.aml.myanmarattractions.MyanmarAttractionsApp;
import com.padc.aml.myanmarattractions.R;
import com.padc.aml.myanmarattractions.views.items.ViewItemCountry;

/**
 * Created by aung on 7/15/16.
 */
public class CountryListAdapter extends BaseAdapter {

    private List<String> mCountryList;
    private LayoutInflater mInflater;

    public CountryListAdapter(List<String> countryList) {
        if (countryList != null) {
            this.mCountryList = countryList;
        } else {
            this.mCountryList = new ArrayList<>();
        }
        mInflater = LayoutInflater.from(MyanmarAttractionsApp.getContext());
    }

    @Override
    public int getCount() {
        return mCountryList.size();
    }

    @Override
    public String getItem(int position) {
        return mCountryList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_item_country, parent, false);
        }

        if (convertView instanceof ViewItemCountry) {
            ViewItemCountry viCountry = (ViewItemCountry) convertView;
            viCountry.setData(getItem(position));
        }

        return convertView;
    }
}