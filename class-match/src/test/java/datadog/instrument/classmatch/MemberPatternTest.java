package datadog.instrument.classmatch;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;

class MemberPatternTest {

  @TableTest({
    "scenario                        | pattern                 | descriptor                          | expected",
    "exact match                     | (ILjava/lang/String;)V  | (ILjava/lang/String;)V              | true    ",
    "exact wrong return              | (ILjava/lang/String;)V  | (ILjava/lang/String;)I              | false   ",
    "params prefix any return        | (I)                     | (I)Ljava/lang/Object;               | true    ",
    "params prefix needs exact count | (I)                     | (II)V                               | false   ",
    "star return                     | (I)*                    | (I)J                                | true    ",
    "one wildcard                    | (?)V                    | ([[Ljava/lang/String;)V             | true    ",
    "one wildcard too many           | (?)V                    | (II)V                               | false   ",
    "one wildcard too few            | (?)V                    | ()V                                 | false   ",
    "positional after wildcards      | (??Lx/Msg;*)            | (IJLx/Msg;Z)V                       | true    ",
    "positional mismatch             | (??Lx/Msg;*)            | (IJLx/Other;)V                      | false   ",
    "positional prefix is not enough | (??Lx/Msg;*)            | (IJLx/Msgs;)V                       | false   ",
    "trailing star empty rest        | (Lx/A;*)                | (Lx/A;)V                            | true    ",
    "trailing star many rest         | (Lx/A;*)                | (Lx/A;[IJLx/B;)V                    | true    ",
    "trailing star wrong first       | (Lx/A;*)                | (Lx/B;)V                            | false   ",
    "any params literal return       | (*)Lscala/Option;       | (ILjava/lang/String;)Lscala/Option; | true    ",
    "any params wrong return         | (*)Lscala/Option;       | (I)Lscala/Some;                     | false   ",
    "any params return suffix trap   | (*)Lscala/Option;       | (Lxscala/Option;)V                  | false   ",
    "wildcard and literal return     | (?*)V                   | (I)V                                | true    ",
    "wildcard needs one param        | (?*)V                   | ()V                                 | false   ",
    "general literal return mismatch | (?)V                    | (I)I                                | false   ",
    "array literal                   | ([Ljava/lang/String;)V  | ([Ljava/lang/String;)V              | true    ",
    "array vs element                | (Ljava/lang/String;)V   | ([Ljava/lang/String;)V              | false   ",
    "no params                       | ()                      | ()Ljava/lang/Object;                | true    ",
    "no params vs one                | ()                      | (I)V                                | false   ",
    "holes everywhere                | (??)?                   | (Ljava/lang/Object;I)Z              | true    ",
    "return hole matches void        | (??)?                   | (Ljava/lang/Object;I)V              | true    ",
    "holes fix the arity             | (??)?                   | (I)Z                                | false   ",
    "holes fix the arity (more)      | (??)?                   | (IIJ)Z                              | false   ",
    "literal param with return hole  | (Ljava/lang/String;)?   | (Ljava/lang/String;)[J              | true    ",
    "literal param mismatch          | (Ljava/lang/String;)?   | (Ljava/lang/Object;)V               | false   ",
    "hole then literal return        | (?)Z                    | ([[Ljava/lang/Object;)Z             | true    ",
    "prefix fast path                | (Ljava/lang/Runnable;*) | (Ljava/lang/Runnable;IJ)V           | true    ",
    "prefix fast path, exact arity   | (Ljava/lang/Runnable;*) | (Ljava/lang/Runnable;)V             | true    ",
    "prefix fast path, wrong type    | (Ljava/lang/Runnable;*) | (Ljava/lang/Runnable2;)V            | false   ",
    "prefix with return              | (I*)Ljava/lang/String;  | (IJ)Ljava/lang/String;              | true    ",
    "prefix with return, no extras   | (I*)I                   | (I)I                                | true    ",
    "prefix with wrong return        | (I*)Ljava/lang/String;  | (IJ)Ljava/lang/Object;              | false   ",
    "star return synonym             | (I*)*                   | (I[J)V                              | true    ",
    "hole return synonym             | (I*)?                   | (I[J)V                              | true    "
  })
  void descriptorPatterns(String pattern, String descriptor, boolean expected) {
    assertEquals(expected, MemberPattern.compile("m" + pattern).matches(0, descriptor));
  }

  @TableTest({
    "scenario          | pattern            | access | expected",
    "required present  | public m()         | 1      | true    ",
    "required missing  | public m()         | 0      | false   ",
    "forbidden absent  | !static m()        | 1      | true    ",
    "forbidden present | !static m()        | 9      | false   ",
    "package private   | package m()        | 0      | true    ",
    "package vs public | package m()        | 1      | false   ",
    "combined          | public !static m() | 1      | true    "
  })
  void modifiers(String pattern, int access, boolean expected) {
    assertEquals(expected, MemberPattern.compile(pattern).matches(access, "()V"));
  }

  @Test
  void names() {
    assertArrayEquals(new String[] {"run"}, MemberPattern.names("run(I)V"));
    assertArrayEquals(new String[] {"a", "b"}, MemberPattern.names("public !static a|b(?)"));
    assertArrayEquals(new String[] {"<init>"}, MemberPattern.names("<init>(??Lx/Msg;*)"));
    assertArrayEquals(new String[] {"value"}, MemberPattern.names("private value:I"));
    assertArrayEquals(new String[] {"execute"}, MemberPattern.names("execute"));
    assertNull(MemberPattern.names("public *(Lx/A;*)"));
  }

  @Test
  void fields() {
    MemberPattern typed = MemberPattern.compile("private value:I");
    assertEquals(true, typed.field);
    assertEquals(true, typed.matches(Modifier.PRIVATE, "I"));
    assertEquals(false, typed.matches(Modifier.PRIVATE, "J"));
    assertEquals(false, typed.matches(Modifier.PUBLIC, "I"));
    assertEquals(true, MemberPattern.compile("value:*").matches(0, "Ljava/lang/String;"));
  }

  @Test
  void malformedPatternsFailAtCompile() {
    for (String bad :
        new String[] {
          "m(*I)V",
          "m(Lx/A)V",
          "m(Q)V",
          "m(I",
          "m(I)Lx/A",
          "m(I)II",
          "bogus m()",
          "!package m()",
          "f:Lx/A",
          "f:II"
        }) {
      assertThrows(IllegalArgumentException.class, () -> MemberPattern.compile(bad), bad);
    }
  }

  @Test
  void namePrefixesAndSuffixes() {
    MemberPattern p = MemberPattern.compile("do*|*schedule|service(I)");
    assertEquals(true, p.nameMatches("doGet"));
    assertEquals(true, p.nameMatches("do"));
    assertEquals(true, p.nameMatches("reschedule"));
    assertEquals(true, p.nameMatches("service"));
    assertEquals(false, p.nameMatches("undo"));
    assertEquals(false, p.nameMatches("schedules"));
    assertEquals(false, p.nameMatches("services"));
    assertEquals(false, MemberPattern.compile("*(I)").nameMatches("<init>"));
    assertEquals(true, MemberPattern.compile("*(I)").nameMatches("run"));
  }

  @Test
  void annotations() {
    assertEquals("javax/ws/rs/Path", MemberPattern.annotation("@javax/ws/rs/Path *"));
    assertEquals("a/B", MemberPattern.annotation("@a/B public !static run(I)"));
    assertNull(MemberPattern.annotation("public run(I)"));
    MemberPattern p = MemberPattern.compile("@javax/ws/rs/Path public *");
    assertNull(p.names);
    String[] marked = {"other/Anno", "javax/ws/rs/Path"};
    assertEquals(true, p.matches(new MethodOutline(Modifier.PUBLIC, "get", "()V", marked)));
    assertEquals(false, p.matches(new MethodOutline(0, "get", "()V", marked)));
    assertEquals(
        false, p.matches(new MethodOutline(Modifier.PUBLIC, "get", "()V", new String[] {"x/Y"})));
    assertThrows(IllegalArgumentException.class, () -> MemberPattern.compile("@a/B @c/D *"));
  }
}
