package io.github.dan2097.jnarinchi;



import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import io.github.dan2097.jnainchi.InchiAtom;
import io.github.dan2097.jnainchi.InchiBond;
import io.github.dan2097.jnainchi.InchiBondType;
import io.github.dan2097.jnarinchi.cheminfo.MdlReactionReader;
import io.github.dan2097.jnarinchi.cheminfo.MdlReactionReaderException;
import io.github.dan2097.jnarinchi.cheminfo.MdlReactionWriter;
//import io.github.dan2097.jnarinchi.RinchiOptions.RinchiOptionsBuilder;
import io.github.dan2097.jnarinchi.cheminfo.PeriodicTable;

public class __DemoApp
{
	public static String react01 =
			"$RXN\r\n" +
					"\r\n" +
					"      ACCLDraw 041920162017\r\n" +
					"\r\n" +
					"  1  1\r\n" +
					"$MOL\r\n" +
					"\r\n" +
					"  ACCLDraw04191620172D\r\n" +
					"\r\n" +
					"  7  7  0  0  0  0  0  0  0  0999 V2000\r\n" +
					"    8.0599  -13.2763    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"    8.0676  -14.1013    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"    8.7833  -14.5029    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"    9.4915  -14.0880    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"    9.4838  -13.2630    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"    8.7680  -12.8530    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"    8.7603  -12.0280    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"  1  2  1  0  0  0  0\r\n" +
					"  2  3  1  0  0  0  0\r\n" +
					"  3  4  1  0  0  0  0\r\n" +
					"  4  5  1  0  0  0  0\r\n" +
					"  5  6  2  0  0  0  0\r\n" +
					"  1  6  1  0  0  0  0\r\n" +
					"  6  7  1  0  0  0  0\r\n" +
					//"M  ISO  2   1  14   2  12\r\n" +
					//"M  ISO  1   3  59\r\n" +
					//"M  CHG  1   2  -3\r\n" +
					"M  ISO  1   1  13\r\n" +
					"M  END\r\n" +
					"$MOL\r\n" +
					"\r\n" +
					"  ACCLDraw04191620172D\r\n" +
					"\r\n" +
					"  1  0  0  0  0  0  0  0  0  0999 V2000\r\n" +
					"   13.2890  -13.5347    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"M  END\r\n";


	public static String react02__ =
			"$RXN\r\n" +
					"Reaction 1\r\n" +
					"      JNA-RIN\r\n" +
					"\r\n" +
					"  1  0\r\n" +
					"$MOL\r\n" +
					"Reagent 1\r\n" +
					"  JNA-RIN\r\n" +
					"\r\n" +
					"  5  4  0  0  1  0  0  0  0  0999 V2000\r\n" +
					"    0.0000    0.0000    0.0000 C   0  0  0  0  0  0\r\n" +
					"    0.0000    0.0000    0.0000 C   0  0  2  0  0  0  0  0  0  0  1  0\r\n" +
					"    0.0000    0.0000    0.0000 N   0  0  0  0  0  0\r\n" +
					"    0.0000    0.0000    0.0000 O   0  0  0  0  0  0\r\n" +
					"    0.0000    0.0000    0.0000 Cl  0  0  0  0  0  0\r\n" +
					"  1  2  1  1  0  0  0\r\n" +
					"  2  3  1  0  0  0  0\r\n" +
					"  2  4  1  0  0  0  0\r\n" +
					"  2  5  1  0  0  0  0\r\n" +
					"M  END";

	//Stereo info of chiral atom defined simultaneously
	//by 3D coordinates + atom and bond flags/attributes set
	public static String react02 =
			"$RXN\r\n" +
					"Reaction 1\r\n" +
					"      JNA-RIN\r\n" +
					"\r\n" +
					"  1  0\r\n" +
					"$MOL\r\n" +
					"Reagent 1\r\n" +
					"  JNA-RIN\r\n" +
					"\r\n" +
					"  5  4  0  0  1  0  0  0  0  0999 V2000\r\n" +
					"    5.1793   -7.2869    0.0000 C   0  0  2  0  0  0  0  0  0  1  1  0\r\n" +
					"    6.2618   -6.6619    0.0000 I   0  0  0  0  0  0  0  0  0  2  0  0\r\n" +
					"    5.1793   -8.5369    0.0000 Br  0  0  0  0  0  0  0  0  0  3  0  0\r\n" +
					"    4.0968   -6.6619    0.0000 F   0  0  0  0  0  0  0  0  0  4  0  0\r\n" +
					"    6.2618   -7.9119    0.0000 Cl  0  0  0  0  0  0  0  0  0  5  0  0\r\n" +
					"  1  2  1  0  0  0  2\r\n" +
					"  1  3  1  0  0  0  2\r\n" +
					"  1  4  1  1  0  0  2\r\n" +
					"  1  5  1  0  0  0  2\r\n" +
					"M  END";

	//Stereo info of chiral atom defined only by 3D coordinates +  bond flag "up/down" set
	public static String react02_B =
			"$RXN\r\n" +
					"Reaction 1\r\n" +
					"      JNA-RIN\r\n" +
					"\r\n" +
					"  1  0\r\n" +
					"$MOL\r\n" +
					"Reagent 1\r\n" +
					" OpenBabel08252216503D\r\n" +
					"\r\n" +
					"  5  4  0  0  1  0  0  0  0  0999 V2000\r\n" +
					"   -0.4820   -0.1360   -0.0130 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"   -0.5730    1.3530   -0.3200 I   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"   -0.1910   -0.9030   -1.2960 Br  0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"   -1.8030   -0.6150    0.5760 F   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"    0.6390   -0.3800    0.9900 Cl  0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"  1  2  1  6  0  0  0\r\n" +
					"  1  3  1  0  0  0  0\r\n" +
					"  1  4  1  0  0  0  0\r\n" +
					"  1  5  1  0  0  0  0\r\n" +
					"M  END";

	//Stereo info of chiral atom defined only by 3D
	public static String react02_B__ =
			"$RXN\r\n" +
					"Reaction 1\r\n" +
					"      JNA-RIN\r\n" +
					"\r\n" +
					"  1  0\r\n" +
					"$MOL\r\n" +
					"Reagent 1\r\n" +
					" OpenBabel08252216503D\r\n" +
					"\r\n" +
					"  5  4  0  0  0  0  0  0  0  0999 V2000\r\n" +
					"   -0.4820   -0.1360   -0.0130 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"   -0.5730    1.3530   -0.3200 I   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"   -0.1910   -0.9030   -1.2960 Br  0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"   -1.8030   -0.6150    0.5760 F   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"    0.6390   -0.3800    0.9900 Cl  0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"  1  2  1  0  0  0  0\r\n" +
					"  1  3  1  0  0  0  0\r\n" +
					"  1  4  1  0  0  0  0\r\n" +
					"  1  5  1  0  0  0  0\r\n" +
					"M  END";

	public static String react03 =
			"$RXN\r\n" +
					"\r\n" +
					"      ACCLDraw 061220161711\r\n" +
					"\r\n" +
					"  1  1\r\n" +
					"$MOL\r\n" +
					"\r\n" +
					"  ACCLDraw06121617112D\r\n" +
					"\r\n" +
					"  5  4  0  0  1  0  0  0  0  0999 V2000\r\n" +
					"    5.1793   -7.2869    0.0000 C   0  0  2  0  0  0  0  0  0  1  1  0\r\n" +
					"    6.2618   -6.6619    0.0000 I   0  0  0  0  0  0  0  0  0  2  0  0\r\n" +
					"    5.1793   -8.5369    0.0000 Br  0  0  0  0  0  0  0  0  0  3  0  0\r\n" +
					"    4.0968   -6.6619    0.0000 F   0  0  0  0  0  0  0  0  0  4  0  0\r\n" +
					"    6.2618   -7.9119    0.0000 Cl  0  0  0  0  0  0  0  0  0  5  0  0\r\n" +
					"  1  2  1  0  0  0  2\r\n" +
					"  1  3  1  0  0  0  2\r\n" +
					"  1  4  1  1  0  0  2\r\n" +
					"  1  5  1  0  0  0  2\r\n" +
					"M  END\r\n" +
					"$MOL\r\n" +
					"\r\n" +
					"  ACCLDraw06121617112D\r\n" +
					"\r\n" +
					"  5  4  0  0  1  0  0  0  0  0999 V2000\r\n" +
					"   12.0439   -7.2869    0.0000 C   0  0  1  0  0  0  0  0  0  1  1  0\r\n" +
					"   13.1264   -6.6619    0.0000 I   0  0  0  0  0  0  0  0  0  2  0  0\r\n" +
					"   12.0439   -8.5369    0.0000 Br  0  0  0  0  0  0  0  0  0  3  0  0\r\n" +
					"   10.9614   -6.6619    0.0000 F   0  0  0  0  0  0  0  0  0  4  0  0\r\n" +
					"   13.1264   -7.9119    0.0000 Cl  0  0  0  0  0  0  0  0  0  5  0  0\r\n" +
					"  1  2  1  0  0  0  2\r\n" +
					"  1  3  1  0  0  0  2\r\n" +
					"  1  4  1  6  0  0  2\r\n" +
					"  1  5  1  0  0  0  2\r\n" +
					"M  END\r\n";

	//Stereo info of chiral atom defined only by 2D and bond "up/down" flag
	public static String react03__ =
			"$RXN\r\n" +
					"\r\n" +
					"      ACCLDraw 061220161711\r\n" +
					"\r\n" +
					"  1  1\r\n" +
					"$MOL\r\n" +
					"\r\n" +
					"  ACCLDraw06121617112D\r\n" +
					"\r\n" +
					"  5  4  0  0  0  0  0  0  0  0999 V2000\r\n" +
					"    5.1793   -7.2869    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"    6.2618   -6.6619    0.0000 I   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"    5.1793   -8.5369    0.0000 Br  0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"    4.0968   -6.6619    0.0000 F   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"    6.2618   -7.9119    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"  1  2  1  0  0  0  2\r\n" +
					"  1  3  1  0  0  0  2\r\n" +
					"  1  4  1  1  0  0  2\r\n" +
					"  1  5  1  0  0  0  2\r\n" +
					"M  END\r\n" +
					"$MOL\r\n" +
					"\r\n" +
					"  ACCLDraw06121617112D\r\n" +
					"\r\n" +
					"  5  4  0  0  1  0  0  0  0  0999 V2000\r\n" +
					"   12.0439   -7.2869    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"   13.1264   -6.6619    0.0000 I   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"   12.0439   -8.5369    0.0000 Br  0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"   10.9614   -6.6619    0.0000 F   0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"   13.1264   -7.9119    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\r\n" +
					"  1  2  1  0  0  0  2\r\n" +
					"  1  3  1  0  0  0  2\r\n" +
					"  1  4  1  6  0  0  2\r\n" +
					"  1  5  1  0  0  0  2\r\n" +
					"M  END\r\n";

	//aromatic test
	public static String react05__ =
			"$RXN\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"  1  0\r\n" + 
					"$MOL\r\n" + 
					"\r\n" + 
					"  CDK     0912221038\r\n" + 
					"\r\n" + 
					"  6  6  0  0  0  0  0  0  0  0999 V2000\r\n" + 
					"    0.0000    0.0000    0.0000 C   0  0  0  0  0  1  0  0  0  0  0  0\r\n" + 
					"    0.0000    0.0000    0.0000 C   0  0  0  0  0  1  0  0  0  0  0  0\r\n" + 
					"    0.0000    0.0000    0.0000 C   0  0  0  0  0  1  0  0  0  0  0  0\r\n" + 
					"    0.0000    0.0000    0.0000 C   0  0  0  0  0  1  0  0  0  0  0  0\r\n" + 
					"    0.0000    0.0000    0.0000 C   0  0  0  0  0  1  0  0  0  0  0  0\r\n" + 
					"    0.0000    0.0000    0.0000 C   0  0  0  0  0  1  0  0  0  0  0  0\r\n" + 
					"  1  2  4  0  0  0  0\r\n" + 
					"  2  3  4  0  0  0  0\r\n" + 
					"  3  4  4  0  0  0  0\r\n" + 
					"  4  5  4  0  0  0  0\r\n" + 
					"  5  6  4  0  0  0  0\r\n" + 
					"  1  6  4  0  0  0  0\r\n" + 
					"M  END\r\n";

	//aromatic test
	public static String react05__b =
			"$RXN\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"  1  0\r\n" + 
					"$MOL\r\n" + 
					"\r\n" + 
					"  CDK     0912221038\r\n" + 
					"\r\n" + 
					"  6  6  0  0  0  0  0  0  0  0999 V2000\r\n" + 
					"    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" + 
					"    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" + 
					"    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" + 
					"    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" + 
					"    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" + 
					"    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\r\n" + 
					"  1  2  4  0  0  0  0\r\n" + 
					"  2  3  4  0  0  0  0\r\n" + 
					"  3  4  4  0  0  0  0\r\n" + 
					"  4  5  4  0  0  0  0\r\n" + 
					"  5  6  4  0  0  0  0\r\n" + 
					"  1  6  4  0  0  0  0\r\n" + 
					"M  END\r\n";


	public static void main( String[] args ) throws Exception
	{
		System.out.println( "Demo of using native code!" );

		//System.setProperty("jna.library.path", "D:\\Projects\\Nina\\RInChI-CDK\\1.00 RInChI\\bin\\rinchi_lib\\windows\\x86_64");
		//System.setProperty("jna.library.path", "D:\\git-repositories\\forks\\jna-inchi\\jna-rinchi-win32-x86\\src\\main\\resources\\win32-x86");
		//System.out.println("jna.library.path = " + System.getProperty("jna.library.path"));

		System.out.println("JnaRinchi.getRinchiLibraryVersion() = " + JnaRinchi.getRinchiLibraryVersion());
		System.out.println("JnaRinchi.getJnaRinchiVersion() = " + JnaRinchi.getJnaRinchiVersion());
		System.out.println();

		//call_rinchi_methods();



		String rinchi = "";
		String auxInfo = "";

		rinchi = "RInChI=1.00.1S/C2H4O2/c1-2(3)4/h1H3,(H,3,4)!C2H6O/c1-2-3/h3H,2H2,1H3<>C4H8O2/c1-3-6-4(2)5/h3H2,1-2H3!H2O/h1H2<>H2O4S/c1-5(2,3)4/h(H2,1,2,3,4)/d+";
		auxInfo = "RAuxInfo=1.00.1/1/N:1,2,3,4/E:(3,4)/rA:4nCCOO/rB:s1;s2;d2;/rC:2.2188,-5.25,0;3.2416,-4.6594,0;3.2416,-3.478,0;4.2648,-5.2502,0;!0/N:3,2,1/rA:3nOCC/rB:s1;s2;/rC:7.5313,-5.0313,0;8.5541,-4.4407,0;9.5773,-5.0314,0;<>0/N:6,1,5,2,4,3/rA:6nCCOOCC/rB:s1;s2;d2;s3;s5;/rC:14.5625,-5.4688,0;15.5854,-4.8782,0;15.5854,-3.6967,0;16.6085,-5.4689,0;16.6085,-3.106,0;17.6317,-3.6967,0;!0/N:1/rA:1nO/rB:/rC:20.6563,-4.625,0;<>1/N:1,3,4,5,2/E:(1,2,3,4)/CRV:5.6/rA:5nOSOOO/rB:s1;s2;d2;d2;/rC:4.8321,-5.7375,0;4.8321,-4.9875,0;4.8321,-4.2375,0;5.5821,-4.9875,0;4.0821,-4.9875,0;";

		//rinchi = "RInChI=1.00.1S/<>C6H10O/c7-6-4-2-1-3-5-6/h4,7H,1-3,5H2/d-/u1-0-0";
		//auxInfo = "RAuxInfo=1.00.1/<>0/N:3,4,2,5,1,6,7/rA:7nCCCCCCO/rB:s1;s2;s3;s4;s1d5;s6;/rC:8.0599,-13.2763,0;8.0676,-14.1013,0;8.7833,-14.5029,0;9.4915,-14.088,0;9.4838,-13.263,0;8.768,-12.853,0;8.7603,-12.028,0;";

		//testInchiesFromRinchi(rinchi, auxInfo);
		//testInchiesFromRinchi2(rinchi, auxInfo);

		/*
        System.out.println("-------RXN--------");
        testRinchiToFileText(rinchi, auxInfo, ReactionFileFormat.RXN);
        System.out.println("-------RD--------");
        testRinchiToFileText(rinchi, auxInfo, ReactionFileFormat.RD);
        testInchiesFromRinchi2(rinchi, auxInfo);
		 */

		//RinchiInputComponent ric = new RinchiInputComponent();
		//ric.addAtom(new InchiAtom("C"));

		//testRinchiInputToRDFile01();

		//testRinchiInputToFile_plus(new String[]{"1-2-3N-4,2-5Cl", "1=2O"}, new String[]{"1-2-3N-4-5-6O", "1Cl"},
		//            new String[]{"1-2-3O", "1-2-3Po"}, ReactionFileFormat.RD, false, false);

		//testRinchiInputToFile_plus(new String[]{"1-2-3N,2-4O,2-5Cl"}, null, null, ReactionFileFormat.RD, false, false);

		//testFileToRinchiInput(react01);

		//miscTest();

		//testRinciToRinciInputToRinchi("RInChI=1.00.1S/<>C6H10O/c7-6-4-2-1-3-5-6/h4,7H,1-3,5H2/d-/u1-0-0",
		//        "RAuxInfo=1.00.1/<>0/N:3,4,2,5,1,6,7/rA:7nCCCCCCO/rB:s1;s2;s3;s4;s1d5;s6;/rC:8.0599,-13.2763,0;8.0676,-14.1013,0;8.7833,-14.5029,0;9.4915,-14.088,0;9.4838,-13.263,0;8.768,-12.853,0;8.7603,-12.028,0;");

		testRinchiOptions();


		//rinchi = "RInChI=1.00.1S/C2H4O2/c1-2(3)4/h1H3,(H,3,4)!C2H6O/c1-2-3/h3H,2H2,1H3<>C4H8O2/c1-3-6-4(2)5/h3H2,1-2H3!H2O/h1H2<>H2O4S/c1-5(2,3)4/h(H2,1,2,3,4)/d+";
		//rinchi = "RInChI=1.00.1S/C6H10O/c7-6-4-2-1-3-5-6/h4,7H,1-3,5H2<>CH4/h1H4/d+";
		rinchi = "RInChI=1.00.1S/C6H9O/c7-6-4-2-1-3-5-6/h2,5,7H,1,3-4H2/q-1/i3+2<>CH4/h1H4/d+";
		//rinchi = "RInChI=1.00.1S/C6H10O/c7-6-4-2-1-3-5-6/h4,7H,1-3,5H2/i5+2<>CH4/h1H4/d+";
		//rinchi = "RInChI=1.00.1S/C6H10O/c7-6-4-2-1-3-5-6/h4,7H,1-3,5H2/i3+2,5+1<>CH4/h1H4/d+";

		//generateRinchiInput(rinchi);
		//generateMdlFileText(rinchi);

		//RinchiInput rInp = generateRinchiInputFromTextFile (react01);
		//generateTextFileFromRinchiInput(rInp, false);


		//String rinchi_ = generateRinchi(react02);
		//String rinchi_ = generateRinchi(react03);
		// String rinchi_ = generateRinchi(react05__b);
		//String[] rinchi__ = generateRinchi_plus_RAuxInfo(react03__);
		//generateMdlFileText(rinchi__);


		//Rinchis with radicals
		//rinchi = "RInChI=1.00.1S/C6H10O/c7-6-4-2-1-3-5-6/h4,7H,1-3,5H2<>CH4/h1H4/d+";    //0
		//rinchi = "RInChI=1.00.1S/C6H8O/c7-6-4-2-1-3-5-6/h4,7H,1-3H2<>CH4/h1H4/d+";    //1
		//rinchi = "RInChI=1.00.1S/C6H9O/c7-6-4-2-1-3-5-6/h4-5,7H,1-3H2<>CH4/h1H4/d+";    //2
		//rinchi = "RInChI=1.00.1S/C6H8O/c7-6-4-2-1-3-5-6/h4,7H,1-3H2<>CH4/h1H4/d+";    //3



		/*
        String rinchi_stereo =  "RInChI=1.00.1S/CBrClFI/c2-1(3,4)5/t1-/m0/s1<>CBrClFI/c2-1(3,4)5/t1-/m1/s1/d+";
        String rAuxInfo_stereo = "RAuxInfo=1.00.1/0/N:1,3,5,4,2/it:im/rA:5cCIBrFCl/rB:s1;s1;P1;s1;/rC:5.1793,-7.2869,0;6.2618,-6.6619,0;5.1793,-8.5369,0;4.0968,-6.6619,0;6.2618,-7.9119,0;<>0/N:1,3,5,4,2/it:im/rA:5cCIBrFCl/rB:s1;s1;N1;s1;/rC:12.0439,-7.2869,0;13.1264,-6.6619,0;12.0439,-8.5369,0;10.9614,-6.6619,0;13.1264,-7.9119,0;";
        String ft_stereo = generateMdlFileText(rinchi_stereo, rAuxInfo_stereo);
        generateRinchi(ft_stereo);
		 */

		//getRinchiBaseIsotope("Zn");
		//testMajorIsotopes();

	}




	public static void call_rinchi_methods()
	{            

		//RinChI + Aux
		PointerByReference out_rinchi_string_p = new PointerByReference();
		PointerByReference out_rinchi_auxinfo_p = new PointerByReference();

		int errCode = RinchiLibrary.rinchilib_rinchi_from_file_text("AUTO", react01, true,
				out_rinchi_string_p, out_rinchi_auxinfo_p);        

		if (errCode != 0)
		{
			System.out.println("errCode = " + errCode);
			String err = RinchiLibrary.rinchilib_latest_err_msg();
			System.out.println("rinchilib_latest_err_msg: " + err);
			return;
		}      

		Pointer p = out_rinchi_string_p.getValue();
		String rinchi = p.getString(0);
		System.out.println(rinchi);

		p = out_rinchi_auxinfo_p.getValue();
		String aux = p.getString(0);
		System.out.println(aux);

		//L RInChI-Key
		PointerByReference out_rinchi_key_L_p = new PointerByReference();        
		errCode = RinchiLibrary.rinchilib_rinchikey_from_rinchi(rinchi, "L", out_rinchi_key_L_p);
		if (errCode != 0)
		{
			System.out.println("errCode = " + errCode);
			String err = RinchiLibrary.rinchilib_latest_err_msg();
			System.out.println("rinchilib_latest_err_msg: " + err);
			return;
		}      
		p = out_rinchi_key_L_p.getValue();
		String rinchi_key_L = p.getString(0);
		System.out.println(rinchi_key_L);

		//S RInChI-Key
		PointerByReference out_rinchi_key_S_p = new PointerByReference();        
		errCode = RinchiLibrary.rinchilib_rinchikey_from_rinchi(rinchi, "S", out_rinchi_key_S_p);
		if (errCode != 0)
		{
			System.out.println("errCode = " + errCode);
			String err = RinchiLibrary.rinchilib_latest_err_msg();
			System.out.println("rinchilib_latest_err_msg: " + err);
			return;
		}      
		p = out_rinchi_key_S_p.getValue();
		String rinchi_key_S = p.getString(0);
		System.out.println(rinchi_key_S);

		//W RInChI-Key
		PointerByReference out_rinchi_key_W_p = new PointerByReference();        
		errCode = RinchiLibrary.rinchilib_rinchikey_from_rinchi(rinchi, "W", out_rinchi_key_W_p);
		if (errCode != 0)
		{
			System.out.println("errCode = " + errCode);
			String err = RinchiLibrary.rinchilib_latest_err_msg();
			System.out.println("rinchilib_latest_err_msg: " + err);
			return;
		}      
		p = out_rinchi_key_W_p.getValue();
		String rinchi_key_W = p.getString(0);
		System.out.println(rinchi_key_W);


		//RD reaction file
		PointerByReference out_file_text_p = new PointerByReference();
		errCode = RinchiLibrary.rinchilib_file_text_from_rinchi(rinchi, "", "RD", out_file_text_p);
		if (errCode != 0)
		{
			System.out.println("errCode = " + errCode);
			String err = RinchiLibrary.rinchilib_latest_err_msg();
			System.out.println("rinchilib_latest_err_msg: " + err);
			return;
		}
		p = out_file_text_p.getValue();
		String RD_file = p.getString(0);
		System.out.println();
		System.out.println(RD_file);

		//RXN reaction file
		PointerByReference out_file_text_p2 = new PointerByReference();
		errCode = RinchiLibrary.rinchilib_file_text_from_rinchi(rinchi, "", "RXN", out_file_text_p2);
		if (errCode != 0)
		{
			System.out.println("errCode = " + errCode);
			String err = RinchiLibrary.rinchilib_latest_err_msg();
			System.out.println("rinchilib_latest_err_msg: " + err);
			return;
		}
		p = out_file_text_p2.getValue();
		String RRN_file = p.getString(0);
		System.out.println();
		System.out.println(RRN_file);

	}


	public static void testInchiesFromRinchi(String rinchi, String auxInfo)
	{          
		System.out.println("testInchiesFromRinchi ----");
		System.out.println(rinchi);
		System.out.println(auxInfo);
		System.out.println("-----------------------");
		PointerByReference out_inchis_text_p = new PointerByReference();        
		int errCode;

		errCode = RinchiLibrary.rinchilib_inchis_from_rinchi(rinchi, auxInfo, out_inchis_text_p);

		if (errCode != 0)
		{
			System.out.println("errCode = " + errCode);
			String err = RinchiLibrary.rinchilib_latest_err_msg();
			System.out.println("rinchilib_latest_err_msg: " + err);
			return;
		}


		Pointer p = out_inchis_text_p.getValue();
		String s = p.getString(0);
		//System.out.println(s);

		String elements[] = s.split("\n");
		for (String el : elements)
			System.out.println(el);

		/*
        //This is nor working but probably because rinchilib_inchis_from_rinchi function
        //returns a single text
        String[] sarray =  p.getStringArray(0);
        for (int i = 0; i < sarray.length; i++)
            System.out.println(i + "\n" + sarray[i]);
		 */    
	}

	public static void testInchiesFromRinchi2(String rinchi, String auxInfo)
	{          
		System.out.println("testInchiesFromRinchi ----");
		System.out.println(rinchi);
		System.out.println(auxInfo);
		System.out.println("-----------------------");


		RinchiDecompositionOutput rdo = JnaRinchi.decomposeRinchi(rinchi, auxInfo);

		if (rdo.getStatus() == Status.ERROR) {
			System.out.println("RinchiDecompositionStatus.ERROR");
			System.out.println("Error Code = " + rdo.getErrorCode());
			System.out.println("Error Message = " + rdo.getErrorMessage());
			return;
		}

		System.out.println("Direction " + rdo.getDirection());

		int nComp = rdo.getNumberOfComponents();
		for (int i = 0; i < nComp; i++) {
			System.out.println("Component " + (i+1) + "  " + rdo.getRoles()[i]);
			System.out.println(rdo.getInchis()[i]);
			System.out.println(rdo.getAuxInfos()[i]);
		}    

	}

	public static void testRinchiToFileText(String rinchi, String auxInfo, ReactionFileFormat format) {

		System.out.println(rinchi);
		System.out.println(auxInfo);
		System.out.println();

		FileTextOutput fileTextOut = JnaRinchi.rinchiToFileText(rinchi, auxInfo, format);
		if (fileTextOut.getStatus() != Status.SUCCESS) {
			System.out.println(fileTextOut.getErrorMessage());
		}
		else
			System.out.println(fileTextOut.getReactionFileText());
	}

	public static void testRinchiInputToRDFile01() {
		RinchiInput rInp = new RinchiInput();

		RinchiInputComponent ric1 = new RinchiInputComponent();
		rInp.addComponent(ric1);
		ric1.setRole(ReactionComponentRole.REAGENT);
		InchiAtom at1 = new InchiAtom("C");
		InchiAtom at2 = new InchiAtom("C");
		InchiAtom at3 = new InchiAtom("O");
		ric1.addAtom(at1);
		ric1.addAtom(at2);
		ric1.addAtom(at3);
		ric1.addBond(new InchiBond(at1,at2, InchiBondType.SINGLE));
		ric1.addBond(new InchiBond(at2,at3, InchiBondType.SINGLE));

		//FileTextUtils ftUtils = new FileTextUtils();
		//String rdfile = ftUtils.rinchiInputToFileText(rInp);
		MdlReactionWriter mdlWriter = new MdlReactionWriter();
		String rdfile = mdlWriter.rinchiInputToFileText(rInp);

		System.out.println(rdfile);

	}

	public static void testRinchiInputToFile_plus(String reagents[], String products[], String agents[],
			ReactionFileFormat format, boolean rinchiToFileText, boolean textFileToRinchiInput)
	{
		__ROSDALParser parser = new __ROSDALParser();                
		RinchiInput rInp = new RinchiInput();
		String err;

		System.out.println("Creating RinchiInput from ROSDALS:");

		if (reagents != null)
			for (int i = 0; i < reagents.length; i++) {
				System.out.println("reagent: " + reagents[i]);
				RinchiInputComponent ric = parser.parseROSDAL(reagents[i]);
				err = parser.getErrorMessages();
				if (!err.equals(""))
					System.out.println("Errors on reagent: " + reagents[i] + "\n" + err);
				else {
					rInp.addComponent(ric);
					ric.setRole(ReactionComponentRole.REAGENT);
				}    
			}

		if (products != null)
			for (int i = 0; i < products.length; i++) {
				System.out.println("product: " + products[i]);
				RinchiInputComponent ric = parser.parseROSDAL(products[i]);
				err = parser.getErrorMessages();
				if (!err.equals(""))
					System.out.println("Errors on product: " + products[i] + "\n" + err);
				else {
					rInp.addComponent(ric);
					ric.setRole(ReactionComponentRole.PRODUCT);
				}    
			}

		if (agents != null)
			for (int i = 0; i < agents.length; i++) {
				System.out.println("agent: " + agents[i]);
				RinchiInputComponent ric = parser.parseROSDAL(agents[i]);
				err = parser.getErrorMessages();
				if (!err.equals(""))
					System.out.println("Errors on agent: " + agents[i] + "\n" + err);
				else {
					rInp.addComponent(ric);
					ric.setRole(ReactionComponentRole.AGENT);
				}    
			}

		System.out.println();
		System.out.println("RinciInput to RD/RXN file:");
		System.out.println();

		//FileTextUtils ftUtils = new FileTextUtils();
		//ftUtils.setFormat(format);
		//String fileText = ftUtils.rinchiInputToFileText(rInp);
		MdlReactionWriter mdlWriter = new MdlReactionWriter();
		//mdlWriter.setFormat(format);
		String fileText = mdlWriter.rinchiInputToFileText(rInp);


		System.out.println();
		System.out.println("Converting RD/RXN file to RInChI");
		RinchiOutput rinchiOut = JnaRinchi.fileTextToRinchi(fileText);
		if (rinchiOut.getStatus() == Status.SUCCESS) {
			System.out.println(rinchiOut.getRinchi());
			System.out.println(rinchiOut.getAuxInfo());
		}    
		else {
			System.out.println("Errors:" + rinchiOut.getErrorMessage());
			return;
		}

		if (rinchiToFileText) {
			System.out.println();
			System.out.println("Converting RInChI to RD/RXN file");
			FileTextOutput ftOutput = JnaRinchi.rinchiToFileText(rinchiOut.getRinchi(), rinchiOut.getAuxInfo(), format);
			if (ftOutput.getStatus() == Status.SUCCESS) {
				System.out.println(ftOutput.getReactionFileText());
			}
			else
				System.out.println("Errors:" + ftOutput.getErrorMessage());
		}

		/*
		if (textFileToRinchiInput) {
			System.out.println();
			System.out.println("Converting RD/RXN file to RinchiInput");

			//RinchiInput rInp2 = ftUtils.fileTextToRinchiInput(fileText);
			MdlReactionReader mdlReader = new MdlReactionReader();
			mdlReader.setFormat(format);
			RinchiInput rInp2 = mdlReader.fileTextToRinchiInput(fileText);

			if (rInp2 == null) {
				System.out.println(mdlReader.getAllErrors());
				return;
			}    
			else {
				System.out.println(rInp2.toString());
			}            
		}
		*/

	}


	public static void testFileToRinchiInput(String fileText) {
		System.out.println(fileText);
		System.out.println();
		System.out.println("RD/RXN file to RinciInput");
		System.out.println();

		//FileTextUtils ftUtils = new FileTextUtils();
		//ftUtils.setFormat(ReactionFileFormat.RXN);
		//RinchiInput rInp = ftUtils.fileTextToRinchiInput(fileText);
		MdlReactionReader mdlReader = new MdlReactionReader();
		//mdlReader.setFormat(ReactionFileFormat.RXN);
		
		/*
		RinchiInput rInp = mdlReader.fileTextToRinchiInput(fileText);
		if (rInp == null) {
			//System.out.println(mdlReader.getAllErrors());
			return;
		}    
		else {
			System.out.println("Read " + rInp.getComponents().size() + " components");
		}
		*/

		System.out.println();
		System.out.println("RinciInput to RD/RXN file");
		System.out.println();
		//String fileText2 = ftUtils.rinchiInputToFileText(rInp);
		MdlReactionWriter mdlWriter = new MdlReactionWriter();
		//mdlWriter.setFormat(ReactionFileFormat.RXN);
		//String fileText2 = mdlWriter.rinchiInputToFileText(rInp);


		//System.out.println(fileText2);

	}

	public static void testRinciToRinciInputToRinchi(String rinchi, String auxInfo) throws Exception {
		System.out.println(rinchi);
		System.out.println(auxInfo);
		System.out.println("-----------------------");

		System.out.println("Converting rinchi --> RXN/RD");
		FileTextOutput ftOut = JnaRinchi.rinchiToFileText(rinchi, auxInfo, ReactionFileFormat.RXN);
		if (ftOut.getStatus() != Status.SUCCESS) {
			System.out.println(ftOut.getErrorMessage());
			return;
		}

		System.out.println("Converting RXN/RD --> RinchiInput");
		 //FileTextUtils ftUtils = new FileTextUtils();
        //ftUtils.setFormat(ReactionFileFormat.RXN);
        //RinchiInput rInp = ftUtils.fileTextToRinchiInput(ftOut.getReactionFileText());
        MdlReactionReader mdlReader = new MdlReactionReader();
        //mdlReader.setFormat(ReactionFileFormat.RXN);
        RinchiInput rInp = mdlReader.fileTextToRinchiInput(ftOut.getReactionFileText());

		if (rInp == null) {
			//System.out.println(mdlReader.getAllErrors());
			return;
		}

		System.out.println("Converting RinchiInput --> RXN/RD ");
		//String fileText2 = ftUtils.rinchiInputToFileText(rInp);
        MdlReactionWriter mdlWriter = new MdlReactionWriter();
       /*
        mdlWriter.setFormat(ReactionFileFormat.RXN);
        String fileText2 = mdlWriter.rinchiInputToFileText(rInp);

		if (!mdlWriter .getErrors().isEmpty()) {
			System.out.println(mdlWriter .getAllErrors());
			return;
		}
		

		System.out.println("Converting RXN/RD --> rinchi");
		RinchiOutput rinchiOut = JnaRinchi.fileTextToRinchi(fileText2);
		if (rinchiOut.getStatus() != Status.SUCCESS) {
			System.out.println(rinchiOut.getErrorMessage());
			return;
		}
		
		

		System.out.println("-----------------------");
		System.out.println(rinchiOut.getRinchi());
		System.out.println(rinchiOut.getAuxInfo());
		
		*/

	}

	public static void testRinchiOptions() {
		System.out.println("Test default RinchiOptions:");
		System.out.println(">" + RinchiOptions.DEFAULT_OPTIONS.toString() + "<");


		System.out.println("Test RinchiOptions:");        
		//Old style of RinchiOptions
		//RinchiOptions options = RinchiOptionBuilder.withFlags(RinchiFlag.ForceEquilibrium);
		//System.out.println(">" + options.toString() + "<");


		/*
        //New style of RinchiOptions (Uli's code updade from PR #3
        RinchiOptions options0= RinchiOptions.builder().build();
        System.out.println(">" + options0.toString() + "<");
        RinchiOptions options1 = RinchiOptions.builder().withFlag(RinchiFlag.ForceEquilibrium).build();
        System.out.println(">" + options1.toString() + "<");
        RinchiOptions options2 = RinchiOptions.builder().
        		withFlag(RinchiFlag.ForceEquilibrium).withTimeoutMilliSeconds(10000).build();
        System.out.println(">" + options2.toString() + "<");
		 */


	}

	public static String generateRinchi(String reactText) {
		RinchiOutput rinchiOut;

		rinchiOut = JnaRinchi.fileTextToRinchi(reactText);
		System.out.println("Generate RInChI from text file:");
		System.out.println(reactText + "\n");
		if (rinchiOut.getStatus() == Status.ERROR)
			System.out.println(rinchiOut.getErrorMessage());
		else
			System.out.println(rinchiOut.getRinchi());

		return (rinchiOut.getRinchi());
	}


	public static String[] generateRinchi_plus_RAuxInfo(String reactText) {
		RinchiOutput rinchiOut;

		rinchiOut = JnaRinchi.fileTextToRinchi(reactText);
		System.out.println("Generate RInChI from text file:");
		System.out.println(reactText + "\n");
		if (rinchiOut.getStatus() == Status.ERROR)
			System.out.println(rinchiOut.getErrorMessage());
		else {
			System.out.println(rinchiOut.getRinchi());
			System.out.println(rinchiOut.getAuxInfo());
		}    

		return (new String[] {rinchiOut.getRinchi(), rinchiOut.getAuxInfo() });
	}


	public static String generateMdlFileText(String rinchi) {
		return generateMdlFileText(rinchi, "");
	}

	public static String generateMdlFileText(String rinchi, String rAuxInfo) {
		System.out.println(rinchi);
		System.out.println(rAuxInfo);
		FileTextOutput fileTextOut = JnaRinchi.rinchiToFileText(rinchi, rAuxInfo, ReactionFileFormat.RXN);
		if (fileTextOut.getStatus() == Status.ERROR)
			System.out.println(fileTextOut.getErrorMessage());
		else
			System.out.println(fileTextOut.getReactionFileText());        
		return fileTextOut.getReactionFileText();
	}


	public static RinchiInput generateRinchiInput(String rinchi) {
		System.out.println(rinchi);
		RinchiInputFromRinchiOutput rInp = JnaRinchi.getRinchiInputFromRinchi(rinchi, "");
		if (rInp.getStatus() == Status.ERROR) {
			System.out.println(rInp.getErrorMessage());
			return null;
		}
		else {
			String s = rInp.getRinchiInput().toString();
			System.out.println(s);
			return rInp.getRinchiInput();    
		}
	}

	public static RinchiInput generateRinchiInputFromTextFile(String fileText, ReactionFileFormat format) throws MdlReactionReaderException {
		System.out.println(fileText);
		 //FileTextUtils ftu = new FileTextUtils();
        //ftu.setFormat(format);        
        //RinchiInput rInp = ftu.fileTextToRinchiInput(fileText);
        MdlReactionReader mdlReader = new MdlReactionReader();
       
        //mdlReader.setFormat(format);
        RinchiInput rInp = mdlReader.fileTextToRinchiInput(fileText);
		
		/*
        if (rInp == null)
			System.out.println(mdlReader.getAllErrors());
		else
			System.out.println(rInp.toString());
		*/	
		return rInp;
	}


	public static String generateTextFileFromRinchiInput(RinchiInput rInp, boolean print_rInp, ReactionFileFormat format) {
		if (print_rInp)
			System.out.println(rInp.toString());
		
		//FileTextUtils ftu = new FileTextUtils();
        //ftu.setFormat(format);        
        //String fileText = ftu.rinchiInputToFileText(rInp);
        MdlReactionWriter mdlWriter = new MdlReactionWriter();
        //mdlWriter.setFormat(format);
        String fileText = mdlWriter.rinchiInputToFileText(rInp);

		/*
		if (mdlWriter.getErrors().isEmpty())
			System.out.println(fileText);
		else
			System.out.println(mdlWriter.getAllErrors());   
		*/	

		return fileText;
	}



	/*
    public static void testMajorIsotopes() throws Exception {
        System.out.println("El#\tElSy\tCDK\tRinchi\tDiff");
        for (int i = 1; i <= 114; i++) {
            String el = PeriodicTable.mElementSymbol[i];
            int rinchiBaseIso = getRinchiBaseIsotope(el);
            //int rinchiBaseIso = 0;
            System.out.println(i + "\t" +el + "\t" + PeriodicTable.majorIsotope[i] + "\t" + rinchiBaseIso
                    + "\t" + (PeriodicTable.majorIsotope[i] - rinchiBaseIso)
                    + "\t\t" + getRinchiForElement_iso_plus_1(el));
        }
    }
	 */

	public static int getRinchiBaseIsotope(String elSy) throws Exception{
		return getRinchiBaseIsotope(elSy, false);
	}


	public static int getRinchiBaseIsotope(String elSy, boolean verbose) throws Exception {
		String reactText0 = getMDLReaction_iso_plus_1(elSy);    
		if (verbose)
			System.out.println(reactText0);

		//generateRinchi(reactText0);
		RinchiOutput rinchiOut = JnaRinchi.fileTextToRinchi(reactText0);
		if (rinchiOut.getStatus() == Status.ERROR) {
			System.out.println(rinchiOut.getErrorMessage());
			return -1;
		}    

		String rinchi = rinchiOut.getRinchi();
		if (verbose)
			System.out.println(rinchi);

		//String reactText = generateMdlFileText(rinchi);
		FileTextOutput fileTextOut = JnaRinchi.rinchiToFileText(rinchi, "", ReactionFileFormat.RXN);
		if (fileTextOut.getStatus() == Status.ERROR) {
			System.out.println(fileTextOut.getErrorMessage());
			return -2;
		}
		String reactText = fileTextOut.getReactionFileText();
		if (verbose)
			System.out.println(reactText);

		//Analyze reactText lines
		String isoStr = null;
		String lines[] = reactText.split("\n");
		for (int i = 10; i < lines.length; i++)
			if (lines[i].startsWith("M  ISO")) {
				isoStr = lines[i].substring(13).trim();
				break;
			}
		int baseIso = Integer.parseInt(isoStr) -1;
		//System.out.println("Base isotope of " + elSy + " is " + baseIso);
		return baseIso;
	}

	public static String getRinchiForElement_iso_plus_1(String elSy) {
		String reactText0 = getMDLReaction_iso_plus_1(elSy);    

		//generateRinchi(reactText0);
		RinchiOutput rinchiOut = JnaRinchi.fileTextToRinchi(reactText0);
		if (rinchiOut.getStatus() == Status.ERROR) {
			System.out.println(rinchiOut.getErrorMessage());
			return "null rinchi";
		}        
		return rinchiOut.getRinchi();
	}

	public static String getMDLReaction_iso_plus_1(String elSy) {
		String s=         "$RXN\r\n" +
				"\r\n" +
				"      Artificial\r\n" +
				"\r\n" +
				"  1  0\r\n" +
				"$MOL\r\n" +
				"\r\n" +
				"  Arificial\r\n" +
				"\r\n" +
				"  1  0  0  0  0  0  0  0  0  0999 V2000\r\n" +
				"   13.2890  -13.5347    0.0000 " + elSy + ((elSy.length()==1)?" ":"") + "  1  0  0  0  0  0  0  0  0  0  0  0\r\n" +
				"M  END\r\n";

		return s;
	}

	public static void miscTest()  throws Exception
	{
		//BufferedReader reader = new BufferedReader(new StringReader("test\ntest2\ntest3\ntest4\n"));
		//BufferedReader reader = new BufferedReader(new StringReader("test\rtest2\rtest3\rtest4\r"));
		BufferedReader reader = new BufferedReader(new StringReader("test\r\ntest2\r\ntest3\r\ntest4\r\n"));

		String line = reader.readLine();
		while (line != null) {
			System.out.println(line);
			line = reader.readLine();
		}


	}


}

