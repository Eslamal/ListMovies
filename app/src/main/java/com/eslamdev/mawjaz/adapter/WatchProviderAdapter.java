package com.eslamdev.mawjaz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.eslamdev.mawjaz.R;
import com.eslamdev.mawjaz.api.Provider;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class WatchProviderAdapter extends RecyclerView.Adapter<WatchProviderAdapter.ProviderViewHolder> {

    private final Context context;
    private List<Provider> providerList = new ArrayList<>();
    private OnProviderClickListener listener;

    public interface OnProviderClickListener {
        void onProviderClick();
    }

    public void setOnProviderClickListener(OnProviderClickListener listener) {
        this.listener = listener;
    }

    public WatchProviderAdapter(Context context) {
        this.context = context;
    }

    public void setProviderList(List<Provider> providerList) {
        this.providerList = (providerList != null) ? providerList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_watch_provider, parent, false);
        return new ProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
        Provider provider = providerList.get(position);
        if (provider.getLogoPath() != null) {
            String imageUrl = "https://image.tmdb.org/t/p/w92" + provider.getLogoPath();
            Picasso.get().load(imageUrl).into(holder.logo);
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProviderClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    static class ProviderViewHolder extends RecyclerView.ViewHolder {
        ImageView logo;
        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.provider_logo);
        }
    }
}