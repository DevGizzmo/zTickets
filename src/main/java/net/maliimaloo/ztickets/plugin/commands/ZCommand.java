/**
 * La classe principale pour gérer toutes les commandes du plugin ZTickets.
 * Cette classe hérite de {@link SimpleCommandGroup} et sert de point d'entrée
 * pour toutes les commandes liées à ZTickets.
 *
 * @see ZAdminCommand
 * @see ReloadCommand
 */
package net.maliimaloo.ztickets.plugin.commands;

import lombok.AccessLevel;
import lombok.Getter;
import net.maliimaloo.ztickets.plugin.ZTickets;
import net.maliimaloo.ztickets.plugin.commands.admin.ZAdminCommand;
import net.maliimaloo.ztickets.plugin.settings.Localization;
import net.maliimaloo.ztickets.plugin.settings.Settings;
import net.maliimaloo.ztickets.plugin.util.Utils;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.ReloadCommand;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.model.SimpleComponent;

import java.util.ArrayList;
import java.util.List;

@AutoRegister
public final class ZCommand extends SimpleCommandGroup {
    /**
     * L'instance statique de la classe {@link ZCommand} pour un accès facile.
     */
    @Getter(value = AccessLevel.PRIVATE)
    private static final ZCommand instance = new ZCommand();

    /**
     * Constructeur de la classe {@link ZCommand}.
     * Initialise les paramètres de la commande principale de ZTickets.
     */
    public ZCommand() {
        super("zticket");

        List<String> aliases = Settings.getAliases();
        super.setAliases(aliases);
    }

    /**
     * Retourne l'en-tête d'aide pour la commande principale.
     * Cette méthode est appelée lorsque la commande principale est exécutée sans paramètres.
     *
     * @return Un tableau de chaînes représentant l'en-tête d'aide.
     */
    @Override
    protected String[] getHelpHeader() {
        return new String[0];
    }

    /**
     * Retourne les composants d'en-tête sans paramètres.
     * Ces composants sont affichés lorsque la commande principale est exécutée sans paramètres.
     *
     * @return Une liste de {@link SimpleComponent} représentant les composants d'en-tête.
     */
    @Override
    protected List<SimpleComponent> getNoParamsHeader() {
        final String author = ZTickets.getInstance().getAuthor();
        final String version = ZTickets.getVersion();

        List<SimpleComponent> helpComponents = new ArrayList<>();

        this.addHeaderComponent(helpComponents, author, version);
        this.addHelpComponent(helpComponents);
        this.addFooterComponent(helpComponents, author, version);

        return helpComponents;
    }

    private void addHeaderComponent(List<SimpleComponent> components, String author, String version) {
        Localization.MainCommand.getNoParamHeader("plugin_version", version, "plugin_author", author)
                .forEach((line) -> components.add(SimpleComponent.of(line)));
    }

    private void addFooterComponent(List<SimpleComponent> components, String author, String version) {
        Localization.MainCommand.getNoParamFooter("plugin_version", version, "plugin_author", author)
                .forEach((line) -> components.add(SimpleComponent.of(line)));
    }

    private void addHelpComponent(List<SimpleComponent> components) {
        if (Utils.hasPermission(super.getSender(), "ztickets.admin.help")) {
            SimpleComponent helpComponent = SimpleComponent
                    .of(Localization.MainCommand.getHelpLinkMessage())
                    .onHover(Localization.MainCommand.getHelpLinkHover())
                    .onClickRunCmd(Localization.MainCommand.getHelpLinkCmd());

            components.add(helpComponent);
        }
    }

    /**
     * Enregistre les sous-commandes pour la commande principale.
     * Les sous-commandes sont gérées par cette classe et peuvent être ajoutées ici.
     */
    @Override
    protected void registerSubcommands() {
        super.registerSubcommand(new ZAdminCommand());
        super.registerSubcommand(new ReloadCommand("zticket.admin.reload"));
    }
}
