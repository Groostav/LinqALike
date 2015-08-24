package com.empowerops.linqalike.common;

/**
 * Created by Geoff on 18/12/2014.
 */
public enum OnFailureToGaurenteeConvertableType {
    FAIL_IMMEDIATELY,
    BEST_EFFORT,
    RUN_ALWAYS,
    ;

    public static final String PropertyName = "com.empowerops.linqalike.OnFailureToGaurenteeConvertableType";

    static {
        if (System.getProperty(PropertyName) == null) {
            System.setProperty(PropertyName, BEST_EFFORT.toString());
        }
    }

    public static OnFailureToGaurenteeConvertableType get(){
        return OnFailureToGaurenteeConvertableType.valueOf(System.getProperty(PropertyName));
    }
}
