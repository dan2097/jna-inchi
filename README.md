# JNA-InChI
Wrapper to access InChI from Java. This is a work in progress to wrap the latest version of [InChI](https://www.inchi-trust.org/) (1.06) using [JNA](https://github.com/java-native-access/jna). A simple native Java interface can then be used to call InChI.

## Examples
Mol file to StdInChI
```java
InchiOutput output = JnaInchi.molToInchi(molText);
if (output.getStatus() == InchiStatus.SUCCESS || output.getStatus() == InchiStatus.WARNING) {
  String inchi = output.getInchi();
}
```

SMILES to StdInChI
```java
InchiOutput output = SmilesToInchi.toInchi(smiles);
if (output.getStatus() == InchiStatus.SUCCESS || output.getStatus() == InchiStatus.WARNING) {
  String inchi = output.getInchi();
}
```

InChI to InChIKey
```java
InchiKeyOutput output = JnaInchi.inchiToInchiKey(inchi);
if (output.getStatus() == InchiKeyStatus.OK) {
  String inchiKey = output.getInchiKey();
}
```

Custom molecule to StdInChI
```java
InchiInput inchiInput = new InchiInput();
inchiInput.addAtom(atom);
inchiInput.addBond(bond);
inchiInput.addStereo(stereo);
InchiOutput output = JnaInchi.toInchi(inchiInput);
```

## License
This project is licensed under the GNU Lesser General Public License v2.1 or later

![Build Status](https://github.com/dan2097/jna-inchi/workflows/ci_build/badge.svg)