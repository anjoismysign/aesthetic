package me.anjoismysign.aesthetic;

import java.util.Locale;

public class NamingConventions {
  public static String toCamelCase(String input) {
    input = input.replaceAll("[^a-zA-Z0-9]", "");
    String[] words = input.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    StringBuilder result = new StringBuilder(words[0].toLowerCase(Locale.ROOT));
    for (int i = 1; i < words.length; i++)
      result.append(words[i].substring(0, 1).toUpperCase(Locale.ROOT))
        .append(words[i].substring(1).toLowerCase(Locale.ROOT)); 
    return result.toString();
  }
  
  public static String toPascalCase(String input) {
    input = input.replaceAll("[^a-zA-Z0-9]", "");
    String[] words = input.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    StringBuilder result = new StringBuilder();
    for (String word : words)
      result.append(word.substring(0, 1).toUpperCase(Locale.ROOT))
        .append(word.substring(1).toLowerCase(Locale.ROOT)); 
    return result.toString();
  }
  
  public static String toSnakeCase(String input) {
    input = input.replaceAll("[^a-zA-Z0-9]", "");
    String[] words = input.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    StringBuilder result = new StringBuilder();
    for (String word : words)
      result.append(word.toLowerCase(Locale.ROOT)).append("_"); 
    result.setLength(result.length() - 1);
    return result.toString();
  }
  
  public static String toScreamingSnakeCase(String input) {
    input = input.replaceAll("[^a-zA-Z0-9]", "");
    String[] words = input.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    StringBuilder result = new StringBuilder();
    for (String word : words)
      result.append(word.toUpperCase(Locale.ROOT)).append("_"); 
    result.setLength(result.length() - 1);
    return result.toString();
  }
  
  public static String toKebabCase(String input) {
    input = input.replaceAll("[^a-zA-Z0-9]", "");
    String[] words = input.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    StringBuilder result = new StringBuilder();
    for (String word : words)
      result.append(word.toLowerCase(Locale.ROOT)).append("-"); 
    result.setLength(result.length() - 1);
    return result.toString();
  }
  
  public static String toScreamingKebabCase(String input) {
    input = input.replaceAll("[^a-zA-Z0-9]", "");
    String[] words = input.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    StringBuilder result = new StringBuilder();
    for (String word : words)
      result.append(word.toUpperCase(Locale.ROOT)).append("-"); 
    result.setLength(result.length() - 1);
    return result.toString();
  }
}
