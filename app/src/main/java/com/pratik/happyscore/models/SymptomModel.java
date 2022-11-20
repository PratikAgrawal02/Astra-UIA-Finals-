package com.pratik.happyscore.models;

public class SymptomModel {
    String name;
    String detail;
    int image;
    Boolean isSelected;
    float contri;

    public SymptomModel(String name, String detail, int image, Boolean isSelected,float contri) {
        this.name = name;
        this.detail = detail;
        this.image = image;
        this.isSelected = isSelected;
        this.contri = contri;
    }

    public float getContri() {
        return contri;
    }

    public void setContri(float contri) {
        this.contri = contri;
    }



    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public SymptomModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
