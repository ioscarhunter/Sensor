package com.starboy.karav.sensor.Bluetooth;

/**
 * Created by Oscar on 1/7/2016.
 */
public class Command {
    public double pitch = 0;
    public double row = 0;
    public int what;

    public static Command newCommand(int w, double p, double r) {
        Command c = new Command();
        c.pitch = p;
        c.row = r;
        c.what = w;

        return c;
    }

    public static Command newCommand(int w) {
        Command c = new Command();
        c.what = w;

        return c;
    }


}
