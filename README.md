# JNA-InChI
Wrapper to access InChI from Java. This is a work in progress to wrap the latest version of [InChI](https://www.inchi-trust.org/) (1.05) using [JNA](https://github.com/java-native-access/jna). A simple native Java interface can then be used to call InChI.

## Examples
Mol file to StdInChI
```java
InchiOutput output = JnaInchi.molToInchi(molText);
if (output.getStatus() == InchiStatus.OKAY || output.getStatus() == InchiStatus.WARNING) {
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
This project is licensed under the GNU General Public License (GPL) 2 or later
