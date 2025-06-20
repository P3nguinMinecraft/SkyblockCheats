package com.sbc.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class Config {
    private static final Map<String, Object> config = new HashMap<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File configFile = new File("config/skyblockcheats/config.json");
    private static ArrayList<String> validKeys = new ArrayList<>();
    public static void init() {
        loadConfig();
    	defaults();
    	cleanConfig();
        saveConfig();
    }
    
    public static void saveConfig() {
        Map<String, Map<String, Object>> wrapped = new HashMap<>();
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            Object value = entry.getValue();
            Map<String, Object> wrapper = new HashMap<>();

            if (value instanceof Integer) {
				wrapper.put("type", "int");
			} else if (value instanceof Boolean) {
				wrapper.put("type", "boolean");
			} else if (value instanceof Double) {
				wrapper.put("type", "double");
			} else if (value instanceof Float) {
				wrapper.put("type", "float");
			} else if (value instanceof Long) {
				wrapper.put("type", "long");
			} else if (value instanceof String) {
				wrapper.put("type", "string");
			} else {
				continue;
			}

            wrapper.put("value", value);
            wrapped.put(entry.getKey(), wrapper);
        }

        configFile.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(wrapped, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig() {
        if (!configFile.exists()) {
			return;
		}

        try (FileReader reader = new FileReader(configFile)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            for (String key : json.keySet()) {
                JsonObject wrapper = json.getAsJsonObject(key);
                String type = wrapper.get("type").getAsString();
                JsonElement valueElement = wrapper.get("value");

                Object value;
                switch (type) {
                    case "int":
                        value = valueElement.getAsInt();
                        break;
                    case "boolean":
                        value = valueElement.getAsBoolean();
                        break;
                    case "double":
                        value = valueElement.getAsDouble();
                        break;
                    case "float":
                        value = valueElement.getAsFloat();
                        break;
                    case "long":
                        value = valueElement.getAsLong();
                        break;
                    case "string":
                        value = valueElement.getAsString();
                        break;
                    default:
                        continue;
                }

                config.put(key, value);
            }
        }
        catch (IOException e) {
			e.printStackTrace();
        }
    }
    
    public static void defaults() {
    	validKeys.clear();
        setDefault("delay", 0.0f);
        setDefault("rgba-block-color", "255-103-103-0.6");
        setDefault("solid-highlight", true);
        setDefault("outline-weight", 10.0f);
        setDefault("ping-on-found", true);
        setDefault("ping-sound", "minecraft:block.anvil.land");
        setDefault("ping-volume", 1.0f);
        setDefault("ping-pitch", 1.0f);
        setDefault("warp-in", "ch");
        setDefault("warp-out", "forge");
        setDefault("filter-Y", false);
        setDefault("filter-Y-max", 64);
        setDefault("auto-melody", false);
        setDefault("ghost-block", false);
        setDefault("max-log-time", 600);
        setDefault("left-click-cps", 10.0f);
        setDefault("right-click-cps", 10.0f);
        setDefault("auto-impel", false);
        setDefault("impel-delay", 0.3f);
        setDefault("impel-rate", 0.5f);
        setDefault("beachball-predictor", true);
        setDefault("auto-beachball", false);
        setDefault("fullauto-beachball", false);
    }
    
    private static void setDefault(String key, Object value) {
    	validKeys.add(key);
		if (!isValid(key)) config.put(key, value);
	}
    
    private static void cleanConfig() {
        Iterator<String> iter = config.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            if (!validKeys.contains(key)) {
                iter.remove();
            }
        }
    }

    public static boolean setConfig(String key, Object value) {
        if (!config.containsKey(key)) {
        	ChatUtils.sendMessage("§cInvalid config key: " + key);
			return false;
        }
        if (getConfig(key) instanceof Integer && (int) value < 0) {
        	ChatUtils.sendMessage("§cInvalid value: " + value + ". Expected value >= 0");
        	return false;
        }
        if (getConfig(key) instanceof Float && (float) value < 0f) {
        	ChatUtils.sendMessage("§cInvalid value: " + value + ". Expected value >= 0");
        	return false;
        }
        if (key.equals("rgbaBlockColor")) {
			String[] parts = value.toString().split("-");
			if (parts.length < 3 || parts.length > 4) {
				ChatUtils.sendMessage("§cInvalid rgbaBlockColor format. Expected format: r-g-b-a Got " + parts.toString());
				return false;
			}
			Float r = Float.parseFloat(parts[0].trim());
			Float g = Float.parseFloat(parts[1].trim());
			Float b = Float.parseFloat(parts[2].trim());
			Float a = parts.length == 4 ? Float.parseFloat(parts[3].trim()) : 1.0f;
			if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255 || a < 0 || a > 1) {
				ChatUtils.sendMessage("§cInvalid rgbaBlockColor values. Expected values: r(0-255)-g(0-255)-b(0-255)-a(0-1) Got " + r + "-" + g + "-" + b + "-" + a);
				return false;
			}
			for (String part : parts) {
				try {
					Float.parseFloat(part.trim());
				} catch (NumberFormatException e) {
					ChatUtils.sendMessage("§cInvalid rgbaBlockColor value: " + part);
					return false;
				}
			}
		}

        if (key.equals("outlineWeight")) {
        	float val = (float) value;
        	if (val < 0.0 || val > 1.0) {
        		ChatUtils.sendMessage("§cInvalid outlineWeight value. Expected value: 0.0-1.0 Got " + val);
        		return false;
        	}
        }
        if (key.equals("pingSound")) {
			String soundId = (String) value;
			if (!soundId.startsWith("minecraft:")) {
				soundId = "minecraft:" + soundId;
				value = soundId;
			}

            Identifier id = Identifier.tryParse(soundId);
            SoundEvent soundEvent = Registries.SOUND_EVENT.get(id);
            
            if (soundEvent == null) {
                ChatUtils.sendMessage("§cSound not found: " + soundId);
                return false;
        	}
        	
        }
        if (key.equals("left-click-cps") || key.equals("right-click-cps")) {
			float cps = (float) value;
			if (cps > 20.0f) {
				ChatUtils.sendMessage("§cCPS cannot be above 20 for safety reasons.");
				return false;
			}
		}

        config.put(key, value);
		defaults();
        saveConfig();

        return true;
    }

    public static void resetConfig(String key) {
		if (config.containsKey(key)) {
			config.remove(key);
			defaults();
			saveConfig();
		}
		else {
			ChatUtils.sendMessage("§cInvalid config key: " + key);
		}
	}

    public static Object getConfig(String key) {
        return config.get(key);
    }


    public static Set<String> getAllKeys() {
        return config.keySet();
    }

    public static boolean isValid(String key) {
        return config.containsKey(key);
    }
    
    public static void resetAllConfigs() {
		config.clear();
		defaults();
		saveConfig();
		ChatUtils.sendMessage("§aAll configs have been reset to default values.");
    }

}
