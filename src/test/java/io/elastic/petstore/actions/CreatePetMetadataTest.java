package io.elastic.petstore.actions;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CreatePetMetadataTest {
    @Test
    public void acceptsAProviderAcknowledgementWithOnlyNameAndStatus() throws Exception {
        try (InputStream input = Files.newInputStream(Paths.get("schemas", "createPet.out.json"));
             JsonReader reader = Json.createReader(input)) {
            JsonObject fields = reader.readObject().getJsonObject("properties");
            assertTrue(fields.getJsonObject("name").getBoolean("required"));
            assertTrue(fields.getJsonObject("status").getBoolean("required"));
            assertFalse(fields.getJsonObject("id").getBoolean("required"));
            assertFalse(fields.getJsonObject("category").getBoolean("required"));
        }
    }
}
