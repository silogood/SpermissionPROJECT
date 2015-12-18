package com.silogood.s_permissions;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChoiDW on 2015-11-28.
 */
public class Applications_NO_SYSTEM extends Fragment {

    public Applications_NO_SYSTEM() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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

        List<Recycler_item> No_System_items;
        PermissionSingleton PS = PermissionSingleton.getInstance();
        No_System_items = PS.getNo_System_items();        // 싱글톤에서 시스템 아이템값들을 뺴옴


        recyclerView.setAdapter(new RecyclerAdapter(getActivity().getApplicationContext(), No_System_items, R.layout.applications));

        return v;
    }
}
