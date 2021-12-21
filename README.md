[![Maven Central](https://img.shields.io/maven-central/v/io.github.dan2097/jna-inchi-all.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.dan2097%22%20AND%20a:%22jna-inchi-all%22)
[![Javadoc](https://javadoc.io/badge/io.github.dan2097/jna-inchi-api.svg)](https://javadoc.io/doc/io.github.dan2097/jna-inchi-api)
[![MIT license](https://img.shields.io/badge/License-LGPLv2.1-blue.svg)](https://opensource.org/licenses/LGPL-2.1)
[![Build Status](https://github.com/dan2097/jna-inchi/workflows/ci_build/badge.svg)](https://github.com/dan2097/jna-inchi/actions)

# JNA-InChI
Wrapper to access InChI from Java. This wraps the latest version of [InChI](https://www.inchi-trust.org/) (1.06) using [JNA](https://github.com/java-native-access/jna). A simple native Java interface can then be used to call InChI.
Java 8 or higher is required.

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

## Supported platforms
InChI is a C library and hence requires a platform-specific binary. Linux (x86/x86-64/ARM), Mac (ARM64/x86-64) and Windows (x86/x86-64) are currently supported. Pull requests for other platforms are welcome.

## Maven artifacts
The simplest way to use the library is with:
```
<dependency>
  <groupId>io.github.dan2097</groupId>
  <artifactId>jna-inchi-all</artifactId>
  <version>1.0</version>
</dependency>
```
which includes binaries for most common platforms and support for converting SMILES to InChI/InChIKey.

If you don't need SMILES support or do not require support for all platforms, the dependency size can be reduced by only including the required modules.

| Artifact                 | Description                                   |
|--------------------------|-----------------------------------------------|
| jna-inchi-all            | Includes all artifacts                        |
| jna-inchi-smiles         | JNA-InChI API with SMILES to InChI support    |
| jna-inchi-core           | JNA-InChI API with binaries for all  platforms|
| jna-inchi-api            | JNA-InChI API                                 |
| jna-inchi-darwin-aarch64 | 64-bit ARM Mac support                        |
| jna-inchi-darwin-x86-64  | 64-bit Intel Mac support                      |
| jna-inchi-linux-arm      | 64-bit ARM Linux support e.g. Raspberry Pi    |
| jna-inchi-linux-x86      | 32-bit Linux support                          |
| jna-inchi-linux-x86-64   | 64-bit Linux support                          |
| jna-inchi-win32-x86      | 32-bit Windows support                        |
| jna-inchi-win32-x86-64   | 64-bit Windows support                        |

For example, `jna-inchi-core` omits SMILES support. If you only need 64-bit linux support, depending on if SMILES support was desired, you would choose `jna-inchi-smiles` + `jna-inchi-linux-x86-64`, or `jna-inchi-api` + `jna-inchi-linux-x86-64`

## License
This project is licensed under the GNU Lesser General Public License v2.1 or later
