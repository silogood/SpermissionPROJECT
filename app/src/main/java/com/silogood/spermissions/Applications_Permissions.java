package com.silogood.s_permissions;

import android.app.Activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 7217-182 on 2015-12-01.
 */
public class Applications_Permissions extends Activity {

    private Context context;
    private ImageButton manageButton;

    private PackageManager mPm;

    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String PACKAGENAME = "PackageName";
    private static final String SECURITYLEVEL = "Securitylevel";
    private static final String PERMISSION = "permission";
    private static final String TAG = "Permissions";
    private List<Map<String, String>> mGroupData;
    private List<List<Map<String, String>>> mChildData;    // 똑같이 그룹 차일드를 받을 변수지정
    private ExpandableListView permissionList;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.applications_permissions);
        mPm = getPackageManager();                          //패키지매니저를 열어줌
        Intent i = getIntent();                              // 인텐트를 받아서
        String title = i.getStringExtra("PackageName");           // title 이라는 String 변수값에  PackageName 으로 넘어온 인텐트값을 저장
        //final String PackageName = title.substring(8);            // 이부분은 브로드캐스트 받으면 앞에 8글자가 딸려와서 그거 제외해주는코드
        final String PackageName = title;

        ExpandableListView list = (ExpandableListView) findViewById(R.id.permissionList);
        mGroupData = new ArrayList<Map<String, String>>();
        mChildData = new ArrayList<List<Map<String, String>>>();
        String permissionName;
        String applicationLabel;
        String packageName;
        PackageInfo pi = null;
        ApplicationInfo ai = null;
        applicationLabel = PackageName;
        int packageVersionCode;
        String packageVersionName;
        String AppName;
        Drawable icon;
        long lastUpdate;
        int system;
        int install;
       ///정렬 및 초기화

        try {
            pi = mPm.getPackageInfo(PackageName, PackageManager.GET_META_DATA);   //패키지 인포값을 패키지네임을 통해 얻어옴

            try {
                icon = mPm.getApplicationIcon(PackageName);                             /////icon  빼기

            } catch (PackageManager.NameNotFoundException e) {
                icon = mPm.getDefaultActivityIcon();
            }

            ImageView imageView = (ImageView) findViewById(android.R.id.icon);
            imageView.setImageDrawable(icon);
                                                                                                //이미지뷰에 아이콘값 뿌려줌
            AppName = pi.packageName;
            packageVersionCode = pi.versionCode;
            packageVersionName = pi.versionName;                                        //패키지 버전 코드 등등을 받음

            ai = mPm.getApplicationInfo(PackageName, PackageManager.GET_META_DATA);     ////라벨 빼오기
            String labelName = mPm.getApplicationLabel(ai).toString();

            ((TextView) findViewById(R.id.curAppName)).setText(labelName); //Appname layout 연결
            ((TextView) findViewById(R.id.packagea)).setText(AppName); //Appname layout 연결
            ((TextView) findViewById(R.id.curAppversion)).setText(packageVersionCode + " / " + packageVersionName);   //코드 네임등을 연결


        } catch (Exception ex) {
            packageVersionCode = 0;
            packageVersionName = "n/a";
            //Log.e("PM", "Error fetching app version");
        }

        manageButton = (ImageButton)findViewById(R.id.application_detail_manage_button);      //메뉴버튼임
        manageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 9) {                                                //sdk 버전에따라서 실행시켜주는  uri값들이 다름
                    try {
                        Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        i.setData(Uri.parse("package:" + PackageName));
                        startActivity(i);
                    } catch (ActivityNotFoundException anfe) {
                        Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivity(i);
                    }
                } else {

                }

            }
        });

        try {
            pi = mPm.getPackageInfo(PackageName, PackageManager.GET_PERMISSIONS); // 패키지인포에서 퍼미션들을 불러옴  pi로 저장
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        mChildData.clear();
        mGroupData.clear();



        try {
            if (pi.requestedPermissions != null) {                    ///파이의 리퀘스트값들이 있으면 아래를 수행
                for (String key : pi.requestedPermissions) {                            //key 값을 pi  의 요구퍼미션들을 하나하나  돌려서
                    try {
                        if (!(key.startsWith("android.permission."))) continue;           // key 값이안드로이드로시작되는 구분이면 계속

                        PermissionInfo pinfo =
                                mPm.getPermissionInfo(key, PackageManager.GET_META_DATA);  //pinfo 값에 key값에 해당하는 퍼미션 인포값을 저장!!!!
                        Map<String, String> curGroupMap = new HashMap<String, String>();
                        Map<String, String> curChildMap = new HashMap<String, String>();
                        List<Map<String, String>> children = new ArrayList<Map<String, String>>();    //현재 돌고있는 그룹, 차일드맵을 만들고
                        CharSequence label = pinfo.loadLabel(mPm);                                     //라벨과 디스크립션을 추출하여 저장
                        CharSequence desc = pinfo.loadDescription(mPm);

                        curGroupMap.put(NAME, (label == null) ? pinfo.name : label.toString());
                        Log.d("AAA", label.toString());
                        curGroupMap.put(SECURITYLEVEL, String.valueOf(pinfo.protectionLevel));
                        curChildMap.put(PERMISSION, key);
                        curChildMap.put(DESCRIPTION, (desc == null) ? "" : desc.toString());
                        curChildMap.put(SECURITYLEVEL, String.valueOf(pinfo.protectionLevel));
                        children.add(curChildMap);
                                                                                                    ///차일드값에 저장해버림 퍼미션들 , 레벨 이름 등등
                        mGroupData.add(curGroupMap);
                        Log.d("CCC", "" + mGroupData);
                        mChildData.add(children);                                                   /// 그룹과 차일드에 해당값들을 저장한다
                        Log.d("DDD", "" + mChildData);


                    } catch (PackageManager.NameNotFoundException e) {
                        Log.i(TAG, "Ignoring unknown permission ");
                        continue;
                    }
                }
            } else {
                ((TextView) findViewById(R.id.iff)).setText(" App 의 요구 권한 이 없습니다 ^^  ");    //파이의 요구권한이없을시 띄워줌

//                mGroupData.add(curGroupMap);
//                mChildData.add(children);
            }
        } catch (NullPointerException e) {
            Log.i(TAG, "Ignoring unknown permission ");
        }


        PermissionAdapter mAdapter = new PermissionAdapter(
                Applications_Permissions.this, mGroupData,                  //어뎁터를 생성 해서 그룹데이터에 연결
                R.layout.marketplay_item,                                   // 마켓플레이 아이템-
                new String[]{NAME},                                          //이름값을생성
                new int[]{R.id.text1},
                mChildData,
                R.layout.marketplay_item_child,
                new String[]{DESCRIPTION,PERMISSION},
                new int[]{R.id.text1,R.id.text2}
                                                                        //차일드를 연결 디스크립션과 퍼미션명을 연결
        );
        list.setAdapter(mAdapter);

        Log.v("BBB", "" + mChildData);



    }


    private class PermissionAdapter extends SimpleExpandableListAdapter {
        public PermissionAdapter(Context context, List<? extends Map<String, ?>> groupData,
                                 int groupLayout, String[] groupFrom, int[] groupTo,
                                 List<? extends List<? extends Map<String, ?>>> childData, int childLayout,
                                 String[] childFrom, int[] childTo) {
            super(context, groupData, groupLayout, groupFrom, groupTo, childData,
                    childLayout, childFrom, childTo);
        }         ///알아서해석ㅎ ....ㅐ.................

        @Override
        @SuppressWarnings("unchecked")
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            final View v = super.getGroupView(groupPosition, isExpanded, convertView, parent);
            Map<String, String> group = (Map<String, String>) getGroup(groupPosition);
            int secLevel=0;
            if(!(group.get(SECURITYLEVEL)==null)) secLevel = Integer.parseInt(group.get(SECURITYLEVEL));
            TextView textView = (TextView) v.findViewById(R.id.text1);
            if (PermissionInfo.PROTECTION_DANGEROUS == secLevel) {
                textView.setTextColor(Color.RED);
            } else {
                textView.setTextColor(Color.BLACK);             //// 그룹의 뷰를 나타내주는곳  시큐리티 레벨을 받아서 색칠해줌
            }
            return v;
        }

        @Override
        @SuppressWarnings("unchecked")
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            final View v = super.getChildView(groupPosition, childPosition, isLastChild,
                    convertView, parent);
            Map<String, String> child =
                    (Map<String, String>) getChild(groupPosition, childPosition);
                                                                        ////자식뷰를 나타냄
            return v;
        }
    }





}
