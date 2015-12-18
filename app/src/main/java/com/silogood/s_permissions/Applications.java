package com.silogood.s_permissions;

import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Family on 2015-12-03.
 */
public class Applications extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private PackageManager mPm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applications_tab);
        getSupportActionBar().setElevation(0);

        Load task = new Load();
        task.execute();

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Applications_NO_SYSTEM(), "다운로드앱");
        adapter.addFragment(new Applications_SYSTEM(), "시스템앱");
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

            dialog1 = new ProgressDialog(Applications.this);

            // 다이얼로그에 표시할 메시지
            // (이 외에 타이틀, 아이콘, 버튼 등을 추가할 수 있음)
            dialog1.setMessage("Loading...");
            dialog1.setCancelable(false);

            // 다이얼로그를 화면에 표시하기
            dialog1.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mPm = getPackageManager();    //패키지 매니저 받아옴
            Drawable icon;

            String applicationLabel;
            String packageName;
            PackageInfo pi = null;
            int system;
            List<ApplicationInfo> appList = mPm.getInstalledApplications(PackageManager.GET_META_DATA);  //인스톨 되어있는애들 패키지매니저에서 뺴서
            List<Recycler_item> System_items = new ArrayList<>();           //리스트를 시스템 and User app 으로 나누어 저장할 배열리스트생성
            List<Recycler_item> No_System_items = new ArrayList<>();
            //앱리스트에 저장(엡인포)



            // Parcourt chaque package du syst�me
            for (ApplicationInfo ai : appList) {           //for문 으로 ai 값(앱인포)  값을 하나하나 appList 와 비교하면서 전체 프로세스를 돌림

                // R�cup�re le nom du package et si possible le label
                packageName = ai.packageName;                       //패키지네임을 받아오고
                try {
                    applicationLabel = mPm.getApplicationLabel(ai).toString();           //라벨을 받아옴
                } catch (Exception ex) { // application not found
                    applicationLabel = packageName;
                }

                try {
                    icon = mPm.getApplicationIcon(packageName);               //아이콘을 받아옴
                } catch (Exception ex) {
                    icon = mPm.getDefaultActivityIcon();
                }


                try {
                    pi = mPm.getPackageInfo(packageName, PackageManager.GET_META_DATA);     //패키지인포를 받아와서 코드 네임등을 저장

                } catch (Exception ex) {

                }

                if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                    system = 1;
                else
                    system = 0;                  //플래그값으로 시스템인지 아닌지를 구별해옴

                try {
                    pi = mPm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
                } catch (PackageManager.NameNotFoundException e) {                             //패키지인포값에서 퍼미션을 추출해옴
                    e.printStackTrace();
                }
                int count = 0;
                try {
                    for (String key : pi.requestedPermissions) {              // String Key 값이      pi(패키지인포 안의 이름별 퍼미션애들) 을 하나하나 비교하면서
 //                       if (key.startsWith("android.permission."))
                            count++;   // key 값의 시작이 android.permission 으로 시작하는 애들 값을 가져와서 카운트를 늘림
                    }
                } catch (NullPointerException e) {

                }

                if(system==1){
                    System_items.add(new Recycler_item((Drawable) icon, applicationLabel + "(" + count + ")", packageName));

                }else if(system==0) {
                    No_System_items.add(new Recycler_item((Drawable) icon, applicationLabel + "(" + count + ")", packageName));

                }

            }
            appList.clear();

            PermissionSingleton PS = PermissionSingleton.getInstance();
            PS.setNo_System_items(No_System_items);
            PS.setSystem_items(System_items);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            viewPager = (ViewPager) findViewById(R.id.viewpager);
            setupViewPager(viewPager);

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);

            tabLayout.getTabAt(0).setIcon(R.drawable.download);
            tabLayout.getTabAt(1).setIcon(R.drawable.system);

            dialog1.dismiss();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }   //메뉴

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }    //옵션 이여

        return super.onOptionsItemSelected(item);
    }
}
