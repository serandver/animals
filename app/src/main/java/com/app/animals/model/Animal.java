package com.app.animals.model;

public class Animal {
    public final String id;
    public final String category;       // animals / birds / insects ...
    public final String nameUk;
    public final String nameEn;
    public final String imageAssetPath;

    public Animal(String id, String category, String nameUk, String nameEn, String imageAssetPath) {
        this.id = id;
        this.category = category;
        this.nameUk = nameUk;
        this.nameEn = nameEn;
        this.imageAssetPath = imageAssetPath;
    }
}
