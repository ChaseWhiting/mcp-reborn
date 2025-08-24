package net.minecraft.item.equipment.trim;

import java.awt.image.BufferedImage;
import java.util.List;

public class PalettedPermutations {

    public static void main(String[] args) {
        PalettedPermutations palettedPermutations = new PalettedPermutations();
        palettedPermutations.generateTrimVariants();
        palettedPermutations.generateItemTrimVariants();
    }

    public static final List<String> TRIM_PATTERN_IDS = List.of(
            "sentry",
            "dune",
            "coast",
            "wild",
            "ward",
            "eye",
            "vex",
            "tide",
            "snout",
            "rib",
            "spire",
            "wayfinder",
            "shaper",
            "silence",
            "raiser",
            "host",
            "bolt",
            "flow");

    public static final List<String> TRIM_MATERIAL_IDS = List.of(
            "quartz",
            "iron",
            "netherite",
            "redstone",
            "copper",
            "gold",
            "emerald",
            "diamond",
            "lapis",
            "amethyst",
            "netherite_darker",
            "iron_darker",
            "gold_darker",
            "diamond_darker",
            "resin",
            "sculk",
            "rose_gold",
            "rose_gold_darker");

    public void generateTrimVariants() {
        String basePath = "G:/MCP-Reborn-1.16-MOJO/src/main/resources/assets/minecraft/textures/trims/";
        BufferedImage paletteKeyImg = loadImage(basePath + "color_palettes/trim_palette.png");
        int[] key = paletteKeyImg.getRGB(0, 0, paletteKeyImg.getWidth(), paletteKeyImg.getHeight(), null, 0, paletteKeyImg.getWidth());

        for (String pattern : TRIM_PATTERN_IDS) {
            BufferedImage mask = loadImage(basePath + "models/armor/" + pattern + ".png");
            BufferedImage leggingsMask = loadImage(basePath + "models/armor/" + pattern + "_leggings.png");

            for (String material : TRIM_MATERIAL_IDS) {
                BufferedImage paletteImg = loadImage(basePath + "color_palettes/" + material + ".png");
                int[] palette = paletteImg.getRGB(0, 0, paletteImg.getWidth(), paletteImg.getHeight(), null, 0, paletteImg.getWidth());

                BufferedImage colored = applyPalette(mask, key, palette);
                String outPath = basePath + "models/armor/" + pattern + "_" + material + ".png";
                saveImage(colored, outPath);

                if (leggingsMask != null) {
                    BufferedImage coloredLeggings = applyPalette(leggingsMask, key, palette);
                    String outPathLeggings = basePath + "models/armor/" + pattern + "_leggings_" + material + ".png";
                    saveImage(coloredLeggings, outPathLeggings);
                }
            }
        }
    }


    public void generateItemTrimVariants() {
        String basePath = "G:/MCP-Reborn-1.16-MOJO/src/main/resources/assets/minecraft/textures/trims/";
        String[] itemBases = {"boots_trim", "chestplate_trim", "helmet_trim", "leggings_trim",
                "pickaxe_trim_edge", "sword_trim_edge", "sword_trim_coat", "pickaxe_trim_coat", "sword_trim_tracer",
                "crossbow/crossbow_standby_edge", "crossbow/crossbow_standby_coat",
                "crossbow/crossbow_standby_coat_arrow", "crossbow/crossbow_pulling_0_coat", "crossbow/crossbow_pulling_1_coat", "crossbow/crossbow_pulling_2_coat",
                "crossbow/crossbow_standby_coat_firework", "crossbow/crossbow_standby_string_coat",
                "crossbow/crossbow_pull_coat", "crossbow/crossbow_pull_0_coat", "crossbow/crossbow_pull_1_coat", "crossbow/crossbow_pull_2_coat",
                "axe/axe_trim_edge",
                "axe/axe_trim_coat",
                "shovel/shovel_trim_edge",
                "shovel/shovel_trim_coat",
                "hoe/hoe_trim_edge",
                "hoe/hoe_trim_coat",
                "spyglass/model/spyglass_model_trimmed",
                "spyglass/spyglass_trimmed",
                "spyglass/scope/spyglass_scope_trim"
        };
        BufferedImage paletteKeyImg = loadImage(basePath + "color_palettes/trim_palette.png");
        int[] key = paletteKeyImg.getRGB(0, 0, paletteKeyImg.getWidth(), paletteKeyImg.getHeight(), null, 0, paletteKeyImg.getWidth());

        for (String itemBase : itemBases) {
            BufferedImage mask = loadImage(basePath + "items/" + itemBase  + ".png");

            if (mask == null) {
                System.err.println("Missing base item mask: " + basePath + "items/" + itemBase + ".png");
                continue;
            }
            for (String material : TRIM_MATERIAL_IDS) {
                BufferedImage paletteImg = loadImage(basePath + "color_palettes/" + material + ".png");
                if (paletteImg == null) {
                    System.err.println("Missing palette: " + material);
                    continue;
                }
                int[] palette = paletteImg.getRGB(0, 0, paletteImg.getWidth(), paletteImg.getHeight(), null, 0, paletteImg.getWidth());

                BufferedImage colored = applyPalette(mask, key, palette);
                String outPath = basePath + "items/" + itemBase + "_" + material + ".png";

                saveImage(colored, outPath);
            }
        }
    }





    private void saveImage(BufferedImage img, String filePath) {
        try {
            java.io.File outputFile = new java.io.File(filePath);
            java.io.File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            javax.imageio.ImageIO.write(img, "PNG", outputFile);
            System.out.println("Saved: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private BufferedImage applyPalette(BufferedImage mask, int[] key, int[] palette) {
        BufferedImage out = new BufferedImage(mask.getWidth(), mask.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < mask.getHeight(); y++) {
            for (int x = 0; x < mask.getWidth(); x++) {
                int color = mask.getRGB(x, y);
                int newColor = color;
                for (int i = 0; i < key.length; i++) {
                    if ((color & 0xFFFFFF) == (key[i] & 0xFFFFFF)) {
                        newColor = (color & 0xFF000000) | (palette[i] & 0xFFFFFF); // Preserve alpha
                        break;
                    }
                }
                out.setRGB(x, y, newColor);
            }
        }
        return out;
    }

    private BufferedImage loadImage(String path) {
        try {
            return javax.imageio.ImageIO.read(new java.io.File(path));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
