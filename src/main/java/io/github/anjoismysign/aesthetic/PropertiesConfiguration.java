package io.github.anjoismysign.aesthetic;

import org.jetbrains.annotations.NotNull;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesConfiguration {

    @NotNull
    public static Map<String, String> create(String... keyOrDefaults) throws IOException {
        Map<String, String> keyOrDefaultsMap = new LinkedHashMap<>();
        for (int index = 0; index < keyOrDefaults.length;
             index++) {
            String key = keyOrDefaults[index];
            index++;
            String value;
            try {
                value = keyOrDefaults[index];
            } catch (ArrayIndexOutOfBoundsException exception) {
                value = " ";
            }
            keyOrDefaultsMap.put(key, value);
        }
        if (keyOrDefaults.length % 2 != 0) {
            int width = 15;
            StringBuilder builder = new StringBuilder("invalid keyOrDefaults:\n");
            keyOrDefaultsMap.forEach((key, value) -> {
                builder.append(String.format("%-" + width + "s | %-" + width + "s%n", "Key", "DefaultsTo"));
                builder.append("-".repeat(width + width + 3)).append("\n");
                builder.append(String.format("%-" + width + "s | %-" + width + "s%n", key, value == null ? "null" : value));
            });
            throw new RuntimeException(builder.toString());
        }

        Properties properties = new Properties();
        File parent = new File(new File("").getAbsolutePath());
        File propertiesFile = new File(parent, "service.properties");
        if (!propertiesFile.exists())
            propertiesFile.createNewFile();

        Map<String, String> data = new HashMap<>();

        try (FileInputStream input = new FileInputStream(propertiesFile)) {
            properties.load(input);
            keyOrDefaultsMap.forEach((key, defaultsTo) -> {
                String property = properties.getProperty(key, defaultsTo);
                properties.setProperty(key, property);
                data.put(key, property);
            });
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        try (FileOutputStream outputStream = new FileOutputStream("service.properties")) {
            properties.store(outputStream, null);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return data;
    }
}
