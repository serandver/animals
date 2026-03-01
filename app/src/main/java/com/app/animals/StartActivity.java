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

    List<CategoryItem> categories = Arrays.asList(
            new CategoryItem("animals", "Тварини", R.drawable.ic_lion),
            new CategoryItem("birds", "Птахи", R.drawable.ic_parrot),
            new CategoryItem("reptiles", "Плазуни", R.drawable.ic_crocodile),
            new CategoryItem("insects", "Комахи", R.drawable.ic_bee),
            new CategoryItem("fish", "Риби", R.drawable.ic_koi),
            new CategoryItem("amphibians", "Земноводні", R.drawable.ic_frog_tomato)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        GridLayout grid = findViewById(R.id.categoriesGrid);
        LayoutInflater inflater = LayoutInflater.from(this);
        int spacingDp = 10;
        float d = getResources().getDisplayMetrics().density;
        int spacingPx = (int) (spacingDp * d);

        for (CategoryItem cat : categories) {
            MaterialCardView card = (MaterialCardView) inflater.inflate(R.layout.item_category, grid, false);

            TextView title = card.findViewById(R.id.txtTitle);
            ImageView icon = card.findViewById(R.id.imgIcon);

            title.setText(cat.titleUk);
            icon.setImageResource(cat.iconRes);
            card.setCardBackgroundColor(getCategoryColor(cat.id));

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.setMargins(spacingPx / 2, spacingPx / 2, spacingPx / 2, spacingPx / 2);
            card.setLayoutParams(lp);

            card.setOnClickListener(v -> {
                v.animate()
                        .scaleX(0.95f)
                        .scaleY(0.95f)
                        .setDuration(80)
                        .withEndAction(() -> {
                            v.animate().scaleX(1f).scaleY(1f).setDuration(80).start();

                            Intent i = new Intent(StartActivity.this, PlayerActivity.class);
                            i.putExtra(EXTRA_CATEGORY, cat.id);
                            i.putExtra(EXTRA_LANG, "uk");
                            startActivity(i);
                        })
                        .start();
            });
            grid.addView(card);

            card.post(() -> {
                int w = card.getWidth();
                if (w > 0 && card.getLayoutParams().height != w) {
                    card.getLayoutParams().height = w;
                    card.requestLayout();
                }
            });
        }
    }

    private int getCategoryColor(String id) {
        return switch (id) {
            case "animals" -> 0xFFF6D8AE;
            case "birds" -> 0xFFE4C1F9;
            case "reptiles" -> 0xFFCDEAC0;
            case "insects" -> 0xFFFFF3B0;
            case "fish" -> 0xFFA8DADC;
            case "amphibians" -> 0xFFD0F4DE;
            default -> 0xFFFFFFFF;
        };
    }
}