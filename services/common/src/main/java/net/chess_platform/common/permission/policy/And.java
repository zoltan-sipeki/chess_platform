package net.chess_platform.common.permission.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

class And implements BooleanSupplier {

    private List<BooleanSupplier> policy = new ArrayList<>();

    And(List<BooleanSupplier> policy) {
        this.policy = policy;
    }

    @Override
    public boolean getAsBoolean() {
        for (var predicate : policy) {
            if (!predicate.getAsBoolean()) {
                return false;
            }
        }

        return true;
    }

}
