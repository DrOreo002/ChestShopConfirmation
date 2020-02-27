package me.droreo002.chestshopconfirmation.inventory;

import me.droreo002.chestshopconfirmation.config.PluginConfig;
import me.droreo002.chestshopconfirmation.model.OpenRule;
import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.helper.ItemMetaType;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import org.bukkit.inventory.ItemStack;

public class InformationInventory extends OreoInventory {

    public InformationInventory(final PluginConfig memory, final OpenRule.TransactionType type) {
        super(27, memory.getIInformationTitle());

        setSoundOnClick(memory.getInfoClickSound());
        setSoundOnOpen(memory.getInfoOpenSound());
        setSoundOnClose(memory.getInfoCloseSound());

        final TextPlaceholder placeholder = new TextPlaceholder(ItemMetaType.LORE, "%transaction_type%", type.asTranslatedString());

        final ItemStack infoSign = CustomItem.fromSection(memory.getIInformationInfoButton(), placeholder);
        final ItemStack fill = CustomItem.fromSection(memory.getIInformationFillItem(), null);

        if (memory.isIInformationFillEmpty()) addBorder(fill, false, 0, 1, 2);

        addButton(new GUIButton(infoSign, memory.getIInformationInfoButtonSlot()).addListener(GUIButton.CLOSE_LISTENER), true);
    }
}
