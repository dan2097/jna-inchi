package io.github.dan2097.jnarinchi;

/**
 * Constants for the two versions of CTAB files, {@link #V2000} and {@link #V3000}.
 * <p>
 *     Please note that the RInChI library v1.00 <b>only support</b> reading and writing
 *     of <b>RXN and RDF V2000</b>. Consequently, this Java wrapper also only supports
 *     conversion from and to RXN and RDF V2000.
 * </p>
 */
public enum CTabVersion {
    /** Constant representing the CTAB V2000 format and its version string. */
    V2000("V2000"),
    /**
     * Constant representing the CTAB V3000 format and its version string.
     * <br>
     * <b>Note:</b> RXN and RDF V3000 is currently <b>not</b> supported.
     */
    V3000("V3000");

    private final String versionString;

    CTabVersion(String versionString) {
        this.versionString = versionString;
    }

    /**
     * Returns the version string of this CTabVersion.
     *
     * @return version string
     */
    public String getVersionString() {
        return this.versionString;
    }

    @Override
    public String toString() {
        return getVersionString();
    }
}
