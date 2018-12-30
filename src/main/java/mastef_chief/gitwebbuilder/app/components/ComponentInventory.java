package mastef_chief.gitwebbuilder.app.components;

import javax.annotation.Nullable;

import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.listener.ItemClickListener;
import com.mrcrayfish.device.core.Laptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;

public class ComponentInventory extends Layout {

    private ItemClickListener<InventorySlot> listener;

    private InventorySlot[] inventory;
    private int selectedIndex;
    private boolean highlightSelectedIndex;

    public ComponentInventory(int left, int top, int width, int height, @Nullable Image gui, boolean highlightSelectedIndex, InventorySlot... inventory) {
        super(left, top, width, height);
        this.listener = null;
        this.highlightSelectedIndex = highlightSelectedIndex;
        this.inventory = inventory;
        if (gui != null) {
            this.addComponent(gui);
        }
    }

    @Override
    public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        super.render(laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
        if (windowActive) {
            this.selectedIndex = -1;
            int removes = 0;
            for (int i = 0; i < this.inventory.length; i++) {
                InventorySlot slot = this.inventory[i];
                ItemStack stack = slot.getStack();
                if (!stack.isEmpty()) {
                    GlStateManager.enableDepth();
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x + slot.getX() + 1, y + slot.getY() + 1);
                    mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x + slot.getX() + 1, y + slot.getY() + 1);
                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.disableDepth();
                    GlStateManager.enableAlpha();
                }
                if (mouseX >= x + slot.getX() && mouseX < x + slot.getX() + 18 && mouseY >= y + slot.getY() && mouseY < y + slot.getY() + 18) {
                    this.selectedIndex = i;
                }
            }

            if (this.selectedIndex >= 0 && this.selectedIndex < this.inventory.length) {
                InventorySlot slot = this.inventory[this.selectedIndex];
                if (!slot.isLocked()) {
                    Gui.drawRect(x + slot.getX() + 1, y + slot.getY() + 1, x + slot.getX() + 17, y + slot.getY() + 17, -2130706433);
                }
            }
        }
    }

    @Override
    public void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
        super.renderOverlay(laptop, mc, mouseX, mouseY, windowActive);
        if (windowActive) {
            if (this.selectedIndex >= 0 && this.selectedIndex < this.inventory.length) {
                ItemStack stack = this.inventory[this.selectedIndex].getStack();
                if (!stack.isEmpty()) {
                    GuiUtils.preItemToolTip(stack);
                    laptop.drawHoveringText(laptop.getItemToolTip(stack), mouseX, mouseY);
                    GuiUtils.postItemToolTip();
                    GlStateManager.disableLighting();
                }
            }
        }
    }

    @Override
    public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (this.selectedIndex >= 0 && this.selectedIndex < this.inventory.length && this.listener != null) {
            if (this.listener != null) {
                this.listener.onClick(this.inventory[this.selectedIndex], this.selectedIndex, mouseButton);
            }
        }
        super.handleMouseClick(mouseX, mouseY, mouseButton);
    }

    public void clearItems() {
        for (int i = 0; i < this.inventory.length; i++) {
            this.inventory[i].setStack(ItemStack.EMPTY);
        }
    }

    public void setStackInSlot(int slot, @Nullable ItemStack stack) {
        if (slot < 0 || slot >= this.inventory.length)
            return;
        this.inventory[slot].setStack(stack);
    }

    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= this.inventory.length)
            return ItemStack.EMPTY;
        return this.inventory[slot].getStack();
    }

    public void setItemClickListener(@Nullable ItemClickListener<InventorySlot> listener) {
        this.listener = listener;
    }

    public static class InventorySlot {

        private ItemStack stack;
        private int x;
        private int y;
        private boolean locked;

        public InventorySlot(int x, int y) {
            this(x, y, false);
        }

        public InventorySlot(int x, int y, boolean locked) {
            this.stack = ItemStack.EMPTY;
            this.x = x;
            this.y = y;
            this.locked = locked;
        }

        public void setStack(@Nullable ItemStack stack) {
            this.stack = stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        }

        public ItemStack getStack() {
            return stack;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean isLocked() {
            return locked;
        }
    }
}