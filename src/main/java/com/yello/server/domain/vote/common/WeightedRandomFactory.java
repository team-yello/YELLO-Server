package com.yello.server.domain.vote.common;

import static com.yello.server.global.common.util.ConstantUtil.EIGHT_POINT_WEIGHT;
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

    private WeightedRandomFactory() {
        throw new IllegalStateException();
    }

    public static Integer randomPoint() {
        weight.put(MIN_POINT, FIRST_POINT_WEIGHT);
        weight.put(MIN_POINT + 5, SECOND_POINT_WEIGHT);
        weight.put(MIN_POINT + 10, THIRD_POINT_WEIGHT);
        weight.put(MIN_POINT + 12, FOURTH_POINT_WEIGHT);
        weight.put(MIN_POINT + 14, FIFTH_POINT_WEIGHT);
        weight.put(MIN_POINT + 15, SIXTH_POINT_WEIGHT);
        weight.put(MIN_POINT + 16, SEVENTH_POINT_WEIGHT);
        weight.put(MIN_POINT + 20, EIGHT_POINT_WEIGHT);

        final double pivot = Math.random() % REMINDER_NUMBER;
        double currentWeight = 0;
        
        for (int key : weight.keySet()) {
            currentWeight += weight.getOrDefault(key, 0.0);
            if (currentWeight >= pivot) {
                return key;
            }
        }
        return MIN_POINT + 20;
    }

}
