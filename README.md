[![Maven Central](https://img.shields.io/maven-central/v/io.github.dan2097/jna-inchi-all.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.dan2097%22%20AND%20a:%22jna-inchi-all%22)
[![Javadoc](https://javadoc.io/badge/io.github.dan2097/jna-inchi-api.svg)](https://javadoc.io/doc/io.github.dan2097/jna-inchi-api)
[![MIT license](https://img.shields.io/badge/License-LGPLv2.1-blue.svg)](https://opensource.org/licenses/LGPL-2.1)
[![Build Status](https://github.com/dan2097/jna-inchi/workflows/ci_build/badge.svg)](https://github.com/dan2097/jna-inchi/actions)

# JNA-InChI
Wrapper to access InChI and RInchI from Java. This wraps the latest version of [InChI](https://www.inchi-trust.org/) (1.06) and RInChI (1.00) using [JNA](https://github.com/java-native-access/jna).
A simple native Java interface can then be used to call InChI.
Java 8 or higher is required.
Detailed information about the capabilities and limitations when converting from and to RInChI can be found in the io.github.dan2097.jnarinchi package documentation.

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

Reaction file to RInChI
```java
RinchiOutput rinchiOutput = JnaRinchi.fileTextToRinchi(reactionFileText);
if (rinchiOutput.getStatus() == Status.SUCCESS || rinchiOutput.getStatus() == Status.WARNING) {
  String rinchi = rinchiOutput.getRinchi();
}
```

RInChI to RInChIKey-Long
```java
RinchiKeyOutput rinchiKeyOutput = JnaRinchi.fileTextToRinchiKey(reactionFileText, RinchiKeyType.LONG);
if (rinchiKeyOutput.getStatus() == Status.SUCCESS) {
  String longRinchiKey = rinchiKeyOutput.getRinchiKey();
}
```

Decompose RInChI into its constituent InChIs (and associated AuxInfo if any)
```java
RinchiDecompositionOutput rinchiDecompositionOutput = JnaRinchi.decomposeRinchi(rinchi, rauxInfo);
if (rinchiDecompositionOutput.getStatus() == Status.SUCCESS) {
  String[] inchis = rinchiDecompositionOutput.getInchis();
  String[] auxInfos = rinchiDecompositionOutput.getAuxInfos();
}
```

## Supported platforms
InChI and RInChI are C libraries and hence require platform-specific binaries.
The following table lists the availability of the binaries for specific platforms.
Pull requests for other platforms are welcome.

| Platform       | InChI | RInChI |
|----------------|-------|--------|
| Linux x86      | Yes   | Yes    |
| Linux x86-64   | Yes   | Yes    |
| Linux ARM      | Yes   | Yes    |
| Windows x86    | Yes   | Yes    |
| Windows x86-64 | Yes   | Yes    |
| Mac ARM64      | Yes   | No     |
| Mac x86-64     | Yes   | No     |

## Maven artifacts
The simplest way to use the library is with:
```
<dependency>
  <groupId>io.github.dan2097</groupId>
  <artifactId>jna-inchi-all</artifactId>
  <version>1.2</version>
</dependency>
```
which includes binaries for most common platforms, support for converting SMILES to InChI/InChIKey and MDL RDfile and RXN to RInChI/RInChIKey.

If you don't need SMILES support or do not require support for all platforms, the dependency size can be reduced by only including the required modules.

| Artifact                 | Description                                       |
|--------------------------|---------------------------------------------------|
| jna-inchi-all            | Includes all artifacts                            |
| jna-inchi-smiles         | JNA-InChI API with SMILES to InChI support        |
| jna-inchi-core           | JNA-InChI API with binaries for all  platforms    |
| jna-inchi-api            | JNA-InChI API                                     |
| jna-inchi-darwin-aarch64 | InChI 64-bit ARM Mac support                      |
| jna-inchi-darwin-x86-64  | InChI 64-bit Intel Mac support                    |
| jna-inchi-linux-arm      | InChI 64-bit ARM Linux support e.g. Raspberry Pi  |
| jna-inchi-linux-x86      | InChI 32-bit Linux support                        |
| jna-inchi-linux-x86-64   | InChI 64-bit Linux support                        |
| jna-inchi-win32-x86      | InChI 32-bit Windows support                      |
| jna-inchi-win32-x86-64   | InChI 64-bit Windows support                      |
| jna-rinchi-core          | JNA-RInChI API with binaries for Windows and Linux|
| jna-inchi-linux-arm      | RInChI 64-bit ARM Linux support e.g. Raspberry Pi |
| jna-inchi-linux-x86      | RInChI32-bit Linux support                        |
| jna-inchi-linux-x86-64   | RInChI64-bit Linux support                        |
| jna-inchi-win32-x86      | RInChI32-bit Windows support                      |
| jna-inchi-win32-x86-64   | RInChI64-bit Windows support                      |

For example, `jna-inchi-core` omits SMILES support. If you only need 64-bit linux support, depending on if SMILES support was desired, you would choose `jna-inchi-smiles` + `jna-inchi-linux-x86-64`, or `jna-inchi-api` + `jna-inchi-linux-x86-64`

## License
This project is licensed under the GNU Lesser General Public License v2.1 or later
