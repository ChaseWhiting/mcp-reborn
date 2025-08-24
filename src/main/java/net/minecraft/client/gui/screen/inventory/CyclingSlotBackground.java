package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class CyclingSlotBackground {
    private static final int ICON_CHANGE_TICK_RATE = 30;
    private static final int ICON_SIZE = 16;
    private static final int ICON_TRANSITION_TICK_DURATION = 4;
    private final int slotIndex;
    private List<ResourceLocation> icons = List.of();
    private int tick;
    private int iconIndex;

    public CyclingSlotBackground(int n) {
        this.slotIndex = n;
    }

    public void tick(List<ResourceLocation> list) {
        if (!this.icons.equals(list)) {
            this.icons = list;
            this.iconIndex = 0;
        }
        if (!this.icons.isEmpty() && ++this.tick % ICON_CHANGE_TICK_RATE == 0) {
            this.iconIndex = (this.iconIndex + 1) % this.icons.size();
        }
    }

    public void render(MatrixStack stack, Container abstractContainerMenu, float f, int n, int n2) {
        Slot slot = abstractContainerMenu.getSlot(this.slotIndex);
        if (this.icons.isEmpty() || slot.hasItem()) {
            return;
        }
        boolean bl = this.icons.size() > 1 && this.tick >= ICON_CHANGE_TICK_RATE;
        float f3 = bl ? this.getIconTransitionTransparency(f) : 1.0f;
        if (f3 < 1.0f) {
            int n3 = Math.floorMod(this.iconIndex - 1, this.icons.size());
            this.renderIcon(stack, slot, this.icons.get(n3), 1.0f - f3, n, n2);
        }
        this.renderIcon(stack, slot, this.icons.get(this.iconIndex), f3, n, n2);
    }

    private void renderIcon(MatrixStack stack, Slot slot, ResourceLocation resourceLocation, float alpha, int n, int n2) {
        Minecraft.getInstance().getTextureManager().bind(resourceLocation);
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, alpha);
        AbstractGui.blit(stack,
                n + slot.x, n2 + slot.y,
                0, 0.0f, 0.0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE
        );

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }



    private float getIconTransitionTransparency(float f) {
        float f2 = (float)(this.tick % ICON_CHANGE_TICK_RATE) + f;
        return Math.min(f2, ICON_TRANSITION_TICK_DURATION) / ICON_TRANSITION_TICK_DURATION;
    }
}