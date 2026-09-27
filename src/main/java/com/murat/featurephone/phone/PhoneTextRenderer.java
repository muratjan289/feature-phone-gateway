package com.murat.featurephone.phone;

import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * Adapts Unicode text for display on legacy feature-phone browsers
 * with limited character and font support.
 *
 * <p>The target browser cannot reliably render Cyrillic and many Unicode
 * characters, so this renderer prepares text before it is sent to the UI:</p
 *
 * <ul>
 *     <li>transliterates Cyrillic characters to Latin;</li>
 *     <li>converts a small set of supported emoji to ASCII equivalents;</li>
 *     <li>removes unsupported Unicode characters;</li>
 *     <li>preserves standard printable ASCII characters.</li>
 * </ul>
 *
 * <p>This transformation is intended for display only.
 * Original Telegram data must remain unchanged.</p>
 */
@Component
public class PhoneTextRenderer {

    //Just 5 most popular emoji to translate  to latin
    private static final Map<String, String> EMOJI = Map.of(
            "😂", ":D",
            "❤", "<3",
            "👍", "+1",
            "😊", ":)",
            "😢", ":("
    );

    private static final Map<Integer, String> CYRILLIC = Map.ofEntries(
            Map.entry((int) 'А', "A"),  Map.entry((int) 'а', "a"),
            Map.entry((int) 'Б', "B"),  Map.entry((int) 'б', "b"),
            Map.entry((int) 'В', "V"),  Map.entry((int) 'в', "v"),
            Map.entry((int) 'Г', "G"),  Map.entry((int) 'г', "g"),
            Map.entry((int) 'Д', "D"),  Map.entry((int) 'д', "d"),
            Map.entry((int) 'Е', "E"),  Map.entry((int) 'е', "e"),
            Map.entry((int) 'Ё', "Yo"), Map.entry((int) 'ё', "yo"),
            Map.entry((int) 'Ж', "Zh"), Map.entry((int) 'ж', "zh"),
            Map.entry((int) 'З', "Z"),  Map.entry((int) 'з', "z"),
            Map.entry((int) 'И', "I"),  Map.entry((int) 'и', "i"),
            Map.entry((int) 'Й', "Y"),  Map.entry((int) 'й', "y"),
            Map.entry((int) 'К', "K"),  Map.entry((int) 'к', "k"),
            Map.entry((int) 'Л', "L"),  Map.entry((int) 'л', "l"),
            Map.entry((int) 'М', "M"),  Map.entry((int) 'м', "m"),
            Map.entry((int) 'Н', "N"),  Map.entry((int) 'н', "n"),
            Map.entry((int) 'О', "O"),  Map.entry((int) 'о', "o"),
            Map.entry((int) 'П', "P"),  Map.entry((int) 'п', "p"),
            Map.entry((int) 'Р', "R"),  Map.entry((int) 'р', "r"),
            Map.entry((int) 'С', "S"),  Map.entry((int) 'с', "s"),
            Map.entry((int) 'Т', "T"),  Map.entry((int) 'т', "t"),
            Map.entry((int) 'У', "U"),  Map.entry((int) 'у', "u"),
            Map.entry((int) 'Ф', "F"),  Map.entry((int) 'ф', "f"),
            Map.entry((int) 'Х', "Kh"), Map.entry((int) 'х', "kh"),
            Map.entry((int) 'Ц', "Ts"), Map.entry((int) 'ц', "ts"),
            Map.entry((int) 'Ч', "Ch"), Map.entry((int) 'ч', "ch"),
            Map.entry((int) 'Ш', "Sh"), Map.entry((int) 'ш', "sh"),
            Map.entry((int) 'Щ', "Sch"),Map.entry((int) 'щ', "sch"),
            Map.entry((int) 'Ъ', ""),   Map.entry((int) 'ъ', ""),
            Map.entry((int) 'Ы', "Y"),  Map.entry((int) 'ы', "y"),
            Map.entry((int) 'Ь', ""),   Map.entry((int) 'ь', ""),
            Map.entry((int) 'Э', "E"),  Map.entry((int) 'э', "e"),
            Map.entry((int) 'Ю', "Yu"), Map.entry((int) 'ю', "yu"),
            Map.entry((int) 'Я', "Ya"), Map.entry((int) 'я', "ya")
    );

    public String render(String text) {

        if (text == null || text.isBlank()) {
            return "";
        }

        // Сначала сохраняем нужные нам emoji,
        // превращая их в обычный ASCII.
        String normalized = text;

        for (Map.Entry<String, String> entry : EMOJI.entrySet()) {
            normalized = normalized.replace(
                    entry.getKey(),
                    entry.getValue()
            );
        }

        StringBuilder result = new StringBuilder();

        // Идём по Unicode code points, а не char.
        normalized.codePoints().forEach(codePoint -> {

            // Русская буква?
            String transliterated = CYRILLIC.get(codePoint);

            if (transliterated != null) {
                result.append(transliterated);
                return;
            }

            // Обычный безопасный ASCII.
            if (codePoint >= 32 && codePoint <= 126) {
                result.appendCodePoint(codePoint);
            }

            // Всё остальное просто выбрасываем.
            // Например: 🚀🔥🥰🤡 и т.д.
        });

        return result.toString()
                .replaceAll("[ ]{2,}", " ")
                .trim();
    }
}