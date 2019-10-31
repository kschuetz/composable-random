package dev.marksman.composablerandom;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.*;
import dev.marksman.composablerandom.random.RandomUtils;
import dev.marksman.composablerandom.util.Labeling;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

import static dev.marksman.composablerandom.Result.result;

public class Primitives {
    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Constant<A> extends Generator<A> {
        private static Maybe<String> LABEL = Maybe.just("constant");

        private final A value;

        @Override
        public Result<? extends Seed, A> run(GeneratorContext context, Seed input) {
            return result(input, value);
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }
    }

    public static <A> Constant<A> constant(A value) {
        return new Constant<A>(value);
    }


    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class Mapped<In, A> extends Generator<A> {
        private static Maybe<String> LABEL = Maybe.just("fmap");

        private final Fn1<In, A> fn;
        private final Generator<In> operand;

        @Override
        public Result<? extends Seed, A> run(GeneratorContext context, Seed input) {
            // TODO: mapped
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class FlatMapped<In, A> extends Generator<A> {
        private static Maybe<String> LABEL = Maybe.just("flatMap");

        private final Generator<In> operand;
        private final Fn1<? super In, ? extends Generator<A>> fn;

        @Override
        public Result<? extends Seed, A> run(GeneratorContext context, Seed input) {
            // TODO: flatMapped
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextBoolean extends Generator<Boolean> {
        private static Maybe<String> LABEL = Maybe.just("boolean");

        private static final NextBoolean INSTANCE = new NextBoolean();

        @Override
        public Result<? extends Seed, Boolean> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    public static NextBoolean nextBoolean() {
        return NextBoolean.INSTANCE;
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextDouble extends Generator<Double> {
        private static Maybe<String> LABEL = Maybe.just("double");

        private static final NextDouble INSTANCE = new NextDouble();

        @Override
        public Result<? extends Seed, Double> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    public static NextDouble nextDouble() {
        return NextDouble.INSTANCE;
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextFloat extends Generator<Float> {
        private static Maybe<String> LABEL = Maybe.just("float");

        private static final NextFloat INSTANCE = new NextFloat();

        @Override
        public Result<? extends Seed, Float> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    public static NextFloat nextFloat() {
        return NextFloat.INSTANCE;
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextInt extends Generator<Integer> {
        private static Maybe<String> LABEL = Maybe.just("int");

        private static final NextInt INSTANCE = new NextInt();

        @Override
        public Result<? extends Seed, Integer> run(GeneratorContext context, Seed input) {
            return RandomUtils.nextBits(input, 32);
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    public static NextInt nextInt() {
        return NextInt.INSTANCE;
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextIntBounded extends Generator<Integer> {
        private final int bound;

        @Override
        public Result<? extends Seed, Integer> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return Maybe.just(Labeling.intInterval(0, bound, true));
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextIntExclusive extends Generator<Integer> {
        private final int origin;
        private final int bound;

        @Override
        public Result<? extends Seed, Integer> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return Maybe.just(Labeling.intInterval(origin, bound, true));
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextIntBetween extends Generator<Integer> {
        private final int min;
        private final int max;

        @Override
        public Result<? extends Seed, Integer> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return Maybe.just(Labeling.intInterval(min, max, false));
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextIntIndex extends Generator<Integer> {
        private final int bound;

        @Override
        public Result<? extends Seed, Integer> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return Maybe.just(Labeling.interval("index", 0, bound, true));
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextLong extends Generator<Long> {
        private static Maybe<String> LABEL = Maybe.just("long");

        private static final NextLong INSTANCE = new NextLong();

        @Override
        public Result<? extends Seed, Long> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    public static NextLong nextLong() {
        return NextLong.INSTANCE;
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextLongBounded extends Generator<Long> {
        private final long bound;

        @Override
        public Result<? extends Seed, Long> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return Maybe.just(Labeling.longInterval(0, bound, true));
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextLongExclusive extends Generator<Long> {
        private final long origin;
        private final long bound;

        @Override
        public Result<? extends Seed, Long> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return Maybe.just(Labeling.longInterval(origin, bound, true));
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextLongBetween extends Generator<Long> {
        private final long min;
        private final long max;

        @Override
        public Result<? extends Seed, Long> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return Maybe.just(Labeling.longInterval(min, max, false));
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextLongIndex extends Generator<Long> {
        private final long bound;

        @Override
        public Result<? extends Seed, Long> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return Maybe.just(Labeling.longInterval(0, bound, true));
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextGaussian extends Generator<Double> {
        private static Maybe<String> LABEL = Maybe.just("gaussian");

        private static final NextGaussian INSTANCE = new NextGaussian();

        @Override
        public Result<? extends Seed, Double> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    public static NextGaussian nextGaussian() {
        return NextGaussian.INSTANCE;
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextByte extends Generator<Byte> {
        private static Maybe<String> LABEL = Maybe.just("byte");

        private static final NextByte INSTANCE = new NextByte();

        @Override
        public Result<? extends Seed, Byte> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    public static NextByte nextByte() {
        return NextByte.INSTANCE;
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextShort extends Generator<Short> {
        private static Maybe<String> LABEL = Maybe.just("short");

        private static final NextShort INSTANCE = new NextShort();

        @Override
        public Result<? extends Seed, Short> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    public static NextShort nextShort() {
        return NextShort.INSTANCE;
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class NextBytes extends Generator<Byte[]> {
        private final int count;

        @Override
        public Result<? extends Seed, Byte[]> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return Maybe.just("bytes[" + count + "]");
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class Sized<A> extends Generator<A> {
        private static Maybe<String> LABEL = Maybe.just("sized");

        private final Fn1<Integer, Generator<A>> fn;

        @Override
        public Result<? extends Seed, A> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class WithMetadata<A> extends Generator<A> {
        private final Maybe<String> label;
        private final Maybe<Object> applicationData;
        private final Generator<A> operand;

        @Override
        public Result<? extends Seed, A> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public boolean isPrimitive() {
            return false;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class Aggregate<Elem, Builder, Out> extends Generator<Out> {
        private static Maybe<String> LABEL = Maybe.just("aggregate");

        private final Fn0<Builder> initialBuilderSupplier;
        private final Fn2<Builder, Elem, Builder> addFn;
        private final Fn1<Builder, Out> buildFn;
        private final Iterable<Generator<Elem>> elements;

        @Override
        public Result<? extends Seed, Out> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class Product2<A, B, Out> extends Generator<Out> {
        private static Maybe<String> LABEL = Maybe.just("product2");

        private final Generator<A> a;
        private final Generator<B> b;
        private final Fn2<A, B, Out> combine;

        @Override
        public Result<? extends Seed, Out> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class Product3<A, B, C, Out> extends Generator<Out> {
        private static Maybe<String> LABEL = Maybe.just("product3");

        private final Generator<A> a;
        private final Generator<B> b;
        private final Generator<C> c;
        private final Fn3<A, B, C, Out> combine;

        @Override
        public Result<? extends Seed, Out> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class Product4<A, B, C, D, Out> extends Generator<Out> {
        private static Maybe<String> LABEL = Maybe.just("product4");

        private final Generator<A> a;
        private final Generator<B> b;
        private final Generator<C> c;
        private final Generator<D> d;
        private final Fn4<A, B, C, D, Out> combine;

        @Override
        public Result<? extends Seed, Out> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class Product5<A, B, C, D, E, Out> extends Generator<Out> {
        private static Maybe<String> LABEL = Maybe.just("product5");

        private final Generator<A> a;
        private final Generator<B> b;
        private final Generator<C> c;
        private final Generator<D> d;
        private final Generator<E> e;
        private final Fn5<A, B, C, D, E, Out> combine;

        @Override
        public Result<? extends Seed, Out> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class Product6<A, B, C, D, E, F, Out> extends Generator<Out> {
        private static Maybe<String> LABEL = Maybe.just("product6");

        private final Generator<A> a;
        private final Generator<B> b;
        private final Generator<C> c;
        private final Generator<D> d;
        private final Generator<E> e;
        private final Generator<F> f;
        private final Fn6<A, B, C, D, E, F, Out> combine;

        @Override
        public Result<? extends Seed, Out> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class Product7<A, B, C, D, E, F, G, Out> extends Generator<Out> {
        private static Maybe<String> LABEL = Maybe.just("product7");

        private final Generator<A> a;
        private final Generator<B> b;
        private final Generator<C> c;
        private final Generator<D> d;
        private final Generator<E> e;
        private final Generator<F> f;
        private final Generator<G> g;
        private final Fn7<A, B, C, D, E, F, G, Out> combine;

        @Override
        public Result<? extends Seed, Out> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class Product8<A, B, C, D, E, F, G, H, Out> extends Generator<Out> {
        private static Maybe<String> LABEL = Maybe.just("product8");

        private final Generator<A> a;
        private final Generator<B> b;
        private final Generator<C> c;
        private final Generator<D> d;
        private final Generator<E> e;
        private final Generator<F> f;
        private final Generator<G> g;
        private final Generator<H> h;
        private final Fn8<A, B, C, D, E, F, G, H, Out> combine;

        @Override
        public Result<? extends Seed, Out> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class Tap<A, B> extends Generator<B> {
        private static Maybe<String> LABEL = Maybe.just("tap");

        private final Generator<A> inner;
        private final Fn2<GeneratorImpl<A>, LegacySeed, B> fn;

        @Override
        public Result<? extends Seed, B> run(GeneratorContext context, Seed input) {
            return null;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }
    }
}