package com.app.animals.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.animals.R;
import com.app.animals.model.Animal;
import com.bumptech.glide.Glide;

import java.util.List;

public class AnimalsAdapter extends RecyclerView.Adapter<AnimalsAdapter.VH> {

    public interface OnAnimalClick {
        void onClick(Animal animal);
    }

    private final Context context;
    private final List<Animal> animals;
    private final boolean useUkrainian;
    private final OnAnimalClick onClick;

    public AnimalsAdapter(Context context, List<Animal> animals, boolean useUkrainian, OnAnimalClick onClick) {
        this.context = context;
        this.animals = animals;
        this.useUkrainian = useUkrainian;
        this.onClick = onClick;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_animal, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Animal a = animals.get(position);
        h.txt.setText(useUkrainian ? a.nameUk : a.nameEn);

        // Load from assets: prepend "file:///android_asset/"
        String url = "file:///android_asset/" + a.imageAssetPath;
        Glide.with(context).load(url).into(h.img);

        h.itemView.setOnClickListener(v -> onClick.onClick(a));
    }

    @Override
    public int getItemCount() {
        return animals.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView txt;
        VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            txt = itemView.findViewById(R.id.txt);
        }
    }
}
