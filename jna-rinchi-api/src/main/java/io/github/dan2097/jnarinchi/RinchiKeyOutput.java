package io.github.dan2097.jnarinchi;

import io.github.dan2097.jnarinchi.RinchiOptions.RinchiKeyType;

public class RinchiKeyOutput 
{
	private final String rinchiKey;
	private final RinchiKeyType rinchiKeyType;
	
	public RinchiKeyOutput (String rinchiKey, RinchiKeyType rinchiKeyType) {
		this.rinchiKey = rinchiKey;
		this.rinchiKeyType = rinchiKeyType;
	}

	public String getRinchiKey() {
		return rinchiKey;
	}

	public RinchiKeyType getRinchiKeyType() {
		return rinchiKeyType;
	}
}
