package com.yello.server.global.common.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListFactory {

    public static <T> List<T> toNonNullableList(List<Optional<T>> optionalList) {
        List<T> result = new ArrayList<>();
        optionalList.forEach(optional -> {
            optional.ifPresent(result::add);
        });
        return result;
    }
}
