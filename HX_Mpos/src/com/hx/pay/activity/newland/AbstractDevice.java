package com.hx.pay.activity.newland;


public abstract class AbstractDevice {
    public abstract void initController();

    public abstract void disconnect();

    public abstract boolean isControllerAlive();

    public abstract BuletootchController getController();

    public abstract void connectDevice();
}
