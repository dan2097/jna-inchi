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
 * RInChI (and RAuxInfo) conversion to a reaction represented as file text (RXN or RDFile formats)
 * @author Nikolay Kochev
 *
 */
public class FileTextOutput 
{
	private final String reactionFileText; 
	private final ReactionFileFormat fileFormat;
	private final Status status;
	private final int errorCode;
	private final String errorMessage;
	
	public FileTextOutput (String reactionFileText, ReactionFileFormat fileFormat,
			Status status, int errorCode, String errorMessage) {
		this.reactionFileText = reactionFileText;
		this.fileFormat = fileFormat;
		this.status = status;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getReactionFileText() {
		return reactionFileText;
	}

	public ReactionFileFormat getFileFormat() {
		return fileFormat;
	}
	
	public Status getStatus() {
		return status;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
