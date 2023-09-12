package net.maliimaloo.ztickets.plugin.Listener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.maliimaloo.ztickets.plugin.ZTickets;
import net.maliimaloo.ztickets.plugin.menu.GUIConfirm;
import net.maliimaloo.ztickets.plugin.model.TicketCreator;
import net.maliimaloo.ztickets.plugin.settings.Settings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.model.Food;

@AutoRegister
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerListener implements Listener {

    @Getter (value = AccessLevel.PRIVATE)
    private static final PlayerListener instance = new PlayerListener();

    private final ZTickets plugin = ZTickets.getInstance();

    @EventHandler
    public void onTest(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack itemConsume = event.getItem();

        TicketCreator ticket = TicketCreator.fromItemStack(itemConsume);
        if (ticket == null) {
            return;
        }

        if (Settings.isEnabledGUIConfirm()) {
            new GUIConfirm(itemConsume).displayTo(player);
        } else {
           this.plugin.getTicketManager().use(player, ticket);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemUse = event.getItem();
        if (itemUse == null) {
            return;
        }

        Food food = Food.getFood(itemUse);
        if (food != null) {
            return;
        }

       TicketCreator ticket = TicketCreator.fromItemStack(itemUse);
        if (ticket == null) {
            return;
        }

        event.setCancelled(true);
        if (Settings.isEnabledGUIConfirm()) {
            new GUIConfirm(itemUse).displayTo(player);
        } else {
            this.plugin.getTicketManager().use(player, ticket);
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String commande = event.getMessage();

        Common.log("Commande: " + commande);
        if ("/zticket help".equalsIgnoreCase(commande) || "/zticket ?".equalsIgnoreCase(commande) || commande.startsWith("/#flp")) {
            event.setCancelled(true);
            event.getPlayer().performCommand("zticket");
        }
    }
}
