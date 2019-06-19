package dev.marksman.composablerandom.choice;

import com.jnape.palatable.lambda.adt.choice.Choice6;
import com.jnape.palatable.lambda.adt.choice.Choice7;
import dev.marksman.composablerandom.FrequencyEntry;
import dev.marksman.composablerandom.Generate;
import dev.marksman.composablerandom.ToGenerate;
import dev.marksman.composablerandom.frequency.FrequencyMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static dev.marksman.composablerandom.Generate.constant;
import static dev.marksman.composablerandom.choice.ChoiceBuilder7.choiceBuilder7;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChoiceBuilder6<A, B, C, D, E, F> implements ToGenerate<Choice6<A, B, C, D, E, F>> {
    private final FrequencyMap<Choice6<A, B, C, D, E, F>> frequencyMap;

    @Override
    public Generate<Choice6<A, B, C, D, E, F>> toGenerate() {
        return frequencyMap.toGenerate();
    }

    public <G> ChoiceBuilder7<A, B, C, D, E, F, G> or(int weight, Generate<G> gen) {
        FrequencyMap<Choice7<A, B, C, D, E, F, G>> newFrequencyMap = frequencyMap
                .<Choice7<A, B, C, D, E, F, G>>fmap(c6 ->
                        c6.match(Choice7::a, Choice7::b, Choice7::c, Choice7::d, Choice7::e, Choice7::f))
                .add(weight, gen.fmap(Choice7::g));
        return choiceBuilder7(newFrequencyMap);
    }

    public <G> ChoiceBuilder7<A, B, C, D, E, F, G> or(Generate<G> gen) {
        return or(1, gen);
    }

    public <G> ChoiceBuilder7<A, B, C, D, E, F, G> or(FrequencyEntry<G> frequencyEntry) {
        return or(frequencyEntry.getWeight(), frequencyEntry.getGenerate());
    }

    public <G> ChoiceBuilder7<A, B, C, D, E, F, G> orValue(int weight, G value) {
        return or(weight, constant(value));
    }

    public <G> ChoiceBuilder7<A, B, C, D, E, F, G> orValue(G value) {
        return or(1, constant(value));
    }

    public static <A, B, C, D, E, F> ChoiceBuilder6<A, B, C, D, E, F> choiceBuilder6(FrequencyMap<Choice6<A, B, C, D, E, F>> frequencyMap) {
        return new ChoiceBuilder6<>(frequencyMap);
    }
}
