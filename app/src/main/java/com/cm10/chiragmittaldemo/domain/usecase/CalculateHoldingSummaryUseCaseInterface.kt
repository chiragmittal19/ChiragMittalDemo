package com.cm10.chiragmittaldemo.domain.usecase

import com.cm10.chiragmittaldemo.domain.model.Holding
import com.cm10.chiragmittaldemo.domain.model.HoldingSummary

interface CalculateHoldingSummaryUseCaseInterface {
    operator fun invoke(holding: Holding?): HoldingSummary?
}
