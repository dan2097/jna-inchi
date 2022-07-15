package io.github.dan2097.jnarinchi;

public enum RinchiKeyType 
{
	LONG ("L"), 
	SHORT ("S"), 
	WEB ("W");
	
	private final String shortDeignation;
	
	private RinchiKeyType (String shortDeignation) {
		this.shortDeignation = shortDeignation; 
	}
	
	public String getShortDeignation() {
		return shortDeignation;
	}
}
