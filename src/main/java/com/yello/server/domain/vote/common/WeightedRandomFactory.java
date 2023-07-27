package com.yello.server.domain.vote.common;

import static com.yello.server.global.common.util.ConstantUtil.FIFTH_POINT_WEIGHT;
import static com.yello.server.global.common.util.ConstantUtil.FIRST_POINT_WEIGHT;
import static com.yello.server.global.common.util.ConstantUtil.FOURTH_POINT_WEIGHT;
import static com.yello.server.global.common.util.ConstantUtil.MIN_POINT;
import static com.yello.server.global.common.util.ConstantUtil.REMINDER_NUMBER;
import static com.yello.server.global.common.util.ConstantUtil.SECOND_POINT_WEIGHT;
import static com.yello.server.global.common.util.ConstantUtil.SEVENTH_POINT_WEIGHT;
import static com.yello.server.global.common.util.ConstantUtil.SIXTH_POINT_WEIGHT;
import static com.yello.server.global.common.util.ConstantUtil.THIRD_POINT_WEIGHT;

import java.util.HashMap;
import java.util.Map;

public class WeightedRandomFactory {

    private static final Map<Integer, Double> weight = new HashMap<>();

    public static Integer randomPoint() {
        weight.put(MIN_POINT, FIRST_POINT_WEIGHT);
        weight.put(MIN_POINT + 1, SECOND_POINT_WEIGHT);
        weight.put(MIN_POINT + 2, THIRD_POINT_WEIGHT);
        weight.put(MIN_POINT + 3, FOURTH_POINT_WEIGHT);
        weight.put(MIN_POINT + 4, FIFTH_POINT_WEIGHT);
        weight.put(MIN_POINT + 5, SIXTH_POINT_WEIGHT);
        weight.put(MIN_POINT + 6, SEVENTH_POINT_WEIGHT);

        final double pivot = Math.random() % REMINDER_NUMBER;
        double currentWeight = 0;
        int i = MIN_POINT;
        for (; i <= REMINDER_NUMBER; i++) {
            currentWeight += weight.get(i);
            if (currentWeight >= pivot) {
                return i;
            }
        }
        return i - 1;
    }

}
