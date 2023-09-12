package net.maliimaloo.ztickets.plugin.settings;

import lombok.Getter;
import net.maliimaloo.ztickets.plugin.model.RewardData;
import net.maliimaloo.ztickets.plugin.model.TicketCreator;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.SimpleSound;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.settings.YamlStaticConfig;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SettingsTicket extends YamlStaticConfig {
    @Getter private static final Set<TicketCreator> cache = new HashSet<>();

    @Override
    protected void onLoad() {
        super.loadConfiguration("tickets/tickets.yml");
    }

    private static void init() {
        SettingsTicket.setPathPrefix(null);

        SerializedMap sectionTickets = SettingsTicket.getMap("Tickets");
        sectionTickets.forEach((key, value) -> {
            SettingsTicket.setPathPrefix("Tickets." + key);
            if (!Valid.isNumber(key)) {
                Common.throwError(new YAMLException("L'uniqueId: " + key + ", doit être un nombre valide."));
                return;
            }

            final int uniqueId = Integer.parseInt(key);
            TicketCreator.Builder ticketCreatorBuilder = TicketCreator.of(uniqueId);
            if (SettingsTicket.isSet("Name")) {
                ticketCreatorBuilder.setName(SettingsTicket.getString("Name"));
            }

            if (SettingsTicket.isSet("Material")) {
                ticketCreatorBuilder.setMaterial(SettingsTicket.getString("Material"));
            }

            if (SettingsTicket.isSet("ModelData")) {
                ticketCreatorBuilder.setModelData(SettingsTicket.getInteger("ModelData"));
            }

            if (SettingsTicket.isSet("Glow")) {
                ticketCreatorBuilder.setGlow(SettingsTicket.getBoolean("Glow"));
            }

            if (SettingsTicket.isSet("Lore")) {
                ticketCreatorBuilder.setLore(SettingsTicket.getStringList("Lore"));
            }

            if (SettingsTicket.isSet("Permission")) {
                ticketCreatorBuilder.setPermission(SettingsTicket.getString("Permission"));
            }

            if (SettingsTicket.isSet("Title_Use")) {
                SettingsTicket.setPathPrefix("Tickets." + key + ".Title_Use");

                if (SettingsTicket.isSet("Title")) {
                    ticketCreatorBuilder.setTitle(SettingsTicket.getString("Title"));
                }

                if (SettingsTicket.isSet("Sub_Title")) {
                    ticketCreatorBuilder.setSubTitle(SettingsTicket.getString("Sub_Title"));
                }

                if (SettingsTicket.isSet("Fade_In")) {
                    ticketCreatorBuilder.setFadeIn(SettingsTicket.getInteger("Fade_In"));
                }

                if (SettingsTicket.isSet("Stay")) {
                    ticketCreatorBuilder.setStay(SettingsTicket.getInteger("Stay"));
                }

                if (SettingsTicket.isSet("Fade_Out")) {
                    ticketCreatorBuilder.setFadeOut(SettingsTicket.getInteger("Fade_Out"));
                }
            }

            SettingsTicket.setPathPrefix("Tickets." + key);

            if (SettingsTicket.isSet("Sound_Use")) {
                SerializedMap sectionSound = SettingsTicket.getMap("Sound_Use");
                sectionSound.forEach((soundKey, soundValue) -> {
                    SettingsTicket.setPathPrefix("Tickets." + key + ".Sound_Use." + soundKey);

                    if (!"NONE".equals(SettingsTicket.getString("Name"))) {
                        CompSound compSound = CompSound.fromName(SettingsTicket.getString("Name"));
                        final float volume = SettingsTicket.get("Volume", Float.class);
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

            SettingsTicket.setPathPrefix("Tickets." + key);

            if (SettingsTicket.isSet("Rewards")) {
                SettingsTicket.setPathPrefix("Tickets." + key + ".Rewards");

                if (SettingsTicket.isSet("Rewards_Constant")) {
                    ticketCreatorBuilder.setRewardsConstant(SettingsTicket.getStringList("Rewards_Constant"));
                }

                if (SettingsTicket.isSet("Amount_Rewards_Chance")) {
                    ticketCreatorBuilder.setRewardsAmountChance(SettingsTicket.getInteger("Amount_Rewards_Chance"));
                }

                if (SettingsTicket.isSet("Rewards_Chance")) {
                    SerializedMap rewardChanceMap = SettingsTicket.getMap("Rewards_Chance");

                    List<RewardData> rewards = new ArrayList<>();
                    rewardChanceMap.forEach((rewardKey, rewardValue) -> {
                        RewardData rewardData = RewardData.fromMap(SerializedMap.of(rewardValue));
                        if (rewardData != null) {
                            rewards.add(rewardData);
                        }
                    });

                    ticketCreatorBuilder.setRewardsChance(rewards);
                }

                SettingsTicket.setPathPrefix("Tickets." + key + ".Rewards");
            }

            cache.add(ticketCreatorBuilder.build());
        });
    }
}