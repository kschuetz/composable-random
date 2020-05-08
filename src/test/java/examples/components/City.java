package examples.components;


import dev.marksman.kraftwerk.Generator;
import dev.marksman.kraftwerk.Generators;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Into3.into3;
import static dev.marksman.kraftwerk.Generators.chooseOneOfValues;
import static dev.marksman.kraftwerk.Generators.generateString;
import static dev.marksman.kraftwerk.frequency.FrequencyMap.frequencyMap;
import static dev.marksman.kraftwerk.weights.MaybeWeights.nothings;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class City {
    String name;

    public static City city(String name) {
        return new City(name);
    }

    public String pretty() {
        return name;
    }

    private static class generators {

        static Generator<String> prefix =
                chooseOneOfValues("North ", "South ", "East ", "West ", "Upper ", "Lower ", "New ");

        static Generator<String> part1 =
                chooseOneOfValues("Ash", "Bar", "Baz", "Brook", "Butter", "East", "Fair", "Foo", "Fox", "Frost",
                        "Glen", "Gold", "Green", "Hamp", "Haver", "Hemp", "Lee", "Long", "Maple", "Middle", "North",
                        "Oak", "Old", "Park", "Pine", "Quux", "Shelby", "Sher", "Silver", "South", "Spring", "Stam",
                        "Stath", "Wander", "West", "Willow", "Wind", "Wood");

        static Generator<String> part2Component =
                chooseOneOfValues("borne", "brook", "bury", "caster", "chester", "crest", "dale", "field",
                        "ford", "ham", "haven", "holme", "hurst", "ingham", "ington", "kirk", "ley", "mere", "more",
                        "mouth", "owoc", "pine", "ram", "shire", "side", "stead", "ston", "swick", "ton", "town", "view",
                        "ville", "wich", "wick", "win", "wood", "worth");

        static Generator<String> part2 =
                frequencyMap(part2Component.weighted(10))
                        .add(generateString(2, part2Component).weighted(4))
                        .add(generateString(3, part2Component).weighted(1))
                        .toGenerator();

        static Generator<String> suffix =
                chooseOneOfValues(" Park", " Village", " Grove", " Heights", " Town", " City", " Springs", " River");

        static Generator<String> rootName = generateString(part1, part2);

        static Generator<City> city =
                Generators.tupled(prefix.maybe(nothings(5).toJusts(1))
                                .fmap(p -> p.orElse("")),
                        rootName,
                        suffix.maybe(nothings(5).toJusts(1))
                                .fmap(s -> s.orElse("")))
                        .fmap(into3((prefix, root, suffix) -> city(prefix + root + suffix)));
    }

    public static Generator<String> generateCityRootName() {
        return generators.rootName;
    }

    public static Generator<City> generateCity() {
        return generators.city;
    }

    public static void main(String[] args) {
        generateCity()
                .run()
                .take(100)
                .forEach(System.out::println);
    }
}
