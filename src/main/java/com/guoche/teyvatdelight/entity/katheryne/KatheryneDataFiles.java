package com.guoche.teyvatdelight.entity.katheryne;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;

/** Creates current-format data files and example packs. */
final class KatheryneDataFiles {
  private KatheryneDataFiles() {}

  static void ensurePack(Path directory) throws IOException {
    Files.createDirectories(directory.resolve("data/teyvatdelight"));
    Path metadata = directory.resolve("pack.mcmeta");
    if (Files.notExists(metadata)) {
      JsonObject pack = new JsonObject();
      pack.addProperty("pack_format",
          SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA));
      pack.addProperty("description", "凯瑟琳本地定义 / Katheryne local definitions");
      JsonObject root = new JsonObject();
      root.add("pack", pack);
      save(metadata, root);
    }
  }

  static Path resourcePath(Path pack, String category, String key) {
    Path root = pack.toAbsolutePath().normalize();
    Path result = root.resolve(KatheryneDataPack.fileName(
        category, KatheryneDataPack.location(key))).normalize();
    if (!result.startsWith(root)) throw new IllegalArgumentException("Unsafe resource path: " + key);
    return result;
  }

  static void save(Path path, JsonObject value) throws IOException {
    save(path, value, null);
  }

  static void save(Path path, JsonObject value, JsonObject previous) throws IOException {
    Files.createDirectories(path.getParent());
    String existing = Files.exists(path) ? Files.readString(path, StandardCharsets.UTF_8) : "";
    boolean commented = path.getFileName().toString().equals(KatheryneShopConfig.FILE_NAME);
    String text = commented
        ? KatheryneConfigComments.format(value, existing, previous)
        : KatheryneConfigLayout.format(value);
    if (Files.exists(path)) {
      if (existing.replace("\r\n", "\n")
          .equals(text.replace("\r\n", "\n"))) return;
    }
    KatheryneShopConfig.writeAtomically(path, text);
  }
}
