package com.github.dan2097.jnainchi;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class InchiOptionsTest {

  @Test
  public void testFixedHydrogenLayer() throws IOException {
    String tautomer1 = "N1=CN=C2N=CNC2=C1";
    String tautomer2 = "N1=CN=C2NC=NC2=C1";
    
    assertEquals("InChI=1S/C5H4N4/c1-4-5(8-2-6-1)9-3-7-4/h1-3H,(H,6,7,8,9)", SmilesToInchi.toInchi(tautomer1).getInchi());
    assertEquals("InChI=1S/C5H4N4/c1-4-5(8-2-6-1)9-3-7-4/h1-3H,(H,6,7,8,9)", SmilesToInchi.toInchi(tautomer2).getInchi());
    
    InchiOptions options = new InchiOptions.InchiOptionsBuilder().withFlag(InchiFlag.FixedH).build();
    assertEquals("InChI=1/C5H4N4/c1-4-5(8-2-6-1)9-3-7-4/h1-3H,(H,6,7,8,9)/f/h7H", SmilesToInchi.toInchi(tautomer1, options).getInchi());
    assertEquals("InChI=1/C5H4N4/c1-4-5(8-2-6-1)9-3-7-4/h1-3H,(H,6,7,8,9)/f/h9H", SmilesToInchi.toInchi(tautomer2, options).getInchi());
  }
  
  @Test
  public void testReconnectedMetalLayer() throws IOException {
    String metalComplex = "[NH3][Pt](Cl)(Cl)[NH3]";
    assertEquals("InChI=1S/2ClH.2H3N.Pt/h2*1H;2*1H3;/q;;;;+2/p-2", SmilesToInchi.toInchi(metalComplex).getInchi());

    InchiOptions options = new InchiOptions.InchiOptionsBuilder().withFlag(InchiFlag.RecMet).build();
    assertEquals("InChI=1/2ClH.2H3N.Pt/h2*1H;2*1H3;/q;;;;+2/p-2/rCl2H6N2Pt/c1-5(2,3)4/h3-4H3", SmilesToInchi.toInchi(metalComplex, options).getInchi());
  }
}
