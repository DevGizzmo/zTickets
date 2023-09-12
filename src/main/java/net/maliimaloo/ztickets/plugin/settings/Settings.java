package net.maliimaloo.ztickets.plugin.settings;

import org.mineacademy.fo.settings.SimpleSettings;

import java.util.List;

public class Settings extends SimpleSettings {
    private static String LOG_PREFIX;

    private static List<String> ALIASES;

    private static Boolean isEnabledGUIConfirm;

    private static void init() {
        Settings.setPathPrefix(null);

        LOG_PREFIX = Settings.getString("Log_Prefix");
        ALIASES = Settings.getStringList("Aliases");

        Settings.setPathPrefix("GUIConfirm");
        isEnabledGUIConfirm = Settings.getBoolean("Enabled");
    }

    public static String getPluginPrefix() { return Settings.PLUGIN_PREFIX; }
    public static String getLogPrefix() { return Settings.LOG_PREFIX; }
    public static List<String> getAliases() { return Settings.ALIASES; }
    public static Boolean isEnabledGUIConfirm() {return Settings.isEnabledGUIConfirm; }
}
