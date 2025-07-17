package com.sbc.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.jetbrains.annotations.Nullable;

public class TextUtils {
    public static String toString(Text text){
        Style style = text.getStyle();
        String color = style.getColor() != null ? style.getColor().getName() : "none";
        boolean bold = style.isBold();
        boolean italic = style.isItalic();
        boolean underlined = style.isUnderlined();
        boolean strikethrough = style.isStrikethrough();
        boolean obfuscated = style.isObfuscated();

        return String.format(
                "Text: \"%s\" → color: %s, bold: %b, italic: %b, underline: %b, strikethrough: %b, obfuscated: %b",
                text.getString(), color, bold, italic, underlined, strikethrough, obfuscated
        );
    }

    @Nullable
    public static String getNestedColor(Text text) {
        JsonElement element = TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, text).result().orElse(null);
        if (element == null || !element.isJsonObject()) return null;

        JsonObject root = element.getAsJsonObject();

        // access root.extra[0].extra[0].color
        if (!root.has("extra")) return null;
        JsonElement firstExtra = root.getAsJsonArray("extra").get(0);
        if (!firstExtra.isJsonObject()) return null;
        JsonObject extra1 = firstExtra.getAsJsonObject();

        if (!extra1.has("extra")) return null;
        JsonElement secondExtra = extra1.getAsJsonArray("extra").get(0);
        if (!secondExtra.isJsonObject()) return null;
        JsonObject extra2 = secondExtra.getAsJsonObject();

        return extra2.has("color") ? extra2.get("color").getAsString() : null;
    }

}
