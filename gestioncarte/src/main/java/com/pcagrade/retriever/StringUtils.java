package com.pcagrade.retriever;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtils {

    public static String removeAccents(String input) {
        if (input == null) return null;

        // Normalise la chaîne (ex: 'à' → 'a' + accent)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        // Supprime tous les caractères d’accent (diacritiques)
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    // Exemple d'utilisation
    public static void main(String[] args) {
        String texte = "àéèçôùïëñ";
        System.out.println(removeAccents(texte));  // Affiche : aeeecouens
    }
}
