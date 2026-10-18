package com.deepconnect.ats.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleUtils {

   private static int defaultRoundDecimalPlaces = 2;

    public static double round(double input, int places, RoundingMode roundingMode) {
        if (places < 0) throw new IllegalArgumentException();
    
        BigDecimal bd = BigDecimal.valueOf(Math.round(input * 100000), 5);
        bd = bd.setScale(places, roundingMode);
        return bd.doubleValue();
    }

    public static double round(double input, int places) {
        return round(input, places, RoundingMode.HALF_UP);
    }

    public static double roundDefault(Double input) {
        return (input == null) ? 0.00 : round(input, defaultRoundDecimalPlaces, RoundingMode.HALF_UP);
    }

    public static int getIntValue(double input){
        return (int)(input - (input % 1));
    }

    public static double getOrElse(Double input, double valueIfNull){
        return (input == null) ? valueIfNull : input.doubleValue();
    }

    public static double get(Double input){
        return (input == null) ? 0.00 : input.doubleValue();
    }

    public static double roundAndGet(Double input, int places) {
        return (input == null) ? 0.00 : round(input, places, RoundingMode.HALF_UP);
    }
    
}
