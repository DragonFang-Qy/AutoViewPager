package com.fangzitcl.autoviewpager.model;

/**
 * class_name: Gravity
 * package_name: com.fangzitcl.autoviewpager.model
 * acthor: Fang_QingYou
 * time: 2015.12.27 9:02
 */
public enum Gravity {
    left(0), center(1), right(2);

    private final int id;

    Gravity(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }

    public static Gravity isGravity(int i) {
        if (i == Gravity.left.getValue()) {
            return Gravity.left;
        } else if (i == Gravity.center.getValue()) {
            return Gravity.center;
        } else if (i == Gravity.right.getValue()) {
            return Gravity.right;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
