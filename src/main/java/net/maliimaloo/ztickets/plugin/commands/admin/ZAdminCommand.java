package net.maliimaloo.ztickets.plugin.commands.admin;

import net.maliimaloo.ztickets.plugin.ZTickets;
import net.maliimaloo.ztickets.plugin.model.TicketCreator;
import net.maliimaloo.ztickets.plugin.util.CommandUtils;
import net.maliimaloo.ztickets.plugin.util.Utils;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.model.SimpleComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminCommand extends SimpleSubCommand {
    private final ZTickets plugin;

    public AdminCommand(ZTickets plugin) {
        super("admin");

        super.setAutoHandleHelp(false);
        super.setMinArguments(1);

        this.plugin = plugin;
    }

    @Override
    protected void onCommand() {
        Common.log("Arguments Size: " + super.args.length);
        for (int i = 0; i < super.args.length; i++) {
            Common.log(i + ":" + super.args[i]);
        }

        final Param param = Param.find(super.args[0]);
        if (param == null) {
            super.returnInvalidArgs();
            return;
        }

        switch (param) {
            case HELP:
                this.handleHelpCommand();
                break;

            case GIVE:
                this.handleGiveCommand();
                break;

            case USE:
                this.handleUseCommand();
                break;

            case LIST:
                List<String> listMsg = new ArrayList<>();

                listMsg.add(Common.chatLine());
                this.plugin.getTicketStorage().getCache().forEach((ticketCreator) -> listMsg.add(" - Id: " + ticketCreator.getUniqueId()));
                listMsg.add(Common.chatLine());

                super.tell(listMsg);
                break;
        }
    }

    private void handleGiveCommand() {
        if (!Utils.hasPermission(super.getSender(), "zticket.admin.give")) {
            super.tellError("Vous n'avez pas la permission d'utiliser cette commandes.");
        } else {
            final String receiverName = super.args[1];
            final String uniqueId = super.args[2];

            final int amount;
            try {
                amount = Integer.parseInt(super.args[3]);
            } catch (NumberFormatException exception) {
                super.tellError("La quantité doit être un nombre valide.");
                return;
            }

            final Player receiver = PlayerUtil.getPlayerByNick(receiverName, true);
            if (receiver == null) {
                super.tellError("Le joueur cible n'est pas en ligne.");
                return;
            }

            if (!this.plugin.getTicketStorage().isExists(uniqueId)) {
                super.tellError("Le ticket &b" + uniqueId + " &cn'existe pas, execute &a/zticket admin list&c.");
                return;
            }

            TicketCreator ticket = this.plugin.getTicketStorage().getTicket(uniqueId);
            PlayerUtil.addItems(receiver.getInventory(), ticket.toItemStack(amount));

            super.tellSuccess("&fVous venez de recevoir " + amount + " ticket(s) &b" + ticket.getUniqueId() + "&f.");
        }
    }

    private void handleUseCommand() {
        if (!Utils.hasPermission(super.getSender(), "zticket.admin.use")) {
            super.tellError("Vous n'avez pas la permission d'utiliser cette commande.");
        } else {
            final String receiverName = super.args[1];
            final String uniqueId = super.args[2];

            final int amount;
            try {
                amount = Integer.parseInt(super.args[3]);
            } catch (NumberFormatException exception) {
                super.tellError("La quantité doit être un nombre valide.");
                return;
            }

            final Player receiver = PlayerUtil.getPlayerByNick(receiverName, true);
            if (receiver == null) {
                super.tellError("Le joueur cible n'est pas en ligne.");
                return;
            }

            if (!this.plugin.getTicketStorage().isExists(uniqueId)) {
                super.tellError("Le ticket &b" + uniqueId + " &cn'existe pas, execute &a/zticket admin list&c.");
                return;
            }

            TicketCreator ticket = this.plugin.getTicketStorage().getTicket(uniqueId);
            ticket.use(receiver, true, amount);

            super.tellSuccess("&fVous venez d'ouvrir &ax" + amount + " " + ticket.getName() + " &fa &a" + receiverName + "&f.");
            Messenger.success(receiver, "&a" + super.getSender().getName() + " &fviens de vous ouvrir &ax" + amount + " " + ticket.getName() + "&f.");
        }
    }

    private void handleHelpCommand() {
        if (!Utils.hasPermission(super.getSender(), "zticket.admin.help")) {
            super.tellError("Vous n'avez pas la permission d'utiliser cette commande.");
        } else {
            List<SimpleComponent> helpComponents = new ArrayList<>();

            this.addEmptyChatLine(helpComponents);

            this.addTitle(helpComponents, "commandes admin disponibles");

            this.addEmptyLine(helpComponents);

            this.addArgumentLegends(helpComponents);

            this.addEmptyLine(helpComponents);

            this.addCommandComponent(helpComponents);

            this.addEmptyLine(helpComponents);

            this.addHoverInstructions(helpComponents);

            this.addEmptyChatLine(helpComponents);

            super.tell(helpComponents);
        }
    }

    private void addEmptyLine(List<SimpleComponent> components) {
        components.add(SimpleComponent.of(" "));
    }

    private void addEmptyChatLine(List<SimpleComponent> components) {
        components.add(SimpleComponent.of(Common.chatLine()));
    }

    private void addTitle(List<SimpleComponent> components, String title) {
        components.add(SimpleComponent.of("&c" + title));
    }

    private void addArgumentLegends(List<SimpleComponent> components) {
        components.add(SimpleComponent.of(" "));
        components.add(SimpleComponent.of("&6[] &f- Arguments Requis"));
        components.add(SimpleComponent.of("&6<> &f- Arguments Optionnels"));
        components.add(SimpleComponent.of(" "));
    }

    private void addCommandComponent(List<SimpleComponent> components) {
        SimpleComponent commandComponent = SimpleComponent.empty();
        CommandUtils.createCommandComponent(commandComponent, "&f" + super.getCurrentLabel() + " " + super.getSublabel(), super.getPlayer(), "help", "", Collections.singletonList("&f- &cAfficher &fla liste des commandes disponibles."), "zticket.admin.help", CommandUtils.Action.SUGGEST_COMMAND.getHover());
        components.add(commandComponent);
    }

    private void addHoverInstructions(List<SimpleComponent> components) {
        components.add(SimpleComponent.of("&f&nSurvolez la commande pour plus d'informations."));
    }

    private enum Param {
        HELP ("help", "h", "?"),
        GIVE ("give", "g"),
        USE ("use", "u"),
        LIST("list", "l");

        private final String label;
        private final String[] aliases;

        Param(final String paramLabel, final String... paramAliases) {
            this.label = paramLabel;
            this.aliases = paramAliases;
        }

        /**
         * Trouve un paramètre à partir de l'argument donné.
         *
         * @param paramArgument L'argument de la sous-commande.
         * @return Le Param correspondant ou null si non trouvé.
         */
        @Nullable
        private static Param find(String paramArgument) {
            String finalParamArgument = paramArgument.toLowerCase();

            return java.util.Arrays.stream(values())
                    .filter(param -> param.label.equals(finalParamArgument) || (param.aliases != null && java.util.Arrays.asList(param.aliases).contains(finalParamArgument)))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public String toString() {
            return this.label;
        }
    }
}
