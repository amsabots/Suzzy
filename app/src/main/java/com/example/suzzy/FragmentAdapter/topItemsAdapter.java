package com.example.suzzy.FragmentAdapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.suzzy.FragmentListClasses.ExploreandShopList;
import com.example.suzzy.FragmentListClasses.topItemsList;
import com.example.suzzy.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class topItemsAdapter extends RecyclerView.Adapter<topItemsAdapter.viewHolder> {
    private Context context;
    private List<topItemsList> itemsLists;
    OnTopItemCardClickListener listener;
    public interface OnTopItemCardClickListener{
        void onTopItemClick(int position);
    }
public void setOnCardclickListener(OnTopItemCardClickListener listener){
        this.listener = listener;
}
    public topItemsAdapter(Context context, List<topItemsList> itemsLists) {
        this.context = context;
        this.itemsLists = itemsLists;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topitems, parent, false);
        return new viewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
topItemsList list = itemsLists.get(position);
holder.textView.setText(list.getName());
       holder.imageView.setImageURI(list.getImageurl());
       holder.tag.setVisibility(!TextUtils.isEmpty(list.getTag())?View.VISIBLE:View.GONE);
       holder.tag.setText(list.getTag());
        holder.price.setText("Kshs "+Long.toString(list.getPrice()) +"/"+list.getUnit());

        Drawable imgview = context.getResources().getDrawable(R.drawable.itemsdrawable);
        Drawable yellowtag = context.getResources().getDrawable(R.drawable.texttagsyellow);
        Drawable blacktag = context.getResources().getDrawable(R.drawable.texttags);
        imgview.mutate();
        yellowtag.mutate();
       blacktag.mutate();
       if(!TextUtils.isEmpty(list.getTag())) {
           if (list.getTag().toLowerCase().indexOf("express".toLowerCase()) != -1) {
               holder.tag.setBackground(blacktag);
           } else holder.tag.setBackground(yellowtag);
       }
        holder.cardView.setBackground(imgview);


    }

    @Override
    public int getItemCount() {
        return (itemsLists != null? itemsLists.size():0);
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView imageView;
        private TextView textView, price, tag;
        private CardView cardView;
        public  viewHolder(@NonNull View itemView, final OnTopItemCardClickListener mlistener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.category_item_image);
            textView = itemView.findViewById(R.id.category_item_text);
            price = itemView.findViewById(R.id.items_price);
            tag = itemView.findViewById(R.id.items_tag);
            cardView = itemView.findViewById(R.id.main_cardview);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 if(mlistener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) mlistener.onTopItemClick(position);
                 }
                }
            });

        }
    }
}
