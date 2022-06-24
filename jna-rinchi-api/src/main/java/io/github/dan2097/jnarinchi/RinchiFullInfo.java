/**
 * JNA-RInChI - Library for calling RInChI from Java
 * Copyright © 2022 Nikolay Kochev
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
