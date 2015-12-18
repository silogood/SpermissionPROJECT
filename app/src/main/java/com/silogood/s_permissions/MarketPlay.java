package com.silogood.s_permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by silogood on 2015-11-21.
 */

public class MarketPlay extends Activity {
    private Context context;


    private PackageManager mPm;

    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String PACKAGENAME = "PackageName";
    private static final String SECURITYLEVEL = "Securitylevel";
    private static final String TAG = "Permissions";
    private List<Map<String, String>> mGroupData;
    private List<List<Map<String, String>>> mChildData;
    private ListView permissionList;

    private int mDangerousColor;
    private int mDefaultTextColor;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main3);
        mPm = getPackageManager();
        Intent i = getIntent();
        String title = i.getStringExtra("title");
        String PackageName = title.substring(8);


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




            try {
                pi = mPm.getPackageInfo(PackageName, PackageManager.GET_META_DATA);

                try {
                    icon = mPm.getApplicationIcon(PackageName);                             /////icon  빼기

                } catch (PackageManager.NameNotFoundException e) {
                    icon = mPm.getDefaultActivityIcon();
                }

                ImageView imageView = (ImageView) findViewById(android.R.id.icon);
                imageView.setImageDrawable(icon);

                AppName = pi.packageName;
                packageVersionCode = pi.versionCode;
                packageVersionName = pi.versionName;
                Log.v("XXXX7", "name:     " + packageVersionName);

                ai = mPm.getApplicationInfo(PackageName, PackageManager.GET_META_DATA);     ////라벨 빼오기
                String labelName = mPm.getApplicationLabel(ai).toString();

                ((TextView) findViewById(R.id.curAppName)).setText(labelName); //Appname layout 연결
                ((TextView) findViewById(R.id.packagea)).setText(AppName); //Appname layout 연결
                ((TextView) findViewById(R.id.curAppversion)).setText(packageVersionCode + " / " + packageVersionName);


            } catch (Exception ex) {
                packageVersionCode = 0;
                packageVersionName = "n/a";
                //Log.e("PM", "Error fetching app version");
            }


///////////////////////////////////////////////////////////////////////////////////////////////////////////
            try {
                pi = mPm.getPackageInfo(PackageName, PackageManager.GET_PERMISSIONS);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


            Map<String, String> curGroupMap = new HashMap<String, String>();
            int count = 0;
            try {
                for (String key : pi.requestedPermissions) {
                    if (key.startsWith("android.permission.")) count++;
                }
                curGroupMap.put(NAME, applicationLabel + "(" + count + ")");
                Log.v("XXXX7", "count :     " + count);
            } catch (NullPointerException e) {
                curGroupMap.put(NAME, applicationLabel + "(" + 0 + ")");
                Log.v("XXXX7", "///count :     " + count);
            }
            curGroupMap.put(DESCRIPTION, packageVersionName);
            Log.v("XXXX7", "Description  :     " + packageVersionName);
            curGroupMap.put(PACKAGENAME, PackageName);
            Log.v("XXXX7", "packageName  :     " + PackageName);


            mChildData.clear();
            mGroupData.clear();
            mGroupData.add(curGroupMap);

            List<Map<String, String>> children = new ArrayList<Map<String, String>>();
            try {
                for (String key : pi.requestedPermissions) {
                    try {
                        Log.v("XXXXXXXXXXXXXXXXXXXXXXX", "    " );
                        if (!(key.startsWith("android.permission."))) continue;
                        Log.d("BBB", key);
                        PermissionInfo pinfo =
                                mPm.getPermissionInfo(key, PackageManager.GET_META_DATA);

                        Map<String, String> curChildMap = new HashMap<String, String >();
                        CharSequence label = pinfo.loadLabel(mPm);
                        CharSequence desc = pinfo.loadDescription(mPm);
                        curChildMap.put(NAME, (label == null) ? pinfo.name : label.toString());
                        curChildMap.put(DESCRIPTION, (desc == null) ? "" : desc.toString() +  " / "   + "(" + key + ")");
                        curChildMap.put(SECURITYLEVEL, String.valueOf(pinfo.protectionLevel));
                        children.add(curChildMap);
                       // int pro = pinfo.protectionLevel ;


                    } catch (PackageManager.NameNotFoundException e) {
                        Log.i(TAG, "Ignoring unknown permission ");
                        continue;
                    }
                }
            } catch (NullPointerException e) {
                Log.i(TAG, "Ignoring unknown permission ");
            }
            mChildData.add(children);

        int ad = pi.requestedPermissions.length ;
        Log.v("XXXXXXXXXXXXXXXXXXXXX1", "    " + children.get(0).get("Securitylevel"));

            SimpleAdapter adapter = new SimpleAdapter(this, children, R.layout.permission_list_item, new String[]{"", "Name", "Description"}, new int[]{R.id.listviewpermissiontext,R.id.text1, R.id.text2});

        if(pi.requestedPermissions != null) {
            permissionList = (ListView) findViewById(R.id.permissionList);

            permissionList.setAdapter(adapter);


        } else {
            ((TextView) findViewById(R.id.iff)).setText(" App 의 요구 권한 이 없습니다 ^^  ");
        }



            Log.v("BBB", "" + mChildData);


            // permissionList = (ListView)findViewById(R.id.permissionL);
            // permissionList.setAdapter(adapter);    ///권한들 .. layout


        }




    }








