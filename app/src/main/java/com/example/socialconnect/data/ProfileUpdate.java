package com.example.socialconnect.data;

import java.util.HashMap;
import java.util.Map;

/** Builds only editable fields; omitted images preserve the stored avatar. */
public final class ProfileUpdate {
    private ProfileUpdate() {}
    public static Map<String, Object> fields(String name, String bio, String newImageUrl) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("name", name.trim());
        fields.put("bio", bio.trim());
        if (newImageUrl != null) fields.put("imageUrl", newImageUrl);
        return fields;
    }
}
