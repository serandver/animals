package com.app.animals.data;

import android.content.Context;
import android.content.SharedPreferences;

public final class AppPrefs {

    private static final String PREFS = "animals_prefs";

    private static final String KEY_LANG = "lang";          // "uk" / "en"
    private static final String KEY_AUTOPLAY = "autoplay";  // boolean
    private static final String KEY_MUTE = "mute";          // boolean
    private static final String KEY_LOCKED = "locked";      // boolean

    private AppPrefs() {}

    private static SharedPreferences p(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static String getLang(Context c) {
        return p(c).getString(KEY_LANG, "en");
    }

    public static void setLang(Context c, String lang) {
        p(c).edit().putString(KEY_LANG, lang).apply();
    }

    public static boolean getAutoplay(Context c) {
        return p(c).getBoolean(KEY_AUTOPLAY, false);
    }

    public static void setAutoplay(Context c, boolean v) {
        p(c).edit().putBoolean(KEY_AUTOPLAY, v).apply();
    }

    public static boolean getMute(Context c) {
        return p(c).getBoolean(KEY_MUTE, false);
    }

    public static void setMute(Context c, boolean v) {
        p(c).edit().putBoolean(KEY_MUTE, v).apply();
    }

    public static boolean getLocked(Context c) {
        return p(c).getBoolean(KEY_LOCKED, false);
    }

    public static void setLocked(Context c, boolean v) {
        p(c).edit().putBoolean(KEY_LOCKED, v).apply();
    }
}
