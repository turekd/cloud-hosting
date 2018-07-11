package it.dturek.cloudhosting.util;

import java.io.File;
import java.util.Arrays;

public class FileUtil {

    private static final String[] IMAGE_EXTENSIONS;

    static {
        IMAGE_EXTENSIONS = new String[]{
                "jpg",
                "png",
                "gif",
        };
    }

    private static final String[] IMAGE_CONTENT_TYPES;

    static {
        IMAGE_CONTENT_TYPES = new String[]{
                "image/jpg",
                "image/jpeg",
                "image/png",
                "image/gif",
        };
    }

    public static String getExtension(String filename) {
        String[] parts = filename.split("\\.");
        if (parts.length > 1) {
            return parts[parts.length - 1];
        }
        return "";
    }

    public static boolean hasValidExtension(String filename, String[] whitelist) {
        String extension = getExtension(filename).toLowerCase();
        return Arrays.stream(whitelist).anyMatch(e -> e.equals(extension));
    }

    public static boolean hasValidContentType(String contentType, String[] whitelist) {
        return Arrays.stream(whitelist).anyMatch(e -> e.equals(contentType));
    }

    public static String getFriendlySize(long bytes) {
        int unit = 1000;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "kMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String createPathToFile(Long id) {
        String idS = String.valueOf(id);
        StringBuilder sb = new StringBuilder();
        if (idS.length() > 1) {
            for (int i = 0; i < idS.length() - 1; i++) {
                sb
                        .append(File.separator)
                        .append(idS.charAt(i));
            }
        } else {
            sb
                    .append(File.separator)
                    .append(idS);
        }
        return sb.toString();
    }

}
