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

public class FileTextUtils {
		
	private String endLine = "\n";	
	private StringBuilder strBuilder;	
	
	private void addRrinchiInputComponentToMolFile(RinchiInputComponent ric, String info) 
	{
		addMolHeader(info);
		//TODO
	}
	
	private void addMolHeader(String info) {
		strBuilder.append(endLine);
		strBuilder.append("  ");
		strBuilder.append(info);
		strBuilder.append(endLine);
		strBuilder.append(endLine);
	}
	
	
}
