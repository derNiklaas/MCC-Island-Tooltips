package de.derniklaas.mccislandtooltips.client;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

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
     * The tooltip style to use for this stack, or {@code null} to leave the tooltip untouched.
     * Only applies while connected to MCC Island and never overrides a style set by the server.
     */
    public static Identifier styleFor(ItemStack stack) {
        if (!MCCIslandTooltipsClient.isOnMCCIsland() || stack.has(DataComponents.TOOLTIP_STYLE)) {
            return null;
        }
        String name = detectStyle(stack);
        return name != null ? style(name) : DEFAULT_STYLE;
    }

    private static String detectStyle(ItemStack stack) {
        Set<String> found = new HashSet<>();
        collectStyles(stack.getHoverName().getString(), found);
        ItemLore lore = stack.get(DataComponents.LORE);
        if (lore != null) {
            for (Component line : lore.lines()) {
                collectStyles(line.getString(), found);
            }
        }
        if (found.isEmpty()) {
            return null;
        }
        for (String name : STYLE_PRIORITY) {
            if (found.contains(name)) {
                return name;
            }
        }
        return null;
    }

    private static void collectStyles(String line, Set<String> found) {
        if (STYLE_GLYPHS.isEmpty()) {
            return;
        }
        for (int i = 0; i < line.length(); ) {
            int codepoint = line.codePointAt(i);
            String name = STYLE_GLYPHS.get(codepoint);
            if (name != null) {
                found.add(name);
            }
            i += Character.charCount(codepoint);
        }
    }

    private static Identifier style(String name) {
        return Identifier.fromNamespaceAndPath("mccislandtooltips", name);
    }
}
