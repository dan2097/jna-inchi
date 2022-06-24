package io.github.dan2097.jnarinchi;

public class RinchiFullInfo 
{
	private String rinchi = null;
	private String auxInfo = null;
	private String rinchiKeyLong = null;
	private String rinchiKeyShort = null;
	private String rinchiKeyWeb = null;
	
	public RinchiFullInfo() {
	}
	
	public RinchiFullInfo(String rinchi, String auxInfo, 
			String rinchiKeyLong, String rinchiKeyShort, String rinchiKeyWeb) {
		this.rinchi = rinchi;
		this.auxInfo = auxInfo;
		this.rinchiKeyLong = rinchiKeyLong;
		this.rinchiKeyShort = rinchiKeyShort;
		this.rinchiKeyWeb = rinchiKeyWeb;
	}

	public String getRinchi() {
		return rinchi;
	}

	public void setRinchi(String rinchi) {
		this.rinchi = rinchi;
	}

	public String getAuxInfo() {
		return auxInfo;
	}

	public void setAuxInfo(String auxInfo) {
		this.auxInfo = auxInfo;
	}

	public String getRinchiKeyLong() {
		return rinchiKeyLong;
	}

	public void setRinchiKeyLong(String rinchiKeyLong) {
		this.rinchiKeyLong = rinchiKeyLong;
	}

	public String getRinchiKeyShort() {
		return rinchiKeyShort;
	}

	public void setRinchiKeyShort(String rinchiKeyShort) {
		this.rinchiKeyShort = rinchiKeyShort;
	}

	public String getRinchiKeyWeb() {
		return rinchiKeyWeb;
	}

	public void setRinchiKeyWeb(String rinchiKeyWeb) {
		this.rinchiKeyWeb = rinchiKeyWeb;
	}
}
