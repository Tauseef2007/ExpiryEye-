package com.expiryeye.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ItemCategory {
    GROCERY,
    MEDICINE,
    WARRANTY,
    INSURANCE,
    VEHICLE_DOCUMENT,
    MOBILE_WIFI_RECHARGE, // <--- Hyphen ki jagah underscore kiya gaya hai
    OTHER;

    @JsonCreator
    public static ItemCategory fromString(String value) {
        if (value == null) return OTHER;
        
        String val = value.toUpperCase().trim();
        
        if (val.contains("GROCER")) return GROCERY;
        if (val.contains("MEDIC")) return MEDICINE;
        if (val.contains("WARRANT")) return WARRANTY;
        if (val.contains("INSUR")) return INSURANCE;
        if (val.contains("VEHICLE")) return VEHICLE_DOCUMENT;
        // New condition:
        if (val.contains("MOBILE") || val.contains("WIFI") || val.contains("RECHARGE")) return MOBILE_WIFI_RECHARGE;
        
        try {
            return ItemCategory.valueOf(val);
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}