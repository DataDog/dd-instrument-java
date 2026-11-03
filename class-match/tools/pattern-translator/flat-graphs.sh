#!/bin/sh
# Derives DDTraceFlatGraphs from DDTraceClassMatcherGraphs: same selectors, but method matchers
# come from Flat (flattened FlatMethodMatcher) instead of MethodMatcher's predicate factories.
set -e
HERE=$(cd "$(dirname "$0")" && pwd)
D="$HERE/../../src/jmh/java/datadog/instrument/classmatch"
sed -e 's/import static datadog.instrument.classmatch.MethodMatcher.method;/import static datadog.instrument.classmatch.Flat.method;/' \
    -e 's/import static datadog.instrument.classmatch.MethodMatcher.constructor;/import static datadog.instrument.classmatch.Flat.constructor;/' \
    -e 's/import static datadog.instrument.classmatch.StandardMatchers.not;/import static datadog.instrument.classmatch.Flat.not;/' \
    -e 's/return m -> true;/return Flat.anyMethod();/' \
    -e 's/DDTraceClassMatcherGraphs/DDTraceFlatGraphs/g' \
    -e 's/^ \* Matcher patterns extracted/ * Flattened (Flat API) copy of the matcher patterns extracted/' \
    "$D/DDTraceClassMatcherGraphs.java" > "$D/DDTraceFlatGraphs.java"
grep -c "result\[" "$D/DDTraceFlatGraphs.java"
