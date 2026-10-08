package com.guoche.teyvatdelight;

/** @deprecated Use {@link com.guoche.teyvatdelight.item.PortableNutritionBagItem} for new integrations. */
@Deprecated
public class PortableNutritionBagItem extends com.guoche.teyvatdelight.item.PortableNutritionBagItem {
    public PortableNutritionBagItem(Properties properties) {
        super(properties);
    }

    public record Contents(int total, int remaining, long nutrition, double saturation) {
        public int servingNutrition() {
            return total == 0 ? 0 : (int) Math.min(Integer.MAX_VALUE, nutrition / total);
        }

        public int servingSaturation() {
            return total == 0 ? 0 : (int) Math.min(Integer.MAX_VALUE,
                    Math.floor(saturation / total + 0.000001D));
        }
    }
}
