package io.elastic.petstore.triggers;

import com.sun.net.httpserver.HttpServer;
import jakarta.json.JsonArray;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class GetPetsByStatusJaxRsTest {
    @Test
    public void readsAJsonArrayFromTheAuthenticatedStatusQuery() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        AtomicReference<String> query = new AtomicReference<>();
        AtomicReference<String> key = new AtomicReference<>();
        server.createContext("/v2/pet/findByStatus", exchange -> {
            query.set(exchange.getRequestURI().getRawQuery());
            key.set(exchange.getRequestHeaders().getFirst("api-key"));
            byte[] body = "[{\"name\":\"Test Pet\",\"status\":\"pending\"}]".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (java.io.OutputStream stream = exchange.getResponseBody()) {
                stream.write(body);
            }
        });
        server.start();
        Client client = ClientBuilder.newClient();
        try {
            String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/v2/pet/findByStatus";
            JsonArray pets = GetPetsByStatusJaxRs.findPets(client.target(url), "pending", "test-key");
            assertEquals("status=pending", query.get());
            assertEquals("test-key", key.get());
            assertEquals(1, pets.size());
            assertEquals("Test Pet", pets.getJsonObject(0).getString("name"));
            assertEquals("pending", pets.getJsonObject(0).getString("status"));
        } finally {
            client.close();
            server.stop(0);
        }
    }
}
