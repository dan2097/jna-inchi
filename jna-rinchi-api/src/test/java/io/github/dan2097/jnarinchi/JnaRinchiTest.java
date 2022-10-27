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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing the main functionality of the jna-rinchi-api module.
 * <p>
 * The testing data is provided by the IUPAC RInChI developers and available
 * at the <a href="https://github.com/IUPAC-InChI/RInChI">RInChI repository</a>.
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
     * Generic test that compares the <i>generated</i> RInChI, RAuxInfo and the three RInChI-Keys with the <i>expected</i> data.
     * <br>
     * If RAuxInfo, Long-RInChI-Keys, Short-RInChI-Keys or Web-RInChI-Keys are not supplied in the text file
     * that contains the expected data, it is excluded from the comparison. For example, if there is no line
     * starting with {@code Long-RInChIKey=} in the text file with the expected data, the generated Long-RInChIKey
     * is excluded from comparing the actual to the expected data. The test still fails if the generation of
     * the excluded data point gives raise to an error.
     * <br>
     * The calculation of the RInChI is carried out with the default options as specified in
     * {@link RinchiOptions#DEFAULT_OPTIONS} which has set forceEquilibrium to <code>false</code>.
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

        if (rfi.getAuxInfo() != null) {
            assertEquals(rfi.getAuxInfo(), rinchiOut.getAuxInfo(), "RAuxInfo for " + reactionFile);
        }

        //Generate Long-RinchiKey from text file
        rinchiKeyOut = JnaRinchi.fileTextToRinchiKey(reactText, RinchiKeyType.LONG);
        if (rfi.getRinchiKeyLong() != null) {
            assertEquals(rfi.getRinchiKeyLong(), rinchiKeyOut.getRinchiKey(), "Long-RinchiKey for " + reactionFile);
        }

        //Generate Short-RinchiKey from text file
        rinchiKeyOut = JnaRinchi.fileTextToRinchiKey(reactText, RinchiKeyType.SHORT);
        if (rfi.getRinchiKeyShort() != null) {
            assertEquals(rfi.getRinchiKeyShort(), rinchiKeyOut.getRinchiKey(), "Short-RinchiKey for " + reactionFile);
        }

        //Generate Web-RinchiKey from text file
        rinchiKeyOut = JnaRinchi.fileTextToRinchiKey(reactText, RinchiKeyType.WEB);
        if (rfi.getRinchiKeyWeb() != null) {
            assertEquals(rfi.getRinchiKeyWeb(), rinchiKeyOut.getRinchiKey(), "Web-RinchiKey for " + reactionFile);
        }

        //Generate Long-RinchiKey from RInChI
        rinchiKeyOut = JnaRinchi.rinchiToRinchiKey(RinchiKeyType.LONG, rinchiOut.getRinchi());
        if (rfi.getRinchiKeyLong() != null) {
            assertEquals(rfi.getRinchiKeyLong(), rinchiKeyOut.getRinchiKey(), "Long-RinchiKey for " + reactionFile
                    + " generated from RInChI");
        }

        //Generate Short-RinchiKey from RInChI
        rinchiKeyOut = JnaRinchi.rinchiToRinchiKey(RinchiKeyType.SHORT, rinchiOut.getRinchi());
        if (rfi.getRinchiKeyShort() != null) {
            assertEquals(rfi.getRinchiKeyShort(), rinchiKeyOut.getRinchiKey(), "Short-RinchiKey for " + reactionFile
                    + " generated from RInChI");
        }

        //Generate Web-RinchiKey from RInChI
        rinchiKeyOut = JnaRinchi.rinchiToRinchiKey(RinchiKeyType.WEB, rinchiOut.getRinchi());
        if (rfi.getRinchiKeyWeb() != null) {
            assertEquals(rfi.getRinchiKeyWeb(), rinchiKeyOut.getRinchiKey(), "Web-RinchiKey for " + reactionFile
                    + " generated from RInChI");
        }
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

    @Test
    public void testExample_two_reactants_no_products() throws IOException {
        genericExampleTest("examples/two_reactants_no_products.rxn", "examples/two_reactants_no_products.rxn.rinchi_strings.txt");
        doubleConversionExampleTest("examples/two_reactants_no_products.rxn", "examples/two_reactants_no_products.rxn.rinchi_strings.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/two_reactants_no_products.rxn", "examples/two_reactants_no_products.rxn.rinchi_strings.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_R005a() throws IOException {
        genericExampleTest("examples/R005a.rxn", "examples/R005a.rxn.rinchi_strings.txt");
        doubleConversionExampleTest("examples/R005a.rxn", "examples/R005a.rxn.rinchi_strings.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/R005a.rxn", "examples/R005a.rxn.rinchi_strings.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_nostruct_two_in_reactants() throws IOException {
        genericExampleTest("examples/nostruct_two_in_reactants.rxn", "examples/nostruct_two_in_reactants.rxn.rinchi_strings.txt");
        doubleConversionExampleTest("examples/nostruct_two_in_reactants.rxn", "examples/nostruct_two_in_reactants.rxn.rinchi_strings.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/nostruct_two_in_reactants.rxn", "examples/nostruct_two_in_reactants.rxn.rinchi_strings.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_nostruct_one_in_reactants() throws IOException {
        genericExampleTest("examples/nostruct_one_in_reactants.rxn", "examples/nostruct_one_in_reactants.rxn.rinchi_strings.txt");
        doubleConversionExampleTest("examples/nostruct_one_in_reactants.rxn", "examples/nostruct_one_in_reactants.rxn.rinchi_strings.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/nostruct_one_in_reactants.rxn", "examples/nostruct_one_in_reactants.rxn.rinchi_strings.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_nostruct_one_in_products() throws IOException {
        genericExampleTest("examples/nostruct_one_in_products.rxn", "examples/nostruct_one_in_products.rxn.rinchi_strings.txt");
        doubleConversionExampleTest("examples/nostruct_one_in_products.rxn", "examples/nostruct_one_in_products.rxn.rinchi_strings.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/nostruct_one_in_products.rxn", "examples/nostruct_one_in_products.rxn.rinchi_strings.txt", ReactionFileFormat.RD);
    }

    @Test
    public void testExample_no_reactants_one_product() throws IOException {
        genericExampleTest("examples/no_reactants_one_product.rxn", "examples/no_reactants_one_product.rxn.rinchi_strings.txt");
        doubleConversionExampleTest("examples/no_reactants_one_product.rxn", "examples/no_reactants_one_product.rxn.rinchi_strings.txt", ReactionFileFormat.RXN);
        doubleConversionExampleTest("examples/no_reactants_one_product.rxn", "examples/no_reactants_one_product.rxn.rinchi_strings.txt", ReactionFileFormat.RD);
    }

    @DisplayName("R005a_with_agents - expected to return an error as agents are not supported by the MDL reader/writer of rinchilib")
    @Test
    public void testFileTextToRinchi_R005a_with_agents() throws IOException {
        // arrange
        final String reactionFilename = "examples/R005a_with_agents.rxn";
        String reactionText = readReactionFromResourceFile(reactionFilename);
        assertNotNull(reactionText, "Reading reaction from text file " + reactionFilename);

        // act
        RinchiOutput rinchiOutput = JnaRinchi.fileTextToRinchi(reactionText);

        // assert
        assertEquals(1, rinchiOutput.getErrorCode());
        assertEquals(Status.ERROR, rinchiOutput.getStatus());
        assertTrue(rinchiOutput.getErrorMessage().endsWith("rinchi::MdlRxnfileReaderError: Reading from 'std::istream', line 5: Invalid component count line - must be 6 characters long."));
        assertEquals("", rinchiOutput.getRinchi(), "Rinchi for " + reactionFilename);

        // a new version of the rinchi software might include the ability to read/write agents from MDL files
        // if so, include the following lines to subject the reaction with agents to a round-trip test
//        genericExampleTest("examples/R005a_with_agents.rxn", "examples/R005a_with_agents.rxn.rinchi_strings.txt");
//        doubleConversionExampleTest("examples/R005a_with_agents.rxn", "examples/R005a_with_agents.rxn.rinchi_strings.txt", ReactionFileFormat.RXN);
//        doubleConversionExampleTest("examples/R005a_with_agents.rxn", "examples/R005a_with_agents.rxn.rinchi_strings.txt", ReactionFileFormat.RD);
    }

    @DisplayName("testFileTextToRinchi_example_01_CCR")
    @Test
    public void testFileTextToRinchi_example_01_CCR() throws IOException {
        // arrange
        final String reactionFilename = "examples/Example_01_CCR.rdf";
        String reactionText = readReactionFromResourceFile(reactionFilename);
        assertNotNull(reactionText, "Reading reaction from text file " + reactionFilename);

        // act
        RinchiOutput rinchiOutput = JnaRinchi.fileTextToRinchi(reactionText);

        // assert
        assertEquals(0, rinchiOutput.getErrorCode());
        assertEquals(Status.SUCCESS, rinchiOutput.getStatus());
        assertEquals("", rinchiOutput.getErrorMessage());
        assertEquals("RInChI=1.00.1S/C18H13NO5S2/c1-12-2-4-14(5-3-12)26(21,22)19-13-6-9-18(10-7-13)23-15-8-11-25-16(15)" +
                "17(20)24-18/h2-11H,1H3/b19-13-<>C18H15NO5S2/c1-12-2-8-15(9-3-12)26(22,23)19-13-4-6-14(7-5-13)24-16-10-11-25-17(16)" +
                "18(20)21/h2-11,19H,1H3,(H,20,21)<>C2H3N/c1-2-3/h1H3!C8H20N.ClHO4/c1-5-9(6-2,7-3)8-4;2-1(3,4)5/h5-8H2,1-4H3;(H,2,3,4,5)/" +
                "q+1;/p-1/d-", rinchiOutput.getRinchi(), "Rinchi for " + reactionFilename);
    }

    @DisplayName("testFileTextToRinchiKey_example_01_CCR")
    @Test
    public void testFileTextToRinchiKey_example_01_CCR() throws IOException {
        // arrange
        final String reactionFilename = "examples/Example_01_CCR.rdf";
        String reactionText = readReactionFromResourceFile(reactionFilename);
        assertNotNull(reactionText, "Reading reaction from text file " + reactionFilename);

        // Long-RInChIKey
        // act
        RinchiKeyOutput longRinchiKeyOut = JnaRinchi.fileTextToRinchiKey(reactionText, RinchiKeyType.LONG);
        // assert
        assertEquals(0, longRinchiKeyOut.getErrorCode());
        assertEquals(RinchiKeyStatus.SUCCESS, longRinchiKeyOut.getStatus());
        assertEquals("", longRinchiKeyOut.getErrorMessage());
        assertEquals("Long-RInChIKey=SA-BUHFF-OCEYRUMTOAEWEA-UYRXBGFRSA-N--HRQNWWCYHJTAPI-UHFFFAOYSA-N--WEVYAHXRMPXWCK-UHFFFAOYSA-N-WGHUNMFFLAMBJD-UHFFFAOYSA-M",
                longRinchiKeyOut.getRinchiKey(), "Long-RinchiKey for " + reactionFilename);

        // Short-RInChIKey
        // act
        RinchiKeyOutput shortRinchiKeyOut = JnaRinchi.fileTextToRinchiKey(reactionText, RinchiKeyType.SHORT);
        // assert
        assertEquals(0, shortRinchiKeyOut.getErrorCode());
        assertEquals(RinchiKeyStatus.SUCCESS, shortRinchiKeyOut.getStatus());
        assertEquals("", shortRinchiKeyOut.getErrorMessage());
        assertEquals("Short-RInChIKey=SA-BUHFF-OCEYRUMTOA-HRQNWWCYHJ-ANFMRDWEKN-NDGAC-NUHFF-MUHFF-ZZZ",
                shortRinchiKeyOut.getRinchiKey(), "Short-RinchiKey for " + reactionFilename);

        // Web-RInChIKey
        // act
        RinchiKeyOutput webRinchiKeyOut = JnaRinchi.fileTextToRinchiKey(reactionText, RinchiKeyType.WEB);
        // assert
        assertEquals(0, webRinchiKeyOut.getErrorCode());
        assertEquals(RinchiKeyStatus.SUCCESS, webRinchiKeyOut.getStatus());
        assertEquals("", webRinchiKeyOut.getErrorMessage());
        assertEquals("Web-RInChIKey=YVHOQDQUXJQWHSZQW-MSWCGYDXJTZWXSA", webRinchiKeyOut.getRinchiKey(), "Web-RinchiKey for " + reactionFilename);
    }

    @DisplayName("testDecomposeRinchi_with_AuxInfo")
    @Test
    public void testDecomposeRinchi_with_AuxInfo() {
        // arrange
        final String rinchi = "RInChI=1.00.1S/C18H13NO5S2/c1-12-2-4-14(5-3-12)26(21,22)19-13-6-9-18(10-7-13)23-15-8-11-25-16(15)17(20)24-18/h2-11H," +
                "1H3/b19-13-<>C18H15NO5S2/c1-12-2-8-15(9-3-12)26(22,23)19-13-4-6-14(7-5-13)24-16-10-11-25-17(16)18(20)21/h2-11,19H,1H3,(H,20,21)" +
                "<>C2H3N/c1-2-3/h1H3!C8H20N.ClHO4/c1-5-9(6-2,7-3)8-4;2-1(3,4)5/h5-8H2,1-4H3;(H,2,3,4,5)/q+1;/p-1/d-";
        final String auxInfo = "RAuxInfo=1.00.1/0/N:26,23,24,21,22,8,9,11,4,5,15,25,13,18,6,10,7,1,16,12,19,20,2,3,14,17/E:(2,3)(4,5)(6,7)(9,10)(21,22)" +
                "/CRV:26.6/rA:26nCOOCCCCCCCCOCSCNSCOOCCCCCC/rB:s1;s1;s1;s1;s2;s3;d4;d5;d6s7;s6;d7;s8s9;s10;d11s14;d13;s16;s17;d17;d17;s18;d18;d21;s22;" +
                "s23d24;s25;/rC:2.9831,-1.7951,0;1.65,-1.0285,0;2.9831,-3.3081,0;2.9831,-.2553,0;4.3162,-2.5416,0;.3265,-1.7951,0;1.65,-4.0813,0;4.3162," +
                ".5112,0;5.6426,-1.7951,0;.3265,-3.3081,0;-1.1738,-1.3285,0;1.65,-5.6211,0;5.6426,-.2553,0;-1.1304,-3.7747,0;-2.0639,-2.5216,0;6.9691," +
                ".5112,0;8.3022,-.2553,0;9.6353,-1.0285,0;9.0754,1.1378,0;7.5357,-1.5351,0;9.6353,-2.5483,0;10.9684,-.2553,0;10.9684,-3.3215,0;12.3082," +
                "-1.0285,0;12.3082,-2.5483,0;13.6413,-3.3215,0;<>1/N:26,23,24,13,14,11,12,21,22,4,8,25,15,7,18,1,2,6,16,9,10,19,20,3,5,17" +
                "/E:(2,3)(4,5)(6,7)(8,9)(20,21)(22,23)/CRV:26.6/rA:26nCCOCSCCCOOCCCCCNSCOOCCCCCC/rB:d1;s1;s1;s2;s2;s3;d4s5;s6;d6;s7;d7;d11;s12;s13d14;" +
                "s15;s16;s17;d17;d17;s18;d18;d21;s22;s23d24;s25;/rC:-10.0504,-6.767,0;-9.6286,-8.2146,0;-9.1595,-5.5177,0;-11.6085,-6.767,0;-10.8357," +
                "-9.0711,0;-8.1587,-8.7063,0;-10.0326,-4.2448,0;-12.0823,-8.1749,0;-7.8513,-10.1616,0;-6.9429,-7.6753,0;-9.3653,-2.8527,0;-11.5645," +
                "-4.3558,0;-10.2081,-1.5918,0;-12.4292,-3.0869,0;-11.7574,-1.7028,0;-12.6309,-.4297,0;-11.7138,.8195,0;-10.7833,2.0559,0;-13.0173," +
                "1.7436,0;-10.5154,-.0808,0;-9.2776,1.889,0;-11.4238,3.4821,0;-8.3605,3.1398,0;-10.4892,4.7258,0;-8.9752,4.5552,0;-8.0705,5.7965,0;" +
                "<>0/N:2,1,3/rA:3nCCN/rB:s1;t1;/rC:.5157,.2238,0;-1.0243,.2238,0;2.088,.2238,0;!1/N:6,7,8,9,2,3,4,5,1;10,11,12,13,14/E:(1,2,3,4)" +
                "(5,6,7,8);(2,3,4,5)/CRV:9+1;1.7/rA:14nN+CCCCCCCCClOOOO-/rB:s1;s1;s1;s1;s2;s3;s4;s5;;d10;d10;d10;s10;/rC:-2.3374,.2568,0;-3.8783," +
                ".2568,0;-1.8238,1.7084,0;-1.0384,-.5695,0;-3.0446,-1.1091,0;-4.3956,-1.1948,0;-3.0222,2.6761,0;.1749,.3759,0;-1.9913,-2.2332,0;3.0595," +
                ".2308,0;3.4763,1.7419,0;3.4949,-1.299,0;4.5222,.2159,0;1.5186,.2308,0;";

        List<String> inchis = new ArrayList<>();
        inchis.add("InChI=1S/C18H15NO5S2/c1-12-2-8-15(9-3-12)26(22,23)19-13-4-6-14(7-5-13)24-16-10-11-25-17(16)18(20)21/h2-11,19H,1H3,(H,20,21)");
        inchis.add("InChI=1S/C18H13NO5S2/c1-12-2-4-14(5-3-12)26(21,22)19-13-6-9-18(10-7-13)23-15-8-11-25-16(15)17(20)24-18/h2-11H,1H3/b19-13-");
        inchis.add("InChI=1S/C2H3N/c1-2-3/h1H3");
        inchis.add("InChI=1S/C8H20N.ClHO4/c1-5-9(6-2,7-3)8-4;2-1(3,4)5/h5-8H2,1-4H3;(H,2,3,4,5)/q+1;/p-1");
        assertEquals(4, inchis.size());

        List<String> auxInfos = new ArrayList<>();
        auxInfos.add("AuxInfo=1/1/N:26,23,24,13,14,11,12,21,22,4,8,25,15,7,18,1,2,6,16,9,10,19,20,3,5,17/E:(2,3)(4,5)(6,7)(8,9)(20,21)(22,23)" +
                "/CRV:26.6/rA:26nCCOCSCCCOOCCCCCNSCOOCCCCCC/rB:d1;s1;s1;s2;s2;s3;d4s5;s6;d6;s7;d7;d11;s12;s13d14;s15;s16;s17;d17;d17;" +
                "s18;d18;d21;s22;s23d24;s25;/rC:-10.0504,-6.767,0;-9.6286,-8.2146,0;-9.1595,-5.5177,0;-11.6085,-6.767,0;-10.8357," +
                "-9.0711,0;-8.1587,-8.7063,0;-10.0326,-4.2448,0;-12.0823,-8.1749,0;-7.8513,-10.1616,0;-6.9429,-7.6753,0;-9.3653,-2.8527,0;" +
                "-11.5645,-4.3558,0;-10.2081,-1.5918,0;-12.4292,-3.0869,0;-11.7574,-1.7028,0;-12.6309,-.4297,0;-11.7138,.8195,0;-10.7833,2.0559,0;" +
                "-13.0173,1.7436,0;-10.5154,-.0808,0;-9.2776,1.889,0;-11.4238,3.4821,0;-8.3605,3.1398,0;-10.4892,4.7258,0;-8.9752,4.5552,0;-8.0705,5.7965,0;");
        auxInfos.add("AuxInfo=1/0/N:26,23,24,21,22,8,9,11,4,5,15,25,13,18,6,10,7,1,16,12,19,20,2,3,14,17/E:(2,3)(4,5)(6,7)(9,10)(21,22)" +
                "/CRV:26.6/rA:26nCOOCCCCCCCCOCSCNSCOOCCCCCC/rB:s1;s1;s1;s1;s2;s3;d4;d5;d6s7;s6;d7;s8s9;s10;d11s14;d13;s16;s17;d17;" +
                "d17;s18;d18;d21;s22;s23d24;s25;/rC:2.9831,-1.7951,0;1.65,-1.0285,0;2.9831,-3.3081,0;2.9831,-.2553,0;4.3162,-2.5416,0;" +
                ".3265,-1.7951,0;1.65,-4.0813,0;4.3162,.5112,0;5.6426,-1.7951,0;.3265,-3.3081,0;-1.1738,-1.3285,0;1.65,-5.6211,0;" +
                "5.6426,-.2553,0;-1.1304,-3.7747,0;-2.0639,-2.5216,0;6.9691,.5112,0;8.3022,-.2553,0;9.6353,-1.0285,0;9.0754,1.1378,0;" +
                "7.5357,-1.5351,0;9.6353,-2.5483,0;10.9684,-.2553,0;10.9684,-3.3215,0;12.3082,-1.0285,0;12.3082,-2.5483,0;13.6413,-3.3215,0;");
        auxInfos.add("AuxInfo=1/0/N:2,1,3/rA:3nCCN/rB:s1;t1;/rC:.5157,.2238,0;-1.0243,.2238,0;2.088,.2238,0;");
        auxInfos.add("AuxInfo=1/1/N:6,7,8,9,2,3,4,5,1;10,11,12,13,14/E:(1,2,3,4)(5,6,7,8);(2,3,4,5)/CRV:9+1;1.7/rA:14nN+CCCCCCCCClOOOO-" +
                "/rB:s1;s1;s1;s1;s2;s3;s4;s5;;d10;d10;d10;s10;/rC:-2.3374,.2568,0;-3.8783,.2568,0;-1.8238,1.7084,0;-1.0384,-.5695,0;" +
                "-3.0446,-1.1091,0;-4.3956,-1.1948,0;-3.0222,2.6761,0;.1749,.3759,0;-1.9913,-2.2332,0;3.0595,.2308,0;3.4763,1.7419,0;" +
                "3.4949,-1.299,0;4.5222,.2159,0;1.5186,.2308,0;");
        assertEquals(4, auxInfos.size());

        List<ReactionComponentRole> componentRoles = new ArrayList<>();
        componentRoles.add(ReactionComponentRole.REAGENT);
        componentRoles.add(ReactionComponentRole.PRODUCT);
        componentRoles.add(ReactionComponentRole.AGENT);
        componentRoles.add(ReactionComponentRole.AGENT);
        assertEquals(4, componentRoles.size());

        // act
        RinchiDecompositionOutput rinchiDecompositionOutput = JnaRinchi.decomposeRinchi(rinchi, auxInfo);

        // assert
        assertEquals(0, rinchiDecompositionOutput.getErrorCode());
        assertEquals(RinchiDecompositionStatus.SUCCESS, rinchiDecompositionOutput.getStatus());
        assertEquals("", rinchiDecompositionOutput.getErrorMessage());
        assertEquals("-", rinchiDecompositionOutput.getDirection().getShortRinchiDesignation(), "Direction for RInChI decomposition");
        assertEquals(inchis.size(), rinchiDecompositionOutput.getNumberOfComponents(), "number of components from RInChI decomposition");

        // assert expected and actual auxinfo and reaction component role
        for (int i = 0; i < rinchiDecompositionOutput.getNumberOfComponents(); i++) {
            assertEquals(inchis.get(i), rinchiDecompositionOutput.getInchis()[i], "asserting inchi equality for array position " + i);
            assertEquals(auxInfos.get(i), rinchiDecompositionOutput.getAuxInfos()[i], "asserting auxinfo equality for array position " + i);
            assertEquals(componentRoles.get(i), rinchiDecompositionOutput.getRoles()[i], "asserting reaction component role equality for array position " + i);
        }
    }

    @DisplayName("testDecomposeRinchi_without_AuxInfo")
    @Test
    public void testDecomposeRinchi_without_AuxInfo() {
        // arrange
        final String rinchi = "RInChI=1.00.1S/C18H13NO5S2/c1-12-2-4-14(5-3-12)26(21,22)19-13-6-9-18(10-7-13)23-15-8-11-25-16(15)17(20)24-18/h2-11H," +
                "1H3/b19-13-<>C18H15NO5S2/c1-12-2-8-15(9-3-12)26(22,23)19-13-4-6-14(7-5-13)24-16-10-11-25-17(16)18(20)21/h2-11,19H,1H3,(H,20,21)" +
                "<>C2H3N/c1-2-3/h1H3!C8H20N.ClHO4/c1-5-9(6-2,7-3)8-4;2-1(3,4)5/h5-8H2,1-4H3;(H,2,3,4,5)/q+1;/p-1/d-";
        final String auxInfo = "";

        List<String> inchis = new ArrayList<>();
        inchis.add("InChI=1S/C18H15NO5S2/c1-12-2-8-15(9-3-12)26(22,23)19-13-4-6-14(7-5-13)24-16-10-11-25-17(16)18(20)21/h2-11,19H,1H3,(H,20,21)");
        inchis.add("InChI=1S/C18H13NO5S2/c1-12-2-4-14(5-3-12)26(21,22)19-13-6-9-18(10-7-13)23-15-8-11-25-16(15)17(20)24-18/h2-11H,1H3/b19-13-");
        inchis.add("InChI=1S/C2H3N/c1-2-3/h1H3");
        inchis.add("InChI=1S/C8H20N.ClHO4/c1-5-9(6-2,7-3)8-4;2-1(3,4)5/h5-8H2,1-4H3;(H,2,3,4,5)/q+1;/p-1");
        assertEquals(4, inchis.size());

        List<String> auxInfos = new ArrayList<>();
        auxInfos.add("");
        auxInfos.add("");
        auxInfos.add("");
        auxInfos.add("");
        assertEquals(4, auxInfos.size());

        List<ReactionComponentRole> componentRoles = new ArrayList<>();
        componentRoles.add(ReactionComponentRole.REAGENT);
        componentRoles.add(ReactionComponentRole.PRODUCT);
        componentRoles.add(ReactionComponentRole.AGENT);
        componentRoles.add(ReactionComponentRole.AGENT);
        assertEquals(4, componentRoles.size());

        // act
        RinchiDecompositionOutput rinchiDecompositionOutput = JnaRinchi.decomposeRinchi(rinchi, auxInfo);

        // assert
        assertEquals(0, rinchiDecompositionOutput.getErrorCode());
        assertEquals(RinchiDecompositionStatus.SUCCESS, rinchiDecompositionOutput.getStatus());
        assertEquals("", rinchiDecompositionOutput.getErrorMessage());
        assertEquals("-", rinchiDecompositionOutput.getDirection().getShortRinchiDesignation(), "Direction for RInChI decomposition");
        assertEquals(inchis.size(), rinchiDecompositionOutput.getNumberOfComponents(), "number of components from RInChI decomposition");

        // assert expected and actual auxinfo and reaction component role
        for (int i = 0; i < rinchiDecompositionOutput.getNumberOfComponents(); i++) {
            assertEquals(inchis.get(i), rinchiDecompositionOutput.getInchis()[i], "asserting inchi equality for array position " + i);
            assertEquals(auxInfos.get(i), rinchiDecompositionOutput.getAuxInfos()[i], "asserting auxinfo equality for array position " + i);
            assertEquals(componentRoles.get(i), rinchiDecompositionOutput.getRoles()[i], "asserting reaction component role equality for array position " + i);
        }
    }

    @DisplayName("testDecomposeRinchi_without_AuxInfo")
    @Test
    public void testDecomposeRinchi_NoStructures() {
        // arrange
        final String rinchi = "RInChI=1.00.1S/C4H9BrO/c1-3(5)4(2)6/h3-4,6H,1-2H3/t3-,4+/m1/s1!Na.H2O/h;1H2/q+1;/p-1/d+/u1-2-3";
        final String auxInfo = "";

        List<String> inchis = new ArrayList<>();
        inchis.add("InChI=1S/C4H9BrO/c1-3(5)4(2)6/h3-4,6H,1-2H3/t3-,4+/m1/s1");
        inchis.add("InChI=1S/Na.H2O/h;1H2/q+1;/p-1");
        inchis.add("InChI=1S//");
        inchis.add("InChI=1S//");
        inchis.add("InChI=1S//");
        inchis.add("InChI=1S//");
        inchis.add("InChI=1S//");
        inchis.add("InChI=1S//");
        assertEquals(8, inchis.size());

        List<String> auxInfos = new ArrayList<>();
        auxInfos.add("");
        auxInfos.add("");
        auxInfos.add("");
        auxInfos.add("");
        auxInfos.add("");
        auxInfos.add("");
        auxInfos.add("");
        auxInfos.add("");
        assertEquals(8, auxInfos.size());

        List<ReactionComponentRole> componentRoles = new ArrayList<>();
        componentRoles.add(ReactionComponentRole.REAGENT);
        componentRoles.add(ReactionComponentRole.REAGENT);
        componentRoles.add(ReactionComponentRole.REAGENT);
        componentRoles.add(ReactionComponentRole.PRODUCT);
        componentRoles.add(ReactionComponentRole.PRODUCT);
        componentRoles.add(ReactionComponentRole.AGENT);
        componentRoles.add(ReactionComponentRole.AGENT);
        componentRoles.add(ReactionComponentRole.AGENT);
        assertEquals(8, componentRoles.size());

        // act
        RinchiDecompositionOutput rinchiDecompositionOutput = JnaRinchi.decomposeRinchi(rinchi, auxInfo);

        // assert
        assertEquals(0, rinchiDecompositionOutput.getErrorCode());
        assertEquals(RinchiDecompositionStatus.SUCCESS, rinchiDecompositionOutput.getStatus());
        assertEquals("", rinchiDecompositionOutput.getErrorMessage());
        assertEquals("+", rinchiDecompositionOutput.getDirection().getShortRinchiDesignation(), "Direction for RInChI decomposition");
        assertEquals(inchis.size(), rinchiDecompositionOutput.getNumberOfComponents(), "number of components from RInChI decomposition");

        // assert expected and actual auxinfo and reaction component role
        for (int i = 0; i < rinchiDecompositionOutput.getNumberOfComponents(); i++) {
            assertEquals(inchis.get(i), rinchiDecompositionOutput.getInchis()[i], "asserting inchi equality for array position " + i);
            assertEquals(auxInfos.get(i), rinchiDecompositionOutput.getAuxInfos()[i], "asserting auxinfo equality for array position " + i);
            assertEquals(componentRoles.get(i), rinchiDecompositionOutput.getRoles()[i], "asserting reaction component role equality for array position " + i);
        }
    }

    @DisplayName("testFileTextToRinchi_example_03_metab_UDM")
    @Test
    public void testFileTextToRinchi_example_03_metab_UDM() throws IOException {
        // arrange
        final String reactionFilename = "examples/Example_03_metab_UDM.rdf";
        String reactionText = readReactionFromResourceFile(reactionFilename);
        assertNotNull(reactionText, "Reading reaction from text file " + reactionFilename);

        // act
        RinchiOutput rinchiOutput = JnaRinchi.fileTextToRinchi(reactionText);

        // assert
        assertEquals(0, rinchiOutput.getErrorCode());
        assertEquals(Status.SUCCESS, rinchiOutput.getStatus());
        assertEquals("", rinchiOutput.getErrorMessage());
        assertEquals("RInChI=1.00.1S/C7H13BrN2O2/c1-3-7(8,4-2)5(11)10-6(9)12/h3-4H2,1-2H3,(H3,9,10,11,12)<>" +
                        "C7H14N2O2/c1-3-5(4-2)6(10)9-7(8)11/h5H,3-4H2,1-2H3,(H3,8,9,10,11)<>2H3O4P/c2*1-5(2,3)4/h2*(H3,1,2,3,4)" +
                        "/p-3!C9H15BrN2O3/c1-4-9(10,5-2)7(14)12-8(15)11-6(3)13/h4-5H2,1-3H3,(H2,11,12,13,14,15)!H2O/h1H2!Na.H/d+",
                rinchiOutput.getRinchi(), "Rinchi for " + reactionFilename);
    }

    @DisplayName("testFileTextToRinchi_example_04_simple")
    @Test
    public void testFileTextToRinchi_example_04_simple() throws IOException {
        // arrange
        final String reactionFilename = "examples/Example_04_simple.rdf";
        String reactionText = readReactionFromResourceFile(reactionFilename);
        assertNotNull(reactionText, "Reading reaction from text file " + reactionFilename);

        // act
        RinchiOutput rinchiOutput = JnaRinchi.fileTextToRinchi(reactionText);

        // assert
        assertEquals(0, rinchiOutput.getErrorCode());
        assertEquals(Status.SUCCESS, rinchiOutput.getStatus());
        assertEquals("", rinchiOutput.getErrorMessage());
        assertEquals("RInChI=1.00.1S/C24H34O2Si/c1-6-21(18-25)17-20(2)19-26-27(24(3,4)5,22-13-9-7-10-14-22)23-15-11-8-12-16-23" +
                        "/h7-16,18,20-21H,6,17,19H2,1-5H3/t20-,21-/m1/s1<>C24H36O2Si/c1-6-21(18-25)17-20(2)19-26-27(24(3,4)5,22-13-9-7-10-14-22)" +
                        "23-15-11-8-12-16-23/h7-16,20-21,25H,6,17-19H2,1-5H3/t20-,21-/m1/s1/d-",
                rinchiOutput.getRinchi(), "Rinchi for " + reactionFilename);
    }

    @DisplayName("testFileTextToRinchi_example_04_simple")
    @Test
    public void testFileTextToRinchi_example_05_groups_UDM() throws IOException {
        // arrange
        final String reactionFilename = "examples/Example_05_groups_UDM.rdf";
        String reactionText = readReactionFromResourceFile(reactionFilename);
        assertNotNull(reactionText, "Reading reaction from text file " + reactionFilename);

        // act
        RinchiOutput rinchiOutput = JnaRinchi.fileTextToRinchi(reactionText);

        // assert
        assertEquals(0, rinchiOutput.getErrorCode());
        assertEquals(Status.SUCCESS, rinchiOutput.getStatus());
        assertEquals("", rinchiOutput.getErrorMessage());
        assertEquals("RInChI=1.00.1S/C28H48O4Si/c1-19(2)33(20(3)4,21(5)6)32-28(24(9)27(30)25(10)29)23(8)16-22(7)17-31-18-26-14-12-11-13-15-26" +
                        "/h11-16,19-22,24-25,28-29H,17-18H2,1-10H3/b23-16+/t22-,24+,25+,28+/m1/s1<>C34H62O4Si2" +
                        "/c1-24(2)40(25(3)4,26(5)6)38-33(29(9)32(35)30(10)37-39(14,15)34(11,12)13)28(8)21-27(7)22-36-23-31-19-17-16-18-20-31" +
                        "/h16-21,24-27,29-30,33H,22-23H2,1-15H3/b28-21+/t27-,29+,30+,33+/m1/s1<>C10H16O4S/c1-9(2)7-3-4-10(9,8(11)5-7)6-15(12,13)14" +
                        "/h7H,3-6H2,1-2H3,(H,12,13,14)!CH2Cl2/c2-1-3/h1H2!CH4O/c1-2/h2H,1H3!CH4O/c1-2/h2H,1H3/d-",
                rinchiOutput.getRinchi(), "Rinchi for " + reactionFilename);
    }

    @DisplayName("testFileTextToRinchi_5_variations_1_step_each")
    @Test
    public void testFileTextToRinchi_5_variations_1_step_each() throws IOException {
        // arrange
        final String reactionFilename = "examples/5_variations_1_step_each.rdf";
        String reactionText = readReactionFromResourceFile(reactionFilename);
        assertNotNull(reactionText, "Reading reaction from text file " + reactionFilename);

        // act
        RinchiOutput rinchiOutput = JnaRinchi.fileTextToRinchi(reactionText);

        // assert
        assertEquals(0, rinchiOutput.getErrorCode());
        assertEquals(Status.SUCCESS, rinchiOutput.getStatus());
        assertEquals("", rinchiOutput.getErrorMessage());
        assertEquals("RInChI=1.00.1S/C6H10O/c7-6-4-2-1-3-5-6/h1-5H2<>C6H12O/c7-6-4-2-1-3-5-6/h6-7H,1-5H2<>3C18H15P.2ClH.Ru" +
                "/c3*1-4-10-16(11-5-1)19(17-12-6-2-7-13-17)18-14-8-3-9-15-18;;;/h3*1-15H;2*1H;/q;;;;;+2/p-2!C3H8O" +
                "/c1-3(2)4/h3-4H,1-2H3!Na.H2O/h;1H2/q+1;/p-1/d+", rinchiOutput.getRinchi(), "Rinchi for " + reactionFilename);
    }

    @DisplayName("testFileTextToRinchi_1_variation_4_steps")
    @Test
    public void testFileTextToRinchi_1_variation_4_steps() throws IOException {
        // arrange
        final String reactionFilename = "examples/1_variation_4_steps.rdf";
        String reactionText = readReactionFromResourceFile(reactionFilename);
        assertNotNull(reactionText, "Reading reaction from text file " + reactionFilename);

        // act
        RinchiOutput rinchiOutput = JnaRinchi.fileTextToRinchi(reactionText);

        // assert
        assertEquals(0, rinchiOutput.getErrorCode());
        assertEquals(Status.SUCCESS, rinchiOutput.getStatus());
        assertEquals("", rinchiOutput.getErrorMessage());
        assertEquals("RInChI=1.00.1S/C12H17NOS/c1-3-13(4-2)12(14)15-10-11-8-6-5-7-9-11/h5-9H,3-4,10H2,1-2H3<>C4H11N/c1-3-5-4-2" +
                        "/h5H,3-4H2,1-2H3!C7H7Br/c8-6-7-4-2-1-3-5-7/h1-5H,6H2!CO/c1-2!S8/c1-2-4-6-8-7-5-3-1<>2ClH.2H2N.Pt/h2*1H;2*1H2;/q;;2*-1;+4" +
                        "/p-2!2ClH.Pd/h2*1H;/q;;+2/p-2!3C2H4O2.Fe/c3*1-2(3)4;/h3*1H3,(H,3,4);/q;;;+3/p-3!C4H10O/c1-3-5-4-2/h3-4H2,1-2H3!C4H8O" +
                        "/c1-2-4-5-3-1/h1-4H2!C4H8O/c1-2-4-5-3-1/h1-4H2!C4H8O/c1-2-4-5-3-1/h1-4H2!C4H9.Li/c1-3-4-2;/h1,3-4H2,2H3;!C6H12" +
                        "/c1-2-4-6-5-3-1/h1-6H2!C6H14/c1-3-5-6-4-2/h3-6H2,1-2H3!C6H6/c1-2-4-6-5-3-1/h1-6H/d-", rinchiOutput.getRinchi(),
                "Rinchi for " + reactionFilename);
    }

    @Test
    public void testFileTextToRinchi_ok__nostruct_A() throws IOException {
        // arrange
        final String reactionFilename = "examples/ok__nostruct-A.rxn";
        String reactionText = readReactionFromResourceFile(reactionFilename);
        assertNotNull(reactionText, "Reading reaction from text file " + reactionFilename);

        // act
        RinchiOutput rinchiOutput = JnaRinchi.fileTextToRinchi(reactionText);

        // assert
        assertEquals(0, rinchiOutput.getErrorCode());
        assertEquals(Status.SUCCESS, rinchiOutput.getStatus());
        assertEquals("", rinchiOutput.getErrorMessage());
        assertEquals("RInChI=1.00.1S//d+/u1-1-0", rinchiOutput.getRinchi(), "Rinchi for " + reactionFilename);
    }

    @Test
    public void testFileTextToRinchi_ok__nostruct_X() throws IOException {
        // arrange
        final String reactionFilename = "examples/ok__nostruct-X.rdf";
        String reactionText = readReactionFromResourceFile(reactionFilename);
        assertNotNull(reactionText, "Reading reaction from text file " + reactionFilename);

        // act
        RinchiOutput rinchiOutput = JnaRinchi.fileTextToRinchi(reactionText);

        // assert
        assertEquals(0, rinchiOutput.getErrorCode());
        assertEquals(Status.SUCCESS, rinchiOutput.getStatus());
        assertEquals("", rinchiOutput.getErrorMessage());
        assertEquals("RInChI=1.00.1S/<><>C4H8O/c1-2-4-5-3-1/h1-4H2!Mn.2O/d+/u1-1-0", rinchiOutput.getRinchi(), "Rinchi for " + reactionFilename);
    }

    @Test
    public void testFileTextToRinchi_ok__R_A() throws IOException {
        // arrange
        final String reactionFilename = "examples/ok__R-A.rxn";
        String reactionText = readReactionFromResourceFile(reactionFilename);
        assertNotNull(reactionText, "Reading reaction from text file " + reactionFilename);

        // act
        RinchiOutput rinchiOutput = JnaRinchi.fileTextToRinchi(reactionText);

        // assert
        assertEquals(0, rinchiOutput.getErrorCode());
        assertEquals(Status.SUCCESS, rinchiOutput.getStatus());
        assertEquals("", rinchiOutput.getErrorMessage());
        assertEquals("RInChI=1.00.1S//d+/u1-1-0", rinchiOutput.getRinchi(), "Rinchi for " + reactionFilename);
    }

    @Test
    public void testFileTextToRinchi_ok__R_X() throws IOException {
        // arrange
        final String reactionFilename = "examples/ok__R-X.rdf";
        String reactionText = readReactionFromResourceFile(reactionFilename);
        assertNotNull(reactionText, "Reading reaction from text file " + reactionFilename);

        // act
        RinchiOutput rinchiOutput = JnaRinchi.fileTextToRinchi(reactionText);

        // assert
        assertEquals(0, rinchiOutput.getErrorCode());
        assertEquals(Status.SUCCESS, rinchiOutput.getStatus());
        assertEquals("", rinchiOutput.getErrorMessage());
        assertEquals("RInChI=1.00.1S/<><>C2H6O/c1-2-3/h3H,2H2,1H3!Cu/d+/u1-1-0", rinchiOutput.getRinchi(), "Rinchi for " + reactionFilename);
    }

    @Test
    public void testFileTextToRinchi_ok__star_star_nostruct() throws IOException {
        // arrange
        final String reactionFilename = "examples/ok__star_star-nostruct.rdf";
        String reactionText = readReactionFromResourceFile(reactionFilename);
        assertNotNull(reactionText, "Reading reaction from text file " + reactionFilename);

        // act
        RinchiOutput rinchiOutput = JnaRinchi.fileTextToRinchi(reactionText);

        // assert
        assertEquals(0, rinchiOutput.getErrorCode());
        assertEquals(Status.SUCCESS, rinchiOutput.getStatus());
        assertEquals("", rinchiOutput.getErrorMessage());
        assertEquals("RInChI=1.00.1S/<><>Cu.O/d+/u2-1-0", rinchiOutput.getRinchi(), "Rinchi for " + reactionFilename);
    }

    @DisplayName("testFileTextToRinchi_err__R_reactant_A_product - expected to return an error status and error message")
    @Test
    public void testExample_err__R_reactant_A_product() throws IOException {
        // arrange
        final String reactionFilename = "examples/err__R_reactant-A_product.rxn";
        String reactionText = readReactionFromResourceFile(reactionFilename);
        assertNotNull(reactionText, "Reading reaction from text file " + reactionFilename);

        // act
        RinchiOutput rinchiOutput = JnaRinchi.fileTextToRinchi(reactionText);

        // assert
        assertEquals(1, rinchiOutput.getErrorCode());
        assertEquals(Status.ERROR, rinchiOutput.getStatus());
        assertTrue(rinchiOutput.getErrorMessage().endsWith("rinchi::InChIGeneratorError: Error: no InChI has been created."));
        assertEquals("", rinchiOutput.getRinchi(), "Rinchi for " + reactionFilename);
    }

    @DisplayName("testFileTextToRinchi_err__R_reactant_A_product - expected to return an error status and error message")
    @Test
    public void testExample_err__star_reactant_product() throws IOException {
        // arrange
        final String reactionFilename = "examples/err__star_reactant-product.rdf";
        String reactionText = readReactionFromResourceFile(reactionFilename);
        assertNotNull(reactionText, "Reading reaction from text file " + reactionFilename);

        // act
        RinchiOutput rinchiOutput = JnaRinchi.fileTextToRinchi(reactionText);

        // assert
        assertEquals(1, rinchiOutput.getErrorCode());
        assertEquals(Status.ERROR, rinchiOutput.getStatus());
        // error messages from rinchi lib differ slightly depending on the platform win/linux
        // win: contains 'class' before any classes; linux: does not contain 'class' before any classes
        // complete message reads:
        // [class] rinchi::MdlRDfileReaderError: Reading from 'std::istream', line 87, [class] rinchi::InChIGeneratorError: Error: no InChI has been created.
        // we assert the parts of the error message string before and after 'class'
        assertTrue(rinchiOutput.getErrorMessage().contains("rinchi::MdlRDfileReaderError: Reading from 'std::istream', line 87,"));
        assertTrue(rinchiOutput.getErrorMessage().endsWith("rinchi::InChIGeneratorError: Error: no InChI has been created."));
        assertEquals("", rinchiOutput.getRinchi(), "Rinchi for " + reactionFilename);
    }
}
