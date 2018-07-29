package com.example.sq.news;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by SQ on 2018/7/26.
 */

public class LinearAdapter extends RecyclerView.Adapter<LinearAdapter.LinearViewHolder> {
    private Context mContext;
    public LinearAdapter(MainActivity mainActivity) {
        mContext = mainActivity;
    }
    @Override
    public LinearAdapter.LinearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.linear_adapter_item,parent,false));
    }

    @Override
    public void onBindViewHolder(LinearAdapter.LinearViewHolder holder, int position) {
        holder.textview.setText("Hello world");

    }

    @Override
    public int getItemCount() {
        return 30;
    }
    class LinearViewHolder extends RecyclerView.ViewHolder{
        private TextView textview;//声明布局里的控件

        public LinearViewHolder(View itemView) {
            super(itemView);
            textview=(TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
