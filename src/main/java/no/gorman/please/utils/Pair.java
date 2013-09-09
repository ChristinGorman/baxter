package no.gorman.please.utils;

import java.util.Objects;

public class Pair<First, Second> {
    public final First from;
    public final Second to;

    public Pair(First from, Second to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to) + Objects.hash(to, from);
    }

    @Override
    public boolean equals(Object obj) {
        return (Objects.equals(from, ((Pair)obj).from ) && Objects.equals(to, ((Pair)obj).to))
                || (Objects.equals(from, ((Pair)obj).to ) && Objects.equals(to, ((Pair)obj).from));
    }
}
