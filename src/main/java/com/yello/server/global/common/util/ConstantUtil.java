package com.yello.server.global.common.util;

import java.time.ZoneId;

public class ConstantUtil {

    public static final ZoneId GlobalZoneId = ZoneId.of("Asia/Seoul");
    public static final String IdempotencyKeyHeader = "IdempotencyKey";
    public static final int RANDOM_COUNT = 4;
    public static final int VOTE_COUNT = 8;
    public static final long TIMER_TIME = 2400L;
    public static final int MIN_POINT = 5;
    public static final double FIRST_POINT_WEIGHT = 0.15;
    public static final double SECOND_POINT_WEIGHT = 0.2;
    public static final double THIRD_POINT_WEIGHT = 0.05;
    public static final double FOURTH_POINT_WEIGHT = 0.05;
    public static final double FIFTH_POINT_WEIGHT = 0.05;
    public static final double SIXTH_POINT_WEIGHT = 0.05;
    public static final double SEVENTH_POINT_WEIGHT = 0.2;
    public static final double EIGHT_POINT_WEIGHT = 0.25;
    public static final int REMINDER_NUMBER = 25;
    public static final int NAME_HINT_POINT = 300;
    public static final int NAME_HINT_DEFAULT = -1;
    public static final int CHECK_FULL_NAME = -2;
    public static final int MINUS_TICKET_COUNT = -1;
    public static final int TIMER_MAX_TIME = 40;
    public static final int COOL_DOWN_TIME = 40;
    public static final int KEYWORD_HINT_POINT = 100;
    public static final String YELLO_PLUS_ID = "YELLO.iOS.yelloPlus.monthly";
    public static final String ONE_TICKET_ID = "YELLO.iOS.nameKey.one";
    public static final String TWO_TICKET_ID = "YELLO.iOS.nameKey.two";
    public static final String FIVE_TICKET_ID = "YELLO.iOS.nameKey.five";
    public static final String GOOGLE_YELLO_PLUS_ID = "yello_plus_subscribe";
    public static final String GOOGLE_ONE_TICKET_ID = "yello_ticket_one";
    public static final String GOOGLE_TWO_TICKET_ID = "yello_ticket_two";
    public static final String GOOGLE_FIVE_TICKET_ID = "yello_ticket_five";
    public static final int ONE_TICKET = 1400;
    public static final int TWO_TICKET = 2800;
    public static final int FIVE_TICKET = 5900;
    public static final int YELLO_PLUS = 3900;
    public static final int SALE_ONE_TICKET = 990;
    public static final int SALE_TWO_TICKET = 1900;
    public static final int SALE_FIVE_TICKET = 3900;
    public static final int SALE_YELLO_PLUS = 2900;
    public static final int RECOMMEND_POINT = 100;
    public static final String GOOGLE_PURCHASE_SUBSCRIPTION_ACTIVE = "SUBSCRIPTION_STATE_ACTIVE";
    public static final String GOOGLE_PURCHASE_SUBSCRIPTION_CANCELED =
        "SUBSCRIPTION_STATE_CANCELED";
    public static final String GOOGLE_PURCHASE_SUBSCRIPTION_EXPIRED = "SUBSCRIPTION_STATE_EXPIRED";
    public static final String GOOGLE_PURCHASE_SUBSCRIPTION_GRACE_PERIOD =
        "SUBSCRIPTION_STATE_IN_GRACE_PERIOD";
    public static final String GOOGLE_PURCHASE_INAPP_PURCHASED = "PURCHASED";
    public static final String GOOGLE_PURCHASE_INAPP_PENDING = "PENDING";
    public static final String GOOGLE_PURCHASE_INAPP_CANCELED = "CANCELED";
    public static final String YELLO_FEMALE = "yello_female";
    public static final String YELLO_MALE = "yello_male";
    public static final String APPLE_NOTIFICATION_CONSUMPTION_REQUEST = "CONSUMPTION_REQUEST";
    public static final String APPLE_NOTIFICATION_SUBSCRIPTION_STATUS_CHANGE =
        "DID_CHANGE_RENEWAL_STATUS";
    public static final String APPLE_NOTIFICATION_REFUND = "REFUND";
    public static final String APPLE_NOTIFICATION_EXPIRED = "EXPIRED";
    public static final String APPLE_NOTIFICATION_TEST = "TEST";
    public static final String APPLE_NOTIFICATION_SUBSCRIBED = "SUBSCRIBED";
    public static final String APPLE_NOTIFICATION_DID_RENEW = "DID_RENEW";
    public static final String APPLE_SUBTYPE_AUTO_RENEW_DISABLED = "AUTO_RENEW_DISABLED";
    public static final String APPLE_SUBTYPE_VOLUNTARY = "VOLUNTARY";
    public static final String APPLE_SUBTYPE_AUTO_RENEW_ENABLED = "AUTO_RENEW_ENABLED";
    public static final String APPLE_SUBTYPE_BILLING_RECOVERY = "BILLING_RECOVERY";
    public static final String APPLE_SUBTYPE_RESUBSCRIBE = "RESUBSCRIBE";
    public static final int REFUND_ONE_TICKET = 1;
    public static final int REFUND_TWO_TICKET = 2;
    public static final int REFUND_FIVE_TICKET = 5;
    public static final int NO_FRIEND_COUNT = 0;
    public static final int SUBSCRIBE_DAYS = 7;
    public static final int PLUS_BASIC_TIME = 0;
    public static final String USER_VOTE_TYPE = "send";
    public static final String ALL_VOTE_TYPE = "all";


    private ConstantUtil() {
        throw new IllegalStateException();
    }

}
