package me.anjoismysign.aesthetic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DirectoryAssistant extends FileDecorator {
  static DirectoryAssistant desktop() {
    File desktop = new File(System.getProperty("user.home") + System.getProperty("user.home") + "Desktop");
    return of(desktop);
  }
  
  static DirectoryAssistant of(@NotNull final File file) {
    Objects.requireNonNull(file, "File cannot be null");
    String path = file.getPath();
    if (!file.exists())
      throw new IllegalArgumentException("File does not exist: " + path); 
    if (!file.isDirectory())
      throw new IllegalArgumentException("File is not a directory: " + path); 
    return new DirectoryAssistant() {
        @NotNull
        public File file() {
          return file;
        }
      };
  }

  File file();
  
  default File[] listDirectories() {
    return file().listFiles(File::isDirectory);
  }
  
  default File[] listFiles() {
    return file().listFiles(File::isFile);
  }
  
  default File[] listFiles(@NotNull String extension) {
    Objects.requireNonNull(extension, "Extension cannot be null");
    return file().listFiles((dir, name) -> name.endsWith(extension));
  }
  
  default Collection<File> listRecursively(@NotNull String... extensions) {
    Objects.requireNonNull(extensions, "Extensions cannot be null");
    List<File> result = new ArrayList<>();
    listRecursivelyHelper(file(), result, extensions);
    return result;
  }
  
  private void listRecursivelyHelper(@NotNull File dir, List<File> result, String[] extensions) {
    File[] files = dir.listFiles();
    if (files != null)
      for (File file : files) {
        if (file.isDirectory()) {
          listRecursivelyHelper(file, result, extensions);
        } else {
          for (String extension : extensions) {
            if (file.getName().endsWith(extension)) {
              result.add(file);
              break;
            } 
          } 
        } 
      }  
  }
  
  default boolean deleteRecursively(@Nullable Consumer<IOException> ifError) {
    Path directory = file().toPath();
    try {
      Stream<Path> walk = Files.walk(directory, new java.nio.file.FileVisitOption[0]);
      try {
        walk.sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .forEach(File::delete);
        boolean bool = true;
        if (walk != null)
          walk.close(); 
        return bool;
      } catch (Throwable throwable) {
        if (walk != null)
          try {
            walk.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (IOException exception) {
      if (ifError != null)
        ifError.accept(exception); 
      return false;
    } 
  }
  
  default boolean deleteRecursively() {
    return deleteRecursively(null);
  }
  
  default void copy(@NotNull File directory, boolean cut, @NotNull String... extensions) {
    Objects.requireNonNull(directory, "'directory' cannot be null");
    File originalDirectory = file();
    String originalDirectoryPath = originalDirectory.getPath();
    String fileSeparator = File.separator;
    listRecursively(extensions).forEach(file -> {
          String filePath = file.getPath();
          String trimmedPath = filePath.replace(originalDirectoryPath + originalDirectoryPath, "");
          File replacement = new File(directory, trimmedPath);
          replacement.getParentFile().mkdirs();
          if (!replacement.exists())
            try {
              replacement.createNewFile();
            } catch (IOException exception) {
              exception.printStackTrace();
            }  
          Path replacementPath = (new File(directory, trimmedPath)).toPath();
          try {
            String replacementContents = Files.readString(file.toPath());
            Files.writeString(replacementPath, replacementContents, new java.nio.file.OpenOption[0]);
            if (cut && !file.delete())
              throw new RuntimeException("Couldn't delete: " + filePath); 
          } catch (IOException exception) {
            exception.printStackTrace();
          } 
        });
    if (isEmpty() && 
      !originalDirectory.delete())
      throw new RuntimeException("Couldn't delete 'originalDirectory'"); 
  }
  
  default boolean isEmpty() {
    File[] files = file().listFiles((d, name) -> 
        (!name.equals("__MACOSX") && !name.equals(".DS_Store")));
    if (files == null)
      return false; 
    return (files.length == 0);
  }
}
