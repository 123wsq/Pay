package com.lk.td.pay.beans;

/**
 * Created by wsq on 2016/5/6.
 */
public class MainDataBean {

    int id;
    String name;
    boolean state;
    String imgPath;
    boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public String toString() {
        return "Data:[id = "+ id +"," +
                " name = "+ name +"," +
                " state = "+ state +"," +
                " url = "+ imgPath +" ]";
    }
}
