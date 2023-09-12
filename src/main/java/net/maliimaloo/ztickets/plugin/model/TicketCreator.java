package net.maliimaloo.ztickets.plugin.model;

import lombok.Getter;
import lombok.Setter;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.maliimaloo.ztickets.plugin.ZTickets;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.model.SkullCreator;
import org.mineacademy.fo.model.SimpleSound;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.nbt.NBTCompound;
import org.mineacademy.fo.remain.nbt.NBTItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un créateur de ticket avec diverses propriétés et méthodes pour l'utilisation.
 */
public final class TicketCreator {

    // Constantes pour les clés NBT
    private static final String KEY_CUSTOM_DATA = "item_data";
    private static final String KEY_UNIQUE_ID = "unique_id";

    // Propriétés du ticket
    @Getter private final int uniqueId;
    @Getter private final String name;
    @Getter private final String permission;
    @Getter private final String material;
    @Getter private final int modelData;
    @Getter private final String title;
    @Getter private final String subTitle;
    @Getter private final int fadeIn;
    @Getter private final int stay;
    @Getter private final int fadeOut;
    @Getter private final boolean glow;
    @Getter private final List<String> lore;
    @Getter private final SimpleSound soundSuccess;
    @Getter private final SimpleSound soundFailed;
    @Getter private final List<String> rewardsConstant;
    @Getter private final int rewardsAmountChance;
    @Getter private final List<RewardData> rewardsChance;

    // Constructeur privé, utilisez le Builder pour créer des instances
    private TicketCreator(Builder builder) {
        this.uniqueId = builder.uniqueId;
        this.name = builder.name;
        this.material = builder.material;
        this.modelData = builder.modelData;
        this.permission = builder.permission;
        this.title = builder.title;
        this.subTitle = builder.subTitle;
        this.fadeIn = builder.fadeIn;
        this.stay = builder.stay;
        this.fadeOut = builder.fadeOut;
        this.glow = builder.glow;
        this.lore = builder.lore;
        this.soundSuccess = builder.soundSuccess;
        this.soundFailed = builder.soundFailed;
        this.rewardsConstant = builder.rewardsConstant;
        this.rewardsAmountChance = builder.rewardsAmountChance;
        this.rewardsChance = builder.rewardsChance;
    }

    /**
     * Convertit ce créateur de ticket en un ItemStack.
     *
     * @param amount Le nombre d'ItemStacks à créer
     * @return L'ItemStack créé
     */
    public ItemStack toItemStack(int amount) {
        ItemCreator itemCreator = this.buildItemCreator(amount);

        if (itemCreator == null) return null;

        NBTItem nbtItem = new NBTItem(itemCreator.make());
        NBTCompound nbtCompound = nbtItem.addCompound(KEY_CUSTOM_DATA);
        nbtCompound.setInteger(KEY_UNIQUE_ID, this.uniqueId);

        return nbtItem.getItem();
    }

    /**
     * Créer un objet TicketCreator à partir d'un ItemStack
     *
     * @param itemStack L'ItemStack à partir duquel créer l'objet TicketCreator
     * @return L'objet TicketCreator créé, ou null si l'ItemStack ne contient pas d'informations valides
     */
    public static TicketCreator fromItemStack(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == CompMaterial.AIR.toMaterial()) {
            return null;
        }

        final ZTickets plugin = ZTickets.getInstance();

        final NBTItem nbtItem = new NBTItem(itemStack);
        final NBTCompound nbtCompound = nbtItem.getCompound(KEY_CUSTOM_DATA);
        if (nbtCompound == null) {
            return null;
        }

        final int uniqueId = nbtCompound.getInteger(KEY_UNIQUE_ID);
        final TicketCreator ticketCreator = plugin.getTicketManager().getTicket(uniqueId);
        if (ticketCreator == null) {
            Common.throwError(new NullPointerException(), "L'itemstack n'a pas été trouvé dans le cache de ticket.");
            return null;
        }

        return ticketCreator;
    }

    private ItemCreator buildItemCreator(int amount) {
        ItemCreator itemCreator;

        if (this.material.contains("-")) {
            String[] materialData = this.material.split("-");

            switch (materialData[0]) {
                case "SKULL":
                    ItemStack skullItem = SkullCreator.itemFromName(materialData[1]);

                    if (skullItem == null) {
                        Common.throwError(new NullPointerException(), "Tête introuvable: " + materialData[1]);
                        return null;
                    }

                    itemCreator = ItemCreator.of(skullItem);
                    break;

                case "HDB":
                    ItemStack hdbItem = new HeadDatabaseAPI().getItemHead(materialData[1]);

                    if (hdbItem == null) {
                        Common.throwError(new NullPointerException(), "Tête introuvable dans HDB: " + materialData[1]);
                        return null;
                    }

                    itemCreator = ItemCreator.of(hdbItem);
                    break;

                default:
                    Common.throwError(new NullPointerException(), "Invalid material type: " + this.material);
                    return null;
            }
        } else {
            itemCreator = ItemCreator.of(CompMaterial.fromString(this.material));
        }

        if (this.modelData != 0) {
            itemCreator.modelData(this.modelData);
        }

        itemCreator.name(this.name);
        itemCreator.lore(this.lore);
        itemCreator.glow(this.glow);
        itemCreator.hideTags(true);
        itemCreator.amount(amount);

        return itemCreator;
    }

    public static Builder of(int uniqueId) {
        return new Builder(uniqueId);
    }

    public static class Builder {
        @Getter @Setter private int uniqueId;

        @Getter @Setter private String name = "Default";

        @Getter @Setter private String material = CompMaterial.BARRIER.toMaterial().toString();

        @Getter @Setter private int modelData = 0;

        @Getter @Setter private boolean glow = false;

        @Getter @Setter private List<String> lore = new ArrayList<>();

        @Getter @Setter private String permission = "NONE";

        @Getter @Setter private String title = "NONE", subTitle = "Default Sub_Title";

        @Getter @Setter private int fadeIn = 1, stay = 2, fadeOut = 1;

        @Getter @Setter private SimpleSound soundSuccess = null, soundFailed = null;

        @Getter @Setter private List<String> rewardsConstant = new ArrayList<>();
        
        @Getter @Setter private List<RewardData> rewardsChance = new ArrayList<>();

        @Getter @Setter private int rewardsAmountChance = 0;


        public Builder(int uniqueId) {
            this.uniqueId = uniqueId;
        }

        public TicketCreator build() {
            return new TicketCreator(this);
        }
    }
}
