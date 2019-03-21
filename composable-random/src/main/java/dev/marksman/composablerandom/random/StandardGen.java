package dev.marksman.composablerandom.random;

import com.jnape.palatable.lambda.adt.Unit;
import dev.marksman.composablerandom.RandomState;
import dev.marksman.composablerandom.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Random;

import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static dev.marksman.composablerandom.Result.result;
import static dev.marksman.composablerandom.random.CacheNextGaussian.cacheNextGaussian;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class StandardGen implements RandomState {
    private final long seedValue;

    @Override
    public final Result<StandardGen, Integer> nextInt(int bound) {
        if (bound <= 0)
            throw new IllegalArgumentException("bound must be positive");

        if ((bound & -bound) == bound) { // bound is a power of 2
            return next(31).fmap(n -> (int) ((bound * (long) n) >> 31));
        }

        int bits, val;
        StandardGen nextSeed;
        Result<StandardGen, Integer> next;
        do {
            next = next(31);
            nextSeed = next._1();
            bits = next._2();
            val = bits % bound;
        } while (bits - val + (bound - 1) < 0);
        return result(nextSeed, val);
    }

    @Override
    public final Result<StandardGen, Integer> nextInt() {
        return next(32);
    }

    @Override
    public final Result<StandardGen, Double> nextDouble() {
        Result<StandardGen, Integer> s1 = next(26);
        Result<StandardGen, Integer> s2 = s1._1().next(27);
        double result = (((long) s1._2() << 27) + s2._2()) / (double) (1L << 53);
        return result(s2._1(), result);
    }

    @Override
    public final Result<StandardGen, Float> nextFloat() {
        return next(24).fmap(n -> n / ((float) (1 << 24)));
    }

    @Override
    public final Result<StandardGen, Long> nextLong() {
        Result<StandardGen, Integer> s1 = next(32);
        Result<StandardGen, Integer> s2 = s1._1().next(32);
        long result = ((long) s1._2() << 32) + s2._2();
        return result(s2._1(), result);
    }

    @Override
    public final Result<StandardGen, Boolean> nextBoolean() {
        return next(1).fmap(n -> n != 0);
    }

    @Override
    public final Result<StandardGen, Unit> nextBytes(byte[] dest) {
        StandardGen nextSeed = this;
        int i = 0;
        while (i < dest.length) {
            Result<StandardGen, Integer> nextInt = nextSeed.nextInt();
            nextSeed = nextInt._1();
            int rnd = nextInt._2();
            for (int n = Math.min(dest.length - i, 4); n-- > 0; rnd >>= 8) {
                dest[i++] = (byte) rnd;
            }
        }

        return result(nextSeed, UNIT);
    }

    @Override
    public final Result<CacheNextGaussian, Double> nextGaussian() {
        StandardGen newSeed = this;
        double v1, v2, s;
        do {
            Result<StandardGen, Double> d1 = newSeed.nextDouble();
            Result<StandardGen, Double> d2 = d1._1().nextDouble();
            newSeed = d2._1();
            v1 = 2 * d1._2() - 1;
            v2 = 2 * d2._2() - 1;
            s = v1 * v1 + v2 * v2;
        } while (s >= 1 || s == 0);
        double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s) / s);
        double result = v1 * multiplier;
        double nextResult = v2 * multiplier;
        return result(cacheNextGaussian(newSeed, nextResult), result);
    }

    private Result<StandardGen, Integer> next(int bits) {
        long newSeedValue = (seedValue * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        int result = (int) (newSeedValue >>> (48 - bits));

        return result(nextStandardGen(newSeedValue), result);
    }

    public static StandardGen initStandardGen(long seed) {
        return nextStandardGen((seed ^ 0x5DEECE66DL) & ((1L << 48) - 1));
    }

    public static StandardGen initStandardGen() {
        Random random = new Random();
        return initStandardGen(random.nextLong());
    }

    private static StandardGen nextStandardGen(long seed) {
        return new StandardGen(seed);
    }

}
