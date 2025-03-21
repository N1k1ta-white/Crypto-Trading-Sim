package com.cryptoTrading.backend.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class KrakenWebSocketClient extends WebSocketClient {
    private static final Logger logger = Logger.getLogger(KrakenWebSocketClient.class.getName());

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public KrakenWebSocketClient(@Value("${kraken.websocket.url}") String url) throws Exception {
        super(new URI(url));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("Connection opened");
        subscribeToTicker(new String[]{"BTC/USD", "MATIC/GBP"});
    }

    @Override
    public void onMessage(String message) {
        logger.info("Received message: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("Connection closed");
    }

    @Override
    public void onError(Exception e) {
        logger.severe("Error: " + e.getMessage());
    }

    private void subscribeToTicker(String[] pairs) {
        Map<String, Object> subscription = Map.of(
                "event", "subscribe",
                "pair", pairs,
                "subscription", Map.of("name", "ticker")
        );
        try {
            String request = objectMapper.writeValueAsString(subscription);
            send(request);
        } catch (Exception e) {
            logger.severe("Error subscribing to ticker: " + e.getMessage());
        }
    }

}
