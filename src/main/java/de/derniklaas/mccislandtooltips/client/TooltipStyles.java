package de.derniklaas.mccislandtooltips.client;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

public final class TooltipStyles {

    /**
     * Styles with a dedicated border, most important first: when an item is marked with
     * several glyphs (e.g. arcane badge + rarity icon), the earliest entry here wins.
     */
    private static final String[] STYLE_PRIORITY = {"arcane", "mythic", "legendary", "epic", "rare", "uncommon", "common"};
    private static final Set<String> STYLE_NAMES = Set.of(STYLE_PRIORITY);

    private static final String MCC_NAMESPACE = "mcc";
    private static final String STYLE_GLYPH_PATH_PREFIX = "_fonts/icon/tooltips/";

    private static final Identifier DEFAULT_STYLE = style("default");

    /**
     * Codepoint of a style glyph from MCC's custom font -> style name.
     * Populated from the server resource pack's font definitions as they load,
     * so it stays correct when MCC reassigns codepoints in a pack update.
     */
    private static final Map<Integer, String> STYLE_GLYPHS = new ConcurrentHashMap<>();

    private TooltipStyles() {
    }

    /**
     * Called from {@code BitmapFontDefinitionMixin} for every bitmap font provider that loads.
     * Remembers the codepoints of MCC's "mcc:_fonts/icon/tooltips/(style).png" glyphs.
     */
    public static void registerFontGlyphs(Identifier file, int[][] codepointGrid) {
        if (!file.getNamespace().equals(MCC_NAMESPACE) || !file.getPath().startsWith(STYLE_GLYPH_PATH_PREFIX)) {
            return;
        }
        String name = file.getPath().substring(STYLE_GLYPH_PATH_PREFIX.length());
        if (name.endsWith(".png")) {
            name = name.substring(0, name.length() - 4);
        }
        if (!STYLE_NAMES.contains(name)) {
            return;
        }
        for (int[] row : codepointGrid) {
            for (int codepoint : row) {
                STYLE_GLYPHS.put(codepoint, name);
            }
        }
    }

    /**
     * The tooltip style for a tooltip that carries no style of its own, from the text it renders:
     * {@code show_text} hover events, widget tooltips and tooltips a mod renders itself.
     * {@code null} leaves the tooltip untouched.
     */
    public static Identifier styleFor(List<? extends FormattedCharSequence> lines) {
        if (!MCCIslandTooltipsClient.isOnMCCIsland()) {
            return null;
        }
        Set<String> found = new HashSet<>();
        for (FormattedCharSequence line : lines) {
            line.accept((index, textStyle, codepoint) -> {
                String name = STYLE_GLYPHS.get(codepoint);
                if (name != null) {
                    found.add(name);
                }
                return true;
            });
        }
        for (String name : STYLE_PRIORITY) {
            if (found.contains(name)) {
                return style(name);
            }
        }
        return DEFAULT_STYLE;
    }

    private static Identifier style(String name) {
        return Identifier.fromNamespaceAndPath("mccislandtooltips", name);
    }
}
