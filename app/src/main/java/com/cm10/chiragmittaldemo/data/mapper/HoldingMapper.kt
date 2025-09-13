package com.cm10.chiragmittaldemo.data.mapper

import com.cm10.chiragmittaldemo.data.remote.dto.HoldingDto
import com.cm10.chiragmittaldemo.domain.model.Holding

class HoldingMapper {
    
    fun mapToDomain(dto: HoldingDto?): Holding? {
        return dto?.let {
            Holding(
                symbol = it.symbol,
                quantity = it.quantity,
                ltp = it.ltp,
                avgPrice = it.avgPrice,
                close = it.close
            )
        }
    }
    
    fun mapToDomainList(dtoList: List<HoldingDto>?): List<Holding>? {
        return dtoList?.mapNotNull { mapToDomain(it) }?.takeIf { it.isNotEmpty() }
    }
}
