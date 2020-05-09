package com.example.suzzy.FragmentAdapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.suzzy.FragmentListClasses.ExploreandShopList;
import com.example.suzzy.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExploreandShopAdapter extends RecyclerView.Adapter<ExploreandShopAdapter.viewHolder> {
    private Context context;
    private List<ExploreandShopList> toplist;
    ExploreandShopCardClickListener mlistener;

    public ExploreandShopAdapter(Context context, List<ExploreandShopList> toplist) {
        this.context = context;
        this.toplist = toplist;
    }
    public interface  ExploreandShopCardClickListener{
        void ExploreShopClick(int position);
    }
public void setOnExploreandShopClickListener(ExploreandShopCardClickListener listener){
    this.mlistener = listener;
}
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exploreandshopcardview, parent, false);
        return new viewHolder(view, this.mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
ExploreandShopList list = toplist.get(position);
holder.textView.setText(list.getCategoryname());
        holder.imageView.setImageURI(list.getImageurl());
        Drawable imgview = context.getResources().getDrawable(R.drawable.card_rounded_image_corners);
        imgview.mutate();
        holder.imageView.setBackground(imgview);
    }

    @Override
    public int getItemCount() {
        return (toplist != null? toplist.size():0);
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView imageView;
        private TextView textView;
        public viewHolder(@NonNull View itemView, final ExploreandShopCardClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.category_item_image);
            textView = itemView.findViewById(R.id.category_item_text);
itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        int position = getAdapterPosition();
        if(position != RecyclerView.NO_POSITION){
           listener.ExploreShopClick(position);
        }
    }
});
        }
    }
}
