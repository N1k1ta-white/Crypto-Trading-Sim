package com.cryptoTrading.backend.mapper;

import java.time.Instant;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.cryptoTrading.backend.dto.TransactionDto;
import com.cryptoTrading.backend.entity.Transaction;
import com.cryptoTrading.backend.enums.TradeType;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "crypto.code", target = "cryptoCode")
    @Mapping(source = "tradeType", target = "tradeType", qualifiedByName = "map")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "mapTimestamp")
    TransactionDto transactionToDto(Transaction transaction);
    
    @Named("map")
    default String map(TradeType value) {
        return value != null ? value.name() : null;
    }

    @Named("mapTimestamp")
    default Long mapTimestamp(Instant value) {
        return value != null ? value.toEpochMilli() : null;
    }
}
