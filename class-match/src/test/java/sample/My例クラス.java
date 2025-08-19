package sample;

import java.io.Serializable;
import java.util.AbstractCollection;
import javax.annotation.Nonnull;

@Nonnull
public abstract class My例クラス<T> extends AbstractCollection<T> implements Serializable {

  @Nonnull @Deprecated String 実例 = "example";

  @Nonnull
  @SafeVarargs
  public final Boolean 何かをする(T... args) {
    return args.length > 0;
  }
}
