package com.yello.server.domain.vote.common;

import java.util.HashMap;
import java.util.Map;

public class WeightedRandom {

    private static final Map<Integer, Double> weight = new HashMap<>();

    public static Integer randomPoint() {
        weight.put(7, 6.4);
        weight.put(8, 2.2);
        weight.put(9, 1.0);
        weight.put(10, 0.4);

        final double pivot = Math.random() % 10;

        int currentWeight = 0;
        int i = 7;
        for (; i <= 10; i++) {
            currentWeight += weight.get(i);
            if (currentWeight >= pivot)
                return i;
        }
        return i;
    }

}
