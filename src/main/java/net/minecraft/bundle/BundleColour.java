package net.minecraft.bundle;

import net.minecraft.item.DyeColor;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public enum BundleColour {
    REGULAR("regular", 0, null),
    WHITE("white", 1, "White"),
    LIGHT_GRAY("light_gray", 2, "Light Gray"),
    GRAY("gray", 3, "Gray"),
    BLACK("black", 4, "Black"),
    BROWN("brown", 5, "Brown"),
    RED("red", 6, "Red"),
    ORANGE("orange", 7, "Orange"),
    YELLOW("yellow", 8, "Yellow"),
    LIME("lime", 9, "Lime"),
    GREEN("green", 10, "Green"),
    CYAN("cyan", 11, "Cyan"),
    LIGHT_BLUE("light_blue", 12, "Light Blue"),
    BLUE("blue", 13, "Blue"),
    PURPLE("purple", 14, "Purple"),
    MAGENTA("magenta", 15, "Magenta"),
    PINK("pink", 16, "Pink");


    private final String name;
    private final int id;
    private final String fullName;

    private static final Map<String, BundleColour> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(BundleColour::getName, (bundleColour) -> {
        return bundleColour;
    }));

    public static BundleColour[] inOrder() {
        return new BundleColour[]{REGULAR, WHITE, LIGHT_GRAY, GRAY, BLACK, BROWN, RED, ORANGE, YELLOW, LIME, GREEN, CYAN, LIGHT_BLUE, BLUE, PURPLE, MAGENTA, PINK};
    }

    public TranslationTextComponent getTranslation() {
        return new TranslationTextComponent("item.minecraft.bundle_" + this.name);
    }

    public static BundleColour byName(String p_221087_0_) {
        return BY_NAME.getOrDefault(p_221087_0_, REGULAR);
    }

    public static BundleColour byDye(DyeColor color) {
        return switch (color) {
            case WHITE -> WHITE;
            case ORANGE -> ORANGE;
            case MAGENTA -> MAGENTA;
            case LIGHT_BLUE -> LIGHT_BLUE;
            case YELLOW -> YELLOW;
            case LIME -> LIME;
            case PINK, CORAL -> PINK;
            case GRAY -> GRAY;
            case LIGHT_GRAY -> LIGHT_GRAY;
            case CYAN -> CYAN;
            case PURPLE -> PURPLE;
            case BLUE -> BLUE;
            case BROWN -> BROWN;
            case GREEN -> GREEN;
            case RED -> RED;
            case BLACK -> BLACK;
        };
    }



    private static final BundleColour[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(BundleColour::getId)).toArray(BundleColour[]::new);

    public static BundleColour byId(int p_221080_0_) {
        if (p_221080_0_ < 0 || p_221080_0_ >= BY_ID.length) {
            p_221080_0_ = 3;
        }

        return BY_ID[p_221080_0_];
    }

    @Nullable
    public String getFullName() {
        return fullName;
    }

    // Constructor to assign id to each color
    private BundleColour(String name, int id, @Nullable String fullName) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
    }

    public int getId() {
        return this.id;
    }

    // Getter method for id
    public String getName() {
        return name;
    }
}
