package net.maliimaloo.ztickets.plugin.commands.admin;

import net.maliimaloo.ztickets.plugin.ZTickets;
import net.maliimaloo.ztickets.plugin.model.TicketCreator;
import net.maliimaloo.ztickets.plugin.settings.Localization;
import net.maliimaloo.ztickets.plugin.util.CommandUtils;
import net.maliimaloo.ztickets.plugin.util.Utils;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.command.ReloadCommand;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.model.SimpleComponent;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.settings.SimpleLocalization;
import org.mineacademy.fo.settings.YamlConfig;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ZAdminCommand extends SimpleSubCommand {
    private final ZTickets plugin = ZTickets.getInstance();

    public ZAdminCommand() {
        super("admin");

        super.setAutoHandleHelp(false);
        super.setPermission("ztickets.command.admin");
    }

    @Override
    protected void onCommand() {
        final Param param = Param.find(args[0]);
        if (param == null) {
            returnInvalidArgs();
            return;
        }

        switch (param) {
            case HELP:
                this.handleHelpCommand();
                break;

            case GIVE:
                this.handleGiveOrUseCommand(true);
                break;

            case USE:
                this.handleGiveOrUseCommand(false);
                break;

            case LIST:
                this.handleListCommand();
                break;

            case RELOAD:
                this.handleReloadCommand();
                break;
        }
    }

    private void handleGiveOrUseCommand(boolean isGiveCommand) {
        final String permission = isGiveCommand ? "ztickets.admin.give" : "ztickets.admin.use";
        if (!Utils.hasPermission(getSender(), permission)) {
            super.tellError(Localization.getNoPermission("permission", permission));
            return;
        }

        if (super.args.length < 4) {
            super.returnInvalidArgs();
            return;
        }

        final String receiverName = super.args[1];
        final Player receiver = PlayerUtil.getPlayerByNick(receiverName, true);
        if (receiver == null) {
            super.tellError(Localization.getPlayerNotOnline("target_player", receiverName));
            return;
        }

        final int uniqueId;
        if (!Valid.isNumber(super.args[2])) {
            super.tellError(Localization.getInvalidNumber("input", super.args[2]));
            return;
        } else {
            uniqueId = Integer.parseInt(super.args[2]);
        }

        if (!this.plugin.getTicketManager().isExists(uniqueId)) {
            super.tellError(Localization.GiveOrUseCommand.getTicketNotExist("ticket_id", uniqueId));
            return;
        }

        final int amount;
        if (!Valid.isNumber(super.args[3])) {
            super.tellError(Localization.getInvalidNumber("input", super.args[3]));
            return;
        } else {
            amount = Integer.parseInt(super.args[3]);
        }


        TicketCreator ticket = this.plugin.getTicketManager().getTicket(uniqueId);
        if (isGiveCommand) {
            PlayerUtil.addItems(receiver.getInventory(), ticket.toItemStack(amount));

            super.tellSuccess(Localization.GiveOrUseCommand.getGiveSender("amount", amount, "ticket_id", ticket.getUniqueId(), "ticket_name", ticket.getName(), "receiver_player", receiver.getName(), "sender_player", super.getPlayer().getName()));
            Messenger.success(receiver,Localization.GiveOrUseCommand.getGiveReceiver("amount", amount, "ticket_id", ticket.getUniqueId(), "ticket_name", ticket.getName(), "receiver_player", receiver.getName(), "sender_player", super.getPlayer().getName()));
        } else {
            this.plugin.getTicketManager().use(receiver, ticket, true, amount);

            super.tellSuccess(Localization.GiveOrUseCommand.getUseSender("amount", amount, "ticket_id", ticket.getUniqueId(), "ticket_name", ticket.getName(), "receiver_player", receiver.getName(), "sender_player", super.getPlayer().getName()));
            Messenger.success(receiver,Localization.GiveOrUseCommand.getUseReceiver("amount", amount, "ticket_id", ticket.getUniqueId(), "ticket_name", ticket.getName(), "receiver_player", receiver.getName(), "sender_player", super.getPlayer().getName()));
        }
    }

    private void handleListCommand() {
        final String permission = "ztickets.admin.list";
        if (!Utils.hasPermission(getSender(), permission)) {
            super.tellError(Localization.getNoPermission("permission", permission));
        } else {
            List<String> message = new ArrayList<>(Localization.ListCommand.getHeader());

            new ArrayList<>(this.plugin.getTicketManager().getCache()).stream()
                    .sorted(Comparator.comparingInt(TicketCreator::getUniqueId))
                    .collect(Collectors.toList())
                    .forEach((ticket) -> message.add(Localization.ListCommand.getLine("ticket_id", ticket.getUniqueId(), "ticket_name", ticket.getName())));

            message.addAll(Localization.ListCommand.getFooter());
            super.tell(message);
        }
    }

    private void handleHelpCommand() {
        final String permission = "ztickets.admin.help";
        if (!Utils.hasPermission(getSender(), permission)) {
            super.tellError(Localization.getNoPermission("permission", permission));
            return;
        }

        List<SimpleComponent> helpComponents = new ArrayList<>();

        this.addHeaderComponent(helpComponents);
        this.addCommandsComponent(helpComponents);
        this.addFooterComponent(helpComponents);

        super.tell(helpComponents);
    }

    private void handleReloadCommand() {
        final String permission = "ztickets.admin.reload";
        if (!Utils.hasPermission(super.getSender(), permission)) {
            super.tellError(Localization.getNoPermission("permission", permission));
            return;
        }

        try {
            super.tellSuccess(SimpleLocalization.Commands.RELOAD_STARTED);
            boolean syntaxParsed = true;
            List<File> yamlFiles = new ArrayList<>();
            this.collectYamlFiles(SimplePlugin.getData(), yamlFiles);

            for (File file : yamlFiles) {
                try {
                    YamlConfig.fromFile(file);
                } catch (Throwable var6) {
                    var6.printStackTrace();
                    syntaxParsed = false;
                }
            }

            if (!syntaxParsed) {
                super.tellError(SimpleLocalization.Commands.RELOAD_FILE_LOAD_ERROR);
                return;
            }

            SimplePlugin.getInstance().reload();
            super.tellSuccess(SimpleLocalization.Commands.RELOAD_SUCCESS);
        } catch (Throwable var7) {
            super.tellError(SimpleLocalization.Commands.RELOAD_FAIL.replace("{error}", var7.getMessage() != null ? var7.getMessage() : "unknown"));
            var7.printStackTrace();
        }
    }

    private void addHeaderComponent(List<SimpleComponent> components) {
        Localization.HelpCommand.getHeader()
                .forEach((line) -> components.add(SimpleComponent.of(line)));
    }

    private void addFooterComponent(List<SimpleComponent> components) {
        Localization.HelpCommand.getFooter()
                .forEach((line) -> components.add(SimpleComponent.of(line)));
    }

    private void addCommandsComponent(List<SimpleComponent> components) {
        Localization.HelpCommand.getHelpData()
                .forEach((helpData) -> {
                    SimpleComponent commandComponent = SimpleComponent.empty();

                    CommandUtils.createCommandComponent(commandComponent, "&f" + super.getCurrentLabel() + " " + super.getSublabel(), super.getPlayer(), helpData.getParam(), "", helpData.getHover(), helpData.getPermission(), CommandUtils.Action.SUGGEST_COMMAND.getHover());
                    components.add(commandComponent);
                }
        );
    }

    private List<File> collectYamlFiles(File directory, List<File> list) {
        if (directory.exists()) {
            File[] var3 = directory.listFiles();
            for (File file : var3) {
                if (file.getName().endsWith("yml")) {
                    list.add(file);
                }

                if (file.isDirectory()) {
                    this.collectYamlFiles(file, list);
                }
            }
        }

        return list;
    }

    private enum Param {
        HELP("help", "h", "?"),
        GIVE("give", "g"),
        USE("use", "u"),
        LIST("list", "l"),
        RELOAD("reload", "rl");

        private final String label;
        private final String[] aliases;

        Param(final String paramLabel, final String... paramAliases) {
            this.label = paramLabel;
            this.aliases = paramAliases;
        }

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
