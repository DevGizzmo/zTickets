package net.maliimaloo.ztickets.plugin.model;

import lombok.Getter;
import lombok.Setter;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.exception.FoException;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.model.SkullCreator;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.List;

public final class MenuItemCreator {
    @Getter
    private String name, material;
    @Getter
    private int modelData;
    @Getter
    private boolean glow;
    @Getter
    private List<String> lore;
    @Getter
    private List<Integer> slots;

    private MenuItemCreator(Builder builder) {
        this.name = builder.name;
        this.material = builder.material;
        this.glow = builder.glow;
        this.lore = builder.lore;
        this.modelData = builder.modelData;
        this.slots = builder.slots;
    }

    public ItemStack toItemStack(SerializedMap replaceVariables) {
        ItemCreator itemCreator;
        if (replaceVariables == null) {
            replaceVariables = new SerializedMap();
        }

        if (this.material.contains("-")) {
            String[] materialData = this.material.split("-");
            switch (materialData[0]) {
                case "SKULL":
                    ItemStack skullItem = SkullCreator.itemFromName(materialData[1]);
                    if (skullItem == null) {
                        throw new FoException("Tête introuvable: " + materialData[1]);
                    }

                    itemCreator = ItemCreator.of(SkullCreator.itemFromName(materialData[1]));
                    break;

                case "HDB":
                    ItemStack hdbItem = new HeadDatabaseAPI().getItemHead(materialData[1]);
                    if (hdbItem == null) {
                        throw new FoException("Tête introuvable: " + materialData[1]);
                    }

                    itemCreator = ItemCreator.of(hdbItem);
                    break;
                default:
                    throw new FoException("Invalid material type: " + this.material);
            }
        } else {
            itemCreator = ItemCreator.of(CompMaterial.fromString(this.material));
        }

        if (!Valid.isNullOrEmpty(this.name)) {
            final String finalName = Replacer.replaceVariables(this.name, replaceVariables);
            itemCreator.name(finalName);
        }

        if (!Valid.isNullOrEmpty(this.lore)) {
            final List<String> finalLore = Replacer.replaceVariables(this.lore, replaceVariables);
            itemCreator.lore(finalLore);
        }

        if (this.modelData != 0) {
            itemCreator.modelData(this.modelData);
        }

        itemCreator.glow(this.isGlow());

        return itemCreator.make();
    }

    public static Builder of() {
        return new Builder();
    }

    public static class Builder {
        @Getter @Setter private String name, material;

        @Getter @Setter private boolean glow;

        @Getter @Setter private int modelData;

        @Getter @Setter private List<String> lore;

        @Getter @Setter private List<Integer> slots;

        public Builder() {
        }

        public MenuItemCreator build() {
            return new MenuItemCreator(this);
        }
    }
}
