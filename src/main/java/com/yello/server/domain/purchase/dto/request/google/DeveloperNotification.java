package com.yello.server.domain.purchase.dto.request.google;

import static com.yello.server.global.common.util.ConstantUtil.GOOGLE_FIVE_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.GOOGLE_ONE_TICKET_ID;
import static com.yello.server.global.common.util.ConstantUtil.GOOGLE_TWO_TICKET_ID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.yello.server.domain.purchase.entity.ProductType;
import org.springframework.util.ObjectUtils;

@JsonInclude(Include.NON_NULL)
public record DeveloperNotification(
    String version,
    String packageName,
    Long eventTimeMillis,
    OneTimeProductNotification oneTimeProductNotification,
    SubscriptionNotification subscriptionNotification,
    TestNotification testNotification
) {

    public ProductType getProductType() {
        if (!ObjectUtils.isEmpty(testNotification)) {
            return ProductType.TEST;
        } else if (!ObjectUtils.isEmpty(subscriptionNotification)) {
            return ProductType.YELLO_PLUS;
        } else if (!ObjectUtils.isEmpty(oneTimeProductNotification)) {
            switch (oneTimeProductNotification.sku()) {
                case GOOGLE_ONE_TICKET_ID -> {
                    return ProductType.ONE_TICKET;
                }
                case GOOGLE_TWO_TICKET_ID -> {
                    return ProductType.TWO_TICKET;
                }
                case GOOGLE_FIVE_TICKET_ID -> {
                    return ProductType.FIVE_TICKET;
                }
            }
        }
        return null;
    }
}
