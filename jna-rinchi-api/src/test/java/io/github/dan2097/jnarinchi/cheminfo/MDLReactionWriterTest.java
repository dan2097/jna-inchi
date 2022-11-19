package io.github.dan2097.jnarinchi.cheminfo;

import io.github.dan2097.jnainchi.InchiAtom;
import io.github.dan2097.jnainchi.InchiBond;
import io.github.dan2097.jnainchi.InchiBondType;
import io.github.dan2097.jnarinchi.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author uli
 */
class MDLReactionWriterTest {
    private static final Pattern linesWithDateAndTimePattern = Pattern.compile("\\A(\\$DATM {4}\\d{2}/\\d{2}/\\d{2} \\d{2}:\\d{2})|( {2}JNA-RIN \\d{10})|( {6}JNA-RIN {2}\\d{12})\\Z");
    private RinchiInput rinchiInputReactionOneWithoutAgents;
    private RinchiInput rinchiInputReactionTwoWithAgents;

    @BeforeEach
    void setup_ReactionOne() {
        rinchiInputReactionOneWithoutAgents = new RinchiInput();

        RinchiInputComponent ric1 = new RinchiInputComponent();
        rinchiInputReactionOneWithoutAgents.addComponent(ric1);
        ric1.setRole(ReactionComponentRole.REAGENT);
        InchiAtom ric1_at1 = new InchiAtom("C");
        InchiAtom ric1_at2 = new InchiAtom("C");
        InchiAtom ric1_at3 = new InchiAtom("C");
        InchiAtom ric1_at4 = new InchiAtom("C");
        InchiAtom ric1_at5 = new InchiAtom("C");
        InchiAtom ric1_at6 = new InchiAtom("C");
        ric1.addAtom(ric1_at1);
        ric1.addAtom(ric1_at2);
        ric1.addAtom(ric1_at3);
        ric1.addAtom(ric1_at4);
        ric1.addAtom(ric1_at5);
        ric1.addAtom(ric1_at6);
        ric1.addBond(new InchiBond(ric1_at1, ric1_at2, InchiBondType.SINGLE));
        ric1.addBond(new InchiBond(ric1_at2, ric1_at3, InchiBondType.DOUBLE));
        ric1.addBond(new InchiBond(ric1_at3, ric1_at4, InchiBondType.SINGLE));
        ric1.addBond(new InchiBond(ric1_at4, ric1_at5, InchiBondType.DOUBLE));
        ric1.addBond(new InchiBond(ric1_at5, ric1_at6, InchiBondType.SINGLE));
        ric1.addBond(new InchiBond(ric1_at6, ric1_at1, InchiBondType.DOUBLE));

        RinchiInputComponent ric2 = new RinchiInputComponent();
        rinchiInputReactionOneWithoutAgents.addComponent(ric2);
        ric2.setRole(ReactionComponentRole.REAGENT);
        InchiAtom ric2_at1 = new InchiAtom("Br");
        ric2.addAtom(ric2_at1);

        RinchiInputComponent ric3 = new RinchiInputComponent();
        rinchiInputReactionOneWithoutAgents.addComponent(ric3);
        ric3.setRole(ReactionComponentRole.PRODUCT);
        InchiAtom ric3_at1 = new InchiAtom("C");
        InchiAtom ric3_at2 = new InchiAtom("C");
        InchiAtom ric3_at3 = new InchiAtom("C");
        InchiAtom ric3_at4 = new InchiAtom("C");
        InchiAtom ric3_at5 = new InchiAtom("C");
        InchiAtom ric3_at6 = new InchiAtom("C");
        InchiAtom ric3_at7 = new InchiAtom("Br");
        ric3.addAtom(ric3_at1);
        ric3.addAtom(ric3_at2);
        ric3.addAtom(ric3_at3);
        ric3.addAtom(ric3_at4);
        ric3.addAtom(ric3_at5);
        ric3.addAtom(ric3_at6);
        ric3.addAtom(ric3_at7);
        ric3.addBond(new InchiBond(ric3_at1, ric3_at2, InchiBondType.SINGLE));
        ric3.addBond(new InchiBond(ric3_at2, ric3_at3, InchiBondType.DOUBLE));
        ric3.addBond(new InchiBond(ric3_at3, ric3_at4, InchiBondType.SINGLE));
        ric3.addBond(new InchiBond(ric3_at4, ric3_at5, InchiBondType.DOUBLE));
        ric3.addBond(new InchiBond(ric3_at5, ric3_at6, InchiBondType.SINGLE));
        ric3.addBond(new InchiBond(ric3_at6, ric3_at1, InchiBondType.DOUBLE));
        ric3.addBond(new InchiBond(ric3_at1, ric3_at7, InchiBondType.SINGLE));
        rinchiInputReactionOneWithoutAgents.setDirection(ReactionDirection.FORWARD);
    }

    @BeforeEach
    void setup_ReactionTwo() {
        rinchiInputReactionTwoWithAgents = new RinchiInput();

        RinchiInputComponent ric1 = new RinchiInputComponent();
        rinchiInputReactionTwoWithAgents.addComponent(ric1);
        ric1.setRole(ReactionComponentRole.REAGENT);
        InchiAtom ric1_at1 = new InchiAtom("C");
        InchiAtom ric1_at2 = new InchiAtom("C");
        InchiAtom ric1_at3 = new InchiAtom("C");
        InchiAtom ric1_at4 = new InchiAtom("C");
        InchiAtom ric1_at5 = new InchiAtom("C");
        InchiAtom ric1_at6 = new InchiAtom("C");
        ric1.addAtom(ric1_at1);
        ric1.addAtom(ric1_at2);
        ric1.addAtom(ric1_at3);
        ric1.addAtom(ric1_at4);
        ric1.addAtom(ric1_at5);
        ric1.addAtom(ric1_at6);
        ric1.addBond(new InchiBond(ric1_at1, ric1_at2, InchiBondType.SINGLE));
        ric1.addBond(new InchiBond(ric1_at2, ric1_at3, InchiBondType.DOUBLE));
        ric1.addBond(new InchiBond(ric1_at3, ric1_at4, InchiBondType.SINGLE));
        ric1.addBond(new InchiBond(ric1_at4, ric1_at5, InchiBondType.DOUBLE));
        ric1.addBond(new InchiBond(ric1_at5, ric1_at6, InchiBondType.SINGLE));
        ric1.addBond(new InchiBond(ric1_at6, ric1_at1, InchiBondType.DOUBLE));

        RinchiInputComponent ric2 = new RinchiInputComponent();
        rinchiInputReactionTwoWithAgents.addComponent(ric2);
        ric2.setRole(ReactionComponentRole.REAGENT);
        InchiAtom ric2_at1 = new InchiAtom("Br");
        ric2.addAtom(ric2_at1);

        RinchiInputComponent ric3 = new RinchiInputComponent();
        rinchiInputReactionTwoWithAgents.addComponent(ric3);
        ric3.setRole(ReactionComponentRole.AGENT);
        InchiAtom ric3_at1 = new InchiAtom("C");
        InchiAtom ric3_at2 = new InchiAtom("C");
        InchiAtom ric3_at3 = new InchiAtom("C");
        InchiAtom ric3_at4 = new InchiAtom("C");
        InchiAtom ric3_at5 = new InchiAtom("C");
        ric3.addAtom(ric3_at1);
        ric3.addAtom(ric3_at2);
        ric3.addAtom(ric3_at3);
        ric3.addAtom(ric3_at4);
        ric3.addAtom(ric3_at5);
        ric3.addBond(new InchiBond(ric3_at1, ric3_at2, InchiBondType.SINGLE));
        ric3.addBond(new InchiBond(ric3_at2, ric3_at3, InchiBondType.SINGLE));
        ric3.addBond(new InchiBond(ric3_at3, ric3_at4, InchiBondType.SINGLE));
        ric3.addBond(new InchiBond(ric3_at4, ric3_at5, InchiBondType.SINGLE));

        RinchiInputComponent ric4 = new RinchiInputComponent();
        rinchiInputReactionTwoWithAgents.addComponent(ric4);
        ric4.setRole(ReactionComponentRole.PRODUCT);
        InchiAtom ric4_at1 = new InchiAtom("C");
        InchiAtom ric4_at2 = new InchiAtom("C");
        InchiAtom ric4_at3 = new InchiAtom("C");
        InchiAtom ric4_at4 = new InchiAtom("C");
        InchiAtom ric4_at5 = new InchiAtom("C");
        InchiAtom ric4_at6 = new InchiAtom("C");
        InchiAtom ric4_at7 = new InchiAtom("Br");
        ric4.addAtom(ric4_at1);
        ric4.addAtom(ric4_at2);
        ric4.addAtom(ric4_at3);
        ric4.addAtom(ric4_at4);
        ric4.addAtom(ric4_at5);
        ric4.addAtom(ric4_at6);
        ric4.addAtom(ric4_at7);
        ric4.addBond(new InchiBond(ric4_at1, ric4_at2, InchiBondType.SINGLE));
        ric4.addBond(new InchiBond(ric4_at2, ric4_at3, InchiBondType.DOUBLE));
        ric4.addBond(new InchiBond(ric4_at3, ric4_at4, InchiBondType.SINGLE));
        ric4.addBond(new InchiBond(ric4_at4, ric4_at5, InchiBondType.DOUBLE));
        ric4.addBond(new InchiBond(ric4_at5, ric4_at6, InchiBondType.SINGLE));
        ric4.addBond(new InchiBond(ric4_at6, ric4_at1, InchiBondType.DOUBLE));
        ric4.addBond(new InchiBond(ric4_at1, ric4_at7, InchiBondType.SINGLE));
        rinchiInputReactionTwoWithAgents.setDirection(ReactionDirection.FORWARD);
    }

    @Test
    void test_ReactionOne_rxn() throws IOException {
        // arrange
        final String expectedFilename = "MDLReactionWriter/MDLReactionWriterTest_ReactionOne.rxn";
        final List<String> expectedLines = TestUtils.readTextFromResourceAsList(expectedFilename);

        // act
        MDLReactionWriter writer = new MDLReactionWriter();
        writer.setFormat(ReactionFileFormat.RXN);
        String reactionText = writer.rinchiInputToFileText(rinchiInputReactionOneWithoutAgents);

        // assert
        Assertions.assertNotNull(reactionText);
//        System.out.println(reactionText);
        String[] actualLines = reactionText.split(TestUtils.LINE_SEPARATOR_NEWLINE);
        Assertions.assertEquals(expectedLines.size(), actualLines.length);
        for (int index = 0; index < expectedLines.size(); index++) {
            // skip lines with date and time
            Matcher matcher = linesWithDateAndTimePattern.matcher(expectedLines.get(index));
            if (matcher.matches()) {
                continue;
            }

            Assertions.assertEquals(expectedLines.get(index), actualLines[index], "Mismatch of expected and actual in line " + index + 1 +
                    ": expected='" + expectedLines.get(index) + "'; actual='" + actualLines[index] +"'");
        }
    }

    @Test
    void test_ReactionOne_rdfile() throws IOException {
        // arrange
        final String expectedFilename = "MDLReactionWriter/MDLReactionWriterTest_ReactionOne.rdf";
        final List<String> expectedLines = TestUtils.readTextFromResourceAsList(expectedFilename);

        // act
        MDLReactionWriter writer = new MDLReactionWriter();
        writer.setFormat(ReactionFileFormat.RD);
        String reactionText = writer.rinchiInputToFileText(rinchiInputReactionOneWithoutAgents);

        // assert
        Assertions.assertNotNull(reactionText);
//        System.out.println(reactionText);
        String[] actualLines = reactionText.split(TestUtils.LINE_SEPARATOR_NEWLINE);
        Assertions.assertEquals(expectedLines.size(), actualLines.length);
        for (int index = 0; index < expectedLines.size(); index++) {
            // skip lines with date and time
            Matcher matcher = linesWithDateAndTimePattern.matcher(expectedLines.get(index));
            if (matcher.matches()) {
                continue;
            }

            Assertions.assertEquals(expectedLines.get(index), actualLines[index], "Mismatch of expected and actual in line " + index + 1 +
                    ": expected='" + expectedLines.get(index) + "'; actual='" + actualLines[index] +"'");
        }
    }

    @Test
    void test_ReactionOne_auto() throws IOException {
        // arrange
        final String expectedFilename = "MDLReactionWriter/MDLReactionWriterTest_ReactionOne.rdf";
        final List<String> expectedLines = TestUtils.readTextFromResourceAsList(expectedFilename);

        // act
        MDLReactionWriter writer = new MDLReactionWriter();
        writer.setFormat(ReactionFileFormat.AUTO);
        String reactionText = writer.rinchiInputToFileText(rinchiInputReactionOneWithoutAgents);

        // assert
        Assertions.assertNotNull(reactionText);
//        System.out.println(reactionText);
        String[] actualLines = reactionText.split(TestUtils.LINE_SEPARATOR_NEWLINE);
        Assertions.assertEquals(expectedLines.size(), actualLines.length);
        for (int index = 0; index < expectedLines.size(); index++) {
            // skip lines with date and time
            Matcher matcher = linesWithDateAndTimePattern.matcher(expectedLines.get(index));
            if (matcher.matches()) {
                continue;
            }

            Assertions.assertEquals(expectedLines.get(index), actualLines[index], "Mismatch of expected and actual in line " + index + 1 +
                    ": expected='" + expectedLines.get(index) + "'; actual='" + actualLines[index] +"'");
        }
    }

    @Test
    void test_ReactionTwo_rxn() throws IOException {
        // arrange
        final String expectedFilename = "MDLReactionWriter/MDLReactionWriterTest_ReactionTwo.rxn";
        final List<String> expectedLines = TestUtils.readTextFromResourceAsList(expectedFilename);

        // act
        MDLReactionWriter writer = new MDLReactionWriter();
        writer.setFormat(ReactionFileFormat.RXN);
        String reactionText = writer.rinchiInputToFileText(rinchiInputReactionTwoWithAgents);

        // assert
        Assertions.assertNotNull(reactionText);
//        System.out.println(reactionText);
        String[] actualLines = reactionText.split(TestUtils.LINE_SEPARATOR_NEWLINE);
        Assertions.assertEquals(expectedLines.size(), actualLines.length);
        for (int index = 0; index < expectedLines.size(); index++) {
            // skip lines with date and time
            Matcher matcher = linesWithDateAndTimePattern.matcher(expectedLines.get(index));
            if (matcher.matches()) {
                continue;
            }

            Assertions.assertEquals(expectedLines.get(index), actualLines[index], "Mismatch of expected and actual in line " + index + 1 +
                    ": expected='" + expectedLines.get(index) + "'; actual='" + actualLines[index] +"'");
        }
    }

    @Test
    void test_ReactionTwo_rdfile() throws IOException {
        // arrange
        final String expectedFilename = "MDLReactionWriter/MDLReactionWriterTest_ReactionTwo.rdf";
        final List<String> expectedLines = TestUtils.readTextFromResourceAsList(expectedFilename);

        // act
        MDLReactionWriter writer = new MDLReactionWriter();
        writer.setFormat(ReactionFileFormat.RD);
        String reactionText = writer.rinchiInputToFileText(rinchiInputReactionTwoWithAgents);

        // assert
        Assertions.assertNotNull(reactionText);
//        System.out.println(reactionText);
        String[] actualLines = reactionText.split(TestUtils.LINE_SEPARATOR_NEWLINE);
        Assertions.assertEquals(expectedLines.size(), actualLines.length);
        for (int index = 0; index < expectedLines.size(); index++) {
            // skip lines with date and time
            Matcher matcher = linesWithDateAndTimePattern.matcher(expectedLines.get(index));
            if (matcher.matches()) {
                continue;
            }

            Assertions.assertEquals(expectedLines.get(index), actualLines[index], "Mismatch of expected and actual in line " + index + 1 +
                    ": expected='" + expectedLines.get(index) + "'; actual='" + actualLines[index] +"'");
        }
    }

    @Disabled
    @Test
    void test_ReactionTwo_auto() throws IOException {
        // arrange
        final String expectedFilename = "MDLReactionWriter/MDLReactionWriterTest_ReactionTwo.rdf";
        final List<String> expectedLines = TestUtils.readTextFromResourceAsList(expectedFilename);

        // act
        MDLReactionWriter writer = new MDLReactionWriter();
        writer.setFormat(ReactionFileFormat.RD);
        String reactionText = writer.rinchiInputToFileText(rinchiInputReactionTwoWithAgents);

        // assert
        Assertions.assertNotNull(reactionText);
//        System.out.println(reactionText);
        String[] actualLines = reactionText.split(TestUtils.LINE_SEPARATOR_NEWLINE);
        Assertions.assertEquals(expectedLines.size(), actualLines.length);
        for (int index = 0; index < expectedLines.size(); index++) {
            // skip lines with date and time
            Matcher matcher = linesWithDateAndTimePattern.matcher(expectedLines.get(index));
            if (matcher.matches()) {
                continue;
            }

            Assertions.assertEquals(expectedLines.get(index), actualLines[index], "Mismatch of expected and actual in line " + index + 1 +
                    ": expected='" + expectedLines.get(index) + "'; actual='" + actualLines[index] +"'");
        }
    }
}