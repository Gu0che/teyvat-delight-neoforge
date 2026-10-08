package com.guoche.teyvatdelight.api;


/**
 * Reserved quality names for future "strange" and "delicious" food variants.
 *
 * <p>The current release only stores the component contract. No existing dish
 * is assigned a non-normal quality yet.</p>
 */
public final class TeyvatFoodQuality {
    public static final String STRANGE = TeyvatItemData.QUALITY_STRANGE;
    public static final String NORMAL = TeyvatItemData.QUALITY_NORMAL;
    public static final String DELICIOUS = TeyvatItemData.QUALITY_DELICIOUS;

    private TeyvatFoodQuality() {
    }
}
