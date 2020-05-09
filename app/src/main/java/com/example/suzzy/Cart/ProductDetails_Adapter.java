package com.example.suzzy.Cart;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.suzzy.FragmentListClasses.topItemsList;
import com.example.suzzy.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductDetails_Adapter extends RecyclerView.Adapter<ProductDetails_Adapter.viewHolder> {
private Context ctx;
private List<topItemsList> mainlist;
OnCardItemClickListener listener;
public interface OnCardItemClickListener{
    void onCardItemClick(int position);
}
public void setOnCardClickListener(OnCardItemClickListener listener){
    this.listener = listener;
}

    public ProductDetails_Adapter(Context ctx, List<topItemsList> mainlist) {
        this.ctx = ctx;
        this.mainlist = mainlist;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_also_like, parent, false);
        return new viewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
topItemsList list = mainlist.get(position);
holder.image.setImageURI(list.getImageurl());
holder.tag.setText(list.getTag());
holder.price.setText(String.valueOf(list.getPrice()));
holder.name.setText(list.getName());
holder.tag.setVisibility(list.getTag() != null?View.VISIBLE:View.GONE);
        Drawable yellowtag = ctx.getResources().getDrawable(R.drawable.texttagsyellow);
        Drawable blacktag = ctx.getResources().getDrawable(R.drawable.texttags);
        yellowtag.mutate();
        blacktag.mutate();
        if(list.getTag() != null) {
            if (list.getTag().toLowerCase().indexOf("express".toLowerCase()) != -1) {
                holder.tag.setBackground(blacktag);
            } else holder.tag.setBackground(yellowtag);
        }
    }

    @Override
    public int getItemCount() {
        return mainlist.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView image;
        TextView name, price, tag;
        public viewHolder(@NonNull View itemView, final OnCardItemClickListener mlistener) {
            super(itemView);
            image = itemView.findViewById(R.id.category_item_image);
            name = itemView.findViewById(R.id.category_item_text);
            price = itemView.findViewById(R.id.items_price);
            tag = itemView.findViewById(R.id.items_tag);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mlistener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mlistener.onCardItemClick(position);
                        }
                    }
                }
            });
        }


    }
}
