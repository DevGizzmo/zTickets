package net.maliimaloo.ztickets.plugin.commands;

import lombok.AccessLevel;
import lombok.Getter;
import net.maliimaloo.ztickets.plugin.ZTickets;
import net.maliimaloo.ztickets.plugin.commands.admin.AdminCommand;
import net.maliimaloo.ztickets.plugin.settings.Settings;
import net.maliimaloo.ztickets.plugin.util.Utils;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.ReloadCommand;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.model.SimpleComponent;

import java.util.ArrayList;
import java.util.List;

@AutoRegister
public final class ZTicketCommand extends SimpleCommandGroup {
    @Getter (value = AccessLevel.PRIVATE)
    private static final ZTicketCommand instance = new ZTicketCommand();

    private final ZTickets plugin = ZTickets.getInstance();

    public ZTicketCommand() {
        super("zticket|zt");

        List<String> aliases = Settings.getAliases();
        super.setAliases(aliases);
    }

    @Override
    protected List<SimpleComponent> getNoParamsHeader() {
        final String author = ZTickets.getInstance().getAuthor();
        final String version = ZTickets.getVersion();

        List<SimpleComponent> headerComponents = new ArrayList<>();

        this.addEmptyChatLine(headerComponents);
        this.addVersionAndAuthor(headerComponents, version, author);

        if (Utils.hasPermission(super.getSender(), "zticket.admin.help")) {
            this.addHelpLink(headerComponents);
        }

        this.addEmptyChatLine(headerComponents);
        return headerComponents;
    }

    @Override
    protected void registerSubcommands() {
        super.registerSubcommand(new AdminCommand(this.plugin));
        super.registerSubcommand(new ReloadCommand("zticket.admin.reload"));
    }

    private void addEmptyChatLine(List<SimpleComponent> components) {
        components.add(SimpleComponent.of("&f" + Common.chatLine()));
    }

    private void addVersionAndAuthor(List<SimpleComponent> components, String version, String author) {
        components.add(SimpleComponent.of("&fEn cours d'exécution &czTickets &f" + version));
        components.add(SimpleComponent.of("&fCréé par &c&n" + author));
    }

    private void addHelpLink(List<SimpleComponent> components) {
        components.add(SimpleComponent.of(" "));
        components.add(SimpleComponent.of("&f&nClique pour voir les sous-commandes disponibles.")
                .onHover("&a&bClique pour voir les commandes disponibles.")
                .onClickRunCmd("/" + super.getLabel() + " admin ?")
        );
    }
}
