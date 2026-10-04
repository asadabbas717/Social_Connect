package com.example.socialconnect.data;

import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class ProfileUpdateTest {
    @Test public void editWithoutImagePreservesAvatarAndMetadata() {
        Map<String, Object> stored = new HashMap<>();
        stored.put("imageUrl", "https://example.test/avatar.jpg");
        stored.put("createdAt", 123L);
        stored.put("fcmToken", "test-token");
        stored.putAll(ProfileUpdate.fields(" Ada ", " Engineer ", null));
        assertEquals("https://example.test/avatar.jpg", stored.get("imageUrl"));
        assertEquals(123L, stored.get("createdAt"));
        assertEquals("test-token", stored.get("fcmToken"));
        assertEquals("Ada", stored.get("name"));
        assertEquals("Engineer", stored.get("bio"));
    }
    @Test public void selectedImageReplacesOnlyAvatar() {
        Map<String, Object> update = ProfileUpdate.fields("Ada", "", "https://example.test/new.jpg");
        assertEquals("https://example.test/new.jpg", update.get("imageUrl"));
        assertFalse(update.containsKey("createdAt"));
        assertFalse(update.containsKey("fcmToken"));
    }
}
