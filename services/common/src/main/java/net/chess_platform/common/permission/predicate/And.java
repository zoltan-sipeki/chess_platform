package net.chess_platform.common.permission.predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.mongodb.core.query.Criteria;

import net.chess_platform.common.permission.SetUtils;

public class And implements QueryCondition {

    private List<QueryCondition> predicates;

    public And(List<QueryCondition> predicates) {
        this.predicates = predicates;
    }

    @Override
    public JPASpecification toJPASpecification() {
        return (path, query, cb) -> cb.and(
                predicates.stream().map(p -> p.toJPASpecification().toPredicate(path, query, cb)).toList());
    }

    @Override
    public Criteria toMongoCriteria() {
        return Criteria.where(null)
                .andOperator(predicates.stream().map(QueryCondition::toMongoCriteria).toList());
    }

    @Override
    public QueryCondition simplify() {
        var flattened = flattenAnd();
        return new And(flattened);
    }

    private List<QueryCondition> flattenAnd() {
        var flattened = new ArrayList<QueryCondition>();

        for (var predicate : predicates) {
            if (predicate instanceof And p) {
                flattened.addAll(p.flattenAnd());
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

        boolean canOmitTrue = false;

        for (var predicate : simplified) {
            if (predicate instanceof False) {
                return predicate;
            }

            if (!(predicate instanceof True)) {
                canOmitTrue = true;
            }

            if (predicate instanceof Eq eq) {
                eqs.computeIfAbsent(eq.getField(), k -> new HashSet<>()).add(eq);
            } else if (predicate instanceof Neq neq) {
                neqs.computeIfAbsent(neq.getField(), k -> new HashSet<>()).add(neq);
            } else if (predicate instanceof In in) {
                ins.computeIfAbsent(in.getField(), k -> new ArrayList<>()).add(in);
            } else if (predicate instanceof Nin nin) {
                nins.computeIfAbsent(nin.getField(), k -> new ArrayList<>()).add(nin);
            } else if (predicate instanceof True && !canOmitTrue) {
                rest.add(predicate);
            } else {
                rest.add(predicate);
            }
        }

        boolean result = convertEqsIntoIns(eqs, ins);
        if (!result) {
            return new False();
        }

        var intersectedIns = intersectIns(ins);
        if (intersectedIns == null) {
            return new False();
        }

        collapseNeqsIntoNins(neqs, nins);

        var unionedNins = unionNins(nins);

        result = subractNinsFromIns(intersectedIns, unionedNins);
        if (!result) {
            return new False();
        }

        simplifyInsAndNins(intersectedIns, unionedNins, rest);

        if (rest.size() == 1) {
            return rest.get(0);
        }

        return new And(rest);
    }

    private static boolean convertEqsIntoIns(Map<String, Set<Eq>> eqs,
            Map<String, List<In>> ins) {
        for (var entry : eqs.entrySet()) {
            var field = entry.getKey();
            var set = entry.getValue();
            if (set.size() > 1) {
                return false;
            }

            var eq = set.iterator().next();
            ins.computeIfAbsent(field, k -> new ArrayList<>())
                    .add(new In(eq.getField(), Set.of(eq.getValue())));
        }

        return true;
    }

    private static Map<String, In> intersectIns(Map<String, List<In>> ins) {
        var collapsed = new HashMap<String, In>();
        for (var entry : ins.entrySet()) {
            var field = entry.getKey();
            var sets = entry.getValue().stream().map(In::getValue).toList();
            var intersection = SetUtils.intersect(sets);
            if (intersection.isEmpty()) {
                return null;
            }

            collapsed.put(field, new In(entry.getKey(), intersection));
        }

        return collapsed;
    }

    private static void collapseNeqsIntoNins(Map<String, Set<Neq>> neqs,
            Map<String, List<Nin>> nins) {
        for (var entry : neqs.entrySet()) {
            var field = entry.getKey();
            var set = entry.getValue();
            nins.computeIfAbsent(field, k -> new ArrayList<>()).add(collapseIntoNin(set));
        }
    }

    private static Map<String, Nin> unionNins(Map<String, List<Nin>> nins) {
        var collapsed = new HashMap<String, Nin>();
        for (var entry : nins.entrySet()) {
            var field = entry.getKey();
            var sets = entry.getValue().stream().map(Nin::getValue).toList();
            var union = SetUtils.union(sets);
            collapsed.put(field, new Nin(entry.getKey(), union));
        }

        return collapsed;
    }

    private static boolean subractNinsFromIns(Map<String, In> ins, Map<String, Nin> nins) {
        for (var entry : ins.entrySet()) {
            var field = entry.getKey();
            var in = entry.getValue();
            var nin = nins.get(field);
            if (nin == null) {
                continue;
            }

            var subtracted = SetUtils.subtract(in.getValue(), nin.getValue());
            if (subtracted.isEmpty()) {
                return false;
            }

            ins.put(field, new In(field, subtracted));
            nins.remove(field);

        }

        return true;
    }

    private static void simplifyInsAndNins(Map<String, In> ins, Map<String, Nin> nins,
            List<QueryCondition> rest) {

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

    private static Nin collapseIntoNin(Set<Neq> neqs) {
        var field = neqs.iterator().next().getField();
        var values = new HashSet<Object>();
        for (var neq : neqs) {
            values.add(neq.getValue());
        }
        return new Nin(field, values);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        builder.append('(');
        for (int i = 0; i < predicates.size(); ++i) {
            builder.append(predicates.get(i).toString());
            if (i < predicates.size() - 1) {
                builder.append(" AND ");
            }
        }
        builder.append(')');

        return builder.toString();
    }

    public List<QueryCondition> getPredicates() {
        return predicates;
    }
}
