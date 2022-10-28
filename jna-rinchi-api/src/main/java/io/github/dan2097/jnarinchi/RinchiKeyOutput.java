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


/**
 * This class is a placeholder for the output/result from 
 * RInChIKey generation
 * 
 * @author Nikolay Kochev
 */
public class RinchiKeyOutput extends Output {
	private final String rinchiKey;
	private final RinchiKeyType rinchiKeyType;
	
	public RinchiKeyOutput (String rinchiKey, RinchiKeyType rinchiKeyType,
				Status status, int errorCode, String errorMessage) {
		super(status, errorCode, errorMessage);
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
