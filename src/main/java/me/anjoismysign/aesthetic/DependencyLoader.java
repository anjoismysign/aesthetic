package me.anjoismysign.aesthetic;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;
import java.util.function.Consumer;

public enum DependencyLoader {
    INSTANCE;

    public <T> void load(Consumer<T> loadConsumer, Class<T> serviceInterface, File addonsDirectory) {
        File[] jars = DirectoryAssistant.of(addonsDirectory).listFiles("jar");
        if (jars == null) return;
        try {
            URL[] urls = new URL[jars.length];
            for (int index = 0; index < jars.length; index++) {
                urls[index] = jars[index].toURI().toURL();
            }
            try (URLClassLoader classLoader = new URLClassLoader(urls, DependencyLoader.class.getClassLoader())) {
                ServiceLoader<T> loader = ServiceLoader.load(serviceInterface, classLoader);
                for (T service : loader) {
                    loadConsumer.accept(service);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
