/**
 * JNA-RInChI - Library for calling RInChI from Java
 * Copyright © 2022 Nikolay Kochev
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dan2097.jnarinchi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;


/**
 * Holds available option settings to be used with RInChI operations.
 * <br>
 * The {@link #DEFAULT_OPTIONS} don't use any customized options for RInChI generation.
 * <br>
 * This object comes with a builder for configuration:
 * <pre>
 * RinchiFlag[] flags = new RinchiFlag[] {RinchiFlag.ForceEquilibrium};
 * RinchiOptions options = RinchiOptions.builder().withFlags(flags).build();
 * </pre>
 *
 * @author Nikolay Kochev
 */
public class RinchiOptions {
    /**
     * RinchiOption object configured with default settings.
     */
    public static final RinchiOptions DEFAULT_OPTIONS = RinchiOptions.builder().build();
    private static final boolean IS_WINDOWS = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("windows");

    private final List<RinchiFlag> flags;

    private RinchiOptions(RinchiOptionsBuilder builder) {
        this.flags = Collections.unmodifiableList(new ArrayList<>(builder.flags));
    }

    /**
     * Returns a builder for this object.
     * @return builder to configure and instantiate a RinchiOption object
     */
    public static RinchiOptionsBuilder builder() {
        return new RinchiOptionsBuilder();
    }

    public List<RinchiFlag> getFlags() {
        return flags;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (RinchiFlag rinchiFlag : flags) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(IS_WINDOWS ? "/" : "-");
            sb.append(rinchiFlag.toString());
        }

        return sb.toString();
    }

    /**
     * Builder for RinchiOptions.
     */
    public static class RinchiOptionsBuilder {
        private final EnumSet<RinchiFlag> flags = EnumSet.noneOf(RinchiFlag.class);

        private RinchiOptionsBuilder() {

        }

        /**
         * Customize the configuration by adding one or more flags.
         * @param flags flags to be added to the configuration
         * @return returns itself to allow for chaining method calls
         */
        public RinchiOptionsBuilder withFlag(RinchiFlag... flags) {
            Collections.addAll(this.flags, flags);
            return this;
        }

        /**
         * Returns an instance of {@link RinchiOptions} as configured by this builder.
         * @return an instance of RinchiOptions whose configuration is customized according to this builder
         */
        public RinchiOptions build() {
            return new RinchiOptions(this);
        }
    }
}
