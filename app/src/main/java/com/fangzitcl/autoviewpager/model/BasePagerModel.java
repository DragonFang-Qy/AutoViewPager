package com.fangzitcl.autoviewpager.model;

import java.io.Serializable;

/**
 * class_name: BasePagerModel
 * package_name: com.fangzitcl.autoviewpager.model
 * acthor: Fang_QingYou
 * time: 2015.12.22 23:38
 */
public class BasePagerModel implements Serializable {

    private String title;
    private String imagePath;

    public BasePagerModel() {
    }

    public BasePagerModel(String title, String imagePath) {
        this.title = title;
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "title = " + title + "\nimagePath = " + imagePath;
    }
}
