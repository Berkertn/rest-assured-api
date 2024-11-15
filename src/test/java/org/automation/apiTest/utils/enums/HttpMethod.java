package org.automation.apiTest.utils.enums;

public enum HttpMethod {
    GET,
    POST,
    DELETE,
    PATCH,
    PUT;

    public static HttpMethod getHttpMethodFromKey(String method) {
        try {
            return HttpMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported HTTP method: " + method +
                    ". Allowed methods are: GET, POST, DELETE, PATCH, PUT.");
        }
    }
}
