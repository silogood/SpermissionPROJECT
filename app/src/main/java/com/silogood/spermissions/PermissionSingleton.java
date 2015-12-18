package com.silogood.s_permissions;

/**
 * Created by 7217-182 on 2015-12-01.
 */
import java.util.List;
import java.util.Map;

/**
 * Created by Family on 2015-11-28.
 */
public class PermissionSingleton {
    //----------- 싱글톤 객체 생성 시작 ----------
    private static PermissionSingleton current = null;

    public static PermissionSingleton getInstance() {
        if (current == null) {
            current = new PermissionSingleton();
        }
        return current;
    }

    public static void freeInstance() {
        current = null;
    }

    private PermissionSingleton() {
        super();
    }
    //----------- 싱글톤 객체 생성 끝 ----------



    private List<Map<String, String>> mGroupData;
    private List<List<Map<String, String>>> mChildData;
    private List<Recycler_item> no_System_items;                  // 사용하는 리스트들을 정의 시킴
    private List<Recycler_item> system_items;

    public List<Map<String, String>> getmGroupData() {
        return mGroupData;
    }

    public void setmGroupData(List<Map<String, String>> mGroupData) {
        this.mGroupData = mGroupData;                                                        //값들저장 ㅇㅇ
    }

    public List<List<Map<String, String>>> getmChildData() {
        return mChildData;
    }

    public void setmChildData(List<List<Map<String, String>>> mChildData) {
        this.mChildData = mChildData;
    }

    public List<Recycler_item> getNo_System_items() {
        return no_System_items;
    }

    public void setNo_System_items(List<Recycler_item> no_System_items) {
        this.no_System_items = no_System_items;
    }

    public List<Recycler_item> getSystem_items() {
        return system_items;
    }

    public void setSystem_items(List<Recycler_item> system_items) {
        this.system_items = system_items;
    }
}