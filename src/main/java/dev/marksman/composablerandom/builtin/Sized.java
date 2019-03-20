package dev.marksman.composablerandom.builtin;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;
import dev.marksman.composablerandom.Generator;
import dev.marksman.composablerandom.SizeParameters;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static dev.marksman.composablerandom.Generator.generateS;
import static dev.marksman.composablerandom.builtin.Primitives.generateBoolean;
import static dev.marksman.composablerandom.builtin.Primitives.generateIntExclusive;

public class Sized {

    public static <A> Generator<A> sized(Fn1<Maybe<Integer>, Generator<A>> g) {
        return generateS(s0 -> {
            SizeParameters sp = s0.getContext().getSizeParameters();

            return sp.getMinSize()
                    .match(_a -> sp.getMaxSize()
                                    .match(_b -> sp.getPreferredSize()
                                                    .match(_c -> noSizeParameters(g),
                                                            preferred -> preferredOnly(preferred, g)),
                                            maxSize -> sp.getPreferredSize()
                                                    .match(_d -> maxOnly(maxSize, g),
                                                            preferred -> maxPreferred(maxSize, preferred, g))),
                            minSize -> sp.getMaxSize()
                                    .match(_e -> sp.getPreferredSize()
                                                    .match(_f -> minOnly(minSize, g),
                                                            preferred -> minPreferred(minSize, preferred, g)),
                                            maxSize -> sp.getPreferredSize()
                                                    .match(_g -> minMax(minSize, maxSize, g),
                                                            preferred -> minMaxPreferred(minSize, maxSize, preferred, g))
                                    ))
                    .run(s0);
        });
    }

    private static <A> Generator<A> minMaxPreferred(int min, int max, int preferred, Fn1<Maybe<Integer>, Generator<A>> g) {
        // TODO
        return null;
    }

    private static <A> Generator<A> minMax(int min, int max, Fn1<Maybe<Integer>, Generator<A>> g) {
        // TODO
        return null;
    }

    private static <A> Generator<A> minPreferred(int min, int preferred, Fn1<Maybe<Integer>, Generator<A>> g) {
        // TODO
        return null;
    }

    private static <A> Generator<A> minOnly(int min, Fn1<Maybe<Integer>, Generator<A>> g) {
        return g.apply(just(Math.max(min, 0)));
    }

    private static <A> Generator<A> maxPreferred(int max, int preferred, Fn1<Maybe<Integer>, Generator<A>> g) {
        return generateBoolean(2, 7)
                .flatMap(usePreferred -> {
                    if (usePreferred) return g.apply(just(preferred));
                    else return maxOnly(max, g);
                });
    }

    private static <A> Generator<A> maxOnly(int max, Fn1<Maybe<Integer>, Generator<A>> g) {
        return generateIntExclusive(max).flatMap(n -> g.apply(just(n)));
    }

    private static <A> Generator<A> preferredOnly(int preferred, Fn1<Maybe<Integer>, Generator<A>> g) {
        return g.apply(just(preferred));
    }

    private static <A> Generator<A> noSizeParameters(Fn1<Maybe<Integer>, Generator<A>> g) {
        return g.apply(nothing());
    }

}