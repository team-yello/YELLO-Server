package com.yello.server.domain.purchase.dto.apple;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

public record InAppVO(
    String productId,
    String transactionId,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss VV")
    Instant purchaseDate,
    String isTrialPeriod
) {

}
