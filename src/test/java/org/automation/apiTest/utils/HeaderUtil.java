package org.automation.apiTest.utils;

import java.util.HashMap;
import java.util.Map;

public class HeaderUtil {

    public static Map<String, Object> getDefaultHeaders() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
       // headers.put("Authorization", "Bearer <token>");
        return headers;
    }
}
