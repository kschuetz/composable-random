package dev.marksman.composablerandom.primitives;

import com.jnape.palatable.lambda.functions.Fn2;
import dev.marksman.composablerandom.GeneratorImpl;
import dev.marksman.composablerandom.LegacySeed;
import dev.marksman.composablerandom.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static dev.marksman.composablerandom.Result.result;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TapImpl<A, B> implements GeneratorImpl<B> {
    private final GeneratorImpl<A> inner;
    private final Fn2<GeneratorImpl<A>, LegacySeed, B> fn;

    @Override
    public Result<? extends LegacySeed, B> run(LegacySeed input) {
        return result(input.nextInt().getNextState(),
                fn.apply(inner, input));
    }

    public static <A, B> TapImpl<A, B> tapImpl(GeneratorImpl<A> inner,
                                               Fn2<GeneratorImpl<A>, LegacySeed, B> f) {
        return new TapImpl<>(inner, f);
    }
}
