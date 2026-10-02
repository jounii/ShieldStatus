# Building the Minecraft 26.3 Fabric port

Use JDK 25. The 26.3 target requires Fabric Loader 0.19.5 and the forked
WalksyLib 1.0.13+26.3, which is not an upstream published release.

Place this checkout and the WalksyLib fork's `upgrade/minecraft-26.3` checkout
in sibling directories. Build and publish the library to the workspace Maven
repository first:

```sh
cd ../WalksyLib
./gradlew build publishMavenJavaPublicationToWorkspaceRepository
cd ../ShieldStatus
./gradlew build
```

The library is resolved from `../.maven-local` as
`main.lib:WalksyLib:1.0.13+26.3`. This local publication is only for development;
coordinate the eventual library release before merging/publishing this port.

The 26.3 build passed. A combined development client loaded Crosshair Addons,
Shield Fixes, Shield Status and the ported library. All three configuration
screens passed an open/render check through Mod Menu 21.0.0 after the library
settings fix. This does not establish full gameplay or multiplayer compatibility.
