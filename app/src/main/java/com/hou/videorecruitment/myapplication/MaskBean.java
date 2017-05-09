package com.hou.videorecruitment.myapplication;

/**
 * Created by hmj on 17/5/8.
 */

public class MaskBean {
    private String name;
    private int maskIcon;
    private int paintColor;

    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    private int backgroundColor;

    public int getMaskIcon() {
        return maskIcon;
    }

    public void setMaskIcon(int maskIcon) {
        this.maskIcon = maskIcon;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
