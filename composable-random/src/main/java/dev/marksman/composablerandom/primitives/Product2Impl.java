package dev.marksman.composablerandom.primitives;

import com.jnape.palatable.lambda.functions.Fn2;
import dev.marksman.composablerandom.CompiledGenerator;
import dev.marksman.composablerandom.RandomState;
import dev.marksman.composablerandom.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static dev.marksman.composablerandom.Result.result;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product2Impl<A, B, Out> implements CompiledGenerator<Out> {
    private final CompiledGenerator<A> a;
    private final CompiledGenerator<B> b;
    private final Fn2<A, B, Out> combine;

    @Override
    public Result<? extends RandomState, Out> run(RandomState input) {
        Result<? extends RandomState, A> r1 = a.run(input);
        Result<? extends RandomState, B> r2 = b.run(r1.getNextState());
        Out result = combine.apply(r1.getValue(), r2.getValue());
        return result(r2.getNextState(), result);
    }

    public static <A, B, Out> Product2Impl<A, B, Out> product2Impl(CompiledGenerator<A> a,
                                                                   CompiledGenerator<B> b,
                                                                   Fn2<A, B, Out> combine) {
        return new Product2Impl<>(a, b, combine);
    }

}