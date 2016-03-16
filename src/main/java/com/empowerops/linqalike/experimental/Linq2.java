package com.empowerops.linqalike.experimental;

import com.empowerops.linqalike.Queryable;

import java.util.Optional;

import static com.empowerops.linqalike.Factories.from;

/**
 * Created by Geoff on 1/20/2016.
 */
public class Linq2 {

    @SafeVarargs
    public static <T> T firstPresent(Optional<T>... options){
        return from(options).first(Optional::isPresent).get();
    }

    /**
     * Find the first presented in a list of Optionals, if there is no optionals appears as present return an empty
     */
    public static <T> Optional<T> firstPresentOrDefault(Queryable<Optional<T>> optionals){
        return optionals.firstOrDefault(Optional::isPresent).orElse(Optional.empty());
    }

}
