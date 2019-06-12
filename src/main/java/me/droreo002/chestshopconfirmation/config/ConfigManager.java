package me.droreo002.chestshopconfirmation.config;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.chestshopconfirmation.object.OpenRule;
import me.droreo002.oreocore.configuration.ConfigMemory;
import me.droreo002.oreocore.configuration.CustomConfig;
import me.droreo002.oreocore.configuration.annotations.ConfigVariable;
import me.droreo002.oreocore.enums.Currency;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager extends CustomConfig {

    @Getter
    private final Memory memory;

    public ConfigManager(JavaPlugin plugin) {
        super(plugin, new File(plugin.getDataFolder(), "config.yml"));
        this.memory = new Memory(this);
        registerMemory(memory);
        setupMemory();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        setupMemory();
    }

    private void setupMemory() {
        // Setup memory value
        memory.getPriceFormat().clear();
        memory.getOpenRule().clear();

        for (String s : memory.getPriceFormatSettings().getKeys(false)) {
            Currency c = Currency.getCurrency(s);
            if (c == null) continue;
            memory.getPriceFormat().put(c, memory.getPriceFormatSettings().getString(s));
        }

        for (String s : memory.getOpenRuleSyntax()) {
            OpenRule rule = new OpenRule(s);
            memory.getOpenRule().add(rule);
        }
    }

    public class Memory implements ConfigMemory {

        private final CustomConfig parent;

        /*
        Other value
         */
        @Getter
        private final Map<Currency, String> priceFormat;

        @Getter
        private final List<OpenRule> openRule;

        /*
        Settings
         */
        @ConfigVariable(path = "Settings.enableConfirmation", isUpdateAbleObject = true)
        @Getter @Setter
        private boolean enableConfirmation;

        @ConfigVariable(path = "Settings.currencySymbol")
        @Getter
        private String currencySymbol;

        @ConfigVariable(path = "Settings.prefix")
        @Getter
        private String prefix;

        @ConfigVariable(path = "Settings.enableLogFile")
        @Getter
        private boolean enableLogFile;

        @ConfigVariable(path = "Settings.rightClickTimeout")
        @Getter
        private int rightClickTimeout;

        @ConfigVariable(path = "Settings.transactionType")
        @Getter
        private ConfigurationSection transactionTypeTranslation;

        @ConfigVariable(path = "Settings.PriceFormat")
        @Getter
        private ConfigurationSection priceFormatSettings;

        @ConfigVariable(path = "Settings.PriceFormat.enable")
        @Getter
        private boolean enablePriceFormat;

        @ConfigVariable(path = "Settings.ConfirmationOpenRule")
        @Getter
        private List<String> openRuleSyntax;

        /*
        Inventory
         */

        // Confirmation inventory
        @ConfigVariable(path = "Inventory.Confirmation.title")
        @Getter
        private String iConfirmTitle;

        @ConfigVariable(path = "Inventory.Confirmation.FillEmpty.should")
        @Getter
        private boolean iConfirmFillEmpty;

        @ConfigVariable(path = "Inventory.Confirmation.enablePreview")
        @Getter
        private boolean iConfirmEnablePreview;

        @ConfigVariable(path = "Inventory.Confirmation.FillEmpty.item")
        @Getter
        private ConfigurationSection iConfirmFillItem;

        @ConfigVariable(path = "Inventory.Confirmation.AcceptButton")
        @Getter
        private ConfigurationSection iConfirmAcceptButton;

        @ConfigVariable(path = "Inventory.Confirmation.DeclineButton")
        @Getter
        private ConfigurationSection iConfirmDeclineButton;

        @ConfigVariable(path = "Inventory.Confirmation.ItemPreviewButton")
        @Getter
        private ConfigurationSection iConfirmPreviewButton;

        @ConfigVariable(path = "Inventory.Confirmation.AcceptButton.slot")
        @Getter
        private int iConfirmAcceptButtonSlot;

        @ConfigVariable(path = "Inventory.Confirmation.DeclineButton.slot")
        @Getter
        private int iConfirmDeclineButtonSlot;

        @ConfigVariable(path = "Inventory.Confirmation.ItemPreviewButton.slot")
        @Getter
        private int iConfirmPreviewButtonSlot;


        // Information inventory
        @ConfigVariable(path = "Inventory.Information.title")
        @Getter
        private String iInformationTitle;

        @ConfigVariable(path = "Inventory.Information.FillEmpty.should")
        @Getter
        private boolean iInformationFillEmpty;

        @ConfigVariable(path = "Inventory.Information.FillEmpty.item")
        @Getter
        private ConfigurationSection iInformationFillItem;

        @ConfigVariable(path = "Inventory.Information.InformationButton")
        @Getter
        private ConfigurationSection iInformationInfoButton;

        @ConfigVariable(path = "Inventory.Information.InformationButton.slot")
        @Getter
        private int iInformationInfoButtonSlot;

        /*
        Sounds
         */
        @ConfigVariable(path = "Inventory.Confirmation.clickSound", isSerializableObject = true)
        @Getter
        private SoundObject confirmClickSound = new SoundObject();

        @ConfigVariable(path = "Inventory.Confirmation.openSound", isSerializableObject = true)
        @Getter
        private SoundObject confirmOpenSound = new SoundObject();

        @ConfigVariable(path = "Inventory.Confirmation.closeSound", isSerializableObject = true)
        @Getter
        private SoundObject confirmCloseSound = new SoundObject();

        @ConfigVariable(path = "Inventory.Information.clickSound", isSerializableObject = true)
        @Getter
        private SoundObject infoClickSound = new SoundObject();

        @ConfigVariable(path = "Inventory.Information.openSound", isSerializableObject = true)
        @Getter
        private SoundObject infoOpenSound = new SoundObject();

        @ConfigVariable(path = "Inventory.Information.closeSound", isSerializableObject = true)
        @Getter
        private SoundObject infoCloseSound = new SoundObject();

        @ConfigVariable(path = "Settings.CommandSound.success", isSerializableObject = true)
        @Getter
        private SoundObject cmdSuccessSound = new SoundObject();

        @ConfigVariable(path = "Settings.CommandSound.error", isSerializableObject = true)
        @Getter
        private SoundObject cmdErrorSound = new SoundObject();

        /*
        Lang
         */

        @ConfigVariable(path = "Messages.command.no-permission")
        @Getter
        private String msgNoPermission;

        @ConfigVariable(path = "Messages.command.invalid-arg")
        @Getter
        private String msgInvalidArg;

        @ConfigVariable(path = "Messages.command.config-reloaded")
        @Getter
        private String msgConfigReloaded;

        @ConfigVariable(path = "Messages.command.confirmation-enabled-global")
        @Getter
        private String msgConfirmationEnabledGlobal;

        @ConfigVariable(path = "Messages.command.confirmation-disabled-global")
        @Getter
        private String msgConfirmationDisabledGlobal;

        @ConfigVariable(path = "Messages.command.player-only")
        @Getter
        private String msgPlayerOnly;

        @ConfigVariable(path = "Messages.cant-open-chest")
        @Getter
        private String msgCantOpenChest;

        @ConfigVariable(path = "Messages.right-click-to-disable")
        @Getter
        private String msgRClickToDisable;

        @ConfigVariable(path = "Messages.right-click-to-enable")
        @Getter
        private String msgRClickToEnable;

        @ConfigVariable(path = "Messages.confirmation-disabled")
        @Getter
        private String msgConfirmationDisabledSelf;

        @ConfigVariable(path = "Messages.confirmation-enabled")
        @Getter
        private String msgConfirmationEnabledSelf;

        @ConfigVariable(path = "Messages.shop-disabled")
        @Getter
        private String msgShopDisabled;

        @ConfigVariable(path = "Messages.shop-enabled")
        @Getter
        private String msgShopEnabled;

        @ConfigVariable(path = "Messages.shop-already-disabled")
        @Getter
        private String msgShopAlreadyDisabled;

        @ConfigVariable(path = "Messages.shop-already-enabled")
        @Getter
        private String msgShopAlreadyEnabled;

        @ConfigVariable(path = "Messages.time-out")
        @Getter
        private String msgTimeOut;

        Memory(CustomConfig parent) {
            this.parent = parent;
            this.priceFormat = new HashMap<>();
            this.openRule = new ArrayList<>();
        }

        @Override
        public CustomConfig getParent() {
            return parent;
        }
    }
}
