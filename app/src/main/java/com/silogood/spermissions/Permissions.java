package com.silogood.s_permissions;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;


/**
 * Created by ChoiDW on 2015-11-28.
 */
public class Permissions extends AppCompatActivity{    // Runnable 로 쓰레드를 백그라운드에서 데이터처리를 위해 돌림

    private static final String TAG = "Permissions";

    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String PACKAGENAME = "PackageName";
    private static final String SECURITYLEVEL = "Securitylevel";

    private int mDangerousColor;
    private int mDefaultTextColor = Color.DKGRAY;
    private  int mSignatureTextColor;

    private static final int PROGRESS_DIALOG = 0;
    private ProgressDialog mProgressDialog;

    private List<Map<String, String>> mGroupData;
    private List<List<Map<String, String>>> mChildData;
    private List<Map<String, String>> mGroupData_S;
    private List<List<Map<String, String>>> mChildData_S;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private PackageManager mPm;   //패키지매니저 저장

    PermissionSingleton PS;                                //싱글톤 객체를 이용함


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permissions_tab);
        getSupportActionBar().setElevation(0);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Load task = new Load();
        task.execute();

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PieChartActivity(), "BIG5");
        adapter.addFragment(new HorizontalBarChartActivity(), "TOP18");
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public class Load extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog dialog1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog1 = new ProgressDialog(Permissions.this);

            // 다이얼로그에 표시할 메시지
            // (이 외에 타이틀, 아이콘, 버튼 등을 추가할 수 있음)
            dialog1.setMessage("Loading...");
            dialog1.setCancelable(false);

            // 다이얼로그를 화면에 표시하기
            dialog1.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            mPm = getPackageManager();

            // mDangerousColor = getResources().getColor(R.color.perms_dangerous_grp_color);
            mGroupData = new ArrayList<Map<String, String>>();
            mChildData = new ArrayList<List<Map<String, String>>>();
            mGroupData_S = new ArrayList<Map<String, String>>();
            mChildData_S = new ArrayList<List<Map<String, String>>>();
            mDefaultTextColor = Color.DKGRAY;
            PS = PermissionSingleton.getInstance();

            List<PackageInfo> appList =
                    mPm.getInstalledPackages(PackageManager.GET_PERMISSIONS);                // appList 에  packageManager 를통해 인스톨된 패키지의 퍼미션값들과 정보를저장
            Map<String, List<PackageInfo>> permList = new TreeMap<String, List<PackageInfo>>();
            // Loop through all installed packaged to get a list of used permissions_tab and PackageInfos
            for (PackageInfo pi : appList) {                                                  //  하나하나 값들을 비교하면서 For 문돌림

                // Do not add System Packages
                if (pi.requestedPermissions == null || pi.packageName.equals("android")) {       //pi값 중 퍼미션요구가 Null값이거나 android 로시작하는값이면 컨티뉴
                    continue;
                }
                for (String perms : pi.requestedPermissions) {
                    //perms  를 다시 pi 의 퍼미션요구값에 맞춰 돌림
                    if (!permList.containsKey(perms)) {                                                 // permList PackageInfo 가 저장된 트리에받아온 요구퍼미션값을 찾아서
                        // First time we get this permission so add it and create a new List
                        permList.put(perms, new ArrayList<PackageInfo>());                             //permList 에 새로운값을 추가해줌  (없는값들 찾거나해서 다추가한다고생각하면댐)
                    }
                    permList.get(perms).add(pi);                                                       //ㅇㅇ 이하동문
                }
            }
            appList.clear();

            Set<String> keys = permList.keySet();

            String sLevel;

            for (String key : keys) {

                Map<String, String> curGroupMap = new HashMap<String, String>();
                try {
                    PermissionInfo pinfo =
                            mPm.getPermissionInfo(key, PackageManager.GET_META_DATA);             // key값(요구퍼미션전체이름) 을 이용해서 퍼미션인포값을 뺴내어 pinfo에 저장
                    CharSequence label = pinfo.loadLabel(mPm);                                     // 라벨 , 디스크립션같은 값들 따로빼냄
                    CharSequence desc = pinfo.loadDescription(mPm);
                    curGroupMap.put("pack", key);                                                       // 키값을( "pack" ) 을 따라 저장해서 그룹맵에 추가
                    sLevel = String.valueOf(pinfo.protectionLevel);
                    curGroupMap.put(SECURITYLEVEL, sLevel);
                    curGroupMap.put(NAME, (label == null) ? pinfo.name : label.toString());            // 라벨을 추출해서 스트링형태로 추가
                    curGroupMap.put(DESCRIPTION, (desc == null) ? "" : desc.toString());            //디스크립션도 또한 ..

                    // 레벨도 일단 받아와서 추가해둠
                } catch (NameNotFoundException e) {
                    Log.i(TAG, "Ignoring unknown permission " + key);
                    continue;
                }
                mGroupData.add(curGroupMap);                                       //그값들을 mGroupData 에 저장해줌


                List<Map<String, String>> children = new ArrayList<Map<String, String>>();
                List<PackageInfo> infos = permList.get(key);                        // 그룹( 퍼미션리스트 ) 에 맞는 키값들을 전부 infos 값에 저장
                // 그룹값을 얻었으니 그에맞는 자식값들을 뽑기위해 리스트생성

                for (PackageInfo child : infos) {                                 // 패키지인포 child 를 infos 에 맞게 하나하나돌림
                    Map<String, String> curChildMap = new HashMap<String, String>();          //현재 돌고있는 챠일드를 위한 맵을 생성
                    String appName = (child.applicationInfo == null) ?
                            child.packageName : child.applicationInfo.loadLabel(mPm).toString();
                    curChildMap.put(NAME, appName);
                    curChildMap.put(DESCRIPTION, child.versionName);
                    curChildMap.put(PACKAGENAME, child.packageName);                   //////정보를 담음
                    curChildMap.put(SECURITYLEVEL, sLevel);
                    children.add(curChildMap);                                               // 현재돌고있는값을   퍄일드맵으로 이전
                }
                mChildData.add(children);                                     // mChildData 에 저장
            }


//        for(int i=0; i<mGroupData.size();i++) {
//
//            if(mGroupData.get(i).get("pack").equals("android.permission.INTERNET") ||
//                    mGroupData.get(i).get("pack").equals("android.permission.CAMERA")) {
//                Map<String, String> zzz;
//                List<Map<String, String>> yyy;
//                zzz = mGroupData.get(i);
//                yyy = mChildData.get(i);
//                mGroupData_S.add(zzz);
//                mChildData_S.add(yyy);
//            }
//        }

            PS.setmGroupData(mGroupData);
            PS.setmChildData(mChildData);       // 싱글톤 객체에 지금까지 진행된 정보들을 저장시킴 ( 전역변수로 쓰기위해서 싱글톤에 저장 )

//        mGroupData.clear();
//        mChildData.clear();




            permList.clear();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            viewPager = (ViewPager) findViewById(R.id.viewpager);
            setupViewPager(viewPager);

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);

            dialog1.dismiss();
        }
    }
}
