package com.silogood.s_permissions;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 7217-182 on 2015-12-01.
 */
public class Applications_Permissions extends Activity implements View.OnClickListener {

    private final String URL = "http://spermission.dothome.co.kr/index.php";
    AsyncHttpClient client;
    HttpResponse httpResponse;

    private ImageButton manageButton;
    private PackageManager mPm;
    private List<Map<String, String>> mGroupData;
    private List<List<Map<String, String>>> mChildData;    // 똑같이 그룹 차일드를 받을 변수지정

    Button like;
    Button hate;
    Button comment;
    TextView like_tv;
    TextView hate_tv;

    String google_id;
    boolean like_hate;
    String package_app_name;
    String package_name;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.applications_permissions);
        mPm = getPackageManager();                          //패키지매니저를 열어줌
        Intent i = getIntent();                              // 인텐트를 받아서
        final String PackageName = i.getStringExtra("PackageName");           // title 이라는 String 변수값에  PackageName 으로 넘어온 인텐트값을 저장
        package_name = PackageName;
        //final String PackageName = title.substring(8);            // 이부분은 브로드캐스트 받으면 앞에 8글자가 딸려와서 그거 제외해주는코드

        like = (Button) findViewById(R.id.like);
        hate = (Button) findViewById(R.id.hate);
        comment = (Button) findViewById(R.id.comment);
        like.setOnClickListener(this);
        hate.setOnClickListener(this);
        comment.setOnClickListener(this);
        like_tv = (TextView) findViewById(R.id.like_tv);
        hate_tv = (TextView) findViewById(R.id.hate_tv);
        client = new AsyncHttpClient();
        httpResponse = new HttpResponse();

        // 구글ID 받아오기
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            String possibleEmail = account.name;
            String type = account.type;
            if (type.equals("com.google")) {
                google_id = possibleEmail;
                break;
            }
        }

        load();

        ExpandableListView list = (ExpandableListView) findViewById(R.id.permissionList);
        mGroupData = new ArrayList<Map<String, String>>();
        mChildData = new ArrayList<List<Map<String, String>>>();
        PackageInfo pi = null;
        ApplicationInfo ai;
        int packageVersionCode;
        String packageVersionName;
        String AppName;
        Drawable icon;

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
            package_app_name = labelName;
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

                        curGroupMap.put("Name", (label == null) ? pinfo.name : label.toString());
                        Log.d("AAA", label.toString());
                        curGroupMap.put("Securitylevel", String.valueOf(pinfo.protectionLevel));
                        curChildMap.put("permission", key);
                        curChildMap.put("Description", (desc == null) ? "" : desc.toString());
                        curChildMap.put("Securitylevel", String.valueOf(pinfo.protectionLevel));
                        children.add(curChildMap);
                        ///차일드값에 저장해버림 퍼미션들 , 레벨 이름 등등
                        mGroupData.add(curGroupMap);
                        Log.d("CCC", "" + mGroupData);
                        mChildData.add(children);                                                   /// 그룹과 차일드에 해당값들을 저장한다
                        Log.d("DDD", "" + mChildData);


                    } catch (PackageManager.NameNotFoundException e) {
                        Log.i("Application_Permissions", "Ignoring unknown permission ");
                        continue;
                    }
                }
            } else {
                ((TextView) findViewById(R.id.iff)).setText(" There is No Permisssions ");    //파이의 요구권한이없을시 띄워줌
            }
        } catch (NullPointerException e) {
            Log.i("Application_Permissions", "Ignoring unknown permission ");
        }


        PermissionAdapter mAdapter = new PermissionAdapter(
                Applications_Permissions.this,
                mGroupData,                  //어뎁터를 생성 해서 그룹데이터에 연결
                R.layout.marketplay_item,                                   // 마켓플레이 아이템-
                new String[]{ "Name" },                                          //이름값을생성
                new int[]{R.id.text1},
                mChildData,
                R.layout.marketplay_item_child,
                new String[]{ "Description", "permission"},
                new int[]{R.id.text1,R.id.text2}
                //차일드를 연결 디스크립션과 퍼미션명을 연결
        );
        list.setAdapter(mAdapter);
    }


    private class PermissionAdapter extends SimpleExpandableListAdapter {
        public PermissionAdapter(Context context, List<? extends Map<String, ?>> groupData,
                                 int groupLayout, String[] groupFrom, int[] groupTo,
                                 List<? extends List<? extends Map<String, ?>>> childData, int childLayout,
                                 String[] childFrom, int[] childTo) {
            super(context, groupData, groupLayout, groupFrom, groupTo, childData,
                    childLayout, childFrom, childTo);
        }

        @Override
        @SuppressWarnings("unchecked")
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            final View v = super.getGroupView(groupPosition, isExpanded, convertView, parent);
            Map<String, String> group = (Map<String, String>) getGroup(groupPosition);
            int secLevel=0;
            if(!(group.get("Securitylevel")==null)) secLevel = Integer.parseInt(group.get("Securitylevel"));
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

    public void load() {
        RequestParams params = new RequestParams();
        params.put("google_id", google_id);
        params.put("package_app_name", package_app_name);
        params.put("package_name", package_name);
        params.put("process","load");

        client.post(URL, params, httpResponse);
    }


    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.like: case R.id.hate:

            if(v.getId()==R.id.like) like_hate = true;
            if(v.getId()==R.id.hate) like_hate = false;

            RequestParams params = new RequestParams();
            params.put("google_id", google_id);
            params.put("like_hate", like_hate);
            params.put("package_app_name", package_app_name);
            params.put("package_name", package_name);
            params.put("process","send");

            client.post(URL, params, httpResponse);

            break;
            case R.id.comment:
                Intent intent = new Intent(Applications_Permissions.this, comment_list.class);
                intent.putExtra("google_id", google_id);
                intent.putExtra("package_app_name", package_app_name);
                intent.putExtra("package_name", package_name);
                startActivity(intent);
                break;
        }

    }

    /* 통신 결과를 받아서 처리할 클래스 - inner class 형태로 정의한다. */
    public class HttpResponse extends JsonHttpResponseHandler {

        ProgressDialog dialog;

        /* 통신은 이루어 졌으나 서버에서 에러코드를 반환할 경우 호출된다.
         * @param stateCode	상태코드 (HTTP 상태코드 값이 전달된다. 404, 500)
         * @param header	HTTP Header
         * @param error		에러정보 객체
         */
        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            // TODO Auto-generated method stub
            String errMsg = "State Code: " + statusCode + "\n";
            errMsg += "Error Message: " + throwable.getMessage();


//            Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_LONG).show();
            super.onFailure(statusCode, headers, responseString, throwable);
        }

        /* 통신 접속 실패시 호출된다.
         * @param stateCode	상태코드 (0이 전달된다.)
         * @param header	HTTP Header
         * @param error		에러정보 객체
         */
        @Override
        public void onFailure(int statusCode, Header[] headers,
                              Throwable throwable, JSONObject errorResponse) {
            // TODO Auto-generated method stub
            //           String errMsg = "State Code: " + statusCode + "\n";
            //           errMsg += "Error Message: " + throwable.getMessage();

//			tv1.setText(errMsg);
            Toast.makeText(getApplicationContext(), "Please check your Internet connection.", Toast.LENGTH_SHORT).show();
            super.onFailure(statusCode, headers, throwable, errorResponse);
        }

        /* 통신 성공시 호출된다.
         * @param stateCode	상태코드
         * @param header	HTTP Header
         * @param response	서버의 응답 내용
         */
        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              JSONObject response) {
            // TODO Auto-generated method stub
            super.onSuccess(statusCode, headers, response);

            try {

                JSONArray results = response.getJSONArray("products");
                JSONObject re = results.getJSONObject(0);
                int like = re.getInt("like");
                int hate = re.getInt("hate");
                int msg = re.getInt("msg");
                if(msg==10) Toast.makeText(Applications_Permissions.this, "Changed!", Toast.LENGTH_LONG).show();
                if(msg==11) Toast.makeText(Applications_Permissions.this, "Already Checked!", Toast.LENGTH_LONG).show();
                like_tv.setText(""+like);
                hate_tv.setText("" + hate);

                //			tv1.setText(statusCode+"/"+name+"/"+type);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /* 성공, 실패 여부에 상관 없이 통신이 종료되면 실행된다. */
        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            super.onFinish();
            dialog.dismiss();
            dialog = null;
        }

        /* 통신 시작시에 실행된다. */
        @Override
        public void onStart() {
            // TODO Auto-generated method stub
            dialog = new ProgressDialog(Applications_Permissions.this);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
            super.onStart();
        }

    } // end class
}
