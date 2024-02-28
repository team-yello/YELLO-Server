package com.yello.server.domain.statistics.exception;

import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.Getter;

@Getter
public class StatisticsNotFoundException extends CustomException {

    public StatisticsNotFoundException(ErrorCode error) {
        super(error, "[StatisticsNotFoundException] " + error.getMessage());
    }
}
