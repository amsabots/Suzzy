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

    public ExploreandShopAdapter(Context context, List<ExploreandShopList> toplist) {
        this.context = context;
        this.toplist = toplist;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exploreandshopcardview, parent, false);
        return new viewHolder(view);
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

    public class viewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView imageView;
        private TextView textView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.category_item_image);
            textView = itemView.findViewById(R.id.category_item_text);

        }
    }
}
