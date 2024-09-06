package ru.practicum.shareit.common;

import lombok.Getter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class BaseDto {
    private final long id;
    private final String name;

    private BaseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static BaseDto from(Object o) {
        if (o == null) return null;
        try {
            List<String> targetMethods = List.of("getId", "getName");
            List<Method> objectMethods = List.of(o.getClass().getMethods());
            Map<String, Method> commonsMethods = objectMethods.stream()
                    .filter(method -> targetMethods.contains(method.getName()))
                    .collect(Collectors.toMap(Method::getName, Function.identity()));

            if (commonsMethods.size() == targetMethods.size()) {
                Long id = (long) commonsMethods.get("getId").invoke(o);
                String name = (String) commonsMethods.get("getName").invoke(o);
                return new BaseDto(id, name);
            }

            throw new IllegalArgumentException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
