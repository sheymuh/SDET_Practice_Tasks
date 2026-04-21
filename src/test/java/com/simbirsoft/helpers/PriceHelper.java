package com.simbirsoft.helpers;

public class PriceHelper {

    public static double parsePrice(String priceText) {
        String cleaned = priceText.replaceAll("[^0-9.,]", "")
                .replace(",", ".")
                .trim();
        return Double.parseDouble(cleaned);
    }
}
