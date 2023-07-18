package com.yello.server.domain.vote.common;

import java.util.HashMap;
import java.util.Map;

import static com.yello.server.global.common.util.ConstantUtil.*;

public class WeightedRandom {

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

        int currentWeight = 0;
        int i = MIN_POINT;
        for (; i <= REMINDER_NUMBER; i++) {
            currentWeight += weight.get(i);
            if (currentWeight >= pivot)
                return i;
        }
        return i - 1;
    }

}
