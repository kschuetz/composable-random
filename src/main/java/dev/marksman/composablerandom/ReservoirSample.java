package dev.marksman.composablerandom;

import dev.marksman.collectionviews.Set;
import dev.marksman.collectionviews.Vector;
import dev.marksman.enhancediterables.FiniteIterable;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Upcast.upcast;

class ReservoirSample {

    static Generator<FiniteIterable<Integer>> reservoirSample(int n, int k) {
        if (k < 1 || n < 1) {
            return Generator.constant(Vector.empty());
        } else if (k >= n) {
            return Generator.constant(Vector.range(n));
        } else if (k > n / 2) {
            return reservoirSampleImpl(n, n - k)
                    .fmap(exclude -> Vector.range(n).filter(i -> !exclude.contains(i)));
        } else {
            return reservoirSampleImpl(n, k).fmap(upcast());
        }
    }

    private static Generator<Set<Integer>> reservoirSampleImpl(int n, int k) {
        return Generator.generate(rs -> {
            LegacySeed current = rs;
            Integer[] result = new Integer[k];
            for (int i = 0; i < k; i++) {
                result[i] = i;
            }
            for (int i = k; i < n; i++) {
                Result<? extends LegacySeed, Integer> next = current.nextIntBounded(i);
                Integer value = next.getValue();
                if (value < k) {
                    result[value] = i;
                }
                current = next.getNextState();
            }
            return Result.result(current, Set.copyFrom(result));
        });

    }

}
