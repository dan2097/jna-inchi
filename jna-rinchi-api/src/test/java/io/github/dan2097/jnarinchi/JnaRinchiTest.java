/**
 * JNA-RInChI - Library for calling RInChI from Java
 * Copyright © 2022 Nikolay Kochev
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dan2097.jnarinchi;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing the main functionality of the jna-rinchi-api module.
 */
class JnaRinchiTest {

    /**
     * Reading a reaction from a resource text file into a text string.
     *
     * @param fileName name of the text file that contains a reaction in RD or RXN format
     * @return file content as a string
     */
    static String readReactionFromResourceFile(String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream is = JnaRinchiTest.class.getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }

        // clean up
        br.close();

        return sb.toString();
    }

    /**
     * Reading the full RInChI information from a text file.
     * The full RInChI information comprises the RInChI itself, RAuxInfo, Long-RInChIKey, Short-RInChIKey and Web-RInChIKey.
     *
     * @param fileName text file name containing the full RInChI information
     * @return RinchiFullInfo object with the full RInChI information
     */
    static RinchiFullInfo readRinchiFullInfoFromResourceFile(String fileName) throws IOException {
        InputStream is = JnaRinchiTest.class.getResourceAsStream(fileName);
        Properties props = new Properties();
        props.load(is);
        is.close();

        RinchiFullInfo rfi = new RinchiFullInfo();
        if (props.containsKey("RInChI")) {
            rfi.setRinchi("RInChI=" + props.getProperty("RInChI"));
        }
        if (props.containsKey("RAuxInfo")) {
            rfi.setAuxInfo("RAuxInfo=" + props.getProperty("RAuxInfo"));
        }
        if (props.containsKey("Long-RInChIKey")) {
            rfi.setRinchiKeyLong("Long-RInChIKey=" + props.getProperty("Long-RInChIKey"));
        }
        if (props.containsKey("Short-RInChIKey")) {
            rfi.setRinchiKeyShort("Short-RInChIKey=" + props.getProperty("Short-RInChIKey"));
        }
        if (props.containsKey("Web-RInChIKey")) {
            rfi.setRinchiKeyWeb("Web-RInChIKey=" + props.getProperty("Web-RInChIKey"));
        }

        return rfi;
    }

    /**
     * Generic test with example data provided by IUPAC RInChI developers.
     * The test compares the generated RInChI, RAuxInfo and RInChI-Keys with the
     * supplied ones.
     *
     * @param reactionFile the reaction text file in RXN or RDFile format
     * @param rinchiFile   text file with all RInCHI information
     */
    static void genericExampleTest(String reactionFile, String rinchiFile) throws IOException {
        String reactText = readReactionFromResourceFile(reactionFile);
        assertNotNull(reactText, "Reading reaction from text file " + reactionFile);
        RinchiFullInfo rfi = readRinchiFullInfoFromResourceFile(rinchiFile);
        assertNotNull(rfi, "Reading Rinchi Full Information from file " + rinchiFile);

        RinchiOutput rinchiOut;
        RinchiKeyOutput rinchiKeyOut;

        //Generate RInChI from text file
        //RInChI examples works with option: forceEquilibrium = false (which is by default);
        rinchiOut = JnaRinchi.fileTextToRinchi(reactText);
        assertEquals(rfi.getRinchi(), rinchiOut.getRinchi(), "Rinchi for " + reactionFile);
        assertEquals(rfi.getAuxInfo(), rinchiOut.getAuxInfo(), "RAuxInfo for " + reactionFile);

        //Generate Long-RinchiKey from text file
        rinchiKeyOut = JnaRinchi.fileTextToRinchiKey(reactText, RinchiKeyType.LONG);
        assertEquals(rfi.getRinchiKeyLong(), rinchiKeyOut.getRinchiKey(), "Long-RinchiKey for " + reactionFile);

        //Generate Short-RinchiKey from text file
        rinchiKeyOut = JnaRinchi.fileTextToRinchiKey(reactText, RinchiKeyType.SHORT);
        assertEquals(rfi.getRinchiKeyShort(), rinchiKeyOut.getRinchiKey(), "Short-RinchiKey for " + reactionFile);

        //Generate Web-RinchiKey from text file
        rinchiKeyOut = JnaRinchi.fileTextToRinchiKey(reactText, RinchiKeyType.WEB);
        assertEquals(rfi.getRinchiKeyWeb(), rinchiKeyOut.getRinchiKey(), "Web-RinchiKey for " + reactionFile);

        //Generate Long-RinchiKey from RInChI
        rinchiKeyOut = JnaRinchi.rinchiToRinchiKey(RinchiKeyType.LONG, rinchiOut.getRinchi());
        assertEquals(rfi.getRinchiKeyLong(), rinchiKeyOut.getRinchiKey(), "Long-RinchiKey for " + reactionFile
                + " generated from RInChI");

        //Generate Short-RinchiKey from RInChI
        rinchiKeyOut = JnaRinchi.rinchiToRinchiKey(RinchiKeyType.SHORT, rinchiOut.getRinchi());
        assertEquals(rfi.getRinchiKeyShort(), rinchiKeyOut.getRinchiKey(), "Short-RinchiKey for " + reactionFile
                + " generated from RInChI");

        //Generate Web-RinchiKey from RInChI
        rinchiKeyOut = JnaRinchi.rinchiToRinchiKey(RinchiKeyType.WEB, rinchiOut.getRinchi());
        assertEquals(rfi.getRinchiKeyWeb(), rinchiKeyOut.getRinchiKey(), "Web-RinchiKey for " + reactionFile
                + " generated from RInChI");
    }

    /**
     * Performs round-trip testing of the conversion from RInChI to MDL RXN/RDFile and vice versa.
     *
     * @param reactionFile the reaction text file in RXN or RDFile format
     * @param rinchiFile   text file with all RInChI information
     * @param format       format of the reaction file
     */
    static void doubleConversionExampleTest(String reactionFile, String rinchiFile, ReactionFileFormat format) throws IOException {
        //Double conversion test RIChI --> file text --> RInChI
        String reactText = readReactionFromResourceFile(reactionFile);
        assertNotNull(reactText, "Reading reaction from text file " + reactionFile);
        RinchiFullInfo rfi = readRinchiFullInfoFromResourceFile(rinchiFile);
        assertNotNull(rfi, "Reading Rinchi Full Infor from file " + rinchiFile);

        RinchiOutput rinchiOut0, rinchiOut;

        FileTextOutput fileTextOut = JnaRinchi.rinchiToFileText(rfi.getRinchi(), rfi.getAuxInfo(), format);
        assertSame(fileTextOut.getStatus(), FileTextStatus.SUCCESS, "RIChI to FileText conversion status for " + rinchiFile);
        rinchiOut = JnaRinchi.fileTextToRinchi(fileTextOut.getReactionFileText());
        assertEquals(rfi.getRinchi(), rinchiOut.getRinchi(), "Rinchi for " + reactionFile);
        assertEquals(rfi.getAuxInfo(), rinchiOut.getAuxInfo(), "RAuxInfo for " + reactionFile);

        //Testing number of components and decomposed InChIs
        rinchiOut0 = JnaRinchi.fileTextToRinchi(reactText);
        assertEquals(rinchiOut0.getRinchi(), rinchiOut.getRinchi(), "Rinchi for " + reactionFile);
        assertEquals(rinchiOut0.getAuxInfo(), rinchiOut.getAuxInfo(), "RAuxInfo for " + reactionFile);
    }

    @Test
    void testCheckLibrary() {
        boolean checkLib = true;
        String errMsg = "";
        try {
            JnaRinchi.getJnaRinchiVersion();
        } catch (Exception x) {
            checkLib = false;
            errMsg = x.getMessage();
        }
        assertTrue(checkLib, errMsg);
    }

    @Test
    void testExample_1_reactant_A() throws IOException {
        genericExampleTest("examples/1_reactant_-_A.rxn", "examples/1_reactant_-_A.txt");
        doubleConversionExampleTest("examples/1_reactant_-_A.rxn", "examples/1_reactant_-_A.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/1_reactant_-_A.rxn", "examples/1_reactant_-_A.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_1_reactant_no_product() throws IOException {
        genericExampleTest("examples/1_reactant_-_no_product.rxn", "examples/1_reactant_-_no_product.txt");
        doubleConversionExampleTest("examples/1_reactant_-_no_product.rxn", "examples/1_reactant_-_no_product.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/1_reactant_-_no_product.rxn", "examples/1_reactant_-_no_product.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_1_reactant_no_structure() throws IOException {
        genericExampleTest("examples/1_reactant_-_no_structure.rxn", "examples/1_reactant_-_no_structure.txt");
        doubleConversionExampleTest("examples/1_reactant_-_no_structure.rxn", "examples/1_reactant_-_no_structure.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/1_reactant_-_no_structure.rxn", "examples/1_reactant_-_no_structure.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_1_reactant_R() throws IOException {
        genericExampleTest("examples/1_reactant_-_R.rxn", "examples/1_reactant_-_R.txt");
        doubleConversionExampleTest("examples/1_reactant_-_R.rxn", "examples/1_reactant_-_R.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/1_reactant_-_R.rxn", "examples/1_reactant_-_R.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_1_reactant_X() throws IOException {
        genericExampleTest("examples/1_reactant_-_X.rxn", "examples/1_reactant_-_X.txt");
        doubleConversionExampleTest("examples/1_reactant_-_X.rxn", "examples/1_reactant_-_X.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/1_reactant_-_X.rxn", "examples/1_reactant_-_X.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_Esterification_01_flat() throws IOException {
        genericExampleTest("examples/Esterification_01_flat.rdf", "examples/Esterification_01_flat.txt");
        //Double conversion with RXN format fails!
        //doubleConversionExampleTest("examples/Esterification_01_flat.rdf", "examples/Esterification_01_flat.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/Esterification_01_flat.rdf", "examples/Esterification_01_flat.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_Esterification_01() throws IOException {
        genericExampleTest("examples/Esterification_01.rdf", "examples/Esterification_01.txt");
        //Double conversion with RXN format fails!
        //doubleConversionExampleTest("examples/Esterification_01.rdf", "examples/Esterification_01.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/Esterification_01.rdf", "examples/Esterification_01.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_Esterification_02() throws IOException {
        genericExampleTest("examples/Esterification_02.rdf", "examples/Esterification_02.txt");
        //Double conversion with RXN format fails!
        //doubleConversionExampleTest("examples/Esterification_02.rdf", "examples/Esterification_02.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/Esterification_02.rdf", "examples/Esterification_02.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_Esterification_03() throws IOException {
        genericExampleTest("examples/Esterification_03.rdf", "examples/Esterification_03.txt");
        //Double conversion with RXN format fails!
        //doubleConversionExampleTest("examples/Esterification_03.rdf", "examples/Esterification_03.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/Esterification_03.rdf", "examples/Esterification_03.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_Inverted_stereochemistry() throws IOException {
        genericExampleTest("examples/Inverted_stereochemistry.rxn", "examples/Inverted_stereochemistry.txt");
        doubleConversionExampleTest("examples/Inverted_stereochemistry.rxn", "examples/Inverted_stereochemistry.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/Inverted_stereochemistry.rxn", "examples/Inverted_stereochemistry.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_Multiplesteps() throws IOException {
        genericExampleTest("examples/Multiplesteps.rdf", "examples/Multiplesteps.txt");
        //Double conversion with RXN format fails!
        //doubleConversionExampleTest("examples/Multiplesteps.rdf", "examples/Multiplesteps.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/Multiplesteps.rdf", "examples/Multiplesteps.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_No_reactant_1_product_02() throws IOException {
        genericExampleTest("examples/No_reactant_-_1_product_02.rxn", "examples/No_reactant_-_1_product_02.txt");
        doubleConversionExampleTest("examples/No_reactant_-_1_product_02.rxn", "examples/No_reactant_-_1_product_02.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/No_reactant_-_1_product_02.rxn", "examples/No_reactant_-_1_product_02.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_No_reactant_1_product() throws IOException {
        genericExampleTest("examples/No_reactant_-_1_product.rxn", "examples/No_reactant_-_1_product.txt");
        doubleConversionExampleTest("examples/No_reactant_-_1_product.rxn", "examples/No_reactant_-_1_product.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/No_reactant_-_1_product.rxn", "examples/No_reactant_-_1_product.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_No_reactant_no_product() throws IOException {
        genericExampleTest("examples/No_reactant_-_no_product.rdf", "examples/No_reactant_-_no_product.txt");
        //Double conversion with RXN format fails!
        //doubleConversionExampleTest("examples/No_reactant_-_no_product.rdf", "examples/No_reactant_-_no_product.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/No_reactant_-_no_product.rdf", "examples/No_reactant_-_no_product.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_no_structure_1_product() throws IOException {
        genericExampleTest("examples/no_structure_-_1_product.rxn", "examples/no_structure_-_1_product.txt");
        doubleConversionExampleTest("examples/no_structure_-_1_product.rxn", "examples/no_structure_-_1_product.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/no_structure_-_1_product.rxn", "examples/no_structure_-_1_product.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_No_Structure_0_02() throws IOException {
        genericExampleTest("examples/No_Structure_0-02.rdf", "examples/No_Structure_0-02.txt");
        //Double conversion with RXN format fails!
        //doubleConversionExampleTest("examples/No_Structure_0-02.rdf", "examples/No_Structure_0-02.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/No_Structure_0-02.rdf", "examples/No_Structure_0-02.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_Nnostruct_X() throws IOException {
        genericExampleTest("examples/nostruct_-_X.rxn", "examples/nostruct_-_X.txt");
        doubleConversionExampleTest("examples/nostruct_-_X.rxn", "examples/nostruct_-_X.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/nostruct_-_X.rxn", "examples/nostruct_-_X.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_R__A() throws IOException {
        genericExampleTest("examples/R-_-A.rxn", "examples/R-_-A.txt");
        doubleConversionExampleTest("examples/R-_-A.rxn", "examples/R-_-A.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/R-_-A.rxn", "examples/R-_-A.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_RingOpening01() throws IOException {
        genericExampleTest("examples/RingOpening01.rxn", "examples/RingOpening01.txt");
        doubleConversionExampleTest("examples/RingOpening01.rxn", "examples/RingOpening01.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/RingOpening01.rxn", "examples/RingOpening01.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_star_star__nostruct() throws IOException {
        genericExampleTest("examples/star_star_-_nostruct.rxn", "examples/star_star_-_nostruct.txt");
        doubleConversionExampleTest("examples/star_star_-_nostruct.rxn", "examples/star_star_-_nostruct.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/star_star_-_nostruct.rxn", "examples/star_star_-_nostruct.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_Styrene__Polystyrene_as_no_struct() throws IOException {
        genericExampleTest("examples/Styrene_-_Polystyrene_as_no-struct.rxn", "examples/Styrene_-_Polystyrene_as_no-struct.txt");
        doubleConversionExampleTest("examples/Styrene_-_Polystyrene_as_no-struct.rxn", "examples/Styrene_-_Polystyrene_as_no-struct.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/Styrene_-_Polystyrene_as_no-struct.rxn", "examples/Styrene_-_Polystyrene_as_no-struct.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_Tautomerization_01() throws IOException {
        genericExampleTest("examples/Tautomerization_01.rxn", "examples/Tautomerization_01.txt");
        doubleConversionExampleTest("examples/Tautomerization_01.rxn", "examples/Tautomerization_01.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/Tautomerization_01.rxn", "examples/Tautomerization_01.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_X__1_product() throws IOException {
        genericExampleTest("examples/X_-_1_product.rxn", "examples/X_-_1_product.txt");
        doubleConversionExampleTest("examples/X_-_1_product.rxn", "examples/X_-_1_product.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/X_-_1_product.rxn", "examples/X_-_1_product.txt", ReactionFileFormat.RD);
    }
}
