package dev.marksman.composablerandom;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.adt.hlist.Tuple3;
import com.jnape.palatable.lambda.adt.product.Product2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.builtin.fn1.Id;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;
import static dev.marksman.composablerandom.Freq.freq;
import static dev.marksman.composablerandom.Random.*;
import static dev.marksman.composablerandom.StandardGen.initStandardGen;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RandomTest {

    private static int SEQUENCE_LENGTH = 17;

    private static final Random<Integer> gen1 = randomInt();
    private static final Random<Double> gen2 = randomGaussian();
    private static final Random<Integer> gen3 = randomInt(1, 10);
    private static final Random<String> gen4 = frequency(freq(3, "foo"), freq(7, "bar"));

    @Test
    void functorIdentity() {
        testFunctorIdentity(gen1);
        testFunctorIdentity(gen2);
        testFunctorIdentity(gen3);
        testFunctorIdentity(gen4);
    }

    @Test
    void functorComposition() {
        testFunctorComposition(gen1);
        testFunctorComposition(gen2);
        testFunctorComposition(gen3);
        testFunctorComposition(gen4);
    }

    @Test
    void monadLeftIdentity() {
        testMonadLeftIdentity(1, gen1);
        testMonadLeftIdentity(0.0, gen2);
        testMonadLeftIdentity(1, gen3);
        testMonadLeftIdentity("foo", gen4);
    }

    @Test
    void monadRightIdentity() {
        testMonadRightIdentity(gen1);
        testMonadRightIdentity(gen2);
        testMonadRightIdentity(gen3);
        testMonadRightIdentity(gen4);
    }

    private static <A> void testFunctorIdentity(Random<A> random1) {
        Random<A> random2 = random1.fmap(id());
        testEquivalent(random1, random2);
    }

    private static <A> void testFunctorComposition(Random<A> random) {
        Fn1<A, Tuple2<A, A>> f = a -> tuple(a, a);
        Fn1<Tuple2<A, A>, Tuple3<A, A, A>> g = t -> t.cons(t._1());
        testEquivalent(random.fmap(f).fmap(g), random.fmap(f.andThen(g)));
    }

    private static <A> void testMonadLeftIdentity(A someValue, Random<A> random) {
        Fn1<A, Random<A>> fn = Id.<A>id().andThen(random::pure);

        Random<A> random1 = fn.apply(someValue);
        Random<A> random2 = random.pure(someValue).flatMap(fn);

        testEquivalent(random1, random2);
    }

    private static <A> void testMonadRightIdentity(Random<A> random) {
        Random<A> random2 = random.flatMap(random::pure);
        testEquivalent(random, random2);
    }

    private static <A> void testEquivalent(Random<A> random1, Random<A> random2) {
        StandardGen initial = initStandardGen();

        Product2<? extends RandomGen, ArrayList<A>> result1 = random1.times(SEQUENCE_LENGTH).run(initial);
        Product2<? extends RandomGen, ArrayList<A>> result2 = random2.times(SEQUENCE_LENGTH).run(initial);

        assertEquals(result1._1(), result2._1(), "outbound RandomGens don't match");
        assertEquals(result1._2(), result2._2(), "values don't match");
    }
}
