package net.maliimaloo.ztickets.plugin.settings;

import lombok.Getter;
import net.maliimaloo.ztickets.plugin.model.MenuItemCreator;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.settings.YamlStaticConfig;

import java.util.ArrayList;
import java.util.List;

public final class SettingsGUIConfirm extends YamlStaticConfig {
    @Getter private static String TITLE;
    @Getter private static Integer ROW;

    private static void init() {
        SettingsGUIConfirm.setPathPrefix(null);

        TITLE = getString("Title");
        ROW = getInteger("Row");
    }

    public final static class Content {
        @Getter private final static List<MenuItemCreator> contents = new ArrayList<>();

        private static void init() {
            SettingsGUIConfirm.setPathPrefix(null);

            SerializedMap sectionContent = getMap("Contents.Content");
            sectionContent.forEach((key, value) -> {
                SerializedMap content = sectionContent.getMap(key);

                MenuItemCreator.Builder contentItemBuilder = MenuItemCreator.of();
                if (content.containsKey("name")) {
                    contentItemBuilder.setName(content.getString("name"));
                }

                if (content.containsKey("material")) {
                    contentItemBuilder.setMaterial(content.getString("material"));
                }

                if (content.containsKey("modelData")) {
                    contentItemBuilder.setModelData(content.getInteger("modelData"));
                }

                if (content.containsKey("glow")) {
                    contentItemBuilder.setGlow(content.getBoolean("glow"));
                }

                if (content.containsKey("lore")) {
                    contentItemBuilder.setLore(content.getStringList("lore"));
                }

                if (content.containsKey("slots")) {
                    List<Integer> slots = content.getList("slots", Integer.class);
                    contentItemBuilder.setSlots(slots);
                }

                contents.add(contentItemBuilder.build());
            });
        }
    }
    public final static class ImmutableContent {
        @Getter private static MenuItemCreator CONFIRM_ITEM;
        @Getter private static MenuItemCreator CANCEL_ITEM;

        private static void init() {
            SettingsGUIConfirm.setPathPrefix(null);

            CONFIRM_ITEM = buildItem("Contents.Immutable_Content.Confirm");
            CANCEL_ITEM = buildItem("Contents.Immutable_Content.Cancel");
        }

        private static MenuItemCreator buildItem(String pathPrefix) {
            SettingsGUIConfirm.setPathPrefix(pathPrefix);

            MenuItemCreator.Builder confirmItemBuilder = MenuItemCreator.of();

            final String name = getInstance().getString("name", "default");
            confirmItemBuilder.setName(name);

            final String material = getInstance().getString("material", "PAPER");
            confirmItemBuilder.setMaterial(material);

            final Boolean glow = getInstance().getBoolean("glow", false);
            confirmItemBuilder.setGlow(glow);

            final Integer modelData = getInstance().getInteger("modelData", 0);
            confirmItemBuilder.setModelData(modelData);

            final List<String> lore = getStringList("lore");
            confirmItemBuilder.setLore(lore);

            final List<Integer> slots = getList("slots", Integer.class);
            confirmItemBuilder.setSlots(slots);

            return confirmItemBuilder.build();
        }
    }

    @Override
    protected void onLoad() {
        super.loadConfiguration("menu/gui_confirmation.yml");
    }
}
