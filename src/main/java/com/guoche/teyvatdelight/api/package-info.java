/**
 * Supported integration facades: TeyvatCropApi, TeyvatMineralApi, KatheryneApi and TeyvatItemData.
 * Existing signatures and meanings are retained; new enum values/content may be added.
 * Do not persist enum ordinals or depend on list ordering, internal classes, menu packets or NBT paths.
 * Queries require completed registry setup. World queries and Katheryne operations use the owning thread.
 * ItemStack setters mutate the supplied stack only; authoritative gameplay changes belong on the server.
 * Use a loader/version-appropriate mod jar. Logical API names are shared, not binary Minecraft types.
 */
package com.guoche.teyvatdelight.api;
