package net.chess_platform.common.permission.policy;

import java.util.Arrays;
import java.util.function.BooleanSupplier;

public class PolicyUtils {

    public static BooleanSupplier and(BooleanSupplier... policies) {
        return new And(Arrays.asList(policies));
    }

    public static BooleanSupplier or(BooleanSupplier... policies) {
        return new Or(Arrays.asList(policies));
    }

    public static BooleanSupplier True() {
        return () -> true;
    }

    public static BooleanSupplier False() {
        return () -> false;
    }
}
