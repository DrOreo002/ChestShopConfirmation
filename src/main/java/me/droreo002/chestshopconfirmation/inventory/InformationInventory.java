package me.droreo002.chestshopconfirmation.inventory;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import me.droreo002.chestshopconfirmation.config.ConfigManager;
import me.droreo002.oreocore.inventory.api.CustomInventory;
import me.droreo002.oreocore.inventory.api.GUIButton;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.helper.ItemMetaType;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class InformationInventory extends CustomInventory {

    public InformationInventory(final ConfigManager.Memory memory, final TransactionEvent.TransactionType type) {
        super(27, memory.getIInformationTitle());

        setSoundOnClick(memory.getInfoClickSound());
        setSoundOnOpen(memory.getInfoOpenSound());
        setSoundOnClose(memory.getInfoCloseSound());

        final TextPlaceholder placeholder = new TextPlaceholder(ItemMetaType.LORE, "%transaction_type%", StringUtils.upperCaseFirstLetter(type.toString().toLowerCase()));

        final ItemStack infoSign = CustomItem.fromSection(memory.getIInformationInfoButton(), placeholder);
        final ItemStack fill = CustomItem.fromSection(memory.getIInformationFillItem(), null);

        if (memory.isIInformationFillEmpty()) addBorder(new int[] { 0, 1, 2 }, fill, false);

        addButton(memory.getIInformationInfoButtonSlot(), new GUIButton(infoSign).setListener(GUIButton.CLOSE_LISTENER), true);
    }

    @Override
    public void onClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public void onClose(InventoryCloseEvent inventoryCloseEvent) {

    }

    @Override
    public void onOpen(InventoryOpenEvent inventoryOpenEvent) {

    }
}
