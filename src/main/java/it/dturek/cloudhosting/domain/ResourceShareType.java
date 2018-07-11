package it.dturek.cloudhosting.domain;

public enum ResourceShareType {

    ONCE,
    TEMPORARILY_HOUR,
    TEMPORARILY_DAY,
    TEMPORARILY_WEEK;

    public static ResourceShareType fromString(String s) {
        if (s != null) {
            switch (s) {
                case "ONCE":
                    return ResourceShareType.ONCE;
                case "TEMPORARILY_HOUR":
                    return ResourceShareType.TEMPORARILY_HOUR;
                case "TEMPORARILY_DAY":
                    return ResourceShareType.TEMPORARILY_DAY;
                case "TEMPORARILY_WEEK":
                    return ResourceShareType.TEMPORARILY_WEEK;
                default:
                    return null;
            }
        }
        return null;
    }

}
