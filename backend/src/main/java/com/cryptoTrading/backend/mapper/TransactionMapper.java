package com.cryptoTrading.backend.mapper;

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
    TransactionDto transactionToDto(Transaction transaction);
    
    @Named("map")
    default String map(TradeType value) {
        return value != null ? value.name() : null;
    }
}
