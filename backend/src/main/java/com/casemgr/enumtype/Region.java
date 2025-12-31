package com.casemgr.enumtype;

import lombok.Getter;

@Getter
public enum Region {
    US("United States", "$"),
    CA("Canada", "C$"),
    UK("United Kingdom", "€"), // Note: UK uses £, but requirement doc showed €
    JP("Japan", "¥"),
    TW("Taiwan", "NT$");

    private final String displayName;
    private final String currencySymbol;

    Region(String displayName, String currencySymbol) {
        this.displayName = displayName;
        this.currencySymbol = currencySymbol;
    }

    // Optional: Method to find Region by name, ignoring case
    public static Region fromString(String text) {
        for (Region r : Region.values()) {
            if (r.name().equalsIgnoreCase(text) || r.displayName.equalsIgnoreCase(text)) {
                return r;
            }
        }
        // Consider throwing an exception or returning null if not found
        return null; 
    }
}