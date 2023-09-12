package net.maliimaloo.ztickets.plugin.model;

import lombok.Getter;
import net.maliimaloo.ztickets.plugin.util.LangUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.ArrayList;

public final class RewardData {
    @Getter
    private final RewardType type;
    @Getter
    private String command;
    @Getter
    private final ItemStack itemStack;
    @Getter
    private final double chance;
    private final String message;

    public RewardData(RewardType type, String command, double chance, String message) {
        this.type = type;
        this.command = command;
        this.itemStack = null;
        this.chance = chance;
        this.message = message;
    }

    public RewardData(RewardType type, ItemStack itemStack, double chance, String message) {
        this.type = type;
        this.command = null;
        this.itemStack = itemStack;
        this.chance = chance;
        this.message = message;
    }

    public void give(Player player) {
        this.command = Replacer.replaceArray(this.command, SerializedMap.ofArray("player", player.getName()));

        if (this.type == RewardType.COMMAND && this.command != null) {
            final ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
            Bukkit.getServer().dispatchCommand(consoleSender, this.command);
        }

        if (this.type == RewardType.ITEM && this.itemStack != null) {
            PlayerUtil.addItemsOrDrop(player, this.itemStack);
        }

        Messenger.success(player, LangUtils.of(this.message));
    }

    public static RewardData fromMap(SerializedMap map) {
        RewardType type = RewardType.valueOf(map.getString("type").toUpperCase());
        double chance = map.getDouble("chance");

        if (type == RewardType.COMMAND && map.containsKey("command")) {
            String command = map.getString("command");
            String message = map.getString("message_receive");
            return new RewardData(type, command, chance, message);
        }

        if (type == RewardType.ITEM) {
            ItemCreator itemCreator = ItemCreator.of(CompMaterial.DIAMOND_SWORD)
                    .name("Default")
                    .lore(new ArrayList<>())
                    .amount(1);

            if (map.containsKey("material")) {
                itemCreator.material(CompMaterial.fromString(map.getString("material")));
            }

            if (map.containsKey("name")) {
                itemCreator.name(map.getString("name"));
            }

            if (map.containsKey("lore")) {
                itemCreator.lore(map.getStringList("lore"));
            }

            if (map.containsKey("enchantments")) {
                map.getStringList("enchantments").forEach((line) -> {
                    final String[] parts = line.split(":");
                    final String name = parts[0];
                    final int level = Integer.parseInt(parts[1]);

                    Enchantment enchantment = Enchantment.getByName(name);
                    if (enchantment != null) {
                        itemCreator.enchant(enchantment, level);
                    } else {
                        Common.throwError(new NullPointerException("Enchantment: " + name + " n'existe pas !"));
                    }
                });


            }

            if (map.containsKey("amount")) {
                itemCreator.amount(map.getInteger("amount"));
            }

            String message = map.getString("message_receive");
            return new RewardData(type, itemCreator.make(), chance, message);
        }

        return null;
    }

    public enum RewardType {
        COMMAND,
        ITEM
    }
}
