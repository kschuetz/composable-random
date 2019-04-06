package dev.marksman.composablerandom.examples;

import com.jnape.palatable.lambda.adt.Maybe;
import dev.marksman.composablerandom.FrequencyEntry;
import dev.marksman.composablerandom.Generator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Into3.into3;
import static dev.marksman.composablerandom.FrequencyEntry.entry;
import static dev.marksman.composablerandom.builtin.Generators.*;
import static dev.marksman.composablerandom.legacy.OldGeneratedStream.streamFrom;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Street {
    private final Maybe<String> compass;
    private final String name;
    private final String suffix;

    public String getFullName() {
        return compass.match(__ -> "", s -> s + " ")
                + name
                + " " + suffix;
    }

    public static final Street street(Maybe<String> compass, String name, String suffix) {
        return new Street(compass, name, suffix);
    }

    private static class Generators {
        private static final Generator<String> compass =
                frequency(entry(8, chooseOneOfValues("N.", "S.", "W.", "E.")),
                        entry(1, chooseOneOfValues("NW", "NE", "SW", "SE")));

        private static final Generator<String> ordinal =
                generateInt(1, 99).fmap(n -> {
                    if (n == 11) return "11th";
                    else if (n % 10 == 1) return n + "st";
                    else if (n % 10 == 2) return n + "nd";
                    else if (n % 10 == 3) return n + "rd";
                    else return n + "th";
                });

        private static final Generator<String> president =
                chooseOneOfValues("Washington", "Adams", "Jefferson", "Madison", "Monroe", "Lincoln");

        private static final Generator<String> tree =
                chooseOneOfValues("Oak", "Maple", "Elm", "Pine", "Spruce", "Sycamore", "Birch", "Apple", "Peach");

        private static final Generator<String> suffix =
                frequency(FrequencyEntry.entryForValue(10, "St."),
                        FrequencyEntry.entryForValue(7, "Ave."),
                        FrequencyEntry.entryForValue(5, "Rd."),
                        FrequencyEntry.entryForValue(3, "Dr."),
                        FrequencyEntry.entryForValue(3, "La."),
                        FrequencyEntry.entryForValue(2, "Blvd."),
                        FrequencyEntry.entryForValue(1, "Ct."));

        private static final Generator<String> name =
                frequency(entry(3, ordinal),
                        entry(2, tree),
                        entry(2, president));

        private static final Generator<Street> street = tupled(
                generateMaybe(3, 1, compass),
                name,
                suffix)
                .fmap(into3(Street::street));
    }

    public static Generator<Street> generateStreet() {
        return Generators.street;
    }

    public static void main(String[] args) {
        streamFrom(generateStreet().fmap(Street::getFullName)).next(100).forEach(System.out::println);
    }
}
