package com.github.dan2097.jnainchi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InchiOptions {
  private static final boolean IS_WINDOWS = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("windows");

  private final List<InchiFlag> flags;

  private InchiOptions(InchiOptionsBuilder builder) {
    this.flags = builder.flags;
  }

  public static class InchiOptionsBuilder {

    private final List<InchiFlag> flags = new ArrayList<>();

    InchiOptionsBuilder withFlag(InchiFlag... flags) {
      for (InchiFlag flag : flags) {
        this.flags.add(flag);
      }
      return this;
    }

    public InchiOptions build() {
      return new InchiOptions(this);
    }
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (InchiFlag inchiFlag : flags) {
      if (sb.length() > 0) {
        sb.append(' ');
      }
      sb.append(IS_WINDOWS ? "/" : "-");
      sb.append(inchiFlag.toString());
    }
    return sb.toString();
  }
}
