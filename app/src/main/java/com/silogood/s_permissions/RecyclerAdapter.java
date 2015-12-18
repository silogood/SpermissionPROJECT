package com.silogood.s_permissions;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ChoiDW on 2015-11-28.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>  {
     Context context;
    List<Recycler_item> items;       //변수들 생성
    int item_layout;
    public RecyclerAdapter(Context context, List<Recycler_item> items, int item_layout) {
        this.context=context;
        this.items=items;
        this.item_layout=item_layout;
    }    //해당값들을 저장(어뎁터용)

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);
        return new ViewHolder(v);    //뷰홀더를 생성 카드뷰랑 연결
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Recycler_item item=items.get(position);
       Drawable drawable=item.getImage();
      //  Drawable drawable = context.getResources().getDrawable(position);
        holder.image.setBackground(drawable);
        holder.title.setText(item.getTitle());                           //// 뷰홀더-> 카드뷰로 데이터들연결
        holder.packagename.setText(item.getPackagename());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                 //카드뷰의 클릭리스너 설정

                    Intent intent = new Intent(context,Applications_Permissions.class);
                    intent.putExtra("PackageName", item.getPackagename());
                    v.getContext().startActivity(intent);                   //// 앱클릭시 앱의 최종 정보창인 Applications_Permissions 창으로이동
                                                                            //// 아이템값으로 저장되어있는 패키지네임을 인텐트로 추가를 해서 보냄


            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView packagename;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            image=(ImageView)itemView.findViewById(R.id.image);
            title=(TextView)itemView.findViewById(R.id.title);
            packagename=(TextView)itemView.findViewById(R.id.packagename);
            cardview=(CardView)itemView.findViewById(R.id.cardview);
            //뷰홀더 매소드 ㅇㅇ저장
        }
    }
}