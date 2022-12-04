package io.github.dan2097.jnarinchi.cheminfo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Uli Fechner
 */
class PeriodicTableTest {

    @Test
    void getAtomicNumberFromElementSymbolTest_elementSymbol_Null() {
        assertEquals(-1, PeriodicTable.getAtomicNumberFromElementSymbol(null), "expected to return -1 if the argument is 'null'");
    }

    @Test
    void getAtomicNumberFromElementSymbolTest_elementSymbol_Beilstein() {
        assertEquals(-1, PeriodicTable.getAtomicNumberFromElementSymbol("Beilstein"), "expected to return -1 if the argument is 'Beilstein'");
    }

    @Test
    void getAtomicNumberFromElementSymbolTest_elementSymbol_Na() {
        assertEquals(11, PeriodicTable.getAtomicNumberFromElementSymbol("Na"), "expected to return 11 if the argument is 'Na'");
    }
}