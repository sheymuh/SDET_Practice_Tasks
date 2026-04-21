package com.simbirsoft.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssertionHelper {

    public static void assertSortedByNameAsc(List<String> actual) {
        List<String> sorted = new ArrayList<>(actual);
        sorted.sort(String.CASE_INSENSITIVE_ORDER);
        assertEquals(sorted, actual, "Products are not sorted by name ascending");
    }

    public static void assertSortedByNameDesc(List<String> actual) {
        List<String> sorted = new ArrayList<>(actual);
        sorted.sort(String.CASE_INSENSITIVE_ORDER.reversed());
        assertEquals(sorted, actual, "Products are not sorted by name descending");
    }

    public static void assertSortedByPriceAsc(List<Double> actual) {
        for (int i = 0; i < actual.size() - 1; i++) {
            assertTrue(actual.get(i) <= actual.get(i + 1),
                    String.format("Price at index %d (%f) > price at index %d (%f)",
                            i, actual.get(i), i + 1, actual.get(i + 1)));
        }
    }

    public static void assertSortedByPriceDesc(List<Double> actual) {
        for (int i = 0; i < actual.size() - 1; i++) {
            assertTrue(actual.get(i) >= actual.get(i + 1),
                    String.format("Price at index %d (%f) < price at index %d (%f)",
                            i, actual.get(i), i + 1, actual.get(i + 1)));
        }
    }
}
