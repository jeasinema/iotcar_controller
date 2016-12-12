package org.icenter.mqtt_car;

/**
 * Created by jeasinema on 13/12/16.
 */
public class Misc {

    public static boolean check_speed_valid(int speed)
    {
        if (speed >= R.integer.min_speed && speed <= R.integer.min_speed)
            return true;
        else
            return false;
    }

    public static boolean check_addr_valid(String addr)
    {
        return true;
    }

    public static boolean check_port_valid(int port)
    {
        if (port < 65536 && port > 0)
            return true;
        else
            return false;
    }
}
