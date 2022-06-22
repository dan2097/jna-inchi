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

import io.github.dan2097.jnarinchi.RinchiOptions.RinchiKeyType;

public class RinchiKeyOutput 
{
	private final String rinchiKey;
	private final RinchiKeyType rinchiKeyType;
	private final RinchiKeyStatus status;
	private final int errorCode;
	private final String errorMessage;
	
	public RinchiKeyOutput (String rinchiKey, RinchiKeyType rinchiKeyType,
				RinchiKeyStatus status, int errorCode, String errorMessage) {
		this.rinchiKey = rinchiKey;
		this.rinchiKeyType = rinchiKeyType;
		this.status = status;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getRinchiKey() {
		return rinchiKey;
	}

	public RinchiKeyType getRinchiKeyType() {
		return rinchiKeyType;
	}

	public RinchiKeyStatus getStatus() {
		return status;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
