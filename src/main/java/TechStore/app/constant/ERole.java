package TechStore.app.constant;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum ERole {
    ADMIN("ADMIN"),
    USER("USER");
    private final String name;

    private static final Map<String, ERole> lookup = new HashMap<>();

    static {
        for (ERole role : ERole.values()) {
            lookup.put(role.name, role);
        }
    }

    ERole(String name) {
        this.name = name;
    }
    public static ERole getByName(String role) {
        return lookup.get(role);
    }
}

