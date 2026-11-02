package datadog.instrument.classmatch.recorder;

/**
 * Prints one line per corpus slot: {@code index TAB FALLBACK}, or {@code index TAB} followed by the
 * tab-separated member patterns. Unassigned slots (omitted selectors) are skipped.
 */
public final class Translate {
  public static void main(String[] args) {
    ClassMatcher[] raw = DDTraceRecorded.branches();
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < raw.length; i++) {
      if (raw[i] == null) {
        continue;
      }
      out.append(i);
      if (raw[i].patterns == null) {
        out.append("\tFALLBACK");
      } else {
        out.append("\tPATTERNS");
        for (String p : raw[i].patterns) {
          out.append('\t').append(p);
        }
      }
      out.append('\n');
    }
    System.out.print(out);
  }
}
