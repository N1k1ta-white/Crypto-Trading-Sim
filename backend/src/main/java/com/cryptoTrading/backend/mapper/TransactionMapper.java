package com.cryptoTrading.backend.mapper;

import org.mapstruct.Mapper;

import com.cryptoTrading.backend.dto.TransactionDto;
import com.cryptoTrading.backend.entity.Crypto;
import com.cryptoTrading.backend.entity.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDto transactionToDto(Transaction transaction);

    default String map(Crypto value) {
        return value.getCode();
    }
}
