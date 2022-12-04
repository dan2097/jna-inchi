# JNA-InChI Release Notes

## 1.2 (TBD)
* Added support for RInChI by wrapping the native RInChI library. The addition of RInChI capabilities was implemented by Ideaconsult Ltd and sponsored by Pending AI.

## v1.1 (2022-01-23)
* Methods on JnaInchi will now throw a RuntimeException rather than an Error if loading the InChI library fails e.g. unsupported platform
* Added methods to retrieve the version number of this library and the bundled InChI library
* Added support for SAbs and OutErrInChI flags
* molToInchi now uses the classic InChI API rather than IXA. This allows S-groups to be ignored when no polymer options are specified ([#18](https://github.com/dan2097/jna-inchi/issues/18))
* Ambiguous combinations of InchiFlags are now rejected e.g. ChiralFlagON and ChiralFlagOFF

## v1.0.1 (2021-12-15)
* Removed duplicated files from jna-inchi-api module

## v1.0 (2021-12-13)
* Initial release with support for InChI 1.06 on Linux (x86/x86-64/ARM), Mac (ARM64/x86-64) and Windows (x86/x86-64)
