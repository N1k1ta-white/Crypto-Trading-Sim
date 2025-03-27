package com.cryptoTrading.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cryptoTrading.backend.dto.HoldingDto;
import com.cryptoTrading.backend.entity.Holding;
@Mapper(componentModel = "spring")
public interface HoldingMapper {
    
    @Mapping(source = "crypto.code", target = "cryptoCode")
    HoldingDto holdingToDto(Holding holding);
}
