package com.example.suzzy.MoreOptions;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.suzzy.BottomSheets.OrderList;
import com.example.suzzy.R;
import com.example.suzzy.GeneralClasses.TimeAgo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.viewHolder>{
    private List<OrderList> mainList;
    private OnCardItemClickListener listener;
    private Context context;
    public interface OnCardItemClickListener{
        void onItemClick(int position);
    }
public void setOnCardItemClickListener(OnCardItemClickListener listener){
  this.listener = listener;
}

    public HistoryAdapter(List<OrderList> mainList, Context context) {
        this.mainList = mainList;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.historycarview,
                parent, false);
        return new viewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.viewHolder holder, int position) {
        OrderList list = mainList.get(position);
        String id = list.getId();
     holder.id.setText(id.substring(0,5)+"......."+id.substring(id.length()-5, id.length()-1));
     holder.time.setText(TimeAgo.getTimeAgo(list.getTime(),context));
     if(!list.getStatus().equalsIgnoreCase("pending confirmation")){
      holder.done_ticks.setImageDrawable(list.getStatus().equalsIgnoreCase("Package on transit")?
              context.getResources().getDrawable(R.drawable.ic_done_all_black_24dp):
              context.getResources().getDrawable(R.drawable.ic_done_black_24dp));
     }
     holder.status.setText(list.getStatus());
     holder.amount.setText("Ksh "+list.getAmount());
    }

    @Override
    public int getItemCount() {
        return mainList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView time, id, amount, status;
        ImageView done_ticks;
        public viewHolder(@NonNull View itemView, final OnCardItemClickListener listener) {
            super(itemView);
            time = itemView.findViewById(R.id.time_go);
            status = itemView.findViewById(R.id.status);
            id = itemView.findViewById(R.id.order_id);
            amount = itemView.findViewById(R.id.amount_paid);
            done_ticks = itemView.findViewById(R.id.done_ticks);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
