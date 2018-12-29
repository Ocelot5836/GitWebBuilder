package mastef_chief.gitwebbuilder.app.components;

import javax.annotation.Nullable;

import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.listener.ItemClickListener;
import com.mrcrayfish.device.core.Laptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;

public class ComponentInventory extends Layout {

    private ItemClickListener<InventorySlot> listener;
    private InventorySlot[] inventory;
    private int selectedIndex;

    public ComponentInventory(int left, int top, int width, int height, @Nullable Image gui, InventorySlot... slots) {
        super(left, top, width, height);
        this.listener = null;
        this.inventory = slots;
        this.addComponent(gui == null ? new Image(0, 0, 0, 0, 256, 256, TextureMap.LOCATION_MISSING_TEXTURE) : gui);
        this.clearItems();
    }

    @Override
    public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        super.render(laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
        this.selectedIndex = -1;
        for (int i = 0; i < this.inventory.length; i++) {
            InventorySlot slot = this.inventory[i];
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty()) {
                GlStateManager.enableDepth();
                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x + slot.getX(), y + slot.getY());
                mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x + slot.getX(), y + slot.getY());
                GlStateManager.disableDepth();
                if (mouseX >= x + slot.getX() - 1 && mouseX < x + slot.getX() + 17 && mouseY >= y + slot.getY() - 1 && mouseY < y + slot.getY() + 17) {
                    this.selectedIndex = i;
                }
            }
        }
    }

    @Override
    public void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
        super.renderOverlay(laptop, mc, mouseX, mouseY, windowActive);
        if (this.selectedIndex >= 0 && this.selectedIndex < this.inventory.length) {
            ItemStack stack = this.inventory[this.selectedIndex].getStack();
            GuiUtils.preItemToolTip(stack);
            laptop.drawHoveringText(laptop.getItemToolTip(stack), mouseX, mouseY);
            GuiUtils.postItemToolTip();
        }
    }

    @Override
    public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (this.selectedIndex >= 0 && this.selectedIndex < this.inventory.length && this.listener != null) {
            this.listener.onClick(this.inventory[this.selectedIndex], this.selectedIndex, mouseButton);
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

    public void setListener(@Nullable ItemClickListener<InventorySlot> listener) {
        this.listener = listener;
    }

    public static class InventorySlot {

        private ItemStack stack;
        private int x;
        private int y;

        public InventorySlot(int x, int y) {
            this.stack = ItemStack.EMPTY;
            this.x = x;
            this.y = y;
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
    }
}