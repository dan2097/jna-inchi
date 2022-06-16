package io.github.dan2097.jnarinchi;

public class RinchiOptions 
{
	public static enum RinchiKeyType {
		LONG, SHORT, WEB
	}
	
	private RinchiKeyType rinchiKeyType = RinchiKeyType.LONG;

	
	public RinchiKeyType getRinchiKeyType() {
		return rinchiKeyType;
	}

	public void setRinchiKeyType(RinchiKeyType rinchiKeyType) {
		this.rinchiKeyType = rinchiKeyType;
	}	
	
	
}
