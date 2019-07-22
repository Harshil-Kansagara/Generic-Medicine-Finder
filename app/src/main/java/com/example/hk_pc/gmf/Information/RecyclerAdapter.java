package com.example.hk_pc.gmf.Information;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hk_pc.gmf.R;
import com.example.hk_pc.gmf.activity_displayData;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    private static final int TYPE_HEAD = 0;
    private static final int TYPE_LIST=1;
    ArrayList<Information> arrayList = new ArrayList<>();
    Context ctx;

    public RecyclerAdapter(ArrayList<Information> arrayList, Context ctx){
        this.arrayList = arrayList;
        this.ctx=ctx;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == TYPE_HEAD){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_layout, parent,false);
            return new RecyclerViewHolder(view, viewType);
        }
        else if(viewType == TYPE_LIST) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent,false);
            return new RecyclerViewHolder(view, viewType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        if(holder.viewType == TYPE_LIST) {
            Information info = arrayList.get(position - 1);
            holder.gn.setText(info.getGn());
            holder.bn.setText(info.getBn());
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Intent intent = new Intent(ctx, activity_displayData.class);
                    intent.putExtra("position",arrayList.get(position-1).getGn());
                    ctx.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount( ) {
        return arrayList.size()+1;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        TextView gn,bn;
        private ItemClickListener itemClickListener;
        int viewType;
        public RecyclerViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_LIST) {
                gn = (TextView) itemView.findViewById(R.id.gn);
                bn = (TextView) itemView.findViewById(R.id.bn);
                this.viewType = TYPE_LIST;
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }
            else if(viewType == TYPE_HEAD){
                this.viewType=TYPE_HEAD;
            }

        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),true);
            return true;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return TYPE_HEAD;
        }
        else {
            return TYPE_LIST;
        }
    }
}
