/**
 * JNA-InChI - Library for calling InChI from Java
 * Copyright © 2018 Daniel Lowe
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.dan2097.jnainchi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class InchiOptions {
  private static final boolean IS_WINDOWS = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("windows");

  private final List<InchiFlag> flags;
  private final int timeout;

  private InchiOptions(InchiOptionsBuilder builder) {
    this.flags = builder.flags;
    this.timeout = builder.timeout;
  }

  public static class InchiOptionsBuilder {

    private final List<InchiFlag> flags = new ArrayList<>();
    private int timeout = 0;

    public InchiOptionsBuilder withFlag(InchiFlag... flags) {
      for (InchiFlag flag : flags) {
        this.flags.add(flag);
      }
      return this;
    }
    
    /**
     * Timeout in seconds (0 = infinite timeout)
     * @param timeout
     * @return
     */
    public InchiOptionsBuilder withTimeout(int timeout) {
      if (timeout < 0) {
        throw new IllegalArgumentException("Timeout should be a time in seconds or 0 for infinite: " + timeout);
      }
      this.timeout = timeout;
      return this;
    }

    public InchiOptions build() {
      return new InchiOptions(this);
    }
  }
  
  public List<InchiFlag> getFlags() {
    return Collections.unmodifiableList(flags);
  } 
  
  public int getTimeout() {
    return timeout;
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
    if (timeout != 0) {
      if (sb.length() > 0) {
        sb.append(' ');
      }
      sb.append(IS_WINDOWS ? "/" : "-");
      sb.append("W");
      sb.append(String.valueOf(timeout));
    }
    return sb.toString();
  }
}
