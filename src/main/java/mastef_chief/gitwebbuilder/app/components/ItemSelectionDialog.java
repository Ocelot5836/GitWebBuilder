package mastef_chief.gitwebbuilder.app.components;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.ScrollableLayout;
import com.mrcrayfish.device.api.app.component.Spinner;
import com.mrcrayfish.device.api.app.listener.ItemClickListener;

import mastef_chief.gitwebbuilder.app.components.ComponentInventory.InventorySlot;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class ItemSelectionDialog extends Dialog {

    private static final NonNullList<ItemStack> ITEMS = NonNullList.create();

    private ItemClickListener<InventorySlot> listener;
    private Predicate<InventorySlot> filter;

    private Layout layout;
    private ScrollableLayout searchLayout;
    private ComponentInventory inventory;
    private Layout loadingLayout;

    @Override
    public void init(@Nullable NBTTagCompound intent) {
        super.init(intent);
        this.setTitle("Select Item");
        this.layout = new Layout(200, 6 * 18);
        this.loadingLayout = new Layout();
        this.loadingLayout.addComponent(new Spinner(this.loadingLayout.width / 2 - 12 / 2, this.loadingLayout.height / 2 - 12 / 2));
        this.setLayout(this.loadingLayout);
        Minecraft.getMinecraft().addScheduledTask(() -> this.generateSlots(0, 0, 12));
    }

    private void generateSlots(int x, int y, int xMaxSlots) {
        if (ITEMS.isEmpty())
            this.populateItems();
        InventorySlot[] slots = new InventorySlot[ITEMS.size()];
        for (int i = 0; i < slots.length; i++) {
            InventorySlot slot = new InventorySlot(x + (i % xMaxSlots) * 18, y + (i / xMaxSlots) * 18);
            slot.setStack(ITEMS.get(i));
            slots[i] = slot;
        }
        this.searchLayout = new ScrollableLayout(Math.max(13, xMaxSlots * 18) + 6, Math.max(1, (int) Math.ceil((float) slots.length / (float) xMaxSlots) * 18), this.layout.height);
        this.searchLayout.setScrollSpeed(18);
        this.layout.width = this.searchLayout.width;

        this.inventory = new ComponentInventory(x, y, Math.max(13, xMaxSlots * 18), Math.max(1, (int) Math.ceil((float) slots.length / (float) xMaxSlots) * 18), null, true, slots);
        this.inventory.setItemClickListener((slot, index, mouseButton) -> {
            if (listener != null) {
                listener.onClick(slot, index, mouseButton);
            }
            this.close();
        });
        this.searchLayout.addComponent(this.inventory);
        this.layout.addComponent(this.searchLayout);
        this.setLayout(this.layout);
    }

    @Override
    public void onClose() {
        ITEMS.clear();
        this.layout.clear();
        this.layout = null;
        if (this.searchLayout != null) {
            this.searchLayout.clear();
            this.searchLayout = null;
        }
        this.loadingLayout.clear();
        this.loadingLayout = null;
        super.onClose();
    }

    private static void populateItems() {
        for (Item item : Item.REGISTRY) {
            if (item != null && (item.getRegistryName().getResourceDomain().equals("minecraft") || item.getRegistryName().getResourceDomain().equals(Reference.MOD_ID))) {
                item.getSubItems(CreativeTabs.SEARCH, ITEMS);
            }
        }
    }

    public void setItemClickListener(@Nullable ItemClickListener<InventorySlot> listener) {
        this.listener = listener;
    }
}