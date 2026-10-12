package com.orca.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Original Orca Client GUI with a soft scale-in animation and animated bubbles. */
public final class OrcaClickGui extends Screen {
    private int panelX, panelY;
    private final int panelW = 470, panelH = 306;
    private boolean dragging;
    private int dragOffsetX, dragOffsetY;
    private String category = "Combat";
    private float openProgress = 0.0f;
    private float elapsed = 0.0f;
    private final List<Bubble> bubbles = new ArrayList<>();
    private final List<ModuleToggle> modules = new ArrayList<>();

    private static final String[] CATEGORIES = {"Combat", "Movement", "Render", "Player", "World", "HUD", "Settings"};

    public OrcaClickGui() {
        super(Component.literal("Orca Client"));
        modules.add(new ModuleToggle("Target HUD", "A clean target information panel", false, "Combat"));
        modules.add(new ModuleToggle("Click Assist", "Example toggle only; no automation", false, "Combat"));
        modules.add(new ModuleToggle("Sprint Display", "Show movement status in the HUD", true, "Movement"));
        modules.add(new ModuleToggle("Motion Trails", "Ocean-inspired visual accent", false, "Render"));
        modules.add(new ModuleToggle("Coordinates", "Display player coordinates", true, "Player"));
        modules.add(new ModuleToggle("Water Theme", "Use Orca's ocean palette", true, "Settings"));
        modules.add(new ModuleToggle("FPS Counter", "Display frame rate in the HUD", true, "HUD"));
        modules.add(new ModuleToggle("Clock", "Display local play-session time", false, "HUD"));
        modules.add(new ModuleToggle("Environment Info", "A home for world information", false, "World"));
        Random random = new Random(2411);
        for (int i = 0; i < 34; i++) bubbles.add(new Bubble(random.nextFloat(), random.nextFloat(), 0.12f + random.nextFloat() * 0.34f, random.nextFloat() * 6.28f));
    }

    @Override
    protected void init() {
        panelX = Math.max(8, (width - panelW) / 2);
        panelY = Math.max(8, (height - panelH) / 2);
        openProgress = 0.0f;
        elapsed = 0.0f;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        elapsed += Math.max(0.0f, delta);
        openProgress = Math.min(1.0f, openProgress + Math.max(0.0f, delta) / 12.0f);
        float t = 1.0f - (float) Math.pow(1.0f - openProgress, 3.0);
        float scale = 0.88f + 0.12f * t;
        int dimAlpha = Math.min(150, Math.round(150 * t));
        g.fill(0, 0, width, height, (dimAlpha << 24) | 0x00040C16);

        // Subtle moving bubbles behind the window.
        for (Bubble bubble : bubbles) {
            float y = (bubble.y * height - elapsed * bubble.speed * 2.0f) % (height + 12);
            if (y < 0) y += height + 12;
            int x = (int) (bubble.x * width + (float) Math.sin(elapsed * 0.035f + bubble.phase) * 9.0f);
            int by = (int) y;
            g.fill(x, by, x + 2, by + 2, 0x6639D9EF);
        }

        g.pose().pushPose();
        float cx = panelX + panelW / 2.0f;
        float cy = panelY + panelH / 2.0f;
        g.pose().translate(cx, cy, 0);
        g.pose().scale(scale, scale, 1.0f);
        g.pose().translate(-cx, -cy, 0);

        // Shadow layers and glass-like panel.
        g.fill(panelX - 4, panelY - 4, panelX + panelW + 4, panelY + panelH + 4, 0x4429CBE8);
        g.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xF0091725);
        g.fill(panelX, panelY, panelX + panelW, panelY + 3, 0xFF32D8EE);
        g.fill(panelX, panelY + 3, panelX + panelW, panelY + 48, 0xF0193042);
        g.fill(panelX + 12, panelY + 12, panelX + 37, panelY + 37, 0xFF0D5369);
        g.drawString(font, "O", panelX + 20, panelY + 18, 0xFFB9F7FF, false);
        g.drawString(font, "ORCA", panelX + 47, panelY + 11, 0xFF73ECF8, false);
        g.drawString(font, "OCEAN SYSTEMS  /  1.21.11", panelX + 47, panelY + 27, 0xFFB5D1DF, false);
        g.drawString(font, "RIGHT SHIFT  ·  CLOSE", panelX + panelW - 145, panelY + 18, 0xFF91ADBC, false);

        int sideX = panelX + 12;
        int sideY = panelY + 60;
        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = sideY + i * 30;
            boolean selected = category.equals(CATEGORIES[i]);
            g.fill(sideX, y - 4, sideX + 119, y + 20, selected ? 0xFF16485B : 0x88203648);
            if (selected) g.fill(sideX, y - 4, sideX + 3, y + 20, 0xFF39DDF0);
            g.drawString(font, CATEGORIES[i], sideX + 11, y + 4, selected ? 0xFFFFFFFF : 0xFF9EB6C5, false);
        }

        int contentX = panelX + 146;
        g.drawString(font, category.toUpperCase() + " MODULES", contentX, panelY + 61, 0xFF6BE4F1, false);
        g.drawString(font, "Select a card to toggle its state", contentX, panelY + 77, 0xFF829EAE, false);
        int shown = 0;
        for (ModuleToggle m : modules) {
            if (!m.category.equals(category)) continue;
            int y = panelY + 101 + shown * 49;
            if (y + 38 > panelY + panelH - 30) break;
            g.fill(contentX, y, panelX + panelW - 14, y + 39, 0xCC112838);
            g.drawString(font, m.name, contentX + 10, y + 7, 0xFFE7F6FA, false);
            g.drawString(font, m.description, contentX + 10, y + 22, 0xFF8DAAB9, false);
            int sx = panelX + panelW - 53;
            g.fill(sx, y + 11, sx + 29, y + 27, m.enabled ? 0xFF168FA4 : 0xFF334B5A);
            g.fill(m.enabled ? sx + 16 : sx + 2, y + 13, m.enabled ? sx + 27 : sx + 13, y + 25, 0xFFFFFFFF);
            shown++;
        }
        if (shown == 0) {
            g.drawString(font, category.equals("Settings") ? "Theme options will live here." : "More Orca modules coming soon.", contentX, panelY + 111, 0xFF9EB6C5, false);
        }
        g.fill(panelX + 12, panelY + panelH - 29, panelX + panelW - 12, panelY + panelH - 28, 0x5539DDF0);
        g.drawString(font, "ORCA CLIENT  •  FABRIC  •  PREVIEW BUILD", panelX + 14, panelY + panelH - 19, 0xFF5C7C8E, false);
        g.drawString(font, "v0.2.0", panelX + panelW - 54, panelY + panelH - 19, 0xFF5C7C8E, false);
        g.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        if (openProgress < 0.85f) return true;
        if (inside(mouseX, mouseY, panelX, panelY, panelX + panelW, panelY + 48)) {
            dragging = true; dragOffsetX = (int) mouseX - panelX; dragOffsetY = (int) mouseY - panelY; return true;
        }
        int sideX = panelX + 12, sideY = panelY + 60;
        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = sideY + i * 30;
            if (inside(mouseX, mouseY, sideX, y - 4, sideX + 119, y + 20)) { category = CATEGORIES[i]; return true; }
        }
        int contentX = panelX + 146;
        int shown = 0;
        for (ModuleToggle m : modules) {
            if (!m.category.equals(category)) continue;
            int y = panelY + 101 + shown * 49;
            if (y + 38 > panelY + panelH - 30) break;
            if (inside(mouseX, mouseY, contentX, y, panelX + panelW - 14, y + 39)) { m.enabled = !m.enabled; return true; }
            shown++;
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging && button == 0) {
            panelX = Math.max(0, Math.min(width - panelW, (int) mouseX - dragOffsetX));
            panelY = Math.max(0, Math.min(height - panelH, (int) mouseY - dragOffsetY));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override public boolean isPauseScreen() { return false; }

    private static boolean inside(double x, double y, int x1, int y1, int x2, int y2) {
        return x >= x1 && x < x2 && y >= y1 && y < y2;
    }

    private static final class ModuleToggle {
        final String name, description, category;
        boolean enabled;
        ModuleToggle(String name, String description, boolean enabled, String category) {
            this.name = name; this.description = description; this.enabled = enabled; this.category = category;
        }
    }

    private static final class Bubble {
        final float x, y, speed, phase;
        Bubble(float x, float y, float speed, float phase) { this.x = x; this.y = y; this.speed = speed; this.phase = phase; }
    }
}
