package dev.marksman.composablerandom.builtin;

import dev.marksman.composablerandom.Generator;
import dev.marksman.composablerandom.Result;
import dev.marksman.composablerandom.State;

import static dev.marksman.composablerandom.Generator.constant;
import static dev.marksman.composablerandom.Result.result;

public class Strings {

    static Generator<String> generateString(int length, Generator<String> g) {
        if (length <= 0) return constant("");
        else if (length == 1) return g;
        else {
            return Generator.contextDependent(s0 -> {
                State current = s0;
                StringBuilder output = new StringBuilder();
                for (int i = 0; i < length; i += 1) {
                    Result<State, String> next = g.run(current);
                    output.append(next.getValue());
                    current = next.getNextState();
                }
                return result(current, output.toString());
            });
        }
    }

    static Generator<String> generateStringFromCharacters(Generator<Character> g) {
        return Generators.sized(size -> generateStringFromCharacters(size, g));
    }

    static Generator<String> generateStringFromCharacters(int length, Generator<Character> g) {
        if (length <= 0) return constant("");
        else if (length == 1) return g.fmap(Object::toString);
        else {
            return Generator.contextDependent(s0 -> {
                State current = s0;
                StringBuilder output = new StringBuilder();
                for (int i = 0; i < length; i += 1) {
                    Result<State, Character> next = g.run(current);
                    output.append(next.getValue());
                    current = next.getNextState();
                }
                return result(current, output.toString());
            });
        }
    }

    @SafeVarargs
    static Generator<String> generateString(Generator<String> first, Generator<String>... more) {
        if (more.length == 0) return first;
        else {
            return Generator.contextDependent(s0 -> {
                Result<State, String> current = first.run(s0);
                StringBuilder output = new StringBuilder(current.getValue());
                for (Generator<String> g : more) {
                    current = g.run(current.getNextState());
                    output.append(current.getValue());
                }
                return result(current.getNextState(), output.toString());
            });
        }
    }

}
