package org.avitoparser.utils;

import java.util.List;
import java.util.Map;

/**
 * Utils for manipulation with data
 */
public class DataUtils {
    public static boolean isEmpty(String item) {
        return item == null || item.trim().isEmpty();
    }

    public static boolean isEmpty(List items) {
        return items == null || items.size() == 0;
    }

    public static boolean isEmpty(Map itemMap) {
        return itemMap == null || itemMap.isEmpty();
    }
}
