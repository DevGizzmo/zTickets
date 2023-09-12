package net.maliimaloo.ztickets.plugin;

import lombok.Getter;
import net.maliimaloo.ztickets.plugin.manager.TicketManager;
import net.maliimaloo.ztickets.plugin.settings.SettingsTicket;
import net.maliimaloo.ztickets.plugin.settings.Settings;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ZTickets extends SimplePlugin {
    protected final String fileLogo = "logo.txt";

    @Getter
    private final String author = "Maliimaloo";

    @Getter
    private SettingsTicket settingsTicket;

    @Getter
    private TicketManager ticketManager;

    @Override
    protected void onPluginStart() {
        this.setupPlugin();
    }

    @Override
    protected void onPluginStop() {
        this.cleanupPlugin();
    }

    /**
     * Configuration du plugin en initialisant différentes composantes et services.
     */
    private void setupPlugin() {
        List<String> initialisationMessage = new ArrayList<>();

        initialisationMessage.add("&9&l" + Common.consoleLine());

        initialisationMessage.add("&cPrefix&a: Initialisation...");
        Common.setTellPrefix(Settings.getPluginPrefix());

        initialisationMessage.add("&cLog Prefix&a: Initialisation...");
        Common.setLogPrefix(Settings.getLogPrefix());

        initialisationMessage.add(" ");

        initialisationMessage.add("&cChargement &ades &ctickets &aen cours...");
        this.ticketManager = new TicketManager();
        initialisationMessage.add("&cChargement &aréussie de &c" + this.ticketManager.getCache().size() + " &aticket(s).");

        initialisationMessage.add(" ");

        if (super.getMainCommand() != null) {
            initialisationMessage.add("&cCommande principal&a: Initialisation...");
            initialisationMessage.add("&cChargement &aréussie de la commande &c" + super.getMainCommand().getLabel() + "&a.");

            initialisationMessage.add(" ");

            initialisationMessage.add("&cAliases&a: Initialisation...");
            initialisationMessage.add("&cChargement &aréussie de " + super.getMainCommand().getAliases().size() + " &aaliases.");
        }

        initialisationMessage.add("&9&l" + Common.consoleLine());

        Common.logNoPrefix(initialisationMessage.toArray(new String[0]));
    }

    /**
     * Nettoie les composantes du plugin lorsque celui-ci se ferme.
     */
    private void cleanupPlugin() {
    }

    @Override
    protected String[] getStartupLogo() {
        File file = FileUtil.extract(this.fileLogo);
        List<String> lines = FileUtil.readLines(file);

        lines.add("&9#=================[&cZTickets &9By &cMaliimaloo&9]================#");
        lines.add("&9# &cZTickets &aest en train de charger. Veuillez lire         §9#");
        lines.add("&9# &aattentivement toutes les sorties qui en découlent.      §9#");
        lines.add("&9#=========================================================#");

        return lines.toArray(new String[0]);
    }

    @Override
    public boolean suggestPaper() {
        return false;
    }

    @Override
    public MinecraftVersion.V getMinimumVersion() {
        return MinecraftVersion.V.v1_8;
    }

    public static ZTickets getInstance() {
        return (ZTickets) SimplePlugin.getInstance();
    }
}
