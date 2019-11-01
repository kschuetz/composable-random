package dev.marksman.composablerandom;

import java.util.ArrayList;
import java.util.Iterator;

import static dev.marksman.composablerandom.Initialize.createInitialSeed;
import static dev.marksman.composablerandom.Initialize.randomInitialSeed;

public class GeneratedStream<A> implements Iterator<A> {
    private final Generator<A> gen;
    private final GeneratorContext context;
    private Seed currentState;

    private GeneratedStream(Generator<A> gen, GeneratorContext context, Seed initialSeed) {
        this.gen = gen;
        this.context = context;
        this.currentState = initialSeed;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public A next() {
        Result<? extends Seed, A> run;
        synchronized (this) {
            run = this.gen.run(context, currentState);
            currentState = run.getNextState();
        }

        return run.getValue();
    }

    public ArrayList<A> next(int n) {
        if (n > 0) {
            ArrayList<A> result = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                result.add(next());
            }
            return result;
        } else {
            return new ArrayList<>();
        }
    }

    public void writeToArray(A[] target) {
        writeToArray(target, 0, target.length);
    }

    public void writeToArray(A[] target, int startIndex) {
        writeToArray(target, startIndex, target.length);
    }

    public void writeToArray(A[] target, int startIndex, int endIndexExclusive) {
        endIndexExclusive = Math.max(startIndex, endIndexExclusive);
        if (startIndex < 0) {
            throw new IndexOutOfBoundsException("invalid startIndex: " + startIndex);
        }
        if (endIndexExclusive > target.length) {
            throw new ArrayIndexOutOfBoundsException("invalid endIndex: " + endIndexExclusive);
        }
        for (int i = startIndex; i < endIndexExclusive; i++) {
            target[i] = next();
        }
    }

    public static <A> GeneratedStream<A> streamFrom(Generator<A> gen, Seed initialSeed) {
        return new GeneratedStream<>(gen, GeneratorContext.defaultContext(), initialSeed);
    }

    public static <A> GeneratedStream<A> streamFrom(Generator<A> gen, long initialSeedValue) {
        return streamFrom(gen, createInitialSeed(initialSeedValue));
    }

    public static <A> GeneratedStream<A> streamFrom(Generator<A> gen, Parameters parameters) {
        return streamFrom(gen, randomInitialSeed());
    }

    public static <A> GeneratedStream<A> streamFrom(Generator<A> gen, Parameters parameters, long initialSeedValue) {
        return streamFrom(gen, createInitialSeed(initialSeedValue));
    }

    public static <A> GeneratedStream<A> streamFrom(Generator<A> gen) {
        return streamFrom(gen, randomInitialSeed());
    }

}
