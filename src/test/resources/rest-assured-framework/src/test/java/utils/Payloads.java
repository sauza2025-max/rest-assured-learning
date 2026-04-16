package utils;

import java.util.HashMap;
import java.util.Map;

public class Payloads {

    public static Map<String, Object> createUser() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "John");
        body.put("job", "QA Engineer");
        return body;
    }

    public static Map<String, Object> updateUser() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "John Updated");
        body.put("job", "Senior QA");
        return body;
    }
}
