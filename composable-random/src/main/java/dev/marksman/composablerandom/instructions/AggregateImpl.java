package dev.marksman.composablerandom.instructions;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import dev.marksman.composablerandom.CompiledGenerator;
import dev.marksman.composablerandom.RandomState;
import dev.marksman.composablerandom.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

import static dev.marksman.composablerandom.Result.result;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AggregateImpl<Elem, Builder, Out> implements CompiledGenerator<Out> {
    private final Supplier<Builder> initialBuilderSupplier;
    private final Fn2<Builder, Elem, Builder> addFn;
    private final Fn1<Builder, Out> buildFn;
    private final Iterable<CompiledGenerator<Elem>> instructions;

    @Override
    public Result<? extends RandomState, Out> run(RandomState input) {
        RandomState current = input;
        Builder builder = initialBuilderSupplier.get();

        for (CompiledGenerator<Elem> instruction : instructions) {
            Result<? extends RandomState, Elem> next = instruction.run(current);
            builder = addFn.apply(builder, next.getValue());
            current = next.getNextState();
        }
        return result(current, buildFn.apply(builder));
    }

    public static <Elem, Builder, Out> AggregateImpl<Elem, Builder, Out> aggregateImpl(
            Supplier<Builder> initialBuilderSupplier,
            Fn2<Builder, Elem, Builder> addFn,
            Fn1<Builder, Out> buildFn,
            Iterable<CompiledGenerator<Elem>> instructions) {
        return new AggregateImpl<>(initialBuilderSupplier, addFn, buildFn, instructions);
    }
}