package io.github.dan2097.jnarinchi;

import java.io.InputStream;
import java.util.Properties;

import com.sun.jna.Platform;



public class JnaRinchi 
{
	private static final String platform;
	private static final Throwable libraryLoadingError;
	static {
		Throwable t = null;
		String p = null;
		try {
			p = Platform.RESOURCE_PREFIX;
			RinchiLibrary.JNA_NATIVE_LIB.getName();
		}
		catch (Throwable e) { 
			t = e;
		}
		platform = p;
		libraryLoadingError = t;
	}


	/**
	 * Returns the version of the wrapped RInChI C library
	 * @return Version number String
	 */
	public static String getRinchiLibraryVersion() {
		try(InputStream is = JnaRinchi.class.getResourceAsStream("jnarinchi_build.props")) {
			Properties props = new Properties();
			props.load(is);
			return props.getProperty("rinchi_version");
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the version of the JNA-RInChI Java library
	 * @return Version number String
	 */
	public static String getJnaRinchiVersion() {
		try(InputStream is = JnaRinchi.class.getResourceAsStream("jnarinchi_build.props")) {
			Properties props = new Properties();
			props.load(is);
			return props.getProperty("jnarinchi_version");
		}
		catch (Exception e) {
			return null;
		}
	}

	private static void checkLibrary() {
		if (libraryLoadingError != null) {
			throw new RuntimeException("Error loading RInChI native code. Please check that the binaries for your platform (" + platform + ") have been included on the classpath.", libraryLoadingError);
		}
	}
}
