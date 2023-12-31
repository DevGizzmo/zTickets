package net.maliimaloo.ztickets.plugin.util;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.SimpleComponent;

@SuppressWarnings("unused")
public final class Utils {
    /**
     * Vérifie si un joueur a une certaine permission.
     *
     * @param paramSender Le joueur a vérifié
     * @param paramString La permission a vérifié
     * @return true si le joueur a la permission, sinon false.
     */
    public static boolean hasPermission(CommandSender paramSender, String paramString) {
        if (paramString == null || paramString.isEmpty() || paramString.equals("none"))
            return true;
        return !(paramSender instanceof Player) || paramSender.hasPermission(paramString) || paramSender.isOp();
    }

    public static boolean hasTitle(String paramString) {
        return paramString != null && !paramString.isEmpty() && !paramString.equalsIgnoreCase("none");
    }

    /**
     * Vérifie si une chaîne commence par une couleur valide.
     *
     * @param paramString La chaîne a vérifié
     * @return true si la chaîne commence par une couleur valide, sinon false.
     */
    public static boolean startWithColor(String paramString) {
        boolean bool = paramString.startsWith("&") || paramString.startsWith("§") || paramString.startsWith("{#") || paramString.startsWith("#");
        if (!bool) {
            Common.log( "Settings: la couleur du message '" + paramString + "' ne commence pas par une couleur. (&, §, {#, #)");
            return false;
        }

        return true;
    }

    public static SimpleComponent itemHoverComponent(Player paramPlayer) {
        SimpleComponent itemComponent = SimpleComponent.of(true, "[i] ");

        ItemStack item;
        try {
            item = paramPlayer.getInventory().getItemInMainHand();
        } catch (NoSuchMethodError error) {
            item = paramPlayer.getInventory().getItemInHand();
        }

        if (item.getType() == Material.AIR) {
            return itemComponent;
        }

        if (item.getItemMeta() == null || !item.getItemMeta().hasDisplayName()) {
            itemComponent = SimpleComponent.of(true, item.getType().name() + " ");
        } else {
            itemComponent = SimpleComponent.of(true, item.getItemMeta().getDisplayName() + " ");
        }

        itemComponent.onHover(item);
        return itemComponent;
    }
}
