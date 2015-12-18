package com.silogood.s_permissions;


import android.graphics.drawable.Drawable;

public class Recycler_item {         //리사이클뷰 안의(카드뷰 레이아웃 안의 저장될 배열공간과 관련된 변수들을 지정)
    Drawable image;
    String title;
    String packagename;

    Drawable getImage(){
        return this.image;
    }
    String getTitle(){
        return this.title;
    }
    String getPackagename() {return this.packagename;}

    Recycler_item(Drawable image, String title , String packagename){
        this.image=image;
        this.title=title;
        this.packagename=packagename;
    }
}