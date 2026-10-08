package com.guoche.teyvatdelight.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class TeyvatDelightConfig {
  public static final ModConfigSpec SPEC;
  public static final ModConfigSpec.BooleanValue COUNTRY_CHALLENGE_WIND_WINGS;
  public static final ModConfigSpec.BooleanValue PRIMOGEMS_IN_CHESTS;

  static {
    ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
    COUNTRY_CHALLENGE_WIND_WINGS =
        builder
            .comment(
                "Grant the matching wind glider when a nation food collection challenge is"
                    + " completed.",
                "完成国度料理收集挑战时，是否给予对应的风之翼。")
            .define("rewards.countryChallengeWindWings", true);
    PRIMOGEMS_IN_CHESTS =
        builder
            .comment(
                "Allow Primogems in Teyvat's Delight's added chest loot. Mora chest loot is"
                    + " unaffected.",
                "战利品箱是否额外出现原石。默认关闭；箱子中的摩拉不受影响。")
            .define("loot.primogemsInChests", false);
    SPEC = builder.build();
  }

  private TeyvatDelightConfig() {}
}
