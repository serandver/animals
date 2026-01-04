package com.app.animals;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.app.animals.data.AnimalsRepository;
import com.app.animals.model.Animal;
import com.app.animals.ui.SlidesAdapter;

import java.util.List;

public class PlayerActivity extends AppCompatActivity {

    private List<Animal> items;
    private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        ImageButton btnAuto = findViewById(R.id.btnAuto);
        ImageButton btnLock = findViewById(R.id.btnLock);
        ImageButton btnMute = findViewById(R.id.btnMute);

// початкові стани (поки що хардкод)
        btnAuto.setSelected(false);
        btnLock.setSelected(false);
        btnMute.setSelected(false);

        btnAuto.setOnClickListener(v -> btnAuto.setSelected(!btnAuto.isSelected()));
        btnLock.setOnClickListener(v -> btnLock.setSelected(!btnLock.isSelected()));
        btnMute.setOnClickListener(v -> btnMute.setSelected(!btnMute.isSelected()));


        String category = getIntent().getStringExtra(StartActivity.EXTRA_CATEGORY);
        lang = getIntent().getStringExtra(StartActivity.EXTRA_LANG);

        List<Animal> all = AnimalsRepository.loadAll(this);
        items = AnimalsRepository.filterByCategory(all, category);

        TextView title = findViewById(R.id.txtTitle);
        ViewPager2 pager = findViewById(R.id.pager);

        pager.setAdapter(new SlidesAdapter(this, items));

        // показати назву першого
        if (!items.isEmpty()) {
            title.setText(getName(items.get(0)));
        } else {
            title.setText("No items");
        }

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position >= 0 && position < items.size()) {
                    title.setText(getName(items.get(position)));
                }
            }
        });
    }

    private String getName(Animal a) {
        return "en".equals(lang) ? a.nameEn : a.nameUk;
    }
}
