package net.chess_platform.common.permission;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetUtils {
    public static <T> Set<T> intersect(List<Set<T>> sets) {
        var intersection = new HashSet<T>(sets.get(0));
        for (var set : sets) {
            intersection.retainAll(set);
        }
        return intersection;
    }

    public static <T> Set<T> union(List<Set<T>> sets) {
        var union = new HashSet<T>(sets.get(0));
        for (var set : sets) {
            union.addAll(set);
        }
        return union;
    }

    public static <T> Set<T> subtract(Set<T> a, Set<T> b) {
        var subtracted = new HashSet<T>(a);
        subtracted.removeAll(b);
        return subtracted;
    }
}
