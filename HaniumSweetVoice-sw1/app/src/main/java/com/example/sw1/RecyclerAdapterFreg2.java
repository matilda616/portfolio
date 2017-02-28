package com.example.sw1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by USER on 2016-07-10.
 */
public class RecyclerAdapterFreg2  extends RecyclerView.Adapter<RecyclerAdapterFreg2.ViewHolder> {
    Context context;
    List<ItemInCardViewFreg2> items;
    int item_layout;

    public RecyclerAdapterFreg2(Context context, List<ItemInCardViewFreg2> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout_freg2, null);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ItemInCardViewFreg2 item = items.get(position);
        //카드뷰에 일정, 날짜 출력
        holder.title.setText(item.getTitle());
        holder.date.setText(item.getDate());

        //카드뷰 클릭시
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, item.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        //카드뷰 수정
        holder.cardview.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                String message_popup="일정을 수정하시겠습니까?";

                alert.setMessage(message_popup);
                alert.setNegativeButton("취소", null);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();
                return false;
            }
        });
        //카드뷰 지우기 버튼
        holder.image_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                String message_popup="일정을 삭제하시겠습니까?";

                alert.setMessage(message_popup);
                alert.setNegativeButton("취소", null);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title; //일정이름
        TextView date;     //일정시간
        CardView cardview;
        ImageView image_delete;   //삭제버튼

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);
            cardview = (CardView) itemView.findViewById(R.id.cardview);
            image_delete = (ImageView)itemView.findViewById(R.id.card_delete);
        }
    }
}
