package net.maliimaloo.ztickets.plugin.settings;

import net.maliimaloo.ztickets.plugin.model.HelpData;
import net.maliimaloo.ztickets.plugin.util.LangUtils;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.settings.SimpleLocalization;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Localization extends SimpleLocalization {
    private static String USE_TICKET, NOT_WIN_REWARD;

    public Localization() {
    }

    private static void init() {
        Localization.setPathPrefix(null);
        USE_TICKET = Localization.getString("Player.Use_Ticket");
        NOT_WIN_REWARD = Localization.getString("Player.Not_Win_Reward");
    }

    public static String getNoPermission(Object... variables) { return LangUtils.of(Localization.NO_PERMISSION, SerializedMap.ofArray(variables)); }
    public static String getPlayerNotOnline(Object... variables) { return LangUtils.of(Player.NOT_ONLINE, SerializedMap.ofArray(variables)); }
    public static String getInvalidNumber(Object... variables) { return LangUtils.of(Commands.INVALID_NUMBER, SerializedMap.ofArray(variables)); }
    public static String getUseTicket(Object... variables) { return LangUtils.of(USE_TICKET, SerializedMap.ofArray(variables)); }
    public static String getNotWinReward() {return LangUtils.of(NOT_WIN_REWARD); }

    public static class MainCommand {
        private static List<String> NO_PARAM_HEADER, NO_PARAM_FOOTER, HELP_LINK_HOVER;
        private static String HELP_LINK_MESSAGE, HELP_LINK_CMD;

        public MainCommand() {
        }

        private static void init() {
            Localization.setPathPrefix("Commands.MainCommand");

            NO_PARAM_HEADER = Localization.getStringList("no_param_header");
            NO_PARAM_FOOTER = Localization.getStringList("no_param_footer");

            Localization.setPathPrefix(Localization.getPathPrefix() + ".help_link");
            HELP_LINK_MESSAGE = Localization.getString("message");
            HELP_LINK_HOVER = Localization.getStringList("on_hover");
            HELP_LINK_CMD = Localization.getString("on_run_cmd");
        }

        public static List<String> getNoParamHeader(Object... variables) { return LangUtils.of(NO_PARAM_HEADER, SerializedMap.ofArray(SerializedMap.ofArray(variables))); }
        public static List<String> getNoParamFooter(Object... variables) { return LangUtils.of(NO_PARAM_FOOTER, SerializedMap.ofArray(SerializedMap.ofArray(variables))); }
        public static List<String> getHelpLinkHover() { return LangUtils.of(HELP_LINK_HOVER); }
        public static String getHelpLinkMessage() { return LangUtils.of(HELP_LINK_MESSAGE); }
        public static String getHelpLinkCmd() { return LangUtils.of(HELP_LINK_CMD); }
    }

    public static class GiveOrUseCommand {
        private static String TICKET_NOT_EXIST;
        private static String GIVE_SENDER, GIVE_RECEIVER, USE_SENDER, USE_RECEIVER;

        public GiveOrUseCommand() {
        }

        private static void init() {
            Localization.setPathPrefix("Commands.GiveOrUseCommand");
            TICKET_NOT_EXIST = Localization.getString("ticket_not_exist");

            Localization.setPathPrefix("Commands.GiveOrUseCommand.give");
            GIVE_SENDER = Localization.getString("sender");
            GIVE_RECEIVER = Localization.getString("receiver");

            Localization.setPathPrefix("Commands.GiveOrUseCommand.use");
            USE_SENDER = Localization.getString("sender");
            USE_RECEIVER = Localization.getString("receiver");
        }

        public static String getTicketNotExist(Object... variables) { return LangUtils.of(TICKET_NOT_EXIST, SerializedMap.ofArray(SerializedMap.ofArray(variables))); }
        public static String getGiveSender(Object... variables) { return LangUtils.of(GIVE_SENDER, SerializedMap.ofArray(SerializedMap.ofArray(variables))); }
        public static String getGiveReceiver(Object... variables) { return LangUtils.of(GIVE_RECEIVER, SerializedMap.ofArray(variables)); }
        public static String getUseSender(Object... variables) { return LangUtils.of(USE_SENDER, SerializedMap.ofArray(variables)); }
        public static String getUseReceiver(Object... variables) { return LangUtils.of(USE_RECEIVER, SerializedMap.ofArray(variables)); }

    }
    public static class HelpCommand {
        private static List<String> HEADER, FOOTER;
        private static Set<HelpData> HELP_DATA;

        private static void init() {
            Localization.setPathPrefix("Commands.HelpCommand");
            HEADER = Localization.getStringList("header");
            FOOTER = Localization.getStringList("footer");
            HELP_DATA = Localization.getSet("commands", HelpData.class);
        }

        public static List<String> getHeader() { return LangUtils.of(HEADER); }
        public static List<String> getFooter() { return LangUtils.of(FOOTER); }
        public static Set<HelpData> getHelpData() {
            final Set<HelpData> cache = new HashSet<>();
            HELP_DATA.forEach((helpData) -> {
                final String param = LangUtils.of(helpData.getParam());
                final String permission = LangUtils.of(helpData.getPermission());
                final List<String> hover = LangUtils.of(helpData.getHover());

                cache.add(new HelpData(param, permission, hover));
            });

            return cache;
        }
    }

    public static class ListCommand {
        private static List<String> HEADER, FOOTER;
        private static String LINE;

        public ListCommand() {
        }

        private static void init() {
            Localization.setPathPrefix("Commands.ListCommand");
            HEADER = Localization.getStringList("header");
            FOOTER = Localization.getStringList("footer");
            LINE = Localization.getString("line");
        }

        public static List<String> getHeader() { return LangUtils.of(HEADER); }
        public static List<String> getFooter() { return LangUtils.of(FOOTER); }
        public static String getLine(Object... variables) { return LangUtils.of(LINE, SerializedMap.ofArray(variables)); }
    }
}
