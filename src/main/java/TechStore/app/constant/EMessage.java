package TechStore.app.constant;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public enum EMessage {
    SYSTEM_ERROR(1000, "A system error has occurred. Please try again later", List.of(EMessageType.REQUEST, EMessageType.GAME)),
    SYSTEM_ERROR_KEY(1001, "System not yet have info Key, check again to gen key first!", List.of(EMessageType.SYSTEM)),

    USER_NOT_FOUND(2000, "User not found", List.of(EMessageType.SYSTEM, EMessageType.REQUEST));

    private final Integer code;
    private final String message;
    private final List<EMessageType> types;

    EMessage(int code, String message, List<EMessageType> types) {
        this.code = code;
        this.message = message;
        this.types = types;
    }

    private static final Map<Integer, EMessage> lookup = new HashMap<>();

    static {
        for (EMessage eMessage : EMessage.values()) {
            lookup.put(eMessage.code, eMessage);
        }
    }

    public static EMessage getEMessage(Integer code) {
        return lookup.get(code);
    }

    public enum EMessageType {
        SYSTEM("SYSTEM"),
        REQUEST("REQUEST"),
        GAME("GAME");

        private final String name;

        EMessageType(String name) {
            this.name = name;
        }

        private static final Map<String, EMessageType> lookup = new HashMap<>();

        static {
            for (EMessageType eMessageType : EMessageType.values()) {
                lookup.put(eMessageType.name, eMessageType);
            }
        }

        public static EMessageType getEMessageTypeByName(String name) {
            return lookup.get(name);
        }
    }
}
