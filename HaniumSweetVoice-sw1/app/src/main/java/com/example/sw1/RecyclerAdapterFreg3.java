package com.example.sw1;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * 그리드뷰와 마찬가지로 이 클래스 자체를 하나의 루프로 생각하면 된다.
 * 카드가 생성될때마다 호출되며 각 포지선별 소스를 뷰홀더의 객체에 뿌려준다.
 */
public class RecyclerAdapterFreg3 extends RecyclerView.Adapter<RecyclerAdapterFreg3.ViewHolder> {

    private Context context;
    private List<ItemInCardViewFreg3> list;

    private String AlarmName;
    private String AlarmTime;
    private String AlarmInterval;
    private String AlarmRepeat;
    private String AlarmSound;
    private String Status;

    public RecyclerAdapterFreg3(Context context, List<ItemInCardViewFreg3> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerAdapterFreg3.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //뷰홀더 생성 :  카드뷰 한칸의 레이아웃 인플레이팅
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout_freg3, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterFreg3.ViewHolder holder, final int position) {
        //각 포지션별 카드에 카드뷰 아이템 리스트의 소스를 뿌려준다
        //리스트에서 소스 가져오기
        final ItemInCardViewFreg3 oneItem = list.get(position);
        Drawable drawable = context.getResources().getDrawable(oneItem.getImg());
        AlarmName = oneItem.getAlarmName();
        AlarmTime = oneItem.getAlarmTime();
        AlarmInterval = oneItem.getAlarmInterval();
        AlarmRepeat= oneItem.getAlarmRepeat();
        AlarmSound= oneItem.getAlarmSound();
        Status=oneItem.getStatus();

        //뷰홀더의 각 객체에 소스 뿌려주기
        holder.img.setImageDrawable(drawable);
        holder.AlarmName.setText(AlarmName);
        holder.AlarmDate.setText(AlarmTime);
        String info=AlarmInterval+"\n"+AlarmRepeat+"\n"+AlarmSound+"\n";
        holder.AlarmInfo.setText(info);

        if (Status.equals("add")){

        }
        //카드뷰 클릭 리스너 등록
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }

        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent;
                intent = new Intent(v.getContext(), DeletePopupActivity.class);
                v.getContext().startActivity(intent);
                return false;
             }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        CardView cardView;
        TextView AlarmName;
        TextView AlarmDate;
        TextView AlarmInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.mImage);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            AlarmName = (TextView) itemView.findViewById(R.id.AlarmName);
            AlarmDate = (TextView) itemView.findViewById(R.id.AlarmDate);
            AlarmInfo = (TextView) itemView.findViewById(R.id.AlarmInfo);
        }
    }
}
//[출처] 분홍뱀의 카드뷰 만들기~!(RecyclerView + CardView)|작성자 pinkysnake

