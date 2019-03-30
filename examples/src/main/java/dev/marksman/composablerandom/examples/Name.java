package dev.marksman.composablerandom.examples;

import com.jnape.palatable.lambda.adt.Maybe;
import dev.marksman.composablerandom.OldGenerator;
import dev.marksman.composablerandom.domain.Characters;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Into4.into4;
import static dev.marksman.composablerandom.GeneratedStream.streamFrom;
import static dev.marksman.composablerandom.legacy.OldFrequencyEntry.entry;
import static dev.marksman.composablerandom.legacy.builtin.OldGenerators.*;
import static java.util.Arrays.asList;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Name {
    private final String first;
    private final Maybe<String> middle;
    private final String last;
    private final Maybe<String> suffix;

    public String getFullName() {
        return first +
                middle.match(__ -> "", s -> " " + s)
                + " " + last
                + suffix.match(__ -> "", s -> ", " + s);
    }

    public static Name name(String first, Maybe<String> middle, String last, Maybe<String> suffix) {
        return new Name(first, middle, last, suffix);
    }

    private static class Generators {
        private static final OldGenerator<String> initial = chooseOneFrom(Characters.alphaUpper()).fmap(c -> c + ".");

        private static final OldGenerator<String> givenNames =
                chooseOneOf("Alice", "Barbara", "Bart", "Billy", "Bobby", "Carol", "Cindy", "Elizabeth",
                        "Eric", "George", "Greg", "Homer", "James", "Jan", "John", "Kenny", "Kyle", "Linda", "Lisa",
                        "Maggie", "Marcia", "Marge", "Mary", "Mike", "Oliver", "Patricia", "Peter", "Stan");

        private static final OldGenerator<String> first =
                frequency(entry(15, givenNames),
                        entry(1, initial));

        private static final OldGenerator<String> middle =
                frequency(entry(1, givenNames),
                        entry(5, initial));

        private static final OldGenerator<String> last =
                chooseOneFrom(asList(
                        "Allen", "Anderson", "Brown", "Clark", "Davis", "Foobar", "Garcia", "Hall", "Harris",
                        "Hernandez", "Jackson", "Johnson", "Jones", "King", "Lee", "Lewis", "Lopez", "Martin",
                        "Martinez", "Miller", "Moore", "Qwerty", "Robinson", "Rodriguez", "Smith", "Taylor",
                        "Thomas", "Thompson", "Walker", "White", "Williams", "Wilson", "Wright", "Young"
                ));

        private static final OldGenerator<String> suffix = chooseOneOf("Jr.", "III", "Sr.");

        private static final OldGenerator<Name> name = tupled(
                first,
                generateMaybe(6, 1, middle),
                last,
                generateMaybe(19, 1, suffix)
        ).fmap(into4(Name::name));

    }

    public static OldGenerator<Name> generateName() {
        return Generators.name;
    }

    public static void main(String[] args) {
        streamFrom(generateName().fmap(Name::getFullName)).next(100).forEach(System.out::println);
    }

}
