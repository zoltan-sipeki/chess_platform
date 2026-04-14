package net.chess_platform.common.permission.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

class Or implements BooleanSupplier {

    private List<BooleanSupplier> policies = new ArrayList<>();

    Or(List<BooleanSupplier> policies) {
        this.policies = policies;
    }

    @Override
    public boolean getAsBoolean() {
        for (var policy : policies) {
            if (policy.getAsBoolean()) {
                return true;
            }
        }
        return false;
    }

}
