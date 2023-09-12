package net.maliimaloo.ztickets.plugin.settings;

import lombok.Getter;
import net.maliimaloo.ztickets.plugin.model.RewardData;
import net.maliimaloo.ztickets.plugin.model.TicketCreator;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.SimpleSound;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public final class TicketStorage extends YamlConfig {
    @Getter
    private final static TicketStorage instance = new TicketStorage();

    private final Set<TicketCreator> cache = new HashSet<>();

    private TicketStorage() {
        super.loadConfiguration("tickets.yml", "tickets.yml");
    }

    @Override
    protected void onLoad() {
        super.setPathPrefix(null);

        SerializedMap sectionTickets = super.getMap("Tickets");
        sectionTickets.forEach((uniqueId, value) -> {
            super.setPathPrefix("Tickets." + uniqueId);

            TicketCreator.Builder ticketCreatorBuilder = TicketCreator.of(uniqueId);
            if (super.isSet("Name")) {
                ticketCreatorBuilder.setName(super.getString("Name"));
            }

            if (super.isSet("Material")) {
                ticketCreatorBuilder.setMaterial(super.getString("Material"));
            }

            if (super.isSet("ModelData")) {
                ticketCreatorBuilder.setModelData(super.getInteger("ModelData"));
            }

            if (super.isSet("Glow")) {
                ticketCreatorBuilder.setGlow(super.getBoolean("Glow"));
            }

            if (super.isSet("Lore")) {
                ticketCreatorBuilder.setLore(super.getStringList("Lore"));
            }

            if (super.isSet("Permission")) {
                ticketCreatorBuilder.setPermission(super.getString("Permission"));
            }

            if (super.isSet("Title_Use")) {
                super.setPathPrefix("Tickets." + uniqueId + ".Title_Use");

                if (super.isSet("Title")) {
                    ticketCreatorBuilder.setTitle(super.getString("Title"));
                }

                if (super.isSet("Sub_Title")) {
                    ticketCreatorBuilder.setSubTitle(super.getString("Sub_Title"));
                }

                if (super.isSet("Fade_In")) {
                    ticketCreatorBuilder.setFadeIn(super.getInteger("Fade_In"));
                }

                if (super.isSet("Stay")) {
                    ticketCreatorBuilder.setStay(super.getInteger("Stay"));
                }

                if (super.isSet("Fade_Out")) {
                    ticketCreatorBuilder.setFadeOut(super.getInteger("Fade_Out"));
                }
            }

            super.setPathPrefix("Tickets." + uniqueId);

            if (super.isSet("Sound_Use")) {
                SerializedMap sectionSound = super.getMap("Sound_Use");
                sectionSound.forEach((soundKey, soundValue) -> {
                    super.setPathPrefix("Tickets." + uniqueId + ".Sound_Use." + soundKey);

                    if (!"NONE".equals(super.getString("Name"))) {
                        CompSound compSound = CompSound.fromName(super.getString("Name"));
                        final float volume = super.get("Volume", Float.class);
                        if (compSound == null) {
                            compSound = CompSound.CLICK;
                        }

                        switch (soundKey) {
                            case "Success":
                                ticketCreatorBuilder.setSoundSuccess(new SimpleSound(compSound.getSound(), volume));
                                break;
                            case "Failed":
                                ticketCreatorBuilder.setSoundFailed(new SimpleSound(compSound.getSound(), volume));
                                break;
                        }
                    }
                });
            }

            super.setPathPrefix("Tickets." + uniqueId);

            if (super.isSet("Rewards")) {
                super.setPathPrefix("Tickets." + uniqueId + ".Rewards");

                if (super.isSet("Rewards_Constant")) {
                    ticketCreatorBuilder.setRewardsConstant(super.getStringList("Rewards_Constant"));
                }

                if (super.isSet("Amount_Rewards_Chance")) {
                    ticketCreatorBuilder.setRewardsAmountChance(super.getInteger("Amount_Rewards_Chance"));
                }

                if (super.isSet("Rewards_Chance")) {
                    SerializedMap rewardChanceMap = super.getMap("Rewards_Chance");

                    List<RewardData> rewards = new ArrayList<>();
                    rewardChanceMap.forEach((rewardKey, rewardValue) -> {
                        RewardData rewardData = RewardData.fromMap(SerializedMap.of(rewardValue));
                        if (rewardData != null) {
                            rewards.add(rewardData);
                        }
                    });

                    ticketCreatorBuilder.setRewardsChance(rewards);
                }

                super.setPathPrefix("Tickets." + uniqueId + ".Rewards");
            }

            this.cache.add(ticketCreatorBuilder.build());
        });
    }

    @Override
    protected void onSave() {
    }
}