package com.app.animals.data;

import android.content.Context;

import com.app.animals.model.Animal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AnimalsRepository {

    public static List<Animal> loadAll(Context context) {
        try {
            String json = readAsset(context, "data/animals.json");
            JSONObject root = new JSONObject(json);
            JSONArray animals = root.getJSONArray("animals");

            List<Animal> result = new ArrayList<>(animals.length());
            for (int i = 0; i < animals.length(); i++) {
                JSONObject a = animals.getJSONObject(i);

                String id = a.getString("id");
                String category = a.optString("category", "animals"); // fallback
                JSONObject names = a.getJSONObject("names");
                String uk = names.optString("uk", id);
                String en = names.optString("en", id);
                String image = a.getString("image");

                result.add(new Animal(id, category, uk, en, image));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load animals.json", e);
        }
    }

    public static List<String> getCategories(List<Animal> all) {
        Set<String> set = new LinkedHashSet<>();
        for (Animal a : all) set.add(a.category);
        return new ArrayList<>(set);
    }

    public static List<Animal> filterByCategory(List<Animal> all, String category) {
        List<Animal> out = new ArrayList<>();
        for (Animal a : all) {
            if (a.category.equalsIgnoreCase(category)) out.add(a);
        }
        return out;
    }

    private static String readAsset(Context context, String path) throws Exception {
        InputStream is = context.getAssets().open(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }
}
