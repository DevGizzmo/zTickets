package net.maliimaloo.ztickets.plugin.model;

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
        super.setTitle("Confirmation");
        super.setSize(9 * 3);

        this.confirmButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                TicketCreator ticket = TicketCreator.fromItemStack(ticketIStack);
                if (ticket == null) {
                    player.closeInventory();
                    Messenger.error(player, "Vous devez avoir un ticket en main !");
                    return;
                }

                ticket.use(player);
                player.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.GREEN_WOOL).name("&aConfirmation !").lore(Arrays.asList(
                        " ", "&aClique &fpour &aconfirmer &fl'utilisation du ticket !"
                )).make();
            }
        };

        this.cancelButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                player.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.RED_WOOL).name("&cAnnulation !").lore(Arrays.asList(
                        " ", "&cClique &fpour &cannuler &fl'utilisation du ticket !"
                )).make();
            }
        };
    }

    @Override
    public ItemStack getItemAt(int slot) {
        if (slot == 9 + 3 || slot == 9 + 2 || slot == 9 + 1) {
            return this.confirmButton.getItem();
        }

        if (slot == 9 + 5 || slot == 9 + 6 || slot == 9 + 7) {
            return this.cancelButton.getItem();
        }

        return ItemCreator.of(CompMaterial.PURPLE_STAINED_GLASS_PANE).name(" ").lore(" ").glow(true).make();
    }
}
