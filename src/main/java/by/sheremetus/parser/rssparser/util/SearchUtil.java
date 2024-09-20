package by.sheremetus.parser.rssparser.util;

public class SearchUtil {

    // Метод отвечающий за парсинг текста
    public static boolean isParseTextHasKey(String textData, String key) {

        textData = textData.toLowerCase();
        key = key.toLowerCase();

        if (key.contains(",")) {

            String[] keyArray = key.split(",");

            for (String k : keyArray) {

                if (textData.contains(k)) {
                    return true;
                }
            }

        } else if (textData.contains(key.trim())) {
            return true;
        }
        return false;
    }
}
