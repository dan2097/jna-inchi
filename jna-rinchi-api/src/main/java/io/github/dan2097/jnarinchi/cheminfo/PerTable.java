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
package io.github.dan2097.jnarinchi.cheminfo;

public class PerTable {
	public static String mElementSymbol[] =
		{
			"*",                                                 //0 any atom
			"H","He","Li","Be","B","C","N","O","F","Ne",         //1-10
			"Na","Mg","Al","Si","P","S","Cl","Ar","K","Ca",      //11-20
			"Sc","Ti","V","Cr","Mn","Fe","Co","Ni","Cu","Zn",    //21-30
			"Ga","Ge","As","Se","Br","Kr","Rb","Sr","Y","Zr",    //31-40
			"Nb","Mo","Tc","Ru","Rh","Pd","Ag","Cd","In","Sn",   //41-50
			"Sb","Te","I","Xe","Cs","Ba","La","Ce","Pr","Nd",    //51-60
			"Pm","Sm","Eu","Gd","Tb","Dy","Ho","Er","Tm","Yb",   //61-70
			"Lu","Hf","Ta","W","Re","Os","Ir","Pt","Au","Hg",    //71-80
			"Tl","Pb","Bi","Po","At","Rn","Fr","Ra","Ac","Th",   //81-90
			"Pa","U","Np","Pu","Am","Cm","Bk","Cf","Es","Fm",    //91-100
			"Md","No","Lr","Rf","Db","Sg","Bh","Hs","Mt","Uun",  //101-110
			"Uuu","Uub"							                 //111-120
		};
	
	public static int getAtomicNumberFromElElement(String s)
	{
		for (int i=1; i < mElementSymbol.length; i++)
		{
			if (s.compareTo(mElementSymbol[i])==0)
				return (i);
		}

		return(-1);
	};
}
