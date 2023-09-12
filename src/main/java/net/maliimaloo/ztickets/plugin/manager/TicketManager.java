package net.maliimaloo.ztickets.plugin.manager;

import lombok.Getter;
import net.maliimaloo.ztickets.plugin.model.RewardData;
import net.maliimaloo.ztickets.plugin.model.TicketCreator;
import net.maliimaloo.ztickets.plugin.settings.SettingsTicket;
import net.maliimaloo.ztickets.plugin.settings.Localization;
import net.maliimaloo.ztickets.plugin.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.remain.Remain;

import java.util.*;

public final class TicketManager {

    @Getter private static final String KEY_CUSTOM_DAT = "item_data";
    @Getter private static final String KEY_UNIQUE_ID = "unique_id";

    @Getter private final Set<TicketCreator> cache = new HashSet<>();

    public TicketManager() {
        Set<TicketCreator> tickets = SettingsTicket.getCache();
        this.loadTicket(tickets);
    }

    public void loadTicket(Set<TicketCreator> tickets) {
        this.cache.addAll(tickets);
    }

    public void use(Player player, TicketCreator ticket) {
        this.use(player, ticket, false, 1);
    }

    /**
     * Utiliser ce ticket pour un joueur
     *
     * @param player Le joueur qui utilise le ticket
     * @param bypass {true} Un gui de confirmation sera ouvert
     * @param amount La quantité de ticket utiliser
     */
    public void use(Player player, TicketCreator ticket, Boolean bypass, int amount) {
        final String permission = ticket.getPermission();
        if (!Utils.hasPermission(player, permission) && !bypass) {
            Messenger.error(player, Localization.getNoPermission("permission", permission));
        } else {
            if (Utils.hasTitle(ticket.getTitle())) {
                Remain.sendTitle(player, ticket.getFadeIn() * 20, ticket.getStay() * 20, ticket.getFadeOut() * 20, ticket.getTitle(), ticket.getSubTitle());
            }

            for (int i = 0; i < amount; i++) {
                this.executeCommands(ticket, "player", player.getName());
                this.chanceCheck(ticket).forEach((reward) -> reward.give(player));
            }

            if (!bypass) {
                ItemStack itemInHand = player.getInventory().getItemInHand();
                itemInHand.setAmount((itemInHand.getAmount() - amount) + 1);

                Remain.takeItemAndSetAsHand(player, itemInHand);
                Messenger.success(player, Localization.getUseTicket("amount", amount, "ticket_id", ticket.getUniqueId(), "ticket_name", ticket.getName()));
            }
        }
    }

    public boolean isExists(int uniqueId) {
        return this.cache.stream().anyMatch(ticketCreator -> ticketCreator.getUniqueId() == uniqueId);
    }

    public TicketCreator getTicket(int uniqueId) {
        return this.cache.stream()
                .filter(ticket -> ticket.getUniqueId() == uniqueId)
                .findFirst()
                .orElse(null);
    }

    private List<RewardData> chanceCheck(TicketCreator ticket) {
        List<RewardData> rewards = new ArrayList<>();
        for (int stop = 0; rewards.size() < ticket.getRewardsAmountChance() && stop <= 2000; stop++) {
            for (RewardData reward : ticket.getRewardsChance()) {
                double chance = new Random().nextDouble() * 100;

                if (chance <= reward.getChance()) {
                    rewards.add(reward);
                }
            }
        }

        while (rewards.size() > ticket.getRewardsAmountChance()) {
            rewards.remove(rewards.size() - 1);
        }

        return rewards;
    }

    private void executeCommands(TicketCreator ticket, Object... variables) {
        CommandSender consoleSender = Bukkit.getServer().getConsoleSender();

        for (String command : ticket.getRewardsConstant()) {
            String formattedCommand = Replacer.replaceArray(command, SerializedMap.ofArray(variables));
            Bukkit.getServer().dispatchCommand(consoleSender, formattedCommand);
        }
    }
}
