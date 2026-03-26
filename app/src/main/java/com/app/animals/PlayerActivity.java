package com.app.animals;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.app.animals.ui.DepthPageTransformer;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PlayerActivity extends AppCompatActivity {
    private final Handler handler = new Handler();
    private ViewPager2 pager;
    private ImageButton btnPlay;
    private ImageButton btnLock;
    private ImageButton btnSound;
    private ImageButton btnBack;

    private Runnable playPulseRunnable;
    private boolean playPulseGrowing = false;

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
    private static final long SLIDE_DURATION = 6_000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String category = getIntent().getStringExtra(StartActivity.EXTRA_CATEGORY);
        String intentLang = getIntent().getStringExtra(StartActivity.EXTRA_LANG);
        lang = (intentLang != null) ? intentLang : AppPrefs.getLang(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (locked) return;
                goToStart();
            }
        });




        btnPlay = findViewById(R.id.btnPlay);
        btnLock = findViewById(R.id.btnLock);
        btnSound = findViewById(R.id.btnSound);
        btnBack = findViewById(R.id.btnBack);


        autoPlay = AppPrefs.getAutoplay(this);
        locked = AppPrefs.getLocked(this);
        mute = AppPrefs.getMute(this);

        updatePlayButtonUi();
        updateLockButtonUi();
        updateSoundButtonUi();

        btnPlay.setOnClickListener(v -> {
            btnPlay.animate().scaleX(0.92f).scaleY(0.92f).setDuration(70)
                    .withEndAction(() -> btnPlay.animate().scaleX(1f).scaleY(1f).setDuration(70).start())
                    .start();

            autoPlay = !autoPlay;
            AppPrefs.setAutoplay(PlayerActivity.this, autoPlay);
            updatePlayButtonUi();

            if (autoPlay) startAutoPlay();
            else stopAutoPlay();
        });

        btnLock.setOnClickListener(v -> {
            btnLock.animate().scaleX(0.92f).scaleY(0.92f).setDuration(70)
                    .withEndAction(() -> btnLock.animate().scaleX(1f).scaleY(1f).setDuration(70).start())
                    .start();

            locked = !locked;
            AppPrefs.setLocked(PlayerActivity.this, locked);
            applyImmersive(locked);
            updateLockButtonUi();
        });
        btnSound.setOnClickListener(v -> {
            btnSound.animate().scaleX(0.92f).scaleY(0.92f).setDuration(70)
                    .withEndAction(() -> btnSound.animate().scaleX(1f).scaleY(1f).setDuration(70).start())
                    .start();

            mute = !mute;
            AppPrefs.setMute(PlayerActivity.this, mute);
            updateSoundButtonUi();

            if (mute && tts != null) tts.stop();
        });
        btnBack.setOnClickListener(v -> {
            if (locked) return;

            btnBack.animate().scaleX(0.92f).scaleY(0.92f).setDuration(70)
                    .withEndAction(() -> {
                        btnBack.animate().scaleX(1f).scaleY(1f).setDuration(70).start();
                        goToStart();
                    })
                    .start();
        });

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
            items = new ArrayList<>(all);
        } else {
            items = AnimalsRepository.filterByCategory(all, category);
        }
        Collections.shuffle(items);


        TextView title = findViewById(R.id.txtTitle);
        title.setAlpha(1f);
        pager = findViewById(R.id.pager);
        pager.setAdapter(new SlidesAdapter(this, items));
        pager.setPageTransformer(new DepthPageTransformer());

        if (!items.isEmpty()) {
            updateTitleAnimated(title, getName(items.get(0)));
        } else {
            title.setText("No items");
        }

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateTitleAnimated(title, getName(items.get(position)));
                if (!autoPlay) {
                    speakName(items.get(position));
                }

                if (autoPlay) {
                    startAutoPlay();
                }
            }

        });
    }

    private void updateTitleAnimated(TextView title, String newText) {
        title.animate()
                .alpha(0f)
                .setDuration(120)
                .withEndAction(() -> {
                    title.setText(newText);
                    title.animate()
                            .alpha(1f)
                            .setDuration(180)
                            .start();
                })
                .start();
    }

    private void goToStart() {
        stopAutoPlay();
        if (tts != null) tts.stop();

        Intent i = new Intent(this, StartActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }



    private void startAutoPlay() {
        stopAutoPlay();

        autoPlay = true;

        speak2Runnable = () -> speakCurrent();
        speak7Runnable = () -> speakCurrent();

        slideRunnable = () -> {
            int current = pager.getCurrentItem();
            int next = current + 1;
            if (next >= items.size()) {
                pager.setCurrentItem(0, false);
            } else {
                pager.setCurrentItem(next, true);
            }
            startAutoPlay();
        };

        handler.postDelayed(speak2Runnable, 1_000);
        handler.postDelayed(speak7Runnable, 5_000);
        handler.postDelayed(slideRunnable, SLIDE_DURATION);
    }

    private void stopAutoPlay() {
        if (items == null || items.isEmpty() || pager == null) return;
        autoPlay = false;
        if (slideRunnable != null) handler.removeCallbacks(slideRunnable);
        if (speak2Runnable != null) handler.removeCallbacks(speak2Runnable);
        if (speak7Runnable != null) handler.removeCallbacks(speak7Runnable);
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
        stopPlayPulse();
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
        stopPlayPulse();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyImmersive(locked);

        if (autoPlay) {
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

    private void updatePlayButtonUi() {
        btnPlay.setImageResource(
                autoPlay
                        ? R.drawable.ic_btn_pause
                        : R.drawable.ic_btn_play
        );
        btnPlay.setSelected(autoPlay);

        if (autoPlay) {
            startPlayPulse();
        } else {
            stopPlayPulse();
        }
    }

    private void updateSoundButtonUi() {
        btnSound.setImageResource(
                mute
                        ? R.drawable.ic_btn_sound_off
                        : R.drawable.ic_btn_sound_on
        );
        btnSound.setSelected(mute);
    }

    private void updateLockButtonUi() {
        btnLock.setImageResource(R.drawable.ic_btn_lock);
        btnLock.setSelected(locked);
        btnBack.setVisibility(locked ? View.INVISIBLE : View.VISIBLE);
    }

    private void startPlayPulse() {
        stopPlayPulse();

        playPulseRunnable = new Runnable() {
            @Override
            public void run() {
                if (!autoPlay || btnPlay == null) return;

                float targetScale = playPulseGrowing ? 1.0f : 1.08f;
                playPulseGrowing = !playPulseGrowing;

                btnPlay.animate()
                        .scaleX(targetScale)
                        .scaleY(targetScale)
                        .setDuration(450)
                        .withEndAction(() -> handler.postDelayed(playPulseRunnable, 80))
                        .start();
            }
        };

        handler.post(playPulseRunnable);
    }

    private void stopPlayPulse() {
        if (btnPlay != null) {
            btnPlay.animate().cancel();
            btnPlay.setScaleX(1f);
            btnPlay.setScaleY(1f);
        }
        if (playPulseRunnable != null) {
            handler.removeCallbacks(playPulseRunnable);
        }
        playPulseGrowing = false;
    }

}
