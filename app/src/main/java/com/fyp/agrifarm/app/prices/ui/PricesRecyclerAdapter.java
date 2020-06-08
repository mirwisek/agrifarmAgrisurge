package com.fyp.agrifarm.app.prices.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.app.prices.model.PriceItem;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class PricesRecyclerAdapter extends RecyclerView.Adapter<PricesRecyclerAdapter.PricesViewHolder> {

    private Context context;
    private List<PriceItem> priceList;

    private PricesRecyclerAdapter.OnItemClickListener mListener;

    public PricesRecyclerAdapter(Context context, List<PriceItem> priceItems){
        this.context = context;
        priceList = priceItems;
    }

    @NonNull
    @Override
    public PricesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.rv_item_price, viewGroup, false);

        return new PricesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PricesViewHolder holder, int i) {
        PriceItem priceItem = priceList.get(i);

//        holder.itemView.setOnClickListener(v -> mListener.onPriceItemClicked(priceItem));

        holder.itemView.setOnClickListener(view -> {
            Snackbar.make(view, priceItem.getName(), Snackbar.LENGTH_SHORT).show();
        });

        holder.tvPriceValue.setText(priceItem.getPrice());
        holder.tvName.setText(priceItem.getName());

        holder.ivSymbol.setImageResource(priceItem.getPriceSymbol());
        holder.ivPricePace.setImageResource(priceItem.getPricePace());
    }

    @Override
    public int getItemCount() {
        if(priceList == null)
            return 0;
        return priceList.size();
    }

    static class PricesViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPriceValue;
        ImageView ivSymbol, ivPricePace;

        public PricesViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvPriceItemName);
            tvPriceValue = view.findViewById(R.id.tvPriceValue);
            ivSymbol = view.findViewById(R.id.ivPriceSymbol);
            ivPricePace = view.findViewById(R.id.ivPricePace);
        }
    }

    public interface OnItemClickListener {
        void onPriceItemClicked(PriceItem item);
    }

    public void updateList(List<PriceItem> list){
        priceList = new ArrayList<>(list);
        notifyDataSetChanged();
    }
}

