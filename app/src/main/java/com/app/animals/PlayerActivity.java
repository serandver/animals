package com.app.animals;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.app.animals.data.AnimalsRepository;
import com.app.animals.model.Animal;
import com.app.animals.ui.SlidesAdapter;

import java.util.List;
import java.util.Locale;

public class PlayerActivity extends AppCompatActivity {

    private List<Animal> items;
    private String lang;

    private TextToSpeech tts;
    private boolean ttsReady = false;
    private boolean mute = false;

    private ViewPager2 pager;


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
        btnMute.setOnClickListener(v -> {
            btnMute.setSelected(!btnMute.isSelected());
            mute = btnMute.isSelected();

            if (mute && tts != null) {
                tts.stop(); // якщо під час промови увімкнули mute — зупиняємо
            }
        });

        mute = btnMute.isSelected();

        String category = getIntent().getStringExtra(StartActivity.EXTRA_CATEGORY);
        lang = getIntent().getStringExtra(StartActivity.EXTRA_LANG);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Locale locale = "en".equals(lang) ? Locale.US : new Locale("uk", "UA");
                int res = tts.setLanguage(locale);
                ttsReady = (res != TextToSpeech.LANG_MISSING_DATA && res != TextToSpeech.LANG_NOT_SUPPORTED);

                if (ttsReady) {
                    runOnUiThread(this::speakCurrent); // <-- ОЦЕ ДОДАТИ
                }
            } else {
                ttsReady = false;
            }
        });


        List<Animal> all = AnimalsRepository.loadAll(this);
        items = AnimalsRepository.filterByCategory(all, category);

        TextView title = findViewById(R.id.txtTitle);
        pager = findViewById(R.id.pager);

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
                    speakName(items.get(position));
                }
            }
        });
    }

    private String getName(Animal a) {
        return "en".equals(lang) ? a.nameEn : a.nameUk;
    }

    private void speakName(Animal a) {
        if (mute) return;
        if (!ttsReady || tts == null) return;

        String text = "en".equals(lang) ? a.nameEn : a.nameUk;

        // Унікальний utteranceId, щоб не залипало
        String utteranceId = "name_" + a.id + "_" + System.currentTimeMillis();

        tts.stop(); // на всяк випадок, щоб не було черги
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }

    private void speakCurrent() {
        if (items == null || items.isEmpty()) return;
        if (pager == null) return;

        int pos = pager.getCurrentItem();
        if (pos < 0 || pos >= items.size()) pos = 0;

        speakName(items.get(pos));
    }


}
