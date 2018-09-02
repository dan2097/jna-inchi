package com.github.dan2097.jnainchi;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

public class SmilesToInchiTest {

	@Test
	public void testConversion() throws IOException{
      try (BufferedReader input = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("smilesintegrationtests.smi"), "UTF-8"))){
        String line = null;
        while ((line = input.readLine()) != null) {
            if(line.isEmpty() || line.startsWith("#")){
                continue;
            }
            String[] lineArray = line.split("\t");
            String inchi = SmilesToInchi.toInchi(lineArray[0]).getInchi();
            if (inchi!=null) {
                String referenceInchi = lineArray[1];

                if (!inchi.equals(referenceInchi)){
                    fail(lineArray[0] +" was misconverted as: " + inchi);
                }
            } else {
                fail(lineArray[0] +" was not converted");
            }
        }
      }
	}

}
