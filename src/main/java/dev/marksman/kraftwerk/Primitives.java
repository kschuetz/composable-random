package dev.marksman.kraftwerk;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.functions.Fn0;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn2.Map;
import dev.marksman.collectionviews.ImmutableNonEmptyVector;
import dev.marksman.collectionviews.Vector;
import dev.marksman.enhancediterables.NonEmptyFiniteIterable;
import dev.marksman.kraftwerk.random.BuildingBlocks;
import dev.marksman.kraftwerk.util.Labeling;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Replicate.replicate;
import static dev.marksman.kraftwerk.BiasSetting.noBias;
import static dev.marksman.kraftwerk.Result.result;
import static dev.marksman.kraftwerk.SizeSelectors.sizeSelector;
import static dev.marksman.kraftwerk.Trace.trace;
import static dev.marksman.kraftwerk.random.BuildingBlocks.*;

class Primitives {

    static <A> Generator<A> withMetadata(Maybe<String> label, Maybe<Object> applicationData, Generator<A> operand) {
        // TODO: withMetadata
        //        if (operand instanceof Primitives.WithMetadata) {
//            Primitives.WithMetadata<A> target1 = (Primitives.WithMetadata<A>) operand;
//            return new Primitives.WithMetadata<>(label, applicationData, target1.getOperand());
//        } else {
//            return new Primitives.WithMetadata<>(label, applicationData, operand);
//        }
        return operand;
    }

    static <A, B> Generator<B> mapped(Fn1<? super A, ? extends B> fn, Generator<A> operand) {
        return new Mapped<>(fn::apply, operand);
    }

    static <A, B> Generator<B> flatMapped(Fn1<? super A, ? extends Generator<B>> fn, Generator<A> operand) {
        return new FlatMapped<>(operand, fn::apply);
    }

    static <A> Generator<A> injectSpecialValues(NonEmptyFiniteIterable<A> specialValues, Generator<A> inner) {
        if (inner instanceof InjectSpecialValues<?>) {
            return ((InjectSpecialValues<A>) inner).add(specialValues);
        } else {
            // TODO:  NonEmptyVector.nonEmptyCopyFrom()
            return new InjectSpecialValues<>(Vector.copyFrom(specialValues).toNonEmptyOrThrow(), inner);
        }
    }

    static <A> Generator<A> injectSpecialValue(A specialValue, Generator<A> inner) {
        return injectSpecialValues(Vector.of(specialValue), inner);
    }

    static <A> ConstantGenerator<A> constant(A value) {
        return new ConstantGenerator<A>(value);
    }

    static Generator<Integer> generateInt() {
        return IntGenerator.INSTANCE;
    }

    static Generator<Integer> generateInt(int min, int max) {
        checkMinMax(min, max);
        if (max == Integer.MAX_VALUE) {
            if (min == Integer.MIN_VALUE) {
                return generateInt();
            } else {
                return generateIntExclusive(min - 1, max)
                        .fmap(n -> n + 1);
            }
        } else {
            return generateIntExclusive(min, max + 1);
        }
    }

    static Generator<Integer> generateIntExclusive(int bound) {
        return generateIntExclusiveImpl(bound, p -> p.getBiasSettings().intBias(0, bound - 1));
    }

    private static Generator<Integer> generateIntExclusiveImpl(int bound,
                                                               Fn1<Parameters, BiasSetting<Integer>> getBias) {
        checkBound(bound);
        Maybe<String> label = Maybe.just(Labeling.intInterval(0, bound, true));

        if ((bound & -bound) == bound) { // bound is a power of 2
            return simpleGenerator(label, getBias, input -> unsafeNextIntBoundedPowerOf2(bound, input));
        } else {
            return simpleGenerator(label, getBias, input -> unsafeNextIntBounded(bound, input));
        }

    }

    static Generator<Integer> generateIntExclusive(int origin, int bound) {
        return generateIntExclusiveImpl(origin, bound, p -> p.getBiasSettings().intBias(origin, bound - 1));
    }

    private static Generator<Integer> generateIntExclusiveImpl(int origin, int bound,
                                                               Fn1<Parameters, BiasSetting<Integer>> getBias) {
        checkOriginBound(origin, bound);
        if (origin == 0) {
            return generateIntExclusive(bound);
        } else {
            long range = (long) bound - origin;
            long m = range - 1;
            if (range < Integer.MAX_VALUE) {
                return simpleGenerator(nothing(), getBias, input -> unsafeNextIntExclusive(origin, (int) range, input));
            } else if ((range & m) == 0) {
                // power of two
                return simpleGenerator(nothing(), getBias, input -> unsafeNextIntExclusivePowerOf2(origin, range, input));
            } else {
                return simpleGenerator(nothing(), getBias, input -> unsafeNextIntExclusiveWide(origin, range, input));
            }
        }
    }

    static Generator<Integer> generateIntIndex(int bound) {
        return generateIntExclusiveImpl(bound, constantly(noBias()));
    }

    static Generator<Boolean> generateBoolean() {
        return BooleanGenerator.INSTANCE;
    }

    static FloatingPointGenerator<Double> generateDouble() {
        return DoubleGenerator.BASIC;
    }

    static Generator<Float> generateFloat() {
        return FloatGenerator.INSTANCE;
    }

    static Generator<Long> generateLong() {
        return LongGenerator.INSTANCE;
    }

    static Generator<Long> generateLong(long min, long max) {
        checkMinMax(min, max);
        if (max == Long.MAX_VALUE) {
            if (min == Long.MIN_VALUE) {
                return generateLong();
            } else {
                return generateLongExclusive(min - 1, max)
                        .fmap(n -> n + 1);
            }
        } else {
            return generateLongExclusive(min, max + 1);
        }
    }

    static Generator<Long> generateLongExclusive(long bound) {
        checkBound(bound);
        if (bound <= Integer.MAX_VALUE) {
            return generateIntExclusive((int) bound).fmap(Integer::longValue);
        } else {
            return generateLongExclusive(0, bound);
        }
    }

    static Generator<Long> generateLongExclusiveImpl(long bound,
                                                     Fn1<Parameters, BiasSetting<Long>> getBias) {
        checkBound(bound);
        if (bound <= Integer.MAX_VALUE) {
            return generateIntExclusive((int) bound).fmap(Integer::longValue);
        } else {
            return generateLongExclusive(0, bound);
        }
    }

    static Generator<Long> generateLongExclusive(long origin, long bound) {
        return generateLongExclusiveImpl(origin, bound, p -> p.getBiasSettings().longBias(origin, bound - 1));
    }

    private static Generator<Long> generateLongExclusiveImpl(long origin, long bound, Fn1<Parameters, BiasSetting<Long>> getBias) {
        checkOriginBound(origin, bound);

        if (origin < 0 && bound > 0 && bound > Math.abs(origin - Long.MIN_VALUE)) {
            return simpleGenerator(nothing(), getBias, input -> unsafeNextLongExclusiveWithOverflow(origin, bound, input));
        }

        long range = bound - origin;
        long m = range - 1;

        if ((range & m) == 0L) {
            // power of two
            return simpleGenerator(nothing(), getBias, input -> unsafeNextLongExclusivePowerOf2(origin, range, input));
        } else {
            return simpleGenerator(nothing(), getBias, input -> unsafeNextLongExclusive(origin, range, input));
        }
    }

    static Generator<Long> generateLongIndex(long bound) {
        return generateLongExclusiveImpl(bound, constantly(noBias()));
    }

    static ByteGenerator generateByte() {
        return ByteGenerator.INSTANCE;
    }

    static ShortGenerator generateShort() {
        return ShortGenerator.INSTANCE;
    }

    static Generator<Double> generateGaussian() {
        return GaussianGenerator.INSTANCE;
    }

    static Generator<Byte[]> generateBytes(int count) {
        checkCount(count);
        return new BytesGenerator(count);
    }

    static <A> Generator<A> sized(Fn1<Integer, Generator<A>> fn) {
        return new SizedGenerator<>(fn);
    }


    static <A, Builder, Out> Generator<Out> aggregate(Fn0<Builder> initialBuilderSupplier,
                                                      Fn2<Builder, A, Builder> addFn,
                                                      Fn1<Builder, Out> buildFn,
                                                      Iterable<Generator<A>> elements) {
        return new Aggregate<>(initialBuilderSupplier, addFn, buildFn, elements);
    }

    static <Elem, Builder, Out> Generator<Out> aggregate(Fn0<Builder> initialBuilderSupplier,
                                                         Fn2<Builder, Elem, Builder> addFn,
                                                         Fn1<Builder, Out> buildFn,
                                                         int size,
                                                         Generator<Elem> gen) {
        return new Aggregate<>(initialBuilderSupplier, addFn, buildFn, replicate(size, gen));
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class ConstantGenerator<A> implements Generator<A> {
        private static Maybe<String> LABEL = Maybe.just("constant");

        private final A value;

        @Override
        public Generate<A> prepare(Parameters parameters) {
            return input -> result(input, value);
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class Mapped<In, A> implements Generator<A> {
        private static Maybe<String> LABEL = Maybe.just("fmap");

        private final Fn1<In, A> fn;
        private final Generator<In> operand;

        @Override
        public Generate<A> prepare(Parameters parameters) {
            Generate<In> g = operand.prepare(parameters);
            return input -> g.apply(input).fmap(fn);
        }

        @Override
        public Generate<Trace<A>> prepareTraced(Parameters parameters) {
            Generate<Trace<In>> g = operand.prepareTraced(parameters);
            return input -> {
                Result<? extends Seed, Trace<In>> result1 = g.apply(input);
                return result1.fmap(t ->
                        trace(fn.apply(t.getResult()),
                                this,
                                Vector.of(result1.getValue())));
            };
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class FlatMapped<In, A> implements Generator<A> {
        private static Maybe<String> LABEL = Maybe.just("flatMap");

        private final Generator<In> operand;
        private final Fn1<? super In, ? extends Generator<A>> fn;

        @Override
        public Generate<A> prepare(Parameters parameters) {
            Fn1<Seed, Result<? extends Seed, In>> runner = operand.prepare(parameters);
            return input -> {
                Result<? extends Seed, In> result1 = runner.apply(input);
                Generator<A> g2 = fn.apply(result1.getValue());
                return g2.prepare(parameters).apply(result1.getNextState());
            };
        }

        @Override
        public Generate<Trace<A>> prepareTraced(Parameters parameters) {
            Generate<Trace<In>> runner = operand.prepareTraced(parameters);
            return input -> {
                Result<? extends Seed, Trace<In>> result1 = runner.apply(input);
                Result<? extends Seed, Trace<A>> result2 = fn.apply(result1.getValue().getResult())
                        .prepareTraced(parameters).apply(result1.getNextState());
                return result(result2.getNextState(),
                        trace(result2.getValue().getResult(),
                                this, Vector.of(result1.getValue(), result2.getValue())));
            };
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class BooleanGenerator implements Generator<Boolean> {
        private static Maybe<String> LABEL = Maybe.just("boolean");

        private static final BooleanGenerator INSTANCE = new BooleanGenerator();

        @Override
        public Generate<Boolean> prepare(Parameters parameters) {
            return BuildingBlocks::nextBoolean;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class DoubleGenerator implements FloatingPointGenerator<Double> {
        private static Maybe<String> LABEL = Maybe.just("double");

        private final boolean includeNaNs;
        private final boolean includeInfinities;

        private static final DoubleGenerator BASIC = new DoubleGenerator(false, false);

        @Override
        public Generate<Double> prepare(Parameters parameters) {
            return applyBiasSetting(parameters.getBiasSettings()
                            .doubleBias(Double.MIN_VALUE, Double.MAX_VALUE),
                    BuildingBlocks::nextDouble);
        }

        @Override
        public FloatingPointGenerator<Double> withNaNs(boolean enabled) {
            return (enabled != includeNaNs)
                    ? new DoubleGenerator(enabled, includeInfinities)
                    : this;
        }

        @Override
        public FloatingPointGenerator<Double> withInfinities(boolean enabled) {
            return (enabled != includeInfinities)
                    ? new DoubleGenerator(includeNaNs, enabled)
                    : this;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }


    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class FloatGenerator implements Generator<Float> {
        private static Maybe<String> LABEL = Maybe.just("float");

        private static final FloatGenerator INSTANCE = new FloatGenerator();

        @Override
        public Generate<Float> prepare(Parameters parameters) {
            return applyBiasSetting(parameters.getBiasSettings()
                            .floatBias(Float.MIN_VALUE, Float.MAX_VALUE),
                    BuildingBlocks::nextFloat);
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class IntGenerator implements Generator<Integer> {
        private static Maybe<String> LABEL = Maybe.just("int");

        private static final IntGenerator INSTANCE = new IntGenerator();

        @Override
        public Generate<Integer> prepare(Parameters parameters) {

            return applyBiasSetting(parameters.getBiasSettings()
                            .intBias(Integer.MIN_VALUE, Integer.MAX_VALUE),
                    BuildingBlocks::nextInt);
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class LongGenerator implements Generator<Long> {
        private static Maybe<String> LABEL = Maybe.just("long");

        private static final LongGenerator INSTANCE = new LongGenerator();

        @Override
        public Generate<Long> prepare(Parameters parameters) {
            return applyBiasSetting(parameters.getBiasSettings()
                            .longBias(Long.MIN_VALUE, Long.MAX_VALUE),
                    BuildingBlocks::nextLong);
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class GaussianGenerator implements Generator<Double> {
        private static Maybe<String> LABEL = Maybe.just("gaussian");

        private static final GaussianGenerator INSTANCE = new GaussianGenerator();

        @Override
        public Generate<Double> prepare(Parameters parameters) {
            return BuildingBlocks::nextGaussian;
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }


    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class ByteGenerator implements Generator<Byte> {
        private static Maybe<String> LABEL = Maybe.just("byte");

        private static final ByteGenerator INSTANCE = new ByteGenerator();

        @Override
        public Generate<Byte> prepare(Parameters parameters) {
            return applyBiasSetting(parameters.getBiasSettings().byteBias(Byte.MIN_VALUE, Byte.MAX_VALUE),
                    input -> nextInt(input).fmap(Integer::byteValue));
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class ShortGenerator implements Generator<Short> {
        private static Maybe<String> LABEL = Maybe.just("short");

        private static final ShortGenerator INSTANCE = new ShortGenerator();

        @Override
        public Generate<Short> prepare(Parameters parameters) {
            return applyBiasSetting(parameters.getBiasSettings().shortBias(Short.MIN_VALUE, Short.MAX_VALUE),
                    input -> nextInt(input).fmap(Integer::shortValue));
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class BytesGenerator implements Generator<Byte[]> {
        private final int count;

        @Override
        public Generate<Byte[]> prepare(Parameters parameters) {
            return input -> {
                byte[] buffer = new byte[count];
                Result<? extends Seed, Unit> next = nextBytes(buffer, input);
                Byte[] result = new Byte[count];
                int i = 0;
                for (byte b : buffer) {
                    result[i++] = b;
                }
                return next.fmap(__ -> result);
            };
        }

        @Override
        public Maybe<String> getLabel() {
            return Maybe.just("bytes[" + count + "]");
        }

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class SizedGenerator<A> implements Generator<A> {
        private static Maybe<String> LABEL = Maybe.just("sized");

        private final Fn1<Integer, Generator<A>> fn;

        @Override
        public Generate<A> prepare(Parameters parameters) {
            Generate<Integer> sizeSelector = applyBiasSetting(parameters.getBiasSettings().sizeBias(parameters.getSizeParameters()),
                    sizeSelector(parameters.getSizeParameters()));
            return input -> {
                Result<? extends Seed, Integer> sizeResult = sizeSelector.apply(input);
                return fn.apply(sizeResult.getValue())
                        .prepare(parameters)
                        .apply(sizeResult.getNextState());
            };
        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class WithMetadata<A> implements Generator<A> {
        private final Maybe<String> label;
        private final Maybe<Object> applicationData;
        private final Generator<A> operand;

        @Override
        public Generate<A> prepare(Parameters parameters) {
            return operand.prepare(parameters);
        }

        @Override
        public boolean isPrimitive() {
            return false;
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static class Aggregate<Elem, Builder, Out> implements Generator<Out> {
        private static Maybe<String> LABEL = Maybe.just("aggregate");

        private final Fn0<Builder> initialBuilderSupplier;
        private final Fn2<Builder, Elem, Builder> addFn;
        private final Fn1<Builder, Out> buildFn;
        private final Iterable<Generator<Elem>> elements;

        @Override
        public Generate<Out> prepare(Parameters parameters) {
            Iterable<Generate<Elem>> runners = Map.map(g -> g.prepare(parameters), elements);
            return input -> {
                Seed current = input;
                Builder builder = initialBuilderSupplier.apply();

                for (Generate<Elem> element : runners) {
                    Result<? extends Seed, Elem> next = element.apply(current);
                    builder = addFn.apply(builder, next.getValue());
                    current = next.getNextState();
                }
                return result(current, buildFn.apply(builder));
            };

        }

        @Override
        public Maybe<String> getLabel() {
            return LABEL;
        }

    }

    @Value
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class InjectSpecialValues<A> implements Generator<A> {
        private final ImmutableNonEmptyVector<A> specialValues;
        private final Generator<A> inner;

        InjectSpecialValues<A> add(NonEmptyFiniteIterable<A> newValues) {
            return new InjectSpecialValues<>(Vector.copyFrom(specialValues.concat(newValues)).toNonEmptyOrThrow(),
                    inner);
        }

        @Override
        public Generate<A> prepare(Parameters parameters) {
            return injectSpecial(specialValues, inner.prepare(parameters));
        }

        @Override
        public Maybe<String> getLabel() {
            return inner.getLabel();
        }
    }

    private static <A> Generator<A> simpleGenerator(Maybe<String> label,
                                                    Fn1<Parameters, BiasSetting<A>> getBias,
                                                    Generate<A> runFn) {
        return new Generator<A>() {
            @Override
            public Generate<A> prepare(Parameters parameters) {
                return applyBiasSetting(getBias.apply(parameters), runFn);
            }

            @Override
            public Maybe<String> getLabel() {
                return label;
            }
        };
    }

    private static <A> Generate<A> applyBiasSetting(BiasSetting<A> biasSetting,
                                                    Generate<A> underlying) {
        return biasSetting.match(__ -> underlying,
                isv -> injectSpecial(isv.getSpecialValues(), underlying));
    }

    private static <A> Generate<A> injectSpecial(ImmutableNonEmptyVector<A> specialValues,
                                                 Generate<A> underlying) {
        return underlying;
    }

    /*

    private final ImmutableNonEmptyVector<Elem> elements;
    private final int specialWeight;
    private final long totalWeight;
    private final GeneratorImpl<Elem> inner;

    private InjectSpecialValuesImpl(ImmutableNonEmptyVector<Elem> elements, long nonSpecialWeight, GeneratorImpl<Elem> inner) {
        this.elements = elements;
        this.specialWeight = elements.size();
        this.totalWeight = Math.max(0, nonSpecialWeight) + specialWeight;
        this.inner = inner;
    }

    @Override
    public Result<? extends LegacySeed, Elem> run(LegacySeed input) {
        // TODO: InjectSpecialValuesImpl
        long n = input.getSeedValue() % totalWeight;
        if (n < specialWeight) {
            Result<? extends LegacySeed, Integer> nextSeed = input.nextInt();
            return result(nextSeed.getNextState(), elements.unsafeGet((int) n));
        } else {
            return inner.run(input);
        }
    }



    if (gen instanceof Generator.InjectSpecialValues) {
            Generator.InjectSpecialValues<A> g1 = (Generator.InjectSpecialValues<A>) gen;
            NonEmptyFiniteIterable<A> acc = g1.getSpecialValues();
            while (g1.getInner() instanceof Generator.InjectSpecialValues) {
                g1 = (Generator.InjectSpecialValues<A>) g1.getInner();
                acc = acc.concat(g1.getSpecialValues());
            }
            ImmutableNonEmptyVector<A> specialValues = NonEmptyVector.copyFromOrThrow(acc);
            return mixInSpecialValuesImpl(specialValues, 20 + 3 * specialValues.size(),
                    context.recurse(g1.getInner()));
        }
     */

}
