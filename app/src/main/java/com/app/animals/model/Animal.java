package com.app.animals.model;

public class Animal {
    public final String id;
    public final String nameUk;
    public final String nameEn;
    public final String imageAssetPath; // e.g. "images/cat.webp"

    public Animal(String id, String nameUk, String nameEn, String imageAssetPath) {
        this.id = id;
        this.nameUk = nameUk;
        this.nameEn = nameEn;
        this.imageAssetPath = imageAssetPath;
    }
}
