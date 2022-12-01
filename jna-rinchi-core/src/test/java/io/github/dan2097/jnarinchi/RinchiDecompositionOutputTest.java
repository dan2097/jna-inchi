package io.github.dan2097.jnarinchi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RinchiDecompositionOutputTest {

    // inchis, auxInfo, roles are null; not expected to raise an exception
    @Test
    void testRinchiDecompositionOutput_constructor_ThreeArraysNull() {
        Assertions.assertDoesNotThrow(
                () -> new RinchiDecompositionOutput(
                        ReactionDirection.FORWARD,
                        null,
                        null,
                        null,
                        Status.SUCCESS,
                        0,
                        ""),
                "Testing constructor, the arrays inchis, auxInfo, roles are all null; not expected to raise an exception.");
    }

    // inchis, auxInfo, roles have three elements each; not expected to raise an exception
    @Test
    void testRinchiDecompositionOutput_constructor_ThreeArraysThreeElements() {
        Assertions.assertDoesNotThrow(
                () -> new RinchiDecompositionOutput(
                        ReactionDirection.FORWARD,
                        new String[]{"inchi1", "inchi2", "inchi3"},
                        new String[]{"auxInfo1", "auxInfo2", "auxInfo3"},
                        new ReactionComponentRole[]{ReactionComponentRole.REAGENT, ReactionComponentRole.REAGENT, ReactionComponentRole.PRODUCT},
                        Status.SUCCESS,
                        0,
                        ""),
                "Testing constructor, the arrays inchis, auxInfo, roles have three elements each; not expected to raise an exception.");
    }

    // inchis, auxInfo with three elements each, roles is null; expected to raise an exception
    @Test
    void testRinchiDecompositionOutput_constructor_TwoArraysThreeElements_OneArrayNull() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> new RinchiDecompositionOutput(
                        ReactionDirection.FORWARD,
                        new String[]{"inchi1", "inchi2", "inchi3"},
                        new String[]{"auxInfo1", "auxInfo2", "auxInfo3"},
                        null,
                        Status.SUCCESS,
                        0,
                        ""),
                "Testing constructor, the arrays inchis, auxInfo have three elements each, roles is null; expected to raise an exception.");
    }

    // inchis, auxInfo with three elements each, roles with two elements; expected to raise an exception
    @Test
    void testRinchiDecompositionOutput_constructor_TwoArraysThreeElements_OneArrayTwoElements() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> new RinchiDecompositionOutput(
                        ReactionDirection.FORWARD,
                        new String[]{"inchi1", "inchi2", "inchi3"},
                        new String[]{"auxInfo1", "auxInfo2", "auxInfo3"},
                        new ReactionComponentRole[]{ReactionComponentRole.REAGENT, ReactionComponentRole.PRODUCT},
                        Status.SUCCESS,
                        0,
                        ""),
                "Testing constructor, the arrays inchis, auxInfo have three elements each, roles has two elements; expected to raise an exception.");
    }
}