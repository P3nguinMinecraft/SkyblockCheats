package com.sbc.util;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.text.Text;

public class ListenerManager {
	// Title
    private static final HashMap<UUID, Consumer<Text>> titleListeners = new HashMap<>();
    
    public static UUID registerTitleListener(Consumer<Text> consumer) {
        UUID uuid = UUID.randomUUID();
        titleListeners.put(uuid, consumer);
        return uuid;
    }
    
    public static void unregisterTitleListener(UUID uuid) {
		titleListeners.remove(uuid);
	}

    public static void onTitle(Text title) {
        titleListeners.values().forEach(listener -> listener.accept(title));
    }

    // Subtitle
    private static final HashMap<UUID, Consumer<Text>> subtitleListeners = new HashMap<>();
    
    public static UUID registerSubtitleListener(Consumer<Text> consumer) {
        UUID uuid = UUID.randomUUID();
        subtitleListeners.put(uuid, consumer);
        return uuid;
    }
    
    public static void unregisterSubtitleListener(UUID uuid) {
    	subtitleListeners.remove(uuid);
    }

    public static void onSubtitle(Text subtitle) {
        subtitleListeners.values().forEach(listener -> listener.accept(subtitle));
    }

    // Overlay
    private static final HashMap<UUID, Consumer<Text>> overlayListeners = new HashMap<>();

    public static UUID registerOverlayListener(Consumer<Text> consumer) {
        UUID uuid = UUID.randomUUID();
        overlayListeners.put(uuid, consumer);
        return uuid;
    }

    public static void unregisterOverlayListener(UUID uuid) {
        overlayListeners.remove(uuid);
    }

    public static void onOverlay(Text overlayMessage) {
        overlayListeners.values().forEach(listener -> listener.accept(overlayMessage));
    }
    
    // SoundInstance
    private static final HashMap<UUID, Consumer<SoundInstance>> soundListeners = new HashMap<>();
    
    public static UUID registerSoundListener(Consumer<SoundInstance> consumer) {
        UUID uuid = UUID.randomUUID();
        soundListeners.put(uuid, consumer);
        return uuid;
    }
    
    public static void unregisterSoundListener(UUID uuid) {
		soundListeners.remove(uuid);
	}
    
    public static void onSound(SoundInstance sound) {
        soundListeners.values().forEach(listener -> listener.accept(sound));
    }
    
    
    // Message
    private static final HashMap<UUID, Consumer<String>> messageListeners = new HashMap<>();
    
    public static UUID registerMessageListener(Consumer<String> consumer) {
        UUID uuid = UUID.randomUUID();
       	messageListeners.put(uuid, consumer);
        return uuid;
    }
    
    public static void unregisterMessageListener(UUID uuid) {
		messageListeners.remove(uuid);
	}
    
    public static void onMessage(String msg) {
        messageListeners.values().forEach(listener -> listener.accept(msg));
    }
}