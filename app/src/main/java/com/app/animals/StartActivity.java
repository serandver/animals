package com.app.animals;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.app.animals.data.AnimalsRepository;
import com.app.animals.model.Animal;
import com.app.animals.data.AppPrefs;

import java.util.List;

public class StartActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String EXTRA_LANG = "extra_lang"; // "uk" or "en"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        RadioButton rbUk = findViewById(R.id.langUk);
        RadioButton rbEn = findViewById(R.id.langEn);
        String savedLang = AppPrefs.getLang(this);
        rbEn.setChecked("en".equals(savedLang));
        rbUk.setChecked(!"en".equals(savedLang));

        List<Animal> all = AnimalsRepository.loadAll(this);
        List<String> categories = AnimalsRepository.getCategories(all);

        LinearLayout container = findViewById(R.id.categoriesContainer);

        for (String cat : categories) {
            Button b = new Button(this);
            b.setAllCaps(false);
            b.setText(displayName(cat)); // локалізуємо назву кнопки
            b.setOnClickListener(v -> {
                String lang = rbEn.isChecked() ? "en" : "uk";
                AppPrefs.setLang(StartActivity.this, lang);
                Intent i = new Intent(StartActivity.this, PlayerActivity.class);
                i.putExtra(EXTRA_CATEGORY, cat);
                i.putExtra(EXTRA_LANG, lang);
                startActivity(i);
            });
            container.addView(b);
        }
    }

    private String displayName(String cat) {
        switch (cat) {
            case "animals": return "Тварини";
            case "birds": return "Птахи";
            case "insects": return "Комахи";
            default: return cat;
        }
    }
}
