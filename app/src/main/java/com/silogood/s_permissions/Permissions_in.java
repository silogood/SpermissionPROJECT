package com.silogood.s_permissions;

import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ChoiDW on 2015-11-28.
 */
public class Permissions_in extends ExpandableListActivity implements Runnable {
    private static final String TAG = "Permissions";

    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String PACKAGENAME = "PackageName";
    private static final String SECURITYLEVEL = "Securitylevel";

    // Installed App Details
    private static final String SCHEME = "package";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    private int mDangerousColor;
    private int mDefaultTextColor;
    private  int mSignatureTextColor;

    private static final int PROGRESS_DIALOG = 0;
    private ProgressDialog mProgressDialog;

    private List<Map<String, String>> mGroupData;
    private List<List<Map<String, String>>> mChildData;
    private List<Map<String, String>> mGroupData_S;
    private List<List<Map<String, String>>> mChildData_S;

    private List<Map<String, ?>> Group_Child;                // 아마도 정렬을 위한 새로운 리스트겠지?

    private PackageManager mPm;

    PermissionSingleton PS;                                             //패키지매니저, 싱글톤을 불러옴

    AlertDialog alertDialog;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {                                             // 1. 오픈
        super.onCreate(savedInstanceState);

        mPm = getPackageManager();
        mDangerousColor = Color.RED;
        mGroupData = new ArrayList<Map<String, String>>();
        mChildData = new ArrayList<List<Map<String, String>>>();
        mGroupData_S = new ArrayList<Map<String, String>>();
        mChildData_S = new ArrayList<List<Map<String, String>>>();
        mDefaultTextColor = Color.DKGRAY;
        mSignatureTextColor = Color.YELLOW;
        PS = PermissionSingleton.getInstance();


        Intent x = getIntent();
        String num = x.getStringExtra("num");

        Log.v("","xxxxxxxsfaf" +num);


        //초기화 와 필요한값들을 불러들이는과정
        showDialog(PROGRESS_DIALOG);                                                                  // 2. 백그라운드 동작
    };

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                int childPosition, long id) {
        showInstalledAppDetails(this, (String) v.getTag());
        return super.onChildClick(parent, v, groupPosition, childPosition, id);
    }
    /// 해당 아이템 클릭시 ShowinstakkedAppDetails 함수실행
    // 말그대로 디테일정보 뿌려줌

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case PROGRESS_DIALOG:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.setCancelable(false);
                Thread thread = new Thread(this);
                thread.start();
                return mProgressDialog;
            default:
                return super.onCreateDialog(id, null);
        }
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            removeDialog(PROGRESS_DIALOG);
            PermissionAdapter mAdapter = new PermissionAdapter(
                    Permissions_in.this,
                    mGroupData_S,                                          ///그룹_S 를 연결 핸들러역할
                    R.layout.permissions_expandable_list_item,
                    new String[] { NAME, "pack"},                            //pack 을 주목  key값이들어감
                    new int[] { android.R.id.text1},
                    mChildData_S,                                                   //챠일드_S 를 연결 핸들러1
                    R.layout.permissions_expandable_list_item_child,
                    new String[] { NAME, DESCRIPTION },
                    new int[] { android.R.id.text1, android.R.id.text2 }

            );
            // Log.i("iver", "XXXXX2" + mGroupData);
            // Log.i("iver", "XXXXX3" + mChildData);
            setListAdapter(mAdapter);

        }
    };


    private class PermissionAdapter extends SimpleExpandableListAdapter {
        public PermissionAdapter(Context context, List<? extends Map<String, ?>> groupData,
                                 int groupLayout, String[] groupFrom, int[] groupTo,
                                 List<? extends List<? extends Map<String, ?>>> childData, int childLayout,
                                 String[] childFrom, int[] childTo) {
            super(context, groupData, groupLayout, groupFrom, groupTo, childData,
                    childLayout, childFrom, childTo);                             //어뎁터로 값을 어떻게 씌워줄건지 정의
        }

        @Override
        @SuppressWarnings("unchecked")
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, final ViewGroup parent) {
            final View v = super.getGroupView(groupPosition, isExpanded, convertView, parent);
            final Map<String, String> group = (Map<String, String>) getGroup(groupPosition);
            int secLevel = Integer.parseInt(group.get(SECURITYLEVEL));
            TextView textView = (TextView) v.findViewById(android.R.id.text1);
            Button des = (Button) v.findViewById(R.id.des);
            des.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Permissions_in.this);

                    builder.setTitle(group.get(NAME));
                    builder.setMessage(group.get(DESCRIPTION));
                    builder.setCancelable(false);


      /* 긍정의 의미를 갖는 버튼 */
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    });

                    builder.create();
                    builder.show();
                }
            });
            if (PermissionInfo.PROTECTION_DANGEROUS == secLevel) {
                textView.setTextColor(mDangerousColor);
            } else if (PermissionInfo.PROTECTION_SIGNATURE ==secLevel){
                textView.setTextColor(mSignatureTextColor);
            }else {
                textView.setTextColor(mDefaultTextColor);
            }


            return v;
        }
        // 부모그룹뷰 어떻게보여줄건지
        @Override
        @SuppressWarnings("unchecked")
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            final View v = super.getChildView(groupPosition, childPosition, isLastChild,
                    convertView, parent);
            ImageView imageView = (ImageView) v.findViewById(android.R.id.icon);
            Map<String, String> child =
                    (Map<String, String>)getChild(groupPosition, childPosition);
            Drawable icon;
            String packageName = (String)child.get(PACKAGENAME);

            try {
                icon = mPm.getApplicationIcon(packageName);
            } catch (PackageManager.NameNotFoundException e) {
                icon = mPm.getDefaultActivityIcon();
            }
            imageView.setImageDrawable(icon);
            v.setTag(packageName);
            return v;
        }
    }                                                                                                     //자식 그룹뷰 어떻게 보여줄건지

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }                                                                                                          // 플레그랑 시스템앱인지 판별을 하기위해 0 이아니면  트루   아니면 퍼스르줌


    public void run() {                                                               // 3.  쓰레드 동작

        mGroupData = PS.getmGroupData();                                          //싱글톤에 저장해놓은 그룹데이터랑 차일드 데이터를 받아옴
        mChildData = PS.getmChildData();
        // String b[] ={"android.permission.SEND_SMS","android.permission.REAED_SMS","android.permission.CALL_PHONE","android.permission.WRITE_EXTERNAL_STORAGE"
        // ,"android.permission.READ_CONTACTS","android.permission.READ_CONTACTS","android.permission.WRITE_CPMTACTS","android.permission.INTERNET","android.permission.CHANGE_WIFI_STATE",
        //       "android.permission.ACCESS_WIFI_STATE", "android.permission.VIBRATE","android.permission.WAKE_LOCK","android.permission.RECEIVE_BOOT_COMPLETED"} ;


        for(int i=0; i<mGroupData.size();i++) {                                         //      0부터 그룹데이터의 총데이터길이를 돌리면서



            if(mGroupData.get(i).get("pack").equals("android.permission.SEND_SMS")
                    || mGroupData.get(i).get("pack").equals("android.permission.READ_SMS")
                    || mGroupData.get(i).get("pack").equals("android.permission.WRITE_EXTERNAL_STORAGE")
                    || mGroupData.get(i).get("pack").equals("android.permission.RECEIVE_SMS")
                    || mGroupData.get(i).get("pack").equals("android.permission.WRITE_SMS")
                    || mGroupData.get(i).get("pack").equals("android.permission.CALL_PHONE")
                    || mGroupData.get(i).get("pack").equals("android.permission.READ_PHONE_STATE")
                    || mGroupData.get(i).get("pack").equals("android.permission.WRITE_EXTERNAL_STORAGE")
                    || mGroupData.get(i).get("pack").equals("android.permission.ACCESS_COARSE_LOCATION")
                    || mGroupData.get(i).get("pack").equals("android.permission.ACCESS_FINE_LOCATION")
                    || mGroupData.get(i).get("pack").equals("android.permission.READ_CONTACTS")
                    || mGroupData.get(i).get("pack").equals("android.permission.WRITE_CONTACTS")
                    || mGroupData.get(i).get("pack").equals("android.permission.INTERNET")
                    || mGroupData.get(i).get("pack").equals("android.permission.CHANGE_WIFI_STATE")
                    || mGroupData.get(i).get("pack").equals("android.permission.ACCESS_WIFI_STATE")
                    || mGroupData.get(i).get("pack").equals("android.permission.VIBRATE")
                    || mGroupData.get(i).get("pack").equals("android.permission.WAKE_LOCK")
                    || mGroupData.get(i).get("pack").equals("android.permission.RECEIVE_BOOT_COMPLETED")
                    )
            {
                Map<String, String> smsG;
                List<Map<String,String>> smsC;
                smsG =mGroupData.get(i);
                smsC=mChildData.get(i);

                mGroupData_S.add(smsG);
                mChildData_S.add(smsC);

               // mGroupData.get(i).get(DESCRIPTION);
               // Log.v("","XXXXXXXXXX" + mGroupData);

            }
        }



        handler.sendEmptyMessage(0);
    }

    public static void showInstalledAppDetails(Context context, String packageName) {                  // 앱 정보 페이지 들어가기
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // above 2.3
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // below 2.3
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }
}
