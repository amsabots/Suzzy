package com.example.suzzy.FragmentAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.suzzy.Cart.Categories;
import com.example.suzzy.FragmentListClasses.topCategoryList;
import com.example.suzzy.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class topCategoryAdapter extends RecyclerView.Adapter<topCategoryAdapter.viewHolder> {
    private Context context;
    private List<topCategoryList> toplist;
    private CategoryCardClickListener mlistener;
    public interface CategoryCardClickListener{
        void onCategoryCardClick(int position);
    }
public void setOnCategoryCardClickListener(CategoryCardClickListener listener){
    this.mlistener = listener;
}
    public topCategoryAdapter(Context context, List<topCategoryList> toplist) {
        this.context = context;
        this.toplist = toplist;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topcategorycard, parent, false);
        return new viewHolder(view, this.mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
final topCategoryList list = toplist.get(position);
holder.textView.setText(list.getCategoryname());
       holder.imageView.setImageURI(list.getImageurl());
        Drawable imgview = context.getResources().getDrawable(R.drawable.bacground_cardview);
        imgview.mutate();
        holder.background.setBackground(imgview);
        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Categories.class);
                intent.putExtra("categoryid", list.getCategoryID());
                intent.putExtra("type", "item");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (toplist != null? toplist.size():0);
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView imageView;
        private TextView textView;
        private CardView background;
        public viewHolder(@NonNull View itemView, final CategoryCardClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.category_item_image);
            textView = itemView.findViewById(R.id.category_item_text);
            background = itemView.findViewById(R.id.main_cardview);
itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(listener != null){
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                listener.onCategoryCardClick(position);
            }
        }
    }
});
        }
    }
}
