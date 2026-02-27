package com.app.animals.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.animals.BuildConfig;
import com.app.animals.R;
import com.app.animals.model.Animal;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

import java.util.List;

public class SlidesAdapter extends RecyclerView.Adapter<SlidesAdapter.VH> {

    private final Context context;
    private final List<Animal> items;

    public SlidesAdapter(Context context, List<Animal> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slide, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Animal a = items.get(position);
        String url = "file:///android_asset/" + a.imageAssetPath;

        Glide.with(context)
                .load(url)
                .signature(new ObjectKey(BuildConfig.VERSION_CODE))
                .into(h.img);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
        }
    }
}
