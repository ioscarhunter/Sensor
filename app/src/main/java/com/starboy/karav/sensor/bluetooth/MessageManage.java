package com.starboy.karav.sensor.bluetooth;

import static com.starboy.karav.sensor.bluetooth.Command.newCommand;

/**
 * Created by Oscar on 1/7/2016.
 */
public class MessageManage {
    public static Command Decode(String code) {
        String receive[] = code.split(":");
        if (receive.length < 2) {
            return newCommand(Integer.parseInt(receive[0]));
        } else {
            return newCommand(Integer.parseInt(receive[0]), Double.parseDouble(receive[1]), Double.parseDouble(receive[2]));
        }

    }

    public static String Encode(Command com) {

        String out = "";
        out += com.what;
        out += ":";
        out += String.format("%.2f", com.pitch);
        out += ":";
        out += String.format("%.2f", com.row);
        out += ":";
        return out;
    }
}
