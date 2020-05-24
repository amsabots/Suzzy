package com.example.suzzy.ExtrasAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.example.suzzy.Cart.ProductList;
import com.example.suzzy.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.viewHolder> {
private List<ProductList> mainList;
private List<ProductList> filteredList;

    public SearchAdapter(List<ProductList> mainList) {
        this.mainList = mainList;
        this.filteredList = new ArrayList<>(mainList);
    }
public Filter getFilterResults(){
        return filter;
}

 private Filter filter = new Filter() {
     @Override
     protected FilterResults performFiltering(CharSequence constraint) {
       List<ProductList> list = new ArrayList<>();
       if(constraint == null || constraint.length() < 1){

       }else{
           String filterPattern = constraint.toString().toLowerCase().trim();
           for (ProductList item:filteredList
                ) {
               if(item.getName().toLowerCase().contains(filterPattern)){
                   list.add(item);
               }

           }
       }
       FilterResults results = new FilterResults();
       results.values = list;
       return results;
     }

     @Override
     protected void publishResults(CharSequence constraint, FilterResults results) {
mainList.clear();
mainList.addAll((List)results.values);
if(listener != null){
    listener.attachedFilteredList(mainList);
}
notifyDataSetChanged();
     }
 };
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchview_result_cardview,
                parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
holder.name.setText(mainList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mainList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView name;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.search_card_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        if(getAdapterPosition() != RecyclerView.NO_POSITION){
                            listener.onItemClick(getAdapterPosition());
                        }
                    }
                }
            });

        }
    }
    OnSearchItemClickListener listener;
    public interface OnSearchItemClickListener{
        void onItemClick(int i);
        void attachedFilteredList(List<ProductList> list);
    }
    public void setOnSearchItemClickListener(OnSearchItemClickListener listener){
        this.listener = listener;
    }
}
