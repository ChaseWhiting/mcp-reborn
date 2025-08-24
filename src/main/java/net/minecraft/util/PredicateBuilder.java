package net.minecraft.util;

import java.util.function.Predicate;

public class PredicateBuilder {

    public static class Builder<O> {
        private Predicate<O> predicate;
        private boolean hasCondition;

        public Builder() {
            this.predicate = o -> true;  // Initialize to a default predicate
            this.hasCondition = false;
        }

        public Builder<O> addCondition(Predicate<O> condition) {
            if (condition == null) {
                throw new IllegalArgumentException("Condition cannot be null");
            }
            this.predicate = this.predicate.and(condition);
            this.hasCondition = true;
            return this;
        }


        public Builder<O> or(Predicate<O> orThis) {
            if (!hasCondition) {
                throw new IllegalStateException("Cannot call or() before addCondition()");
            }
            if (orThis == null) {
                throw new IllegalArgumentException("Predicate cannot be null");
            }
            this.predicate = this.predicate.or(orThis);
            return this;
        }

        public Predicate<O> build() {
            return predicate;
        }
    }
}
