package net.eaglemc.eagleduel.plugin.util;

import net.eaglemc.eagleduel.plugin.settings.Settings;
import org.bukkit.entity.Player;
import org.mineacademy.fo.*;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.JavaScriptExecutor;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.fo.settings.SimpleLocalization;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class LangUtils {
    private final static Map<Player, Long> messageCooldowns = new HashMap<>();
    private final static Map<Player, String> lastSentMessages = new HashMap<>();

    public static String of(String message, Object... variables) {
        return of(message, null, variables);
    }

    public static String of(String message, @Nullable SerializedMap serializedMap, Object... variables) {
        if (serializedMap != null && !serializedMap.isEmpty()) {
            message = Replacer.replaceArray(message, serializedMap);
        }
        message = Messenger.replacePrefixes(message);
        message = translate(message, variables);
        return message;
    }

    public static List<String> of(List<String> list, Object... variables) {
        return of(list, null, variables);
    }

    public static List<String> of(List<String> list, @Nullable SerializedMap serializedMap, Object... variables) {
        List<String> finalListMessage = new ArrayList<>();
        for (String paramMessage : list) {
            if (serializedMap == null ||serializedMap.isEmpty()) {
                paramMessage = LangUtils.of(paramMessage, variables);
            } else {
                paramMessage = LangUtils.of(paramMessage, serializedMap, variables);
            }

            finalListMessage.add(paramMessage);
        }

        return finalListMessage;
    }

    public static String ofScript(String message, SerializedMap scriptVariables, Object... stringVariables) {
        String script = LangUtils.of(message, stringVariables);
        if (!script.contains("?") && !script.contains(":") && !script.contains("+") && !script.startsWith("'") && !script.endsWith("'")) {
            script = "'" + script + "'";
        }

        Object result;
        try {
            result = JavaScriptExecutor.run(script, scriptVariables.asMap());
        } catch (Throwable var6) {
            if (Settings.LOCALE_PREFIX.equals("en")) {
                Common.error(var6, "Failed to compile localization key!", "It must be a valid JavaScript code, if you modified it, check the syntax!", "Variables: " + scriptVariables, "String variables: " + Common.join(stringVariables), "Script: " + script, "Error: %error%");
            } else {
                Common.error(var6, "Échec de la compilation de la clé de localization !", "Il doit s'agir d'un code JavaScript valide. Si vous l'avez modifié, vérifier la syntaxe !", "Variables: " + scriptVariables, "String variables: " + Common.join(stringVariables), "Script: " + script, "Erreur: %error%");
            }
            return script;
        }

        return result.toString();
    }

    public static List<String> ofScriptList(List<String> paramList, SerializedMap paramSerializedMap, Object... stringVariables) {
        final List<String> finalList = new ArrayList<>();

        for (String paramString : paramList) {
            try {
                paramString = ofScript(paramString, paramSerializedMap, stringVariables);
            } catch (NullPointerException ignored) {
            }

            finalList.add(paramString);
        }

        return finalList;
    }

    public static void sendMessageCooldown(Player player, String message, int defaultCooldownDuration) {
        long currentTime = System.currentTimeMillis();
        long cooldownDuration = defaultCooldownDuration * 1000L;
        if (messageCooldowns.containsKey(player)) {
            long lastCooldownTime = messageCooldowns.get(player);
            if (currentTime - lastCooldownTime < cooldownDuration) {
                // Cooldown hasn't passed yet, check if the message is similar to the last one
                if (lastSentMessages.containsKey(player) && lastSentMessages.get(player).equals(message)) {
                    return; // Skip sending the message
                }
            }
        }

        // Update the cooldown time and the last sent message
        messageCooldowns.put(player, currentTime);
        lastSentMessages.put(player, message);

        Common.tell(player, message);
    }

    private static String translate(String key, Object... variables) {
        Valid.checkNotNull(key, "Impossible de translate une clef null avec les variables: " + Common.join(variables));
        if (variables != null) {
            for(int i = 0; i < variables.length; ++i) {
                Object variable = variables[i];
                variable = Common.getOrDefaultStrict(SerializeUtil.serialize(SerializeUtil.Mode.YAML, variable), SimpleLocalization.NONE);
                Valid.checkNotNull(variable, "Échec de remplacement {" + i + "} à " + variable + " (raw = " + variables[i] + ")");
                key = key.replace("{" + i + "}", variable.toString());
            }
        }

        return key;
    }

    public enum MessageType {
        SUCCESS,
        ERROR
    }
}