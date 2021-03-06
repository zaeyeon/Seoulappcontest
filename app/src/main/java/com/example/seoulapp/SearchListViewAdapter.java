package com.example.seoulapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SearchListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<SearchListViewItem> SearchListViewItemList = new ArrayList<SearchListViewItem>();

    // ListViewAdapter의 생성자
    public SearchListViewAdapter() {

    }


    @Override
    public int getCount() {
        return SearchListViewItemList.size();
    }

    //position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "SearchListView_item" Layout를 inflate하여 convertView 참조 획득.
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.searchlistview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView searchProfileView = (ImageView)convertView.findViewById(R.id.searchedShopProfile);
        TextView searchNameView = (TextView)convertView.findViewById(R.id.searchedShopName);
        TextView searchBuildingView = (TextView)convertView.findViewById(R.id.searchedShopBuilding);
        TextView searchTypeView = (TextView)convertView.findViewById(R.id.searchedType);

        // Data Set(searchListViewItemList)에서 position에 위치한 데이터 참조 획득
        SearchListViewItem searchListViewItem = SearchListViewItemList.get(position);

       // SearchListViewItemList.clear();


        // 아이템 내 각 위젯에 데이터 반영
        Glide.with(context).load(searchListViewItem.getSearchProfile()).into(searchProfileView);
        searchBuildingView.setText(searchListViewItem.getSearchBuilding());
        searchTypeView.setText(searchListViewItem.getSearchType());
        searchNameView.setText(searchListViewItem.getSearchName());


        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return SearchListViewItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수
    public void addItem(String profile, String name, String building, String type) {
        SearchListViewItem item = new SearchListViewItem();

        item.setSearchProfile(profile);
        item.setSearchName(name);
        item.setSearchBuilding(building);
        item.setSearchType(type);

        SearchListViewItemList.add(item);
    }
}
