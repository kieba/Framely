package com.rk.framely.handler;

import com.rk.framely.reference.Reference;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigurationHandler {

    public static Configuration cfg;
    public static boolean testValue;

    public static void init(File configFile) {
        cfg = new Configuration(configFile);
        loadConfig();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if(event.modID.equalsIgnoreCase(Reference.MOD_ID)) {
            loadConfig();
        }
    }

    private static void loadConfig() {
        testValue = cfg.get(Configuration.CATEGORY_GENERAL, "testValue", true, "Just a test value.").getBoolean();

        if(cfg.hasChanged()) {
            cfg.save();
        }
    }
}
