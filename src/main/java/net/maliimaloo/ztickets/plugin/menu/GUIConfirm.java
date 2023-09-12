package net.maliimaloo.ztickets.plugin.menu;

import net.maliimaloo.ztickets.plugin.ZTickets;
import net.maliimaloo.ztickets.plugin.model.MenuItemCreator;
import net.maliimaloo.ztickets.plugin.model.TicketCreator;
import net.maliimaloo.ztickets.plugin.settings.SettingsGUIConfirm;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.Arrays;

public class GUIConfirm extends Menu {
    private final Button confirmButton;
    private final Button cancelButton;

    public GUIConfirm(ItemStack ticketIStack) {
        final String title = SettingsGUIConfirm.getTITLE();
        final int row = SettingsGUIConfirm.getROW();

        super.setTitle(title);
        super.setSize(9 * row);

        this.confirmButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                final ZTickets plugin = ZTickets.getInstance();

                TicketCreator ticket = TicketCreator.fromItemStack(ticketIStack);
                if (ticket == null) {
                    player.closeInventory();
                    Messenger.error(player, "Vous devez avoir un ticket en main !");
                    return;
                }

                if (clickType.isRightClick()) {
                    plugin.getTicketManager().use(player, ticket, false, ticketIStack.getAmount());
                } else {
                    plugin.getTicketManager().use(player, ticket);
                }

                player.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return SettingsGUIConfirm.ImmutableContent.getCONFIRM_ITEM().toItemStack(null);
            }
        };

        this.cancelButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                player.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return SettingsGUIConfirm.ImmutableContent.getCANCEL_ITEM().toItemStack(null);
            }
        };
    }

    @Override
    public ItemStack getItemAt(int slot) {
        slot += 1;

        if (SettingsGUIConfirm.ImmutableContent.getCONFIRM_ITEM().getSlots().contains(slot)) {
            return this.confirmButton.getItem();
        }

        if (SettingsGUIConfirm.ImmutableContent.getCANCEL_ITEM().getSlots().contains(slot)) {
            return this.cancelButton.getItem();
        }

        for (MenuItemCreator menuItemContent : SettingsGUIConfirm.Content.getContents()) {
            if (menuItemContent.getSlots().contains(slot)) {
                return menuItemContent.toItemStack(null);
            }
        }

        return null;
    }
}
