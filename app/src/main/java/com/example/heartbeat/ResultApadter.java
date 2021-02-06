package com.example.heartbeat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ResultApadter extends RecyclerView.Adapter<ResultApadter.VH> {
List<Users> arrayList;
Context context;

    public ResultApadter(List<Users> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.all_user_result,parent,false);
        return  new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
       Users users=arrayList.get(position);
        holder.bpm.setText(users.bpm);
        holder.times.setText(users.time);
        holder.condition.setText(users.condition);
    }

    @Override
    public int getItemCount()
    {
        return arrayList.size();
    }


    class VH extends RecyclerView.ViewHolder{
TextView bpm,times,condition;
        public VH(@NonNull View itemView) {
            super(itemView);

            bpm=itemView.findViewById(R.id.i_bpm);
            times=itemView.findViewById(R.id.i_time);
            condition=itemView.findViewById(R.id.i_condition);
        }
    }
}
