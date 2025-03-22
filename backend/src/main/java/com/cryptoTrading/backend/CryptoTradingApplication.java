package com.cryptoTrading.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cryptoTrading.backend.clients.KrakenWebSocketClient;

@SpringBootApplication
public class CryptoTradingApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(CryptoTradingApplication.class, args);
	}

	@Autowired
	private KrakenWebSocketClient krakenWebSocketClient;

	@Override
	public void run(String... args) throws Exception {
		krakenWebSocketClient.connect();
	}
}
