package com.example.suzzy.Cart;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.suzzy.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Categories_Adapter extends RecyclerView.Adapter<Categories_Adapter.viewHolder>{
    private List<ProductList> list;
    private Context context;
   OnItemClickListener listener;

public interface OnItemClickListener{
    void onLikedClikcked(int position);
    void onAddItemClicked(int position);
    void openDetailsClicked(int position);
}
public void setOnItemclickListener(OnItemClickListener listener){
   this.listener = listener;
}
    public Categories_Adapter(List<ProductList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Categories_Adapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item_list_cardview, parent, false);
        return new viewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull Categories_Adapter.viewHolder viewHolder, int position) {
        ProductList model = list.get(position);
        viewHolder.imageview.setImageURI(model.getImageurl());
        viewHolder.name.setText(model.getName());
        viewHolder.price.setText("Kshs " + Long.toString(model.getPrice()) + "/");
        viewHolder.size.setText(model.getSize());
        viewHolder.tag.setVisibility(model.getTag() != null ? View.VISIBLE : View.GONE);
        viewHolder.size.setVisibility(model.getSize() != null ? View.VISIBLE : View.GONE);
        viewHolder.tag.setText(model.getTag());
        viewHolder.unit.setText(model.getUnit());
        Drawable yellowtag = context.getResources().getDrawable(R.drawable.texttagsyellow);
        Drawable blacktag = context.getResources().getDrawable(R.drawable.texttags);
        yellowtag.mutate();
        blacktag.mutate();
        if(model.getTag() != null) {
            if (model.getTag().toLowerCase().indexOf("express".toLowerCase()) != -1) {
                viewHolder.tag.setBackground(blacktag);
            } else viewHolder.tag.setBackground(yellowtag);
        }
//        if(model.isSavedinCart()){
//            viewHolder.product_add_to_cart.setEnabled(false);
//            viewHolder.product_add_to_cart.setVisibility(View.GONE);
//            viewHolder.saveTolist.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends  RecyclerView.ViewHolder{
        TextView tag, price, unit, name, size, product_add_to_cart;
        SimpleDraweeView imageview;
        LinearLayout saveTolist;
        public viewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tag = itemView.findViewById(R.id.product_tag);
            price = itemView.findViewById(R.id.product_price);
            unit = itemView.findViewById(R.id.product_unit);
            name = itemView.findViewById(R.id.product_name);
            size = itemView.findViewById(R.id.product_size);
            imageview = itemView.findViewById(R.id.product_image);
            product_add_to_cart = itemView.findViewById(R.id.product_add_to_cart);
            saveTolist = itemView.findViewById(R.id.product_saved_to_cart);
            product_add_to_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  if(listener != null){
                      int index = getAdapterPosition();
                      if(index != RecyclerView.NO_POSITION){
                          listener.onAddItemClicked(index);
                      }
                  }
                }
            });
           itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  if(listener != null){
                      int index = getAdapterPosition();
                      if(index != RecyclerView.NO_POSITION){
                          listener.openDetailsClicked(index);
                      }
                  }
                }
            });
        }

    }
}
