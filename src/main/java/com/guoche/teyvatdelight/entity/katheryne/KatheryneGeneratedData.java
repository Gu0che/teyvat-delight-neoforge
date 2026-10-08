package com.guoche.teyvatdelight.entity.katheryne;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Generates the current examples once; existing definitions belong to their owner. */
final class KatheryneGeneratedData {
  private KatheryneGeneratedData() {}

  static void initialize(Path configDirectory) throws IOException {
    Path pack = configDirectory.resolve(KatheryneDataPack.PACK_DIRECTORY);
    if (Files.notExists(pack)) {
      KatheryneDataFiles.ensurePack(pack);
      KatheryneDataPack.writeBundled(pack, false);
    }
    // Add missing teaching examples only; never replace the owner's definitions.
    KatheryneDataPack.writeBundled(pack, true);
  }
}
