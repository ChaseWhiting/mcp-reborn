package net.minecraft.util;

import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.item.DyeColor;

import java.util.Optional;
import java.util.Random;

public class FireworkUtility {
    private static final Random RANDOM = new Random();


    public static CompoundNBT getFireworksTag(ItemStack stack) {
        return stack.getOrCreateTagElement("Fireworks");
    }

    public static ListNBT getExplosionsTag(ItemStack stack) {
        CompoundNBT fireworksTag = getFireworksTag(stack);
        return fireworksTag.getList("Explosions", 10); // 10 is the NBT type for CompoundNBT
    }

    public static void addExplosionToFirework(ItemStack stack, CompoundNBT newExplosion) {
        ListNBT explosions = getExplosionsTag(stack);
        explosions.add(newExplosion);

        CompoundNBT fireworksTag = getFireworksTag(stack);
        fireworksTag.put("Explosions", explosions);

        stack.setTag(fireworksTag);
    }

    public static class Builder {
        private final CompoundNBT fireworkTag;
        private final ListNBT explosions;
        private final Optional<CompoundNBT> extraExplosion;

        public Builder() {
            fireworkTag = new CompoundNBT();
            extraExplosion = Optional.empty();
            explosions = new ListNBT();
        }

        public Builder(Optional<CompoundNBT> extraExplosion) {
            this.fireworkTag = new CompoundNBT();
            this.explosions = new ListNBT();
            this.extraExplosion = extraExplosion.isPresent() ? extraExplosion : Optional.empty();
        }

        public Builder setFlightDuration(int duration) {
            fireworkTag.putByte("Flight", (byte) duration);
            return this;
        }

        public Builder setExtraExplosion(CompoundNBT extraExplosion) {
            return new Builder(Optional.ofNullable(extraExplosion));
        }

        public Builder addExplosion(FireworkRocketItem.Shape type, int[] colors, int[] fadeColors, boolean trail, boolean flicker) {
            CompoundNBT explosion = new CompoundNBT();
            int fireworkType = type.getId();
            explosion.putByte("Type", (byte) fireworkType);
            explosion.putIntArray("Colors", colors);
            explosion.putIntArray("FadeColors", fadeColors);
            explosion.putBoolean("Trail", trail);
            explosion.putBoolean("Flicker", flicker);
            explosions.add(explosion);
            return this;
        }

        public Builder addExplosion(FireworkRocketItem.Shape type, DyeColor[] colors, DyeColor[] fadeColors, boolean trail, boolean flicker) {
            int[] intColors = convertDyeColors(colors);
            int[] intFadeColors = convertDyeColors(fadeColors);
            return addExplosion(type, intColors, intFadeColors, trail, flicker);
        }

        public ItemStack addExtraExplosion(ItemStack stack, CompoundNBT nbt) {
            FireworkUtility.addExplosionToFirework(stack, nbt);
            return stack;
        }


        private int[] convertDyeColors(DyeColor[] colors) {
            int[] intColors = new int[colors.length];
            for (int i = 0; i < colors.length; i++) {
                intColors[i] = colors[i].getMaterialColor().col;
            }
            return intColors;
        }

        public ItemStack build() {
            fireworkTag.put("Explosions", explosions);
            ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET, 1);
            CompoundNBT itemTag = new CompoundNBT();
            itemTag.put("Fireworks", fireworkTag);
            fireworkStack.setTag(itemTag);
            extraExplosion.ifPresent(compoundNBT -> FireworkUtility.addExplosionToFirework(fireworkStack, compoundNBT));
            return fireworkStack;
        }
    }

    public static int[] generateRandomColors() {
        int numColors = RANDOM.nextInt(3) + 1; // Random number of colors (1-3)
        int[] colors = new int[numColors];
        for (int i = 0; i < numColors; i++) {
            colors[i] = RANDOM.nextInt(0xFFFFFF + 1); // Random color (0x000000 to 0xFFFFFF)
        }
        return colors;
    }

    public static DyeColor[] randomColors() {
        int numColors = RANDOM.nextInt(3) + 1;
        DyeColor[] colors = new DyeColor[numColors];
        for (int i = 0; i < numColors; i++) {
            colors[i] = DyeColor.getRandomColor();
        }
        return colors;
    }

    public static ItemStack createRandomFirework() {
        FireworkRocketItem.Shape shape = FireworkRocketItem.Shape.getRandomShape();
        return new Builder()
                .setFlightDuration(RANDOM.nextInt(3)) // Random flight duration (0-2)
                .addExplosion(
                        shape, // Random type of explosion (0-5)
                        generateRandomColors(), // Random colors
                        generateRandomColors(), // Random fade colors
                        RANDOM.nextBoolean(), // Random trail
                        RANDOM.nextBoolean() // Random flicker
                )
                .build();
    }

    public static ItemStack newFirework() {
        FireworkUtility.Builder builder = new FireworkUtility.Builder();
        FireworkType fireworkType = FireworkType.getRandomType();
        DyeColor[] colors;
        DyeColor[] flickerColors;

        switch (fireworkType) {
            case HALLOWEEN:
                colors = new DyeColor[]{DyeColor.ORANGE, DyeColor.BLACK};
                flickerColors = new DyeColor[]{DyeColor.ORANGE, DyeColor.RED};
                break;
            case LIME:
                colors = new DyeColor[]{DyeColor.LIME, DyeColor.WHITE};
                flickerColors = new DyeColor[]{DyeColor.LIME, DyeColor.YELLOW};
                break;
            case SUNSET:
                colors = new DyeColor[]{DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.RED};
                flickerColors = new DyeColor[]{DyeColor.PINK, DyeColor.PURPLE};
                break;
            case OCEAN:
                colors = new DyeColor[]{DyeColor.BLUE, DyeColor.CYAN, DyeColor.LIGHT_BLUE};
                flickerColors = new DyeColor[]{DyeColor.WHITE, DyeColor.GRAY};
                break;
            case FOREST:
                colors = new DyeColor[]{DyeColor.GREEN, DyeColor.BROWN, DyeColor.LIME};
                flickerColors = new DyeColor[]{DyeColor.YELLOW, DyeColor.ORANGE};
                break;
            case GALAXY:
                colors = new DyeColor[]{DyeColor.PURPLE, DyeColor.BLUE, DyeColor.BLACK};
                flickerColors = new DyeColor[]{DyeColor.MAGENTA, DyeColor.CYAN};
                break;
            case RAINBOW:
                colors = new DyeColor[]{DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.GREEN, DyeColor.BLUE, DyeColor.PURPLE};
                flickerColors = new DyeColor[]{DyeColor.WHITE};
                break;
            case FIRE:
                colors = new DyeColor[]{DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW};
                flickerColors = new DyeColor[]{DyeColor.WHITE, DyeColor.GRAY};
                break;
            case ICE:
                colors = new DyeColor[]{DyeColor.LIGHT_BLUE, DyeColor.CYAN, DyeColor.WHITE};
                flickerColors = new DyeColor[]{DyeColor.GRAY, DyeColor.LIGHT_GRAY};
                break;
            case VOLCANO:
                colors = new DyeColor[]{DyeColor.RED, DyeColor.BLACK, DyeColor.ORANGE};
                flickerColors = new DyeColor[]{DyeColor.YELLOW};
                break;
            case DESERT:
                colors = new DyeColor[]{DyeColor.YELLOW, DyeColor.BROWN, DyeColor.ORANGE};
                flickerColors = new DyeColor[]{DyeColor.WHITE, DyeColor.LIGHT_GRAY};
                break;
            case TROPICAL:
                colors = new DyeColor[]{DyeColor.LIME, DyeColor.YELLOW, DyeColor.CYAN};
                flickerColors = new DyeColor[]{DyeColor.PINK, DyeColor.ORANGE};
                break;
            case AURORA:
                colors = new DyeColor[]{DyeColor.LIGHT_BLUE, DyeColor.MAGENTA, DyeColor.PURPLE};
                flickerColors = new DyeColor[]{DyeColor.WHITE, DyeColor.CYAN};
                break;
            case METALLIC:
                colors = new DyeColor[]{DyeColor.GRAY, DyeColor.LIGHT_GRAY, DyeColor.BLACK};
                flickerColors = new DyeColor[]{DyeColor.WHITE};
                break;
            case STORM:
                colors = new DyeColor[]{DyeColor.BLACK, DyeColor.GRAY, DyeColor.LIGHT_BLUE};
                flickerColors = new DyeColor[]{DyeColor.WHITE, DyeColor.CYAN};
                break;
            default:
                colors = randomColors();
                flickerColors = randomColors();
                break;
        }

        return builder.addExplosion(FireworkRocketItem.Shape.getRandomShape(), colors, flickerColors, true, true).build();
    }



    public enum FireworkType {
        HALLOWEEN(0, "halloween"),
        LIME(1, "lime"),
        SUNSET(2, "sunset"),
        OCEAN(3, "ocean"),
        FOREST(4, "forest"),
        GALAXY(5, "galaxy"),
        RAINBOW(6, "rainbow"),
        FIRE(7, "fire"),
        ICE(8, "ice"),
        VOLCANO(9, "volcano"),
        DESERT(10, "desert"),
        TROPICAL(11, "tropical"),
        AURORA(12, "aurora"),
        METALLIC(13, "metallic"),
        STORM(14, "storm");

        public static FireworkType getRandomType() {
            FireworkType[] types = FireworkType.values();
            int randomIndex = new Random().nextInt(types.length);
            return types[randomIndex];
        }

        private String name;
        private int id;

        private FireworkType(int id, String name) {
            this.name = name;
            this.id = id;
        }


    }


}
