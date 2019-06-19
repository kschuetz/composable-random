package dev.marksman.composablerandom.primitives;

import dev.marksman.composablerandom.Generator;
import dev.marksman.composablerandom.RandomState;
import dev.marksman.composablerandom.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NextDoubleImpl implements Generator<Double> {
    private static NextDoubleImpl INSTANCE = new NextDoubleImpl();

    @Override
    public Result<? extends RandomState, Double> run(RandomState input) {
        return input.nextDouble();
    }

    public static NextDoubleImpl nextDoubleImpl() {
        return INSTANCE;
    }
}
