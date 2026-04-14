package net.chess_platform.common.permission.predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.mongodb.core.query.Criteria;

import net.chess_platform.common.permission.SetUtils;

public class Or implements QueryCondition {

    private List<QueryCondition> predicates;

    public Or(List<QueryCondition> predicates) {
        this.predicates = predicates;
    }

    @Override
    public JPASpecification toJPASpecification() {
        return (path, query, cb) -> cb
                .or(predicates.stream().map(p -> p.toJPASpecification().toPredicate(path, query, cb)).toList());
    }

    @Override
    public Criteria toMongoCriteria() {
        return Criteria.where(null)
                .orOperator(predicates.stream().map(QueryCondition::toMongoCriteria).toList());
    }

    @Override
    public QueryCondition simplify() {
        var flattened = flattenOr();
        return new Or(flattened);
    }

    private List<QueryCondition> flattenOr() {
        var flattened = new ArrayList<QueryCondition>();

        for (var predicate : predicates) {
            if (predicate instanceof Or p) {
                flattened.addAll(p.flattenOr());
            } else {
                flattened.add(predicate.simplify());
            }
        }

        return flattened;
    }

    @Override
    public QueryCondition optimize() {
        var simplified = new ArrayList<QueryCondition>();
        for (var predicate : predicates) {
            simplified.add(predicate.optimize());
        }

        var eqs = new HashMap<String, Set<Eq>>();
        var neqs = new HashMap<String, Set<Neq>>();
        var ins = new HashMap<String, List<In>>();
        var nins = new HashMap<String, List<Nin>>();
        var rest = new ArrayList<QueryCondition>();

        boolean canOmitFalse = false;

        for (var predicate : simplified) {
            if (predicate instanceof True) {
                return predicate;
            }

            if (!(predicate instanceof False)) {
                canOmitFalse = true;
            }

            if (predicate instanceof Eq eq) {
                eqs.computeIfAbsent(eq.getField(), k -> new HashSet<>()).add(eq);
            } else if (predicate instanceof Neq neq) {
                neqs.computeIfAbsent(neq.getField(), k -> new HashSet<>()).add(neq);
            } else if (predicate instanceof In in) {
                ins.computeIfAbsent(in.getField(), k -> new ArrayList<>()).add(in);
            } else if (predicate instanceof Nin nin) {
                nins.computeIfAbsent(nin.getField(), k -> new ArrayList<>()).add(nin);
            } else if (predicate instanceof False && !canOmitFalse) {
                rest.add(predicate);
            } else {
                rest.add(predicate);
            }
        }

        collapseEqsIntoIns(eqs, ins);

        var unionedIns = unionIns(ins);

        boolean result = convertNeqsIntoNins(neqs, nins);
        if (!result) {
            return new True();
        }

        var intersectedNins = intersectNins(nins);
        if (intersectedNins == null) {
            return new True();
        }

        result = subtractInsFromNins(intersectedNins, unionedIns);
        if (!result) {
            return new True();
        }

        simplifyInsAndNins(unionedIns, intersectedNins, rest);

        if (rest.size() == 1) {
            return rest.get(0);
        }

        return new Or(rest);
    }

    private void simplifyInsAndNins(Map<String, In> ins, Map<String, Nin> nins,
            ArrayList<QueryCondition> rest) {
        for (var entry : ins.entrySet()) {
            var field = entry.getKey();
            var in = entry.getValue();

            if (in.getValue().size() == 1) {
                rest.add(new Eq(field, in.getValue().iterator().next()));
            } else {
                rest.add(in);
            }
        }

        for (var entry : nins.entrySet()) {
            var field = entry.getKey();
            var nin = entry.getValue();

            if (nin.getValue().size() == 1) {
                rest.add(new Neq(field, nin.getValue().iterator().next()));
            } else {
                rest.add(nin);
            }
        }
    }

    private static void collapseEqsIntoIns(Map<String, Set<Eq>> eqs,
            Map<String, List<In>> ins) {
        for (var entry : eqs.entrySet()) {
            var field = entry.getKey();
            var set = entry.getValue();
            ins.computeIfAbsent(field, k -> new ArrayList<>())
                    .add(collapstIntoIn(set));
        }
    }

    private static Map<String, In> unionIns(Map<String, List<In>> ins) {
        var result = new HashMap<String, In>();
        for (var entry : ins.entrySet()) {
            var field = entry.getKey();
            var sets = entry.getValue().stream().map(In::getValue).toList();
            var union = SetUtils.union(sets);
            result.put(field, new In(field, union));
        }
        return result;

    }

    private static boolean convertNeqsIntoNins(Map<String, Set<Neq>> neqs,
            Map<String, List<Nin>> nins) {
        for (var entry : neqs.entrySet()) {
            var field = entry.getKey();
            var set = entry.getValue();
            if (set.size() > 1) {
                return false;
            }

            var neq = set.iterator().next();
            nins.computeIfAbsent(field, k -> new ArrayList<>())
                    .add(new Nin(field, Set.of(neq.getValue())));
        }

        return true;
    }

    private static Map<String, Nin> intersectNins(Map<String, List<Nin>> nins) {
        var result = new HashMap<String, Nin>();
        for (var entry : nins.entrySet()) {
            var field = entry.getKey();
            var sets = entry.getValue().stream().map(Nin::getValue).toList();
            var intersection = SetUtils.intersect(sets);
            if (intersection.isEmpty()) {
                return null;
            }

            result.put(field, new Nin(field, intersection));
        }

        return result;
    }

    private static boolean subtractInsFromNins(Map<String, Nin> nins, Map<String, In> ins) {
        for (var entry : nins.entrySet()) {
            var field = entry.getKey();
            var nin = entry.getValue();
            var in = ins.get(field);
            if (in == null) {
                continue;
            }

            var subtraction = SetUtils.subtract(nin.getValue(), in.getValue());
            if (subtraction.isEmpty()) {
                return false;
            }

            nins.put(field, new Nin(field, subtraction));
            ins.remove(field);
        }

        return true;
    }

    private static In collapstIntoIn(Set<Eq> eqs) {
        var field = eqs.iterator().next().getField();
        var values = new HashSet<Object>();
        for (var eq : eqs) {
            values.add(eq.getValue());
        }
        return new In(field, values);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        builder.append('(');
        for (int i = 0; i < predicates.size(); ++i) {
            builder.append(predicates.get(i).toString());
            if (i < predicates.size() - 1) {
                builder.append(" OR ");
            }
        }
        builder.append(')');

        return builder.toString();
    }

    public List<QueryCondition> getPredicates() {
        return predicates;
    }
}
