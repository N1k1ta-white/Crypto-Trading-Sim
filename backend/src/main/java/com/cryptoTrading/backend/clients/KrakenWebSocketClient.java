package com.cryptoTrading.backend.clients;

import com.cryptoTrading.backend.dto.CryptocurrencyDto;
import com.cryptoTrading.backend.service.CryptoService;
import com.cryptoTrading.backend.entity.Crypto;
import com.cryptoTrading.backend.repository.CryptoInfoRepository;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

// TODO: Implement exception throwing for all methods
// TODO: Refactor to implement repository pattern for cryptocurrency data management
// - Create a separate CryptocurrencyRepository interface
// - Implement in-memory and/or persistent storage implementations
// - Decouple data access from WebSocket client logic

@Component
public class KrakenWebSocketClient extends WebSocketClient {
    private static final Logger logger = Logger.getLogger(KrakenWebSocketClient.class.getName());
    private static final int RECONNECT_DELAY_MS = 5000;
    private static final int CONNECTION_LOST_TIMEOUT = 10;
    private static final String TICKER_CHANNEL = "ticker";
    private static final String SUBSCRIBE_METHOD = "subscribe";
    private static final String CHANNEL_PARAM = "channel";
    private static final String SYMBOL_PARAM = "symbol";
    private static final String METHOD_PARAM = "method";
    private static final String PARAMS_PARAM = "params";
    private static final String DATA_FIELD = "data";
    private static final String TYPE_PARAM = "type";
    private static final int FIRST_ELEMENT = 0;

    private final Gson gson = new Gson();
    private boolean reconnectOnClose = true;
    private final SimpMessagingTemplate messagingTemplate;

    private final CryptoService cryptoService;
    private final CryptoInfoRepository cryptoPricesRepository;

    private enum MessageType {
        HEARTBEAT("heartbeat"),
        SNAPSHOT("snapshot"),
        UPDATE("update"),
        STATUS("status"),
        UNKNOWN("unknown"),
        ERROR("error");

        private final String field;

        MessageType(String field) {
            this.field = field;
        }
    
        public static MessageType determineMessageType(JsonObject jsonObject) {
            try {
                if (jsonObject.has(ERROR.field)) {
                    return MessageType.ERROR;
                }

                String channel = jsonObject.get(CHANNEL_PARAM).getAsString();
                if (channel.equals(HEARTBEAT.field)) {
                    return HEARTBEAT;
                } else if (channel.equals(STATUS.field)) {
                    return STATUS;
                }
    
                String type = jsonObject.get(TYPE_PARAM).getAsString();
                if (type.equals(SNAPSHOT.field)) {
                    return SNAPSHOT;
                } else if (type.equals(UPDATE.field)) {
                    return UPDATE;
                } else {
                    return UNKNOWN;
                }
            } catch (Exception e) {
                return MessageType.UNKNOWN;
            }
        }
    }

    public KrakenWebSocketClient(@Value("${kraken.websocket.url}") String url, 
                                    SimpMessagingTemplate messagingTemplate,
                                    CryptoService cryptoService,
                                    CryptoInfoRepository cryptoPricesRepository) throws Exception {
        super(new URI(url));
        this.messagingTemplate = messagingTemplate;
        this.cryptoService = cryptoService;
        this.cryptoPricesRepository = cryptoPricesRepository;
        this.setConnectionLostTimeout(CONNECTION_LOST_TIMEOUT);
    }

    // Main WebSocket lifecycle methods
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("WebSocket connection opened. Status: " + handshakedata.getHttpStatus());
        subscribeToTicker(cryptoService.getAllCryptos().stream()
                                                       .map(Crypto::getCode)
                                                       .map(code -> code + "/USD")
                                                       .toArray(String[]::new));
    }

    @Override
    public void onMessage(String message) {
        try {
            JsonObject parsedMessage = gson.fromJson(message, JsonObject.class);
            processMessageByType(parsedMessage);
        } catch (Exception e) {
            logger.warning("Error processing message: " + e.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("WebSocket connection closed. Code: " + code + ", Reason: " + reason + ", Remote: " + remote);
        if (reconnectOnClose) {
            attemptReconnect();
        }
    }

    @Override
    public void onError(Exception e) {
        logger.severe("WebSocket error: " + e.getMessage());
        e.printStackTrace();
    }

    @EventListener(ContextClosedEvent.class)
    public void shutdown() {
        reconnectOnClose = false;
        close();
    }

    public Collection<CryptocurrencyDto> fetchTradingPairs() {
        return cryptoPricesRepository.getAllCryptocurrencyInfo();
    }
    
    private void processMessageByType(JsonObject messageData) {
        MessageType messageType = MessageType.determineMessageType(messageData);

        switch (messageType) {
            case ERROR:
                handleErrorMessage(messageData);
            break;
            case HEARTBEAT:
                handleHeartbeat(messageData);
            break;
            case SNAPSHOT:
                handleTickerSnapshot(messageData.get(DATA_FIELD));
            break;
            case UPDATE:
                handleTickerUpdate(messageData.get(DATA_FIELD));
            break;
            case STATUS:
                handleStatusMessage(messageData.get(DATA_FIELD));
            break;
            default:
                logger.info("Unhandled message type: " + messageType + " - " + messageData);
            break;
        }
    }

    private void handleHeartbeat(JsonObject object) {
        logger.fine("Received heartbeat");
    }

    private void handleErrorMessage(JsonObject object) {
        logger.warning("Received error message: " + object.get(MessageType.ERROR.field));
    }

    private void handleTickerSnapshot(JsonElement obj) {
        try {
            CryptocurrencyDto cryptoDto = gson.fromJson(obj, CryptocurrencyDto[].class)[FIRST_ELEMENT];
            cryptoPricesRepository.addCryptocurrencyInfo(cryptoDto);
            // logger.info("Received ticker snapshot");
        } catch (Exception e) {
            logger.warning("Error processing ticker update: " + e.getMessage());
        }
    }

    private void handleTickerUpdate(JsonElement obj) {
        try {
            CryptocurrencyDto newCryptoDto = gson.fromJson(obj, CryptocurrencyDto[].class)[FIRST_ELEMENT];
            newCryptoDto = cryptoPricesRepository.updateCryptocurrencyInfo(newCryptoDto);

            messagingTemplate.convertAndSend("/update/crypto", newCryptoDto);
            // logger.info("Received ticker update");
        } catch (Exception e) {
            logger.warning("Error processing ticker update: " + e.getMessage());
        }
    }

    private void handleStatusMessage(JsonElement object) {
        logger.warning("Received status message: " + object);
    }

    private void subscribeToTicker(String[] pairs) {
        if (pairs == null || pairs.length == 0) {
            logger.warning("No trading pairs provided for subscription");
            return;
        }

        try {
            String request = createSubscriptionRequest(pairs);
            logger.info("Subscribing to ticker with pairs: " + String.join(", ", pairs));
            logger.info("Subscription payload: " + request);
            send(request);
        } catch (Exception e) {
            logger.severe("Error subscribing to ticker: " + e.getMessage());
        }
    }

    private String createSubscriptionRequest(String[] pairs) {
        Map<String, Object> params = new HashMap<>();
        params.put(CHANNEL_PARAM, TICKER_CHANNEL);
        params.put(SYMBOL_PARAM, pairs);
        
        Map<String, Object> subscription = new HashMap<>();
        subscription.put(METHOD_PARAM, SUBSCRIBE_METHOD);
        subscription.put(PARAMS_PARAM, params);
        
        return gson.toJson(subscription);
    }

    private void attemptReconnect() {
        new Thread(() -> {
            try {
                logger.info("Attempting to reconnect in " + RECONNECT_DELAY_MS/1000 + " seconds");
                Thread.sleep(RECONNECT_DELAY_MS);
                reconnect();
            } catch (Exception e) {
                logger.severe("Failed to reconnect: " + e.getMessage());
            }
        }).start();
    }
}
