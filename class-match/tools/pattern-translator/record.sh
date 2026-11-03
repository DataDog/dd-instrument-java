#!/bin/sh
# Compiles the generated DD_TRACE corpus against the recording API and prints the translation.
set -e
HERE=$(cd "$(dirname "$0")" && pwd)
SRC="$HERE/../../src/jmh/java/datadog/instrument/classmatch/DDTraceClassMatcherGraphs.java"
OUT=$(mktemp -d)
mkdir -p "$OUT/gen"
sed -e 's/^package datadog.instrument.classmatch;/package datadog.instrument.classmatch.recorder;/' \
    -e 's/import static datadog.instrument.classmatch\./import static datadog.instrument.classmatch.recorder./' \
    -e 's/DDTraceClassMatcherGraphs/DDTraceRecorded/g' \
    -e 's/return m -> true;/return MethodMatcher.anyMethod();/' \
    -e 's/return f -> true;/return FieldMatcher.anyField();/' \
    -e 's/return withoutGaps(result);/return result;/' \
    -e 's/(AccessMatcher) (acc -> (acc & \([0-9]*\)) != 0)/AccessMatcher.bit(\1)/' \
    "$SRC" > "$OUT/gen/DDTraceRecorded.java"
javac -nowarn -d "$OUT/classes" "$HERE"/src/datadog/instrument/classmatch/recorder/*.java "$OUT/gen/DDTraceRecorded.java"
java ${STRICT:+-Dstrict=true} -cp "$OUT/classes" datadog.instrument.classmatch.recorder.Translate
