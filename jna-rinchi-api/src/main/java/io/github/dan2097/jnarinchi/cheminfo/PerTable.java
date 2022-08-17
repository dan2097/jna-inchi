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
	public static final String mElementSymbol[] =
		{
			"*",                                                 //0 any atom, generally should not be used
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
			"Md","No","Lr","Rf","Db","Sg","Bh","Hs","Mt","Ds",   //101-110
			"Rg","Cn","Nh","Fl" 		           			     //111-120    "Uuu","Uub","Uut","Uuq"
		};
	
	public static final int majorIsotope[] =
		{
				0, //Any atom (should not be used)
				1, //H 1
				4, //He 2
				7, //Li 3
				9, //Be 4
				11, //B 5
				12, //C 6
				14, //N 7
				16, //O 8
				19, //F 9
				20, //Ne 10
				23, //Na 11
				24, //Mg 12
				27, //Al 13
				28, //Si 14
				31, //P 15
				32, //S 16
				35, //Cl 17
				40, //Ar 18
				39, //K 19
				40, //Ca 20
				45, //Sc 21
				48, //Ti 22
				51, //V 23
				52, //Cr 24
				55, //Mn 25
				56, //Fe 26
				59, //Co 27
				58, //Ni 28
				63, //Cu 29
				64, //Zn 30
				69, //Ga 31
				74, //Ge 32
				75, //As 33
				80, //Se 34
				79, //Br 35
				84, //Kr 36
				85, //Rb 37
				88, //Sr 38
				89, //Y 39
				90, //Zr 40
				93, //Nb 41
				98, //Mo 42
				85, //Tc 43  using min value of: 85 86 87 88 89 90 91 92 93 94 95 96 97 98 99 100 101 102 103 104 105 106 107 108 109 110 111 112 113 114 115 116 117 118
				102, //Ru 44
				103, //Rh 45
				106, //Pd 46
				107, //Ag 47
				114, //Cd 48
				115, //In 49
				120, //Sn 50
				121, //Sb 51
				130, //Te 52
				127, //I 53
				132, //Xe 54
				133, //Cs 55
				138, //Ba 56
				139, //La 57
				140, //Ce 58
				141, //Pr 59
				142, //Nd 60
				126, //Pm 61  using min value of: 126 127 128 129 130 131 132 133 134 135 136 137 138 139 140 141 142 143 144 145 146 147 148 149 150 151 152 153 154 155 156 157 158 159 160 161 162 163
				152, //Sm 62
				153, //Eu 63
				158, //Gd 64
				159, //Tb 65
				164, //Dy 66
				165, //Ho 67
				166, //Er 68
				169, //Tm 69
				174, //Yb 70
				175, //Lu 71
				180, //Hf 72
				181, //Ta 73
				184, //W 74
				187, //Re 75
				192, //Os 76
				193, //Ir 77
				195, //Pt 78
				197, //Au 79
				202, //Hg 80
				205, //Tl 81
				208, //Pb 82
				209, //Bi 83
				188, //Po 84  using min value of: 188 189 190 191 192 193 194 195 196 197 198 199 200 201 202 203 204 205 206 207 208 209 210 211 212 213 214 215 216 217 218 219 220
				193, //At 85  using min value of: 193 194 195 196 197 198 199 200 201 202 203 204 205 206 207 208 209 210 211 212 213 214 215 216 217 218 219 220 221 222 223
				195, //Rn 86  using min value of: 195 196 197 198 199 200 201 202 203 204 205 206 207 208 209 210 211 212 213 214 215 216 217 218 219 220 221 222 223 224 225 226 227 228
				199, //Fr 87  using min value of: 199 200 201 202 203 204 205 206 207 208 209 210 211 212 213 214 215 216 217 218 219 220 221 222 223 224 225 226 227 228 229 230 231 232
				202, //Ra 88  using min value of: 202 203 204 205 206 207 208 209 210 211 212 213 214 215 216 217 218 219 220 221 222 223 224 225 226 227 228 229 230 231 232 233 234
				206, //Ac 89  using min value of: 206 207 208 209 210 211 212 213 214 215 216 217 218 219 220 221 222 223 224 225 226 227 228 229 230 231 232 233 234 235 236
				232, //Th 90
				231, //Pa 91
				238, //U 92
				225, //Np 93  using min value of: 225 226 227 228 229 230 231 232 233 234 235 236 237 238 239 240 241 242 243 244
				228, //Pu 94  using min value of: 228 229 230 231 232 233 234 235 236 237 238 239 240 241 242 243 244 245 246 247
				231, //Am 95  using min value of: 231 232 233 234 235 236 237 238 239 240 241 242 243 244 245 246 247 248 249
				233, //Cm 96  using min value of: 233 234 235 236 237 238 239 240 241 242 243 244 245 246 247 248 249 250 251 252
				235, //Bk 97  using min value of: 235 236 237 238 239 240 241 242 243 244 245 246 247 248 249 250 251 252 253 254
				237, //Cf 98  using min value of: 237 238 239 240 241 242 243 244 245 246 247 248 249 250 251 252 253 254 255 256
				240, //Es 99  using min value of: 240 241 242 243 244 245 246 247 248 249 250 251 252 253 254 255 256 257 258
				242, //Fm 100  using min value of: 242 243 244 245 246 247 248 249 250 251 252 253 254 255 256 257 258 259 260
				245, //Md 101  using min value of: 245 246 247 248 249 250 251 252 253 254 255 256 257 258 259 260 261 262
				248, //No 102  using min value of: 248 249 250 251 252 253 254 255 256 257 258 259 260 261 262 263 264
				251, //Lr 103  using min value of: 251 252 253 254 255 256 257 258 259 260 261 262 263 264 265 266
				253, //Rf 104  using min value of: 253 254 255 256 257 258 259 260 261 262 263 264 265 266 267 268
				255, //Db 105  using min value of: 255 256 257 258 259 260 261 262 263 264 265 266 267 268 269 270
				258, //Sg 106  using min value of: 258 259 260 261 262 263 264 265 266 267 268 269 270 271 272 273
				260, //Bh 107  using min value of: 260 261 262 263 264 265 266 267 268 269 270 271 272 273 274 275
				263, //Hs 108  using min value of: 263 264 265 266 267 268 269 270 271 272 273 274 275 276 277
				265, //Mt 109  using min value of: 265 266 267 268 269 270 271 272 273 274 275 276 277 278 279
				267, //Ds 110  using min value of: 267 268 269 270 271 272 273 274 275 276 277 278 279 280 281
				272, //Rg 111  using min value of: 272 273 274 275 276 277 278 279 280 281 282 283  (Uuu)
				277, //Cn 112  using min value of: 277 278 279 280 281 282 283 284 285 (Uub)
				283, //Nh 113  using min value of: 283 284 285 286 287  (Uut)
				285  //Fl 114  using min value of: 285 286 287 288 289  (Uuq)
		};
	
	public static int getAtomicNumberFromElSymbol(String s)
	{
		for (int i=1; i < mElementSymbol.length; i++)
		{
			if (s.compareTo(mElementSymbol[i])==0)
				return (i);
		}

		return(-1);
	};
}
