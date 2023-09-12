package net.maliimaloo.ztickets.plugin.model;

import lombok.Getter;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;

import java.util.List;

public final class HelpData implements ConfigSerializable {
    @Getter private String param;
    @Getter private String permission;
    @Getter private List<String> hover;

    public HelpData(String param, String permission, List<String> hover) {
        this.param = param;
        this.permission = permission;
        this.hover = hover;
    }

    @Override
    public SerializedMap serialize() {
        return SerializedMap.ofArray(
                "param", this.param,
                "permission", this.permission,
                "hover", this.hover
        );
    }

    public static HelpData deserialize(SerializedMap map) {
        final String param = map.getString("param");
        final String permission = map.getString("permission");
        final List<String> hover = map.getStringList("hover");

        return new HelpData(param, permission, hover);
    }
}
