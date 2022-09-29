package io.github.dan2097.jnarinchi;

/**
 * Constants for the two versions of CTAB files, {@link #V2000} and {@link #V3000}.
 */
public enum CTabVersion {
    V2000("V2000"),
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
