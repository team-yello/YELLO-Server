package com.yello.server.global.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListUtil {

    public static <T> List<T> toList(List<Optional<T>> optionalList) {
        List<T> result = new ArrayList<>();
        optionalList.forEach(optional -> {
            optional.ifPresent(result::add);
        });
        return result;
    }
}
