package com.app.animals;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.animals.data.AppPrefs;
import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String EXTRA_LANG = "extra_lang";

    static class CategoryItem {
        final String id;      // "animals", "birds"...
        final String titleUk; // "Тварини"...
        final int iconRes;    // тимчасово

        CategoryItem(String id, String titleUk, int iconRes) {
            this.id = id;
            this.titleUk = titleUk;
            this.iconRes = iconRes;
        }
    }

    private final List<CategoryItem> categories = Arrays.asList(
            new CategoryItem("animals", "Тварини", R.drawable.ic_category_placeholder),
            new CategoryItem("birds", "Птахи", R.drawable.ic_category_placeholder),
            new CategoryItem("reptiles", "Плазуни", R.drawable.ic_category_placeholder),
            new CategoryItem("insects", "Комахи", R.drawable.ic_category_placeholder),
            new CategoryItem("fish", "Риби", R.drawable.ic_category_placeholder),
            new CategoryItem("amphibians", "Земноводні", R.drawable.ic_category_placeholder)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // MVP: фіксуємо укр. мову (UI перемикача немає)
        AppPrefs.setLang(this, "uk");

        GridLayout grid = findViewById(R.id.categoriesGrid);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (CategoryItem cat : categories) {
            MaterialCardView card = (MaterialCardView) inflater.inflate(R.layout.item_category, grid, false);

            TextView title = card.findViewById(R.id.txtTitle);
            ImageView icon = card.findViewById(R.id.imgIcon);

            title.setText(cat.titleUk);
            icon.setImageResource(cat.iconRes);

            // TODO: пізніше дамо кожній картці свій пастельний фон
            // card.setCardBackgroundColor(...)

            card.setOnClickListener(v -> {
                Intent i = new Intent(StartActivity.this, PlayerActivity.class);
                i.putExtra(EXTRA_CATEGORY, cat.id);
                i.putExtra(EXTRA_LANG, "uk");
                startActivity(i);
            });

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = GridLayout.LayoutParams.WRAP_CONTENT;

// 2 колонки: кожна займає 1/2
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.rowSpec = GridLayout.spec(GridLayout.UNDEFINED);

            card.setLayoutParams(lp);
            grid.addView(card);
        }
    }
}