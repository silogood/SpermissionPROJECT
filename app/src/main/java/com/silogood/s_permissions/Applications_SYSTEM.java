package com.silogood.s_permissions;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ChoiDW on 2015-11-28.
 */
public class Applications_SYSTEM extends Fragment {

    public Applications_SYSTEM() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

     Log.v("","xxxx" +id);


        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.applications, container, false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.RecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        ///리사이클뷰를 생성

        List<Recycler_item> System_items;
        PermissionSingleton PS = PermissionSingleton.getInstance();
        System_items = PS.getSystem_items();        // 싱글톤에서 시스템 아이템값들을 뺴옴


        recyclerView.setAdapter(new RecyclerAdapter(getActivity().getApplicationContext(), System_items, R.layout.applications));

        return v;

    }
}