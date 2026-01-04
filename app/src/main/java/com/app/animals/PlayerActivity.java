package com.app.animals;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.animals.data.AnimalsRepository;
import com.app.animals.model.Animal;

import java.util.List;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        String category = getIntent().getStringExtra(StartActivity.EXTRA_CATEGORY);
        String lang = getIntent().getStringExtra(StartActivity.EXTRA_LANG);

        List<Animal> all = AnimalsRepository.loadAll(this);
        List<Animal> items = AnimalsRepository.filterByCategory(all, category);

        TextView txt = findViewById(R.id.txtInfo);
        txt.setText("Category: " + category + "\nLang: " + lang + "\nItems: " + items.size());
    }
}
