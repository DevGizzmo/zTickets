package net.maliimaloo.ztickets.plugin.util;

public class RandomUtils {
    /**
     * Génère une liste de commandes en fonction des pourcentages spécifiés.
     *
     * @param commandEntries La liste des entrées de commandes parmi lesquelles sélectionner.
     * @param amount         Le nombre d'entrées de commandes à sélectionner.
     * @return Une liste de commandes sélectionnées en fonction des pourcentages.
     *
    public static List<CommandEntry> selectCommands(List<CommandEntry> commandEntries, int amount) {
        final List<CommandEntry> selectedCommands = new ArrayList<>();

        commandEntries.sort((c1, c2) -> Integer.compare(c2.getPercentage(), c1.getPercentage()));

        while (selectedCommands.size() != amount) {
            int randomValue = RandomUtil.nextInt(100);
            for (CommandEntry command : commandEntries) {
                if (selectedCommands.size() == amount) {
                    break;
                }

                if (randomValue < command.getPercentage()) {
                    selectedCommands.add(command);
                }
            }
        }

        return selectedCommands;
    }*/
}
