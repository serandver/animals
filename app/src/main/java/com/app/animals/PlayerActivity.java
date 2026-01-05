package com.app.animals;

import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;


import com.app.animals.data.AnimalsRepository;
import com.app.animals.model.Animal;
import com.app.animals.ui.SlidesAdapter;
import com.app.animals.data.AppPrefs;


import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PlayerActivity extends AppCompatActivity {
    private final Handler handler = new Handler();
    private ViewPager2 pager;
    private ImageButton btnAuto;
    private ImageButton btnLock;
    private ImageButton btnMute;
    private List<Animal> items;
    private String lang;
    private TextToSpeech tts;
    private Runnable slideRunnable;
    private Runnable speak2Runnable;
    private Runnable speak7Runnable;
    private boolean autoPlay = false;
    private boolean ttsReady = false;
    private boolean mute = false;
    private boolean locked = false;
    private final long SLIDE_DURATION = 10_000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        String category = getIntent().getStringExtra(StartActivity.EXTRA_CATEGORY);
        String intentLang = getIntent().getStringExtra(StartActivity.EXTRA_LANG);
        lang = (intentLang != null) ? intentLang : AppPrefs.getLang(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (locked) {
                    return;
                }
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });


        btnAuto = findViewById(R.id.btnAuto);
        btnLock = findViewById(R.id.btnLock);
        btnMute = findViewById(R.id.btnMute);

        btnAuto.setSelected(AppPrefs.getAutoplay(this));
        btnLock.setSelected(AppPrefs.getLocked(this));
        btnMute.setSelected(AppPrefs.getMute(this));

        locked = btnLock.isSelected();
        applyImmersive(locked);

        btnLock.setOnClickListener(v -> {
            btnLock.setSelected(!btnLock.isSelected());
            locked = btnLock.isSelected();
            applyImmersive(locked);
        });

        btnAuto.setOnClickListener(v -> {
            btnAuto.setSelected(!btnAuto.isSelected());
            autoPlay = btnAuto.isSelected();
            AppPrefs.setAutoplay(PlayerActivity.this, autoPlay);

            if (autoPlay) startAutoPlay();
            else stopAutoPlay();
        });

        btnLock.setOnClickListener(v -> {
            btnLock.setSelected(!btnLock.isSelected());
            locked = btnLock.isSelected();
            AppPrefs.setLocked(PlayerActivity.this, locked);
            applyImmersive(locked);
        });
        btnMute.setOnClickListener(v -> {
            btnMute.setSelected(!btnMute.isSelected());
            mute = btnMute.isSelected();
            AppPrefs.setMute(PlayerActivity.this, mute);

            if (mute && tts != null) tts.stop();
        });


        mute = btnMute.isSelected();


        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {

                Locale locale;
                if ("uk".equals(lang)) {
                    locale = new Locale("uk", "UA");
                } else {
                    locale = Locale.US;
                }

                int result = tts.setLanguage(locale);

                ttsReady = result != TextToSpeech.LANG_MISSING_DATA
                        && result != TextToSpeech.LANG_NOT_SUPPORTED;

                if (!ttsReady) {
                    Log.w("TTS", "Language NOT supported: " + locale);
                } else {
                    Log.i("TTS", "Language OK: " + locale);

                    runOnUiThread(() -> {
                        if (!autoPlay) speakCurrent();
                    });
                }
            } else {
                Log.e("TTS", "TTS init failed: " + status);
                ttsReady = false;
            }
        });

        List<Animal> all = AnimalsRepository.loadAll(this);
        if ("all".equals(category)) {
            items = all;
        } else {
            items = AnimalsRepository.filterByCategory(all, category);
        }
        Collections.shuffle(items);

        TextView title = findViewById(R.id.txtTitle);
        pager = findViewById(R.id.pager);

        pager.setAdapter(new SlidesAdapter(this, items));

        if (!items.isEmpty()) {
            title.setText(getName(items.get(0)));
        } else {
            title.setText("No items");
        }

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                title.setText(getName(items.get(position)));
                if (!autoPlay) {
                    speakName(items.get(position));
                }

                if (autoPlay) {
                    startAutoPlay();
                }
            }

        });
    }

    private void startAutoPlay() {
        stopAutoPlay();

        autoPlay = true;

        speak2Runnable = () -> speakCurrent();
        speak7Runnable = () -> speakCurrent();

        slideRunnable = () -> {
            int next = (pager.getCurrentItem() + 1) % items.size();
            pager.setCurrentItem(next, true);
            startAutoPlay();
        };

        handler.postDelayed(speak2Runnable, 2_000);
        handler.postDelayed(speak7Runnable, 7_000);
        handler.postDelayed(slideRunnable, SLIDE_DURATION);
    }

    private void stopAutoPlay() {
        autoPlay = false;
        handler.removeCallbacksAndMessages(null);
    }


    private String getName(Animal a) {
        return "en".equals(lang) ? a.nameEn : a.nameUk;
    }

    private void speakName(Animal a) {
        if (mute) return;
        if (!ttsReady || tts == null) return;

        String text = "en".equals(lang) ? a.nameEn : a.nameUk;

        String utteranceId = "name_" + a.id + "_" + System.currentTimeMillis();

        tts.stop();
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

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoPlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyImmersive(locked);

        if (btnAuto.isSelected()) {
            startAutoPlay();
        }
    }

    private void applyImmersive(boolean enabled) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), !enabled);

        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());

        if (enabled) {
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars());
        }
    }

}
