package dev.marksman.kraftwerk.constraints;

import java.time.LocalDateTime;
import java.util.Objects;

import static dev.marksman.kraftwerk.constraints.RangeInputValidation.validateRangeExclusive;
import static dev.marksman.kraftwerk.constraints.RangeInputValidation.validateRangeInclusive;

public final class LocalDateTimeRange implements Constraint<LocalDateTime> {
    private final LocalDateTime minInclusive;
    private final LocalDateTime maxInclusive;

    private LocalDateTimeRange(LocalDateTime minInclusive, LocalDateTime maxInclusive) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    public static LocalDateTimeRangeFrom from(LocalDateTime minInclusive) {
        return new LocalDateTimeRangeFrom() {
            @Override
            public LocalDateTimeRange to(LocalDateTime maxInclusive) {
                return inclusive(minInclusive, maxInclusive);
            }

            @Override
            public LocalDateTimeRange until(LocalDateTime maxExclusive) {
                return exclusive(minInclusive, maxExclusive);
            }
        };
    }

    public static LocalDateTimeRange inclusive(LocalDateTime minInclusive, LocalDateTime maxInclusive) {
        validateRangeInclusive(minInclusive, maxInclusive);
        return new LocalDateTimeRange(minInclusive, maxInclusive);
    }

    public static LocalDateTimeRange exclusive(LocalDateTime minInclusive, LocalDateTime maxExclusive) {
        validateRangeExclusive(minInclusive, maxExclusive);
        return new LocalDateTimeRange(minInclusive, maxExclusive.minusNanos(1));
    }

    public LocalDateTime minInclusive() {
        return minInclusive;
    }

    public LocalDateTime maxInclusive() {
        return maxInclusive;
    }

    @Override
    public boolean includes(LocalDateTime dateTime) {
        return !(dateTime.isBefore(minInclusive) || dateTime.isAfter(maxInclusive));
    }

    public LocalDateTimeRange withMinInclusive(LocalDateTime minInclusive) {
        return inclusive(minInclusive, maxInclusive);
    }

    public LocalDateTimeRange withMaxInclusive(LocalDateTime maxInclusive) {
        return inclusive(minInclusive, maxInclusive);
    }

    public LocalDateTimeRange withMaxExclusive(LocalDateTime maxExclusive) {
        return exclusive(minInclusive, maxExclusive);
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof LocalDateTimeRange)) return false;
        final LocalDateTimeRange other = (LocalDateTimeRange) o;
        if (!Objects.equals(this.minInclusive, other.minInclusive))
            return false;
        return Objects.equals(this.maxInclusive, other.maxInclusive);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $minInclusive = this.minInclusive;
        result = result * PRIME + ($minInclusive == null ? 43 : $minInclusive.hashCode());
        final Object $maxInclusive = this.maxInclusive;
        result = result * PRIME + ($maxInclusive == null ? 43 : $maxInclusive.hashCode());
        return result;
    }

    public interface LocalDateTimeRangeFrom {
        LocalDateTimeRange to(LocalDateTime maxInclusive);

        LocalDateTimeRange until(LocalDateTime maxExclusive);
    }

}
