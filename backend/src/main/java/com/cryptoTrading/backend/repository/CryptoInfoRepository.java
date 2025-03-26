package com.cryptoTrading.backend.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cryptoTrading.backend.dto.CryptocurrencyDto;

@Repository
public class CryptoInfoRepository {
        Map<String, CryptocurrencyDto> cryptocurrencyDTOMap = new HashMap<>();

        public void addCryptocurrencyInfo(CryptocurrencyDto cryptocurrencyDto) {
                cryptocurrencyDTOMap.put(cryptocurrencyDto.getSymbol(), cryptocurrencyDto);
        }

        public CryptocurrencyDto updateCryptocurrencyInfo(CryptocurrencyDto newCryptocurrencyDto) {
                CryptocurrencyDto existingData = cryptocurrencyDTOMap.get(newCryptocurrencyDto.getSymbol());

                existingData = updateData(existingData, newCryptocurrencyDto);
                cryptocurrencyDTOMap.put(newCryptocurrencyDto.getSymbol(), existingData);
                return existingData;
        }

        public CryptocurrencyDto getCryptocurrencyInfo(String code) {
                return cryptocurrencyDTOMap.get(code);
        }

        public Collection<CryptocurrencyDto> getAllCryptocurrencyInfo() {
                return cryptocurrencyDTOMap.values();
        }

        private CryptocurrencyDto updateData(CryptocurrencyDto existingData, 
                                        CryptocurrencyDto latestMarketData) {
                Optional.ofNullable(latestMarketData.getBid()).ifPresent(existingData::setBid);
                Optional.ofNullable(latestMarketData.getBidQty()).ifPresent(existingData::setBidQty);
                Optional.ofNullable(latestMarketData.getAsk()).ifPresent(existingData::setAsk);
                Optional.ofNullable(latestMarketData.getAskQty()).ifPresent(existingData::setAskQty);
                Optional.ofNullable(latestMarketData.getLast()).ifPresent(existingData::setLast);
                Optional.ofNullable(latestMarketData.getVolume()).ifPresent(existingData::setVolume);
                Optional.ofNullable(latestMarketData.getVwap()).ifPresent(existingData::setVwap);
                Optional.ofNullable(latestMarketData.getLow()).ifPresent(existingData::setLow);
                Optional.ofNullable(latestMarketData.getHigh()).ifPresent(existingData::setHigh);
                Optional.ofNullable(latestMarketData.getChange()).ifPresent(existingData::setChange);
                Optional.ofNullable(latestMarketData.getChangePct()).ifPresent(existingData::setChangePct);

                return existingData;
        }
}
