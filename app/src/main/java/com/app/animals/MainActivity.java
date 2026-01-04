package com.app.animals;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.animals.data.AnimalsRepository;
import com.app.animals.model.Animal;
import com.app.animals.ui.AnimalsAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Animal> animals = AnimalsRepository.loadAnimals(this);

        RecyclerView rv = findViewById(R.id.rvAnimals);
        rv.setLayoutManager(new GridLayoutManager(this, 2));

        boolean useUkrainian = true; // поки хардкод. Потім візьмемо з Settings/SharedPreferences
        rv.setAdapter(new AnimalsAdapter(this, animals, useUkrainian, animal -> {
            // TODO: на наступному кроці відкриємо AnimalDetailActivity
        }));
    }
}
