package me.ichun.mods.googlyeyes.common;

import me.ichun.mods.googlyeyes.common.core.Config;
import me.ichun.mods.googlyeyes.common.core.EventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(name = GooglyEyes.name, modid = GooglyEyes.modid, version = GooglyEyes.version,
        clientSideOnly = true,
        acceptableRemoteVersions = "*",
        dependencies = "required-after:forge@[13.20.0.2214,)"
)
public class GooglyEyes
{
    public static final String version = "6.0.0";

    public static final String name = "GooglyEyes";
    public static final String modid = "googlyeyes";

    public static EventHandler eventHandler;

    public static Config config;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        GooglyEyes.config = new Config();
        GooglyEyes.config.disabledGoogly = config.getStringList("disabledGoogly", "general", GooglyEyes.config.disabledGoogly, "To disable Googly Eyes on a specific entity, put their registry name in here.\nFor Players, put \"player\"");
        GooglyEyes.config.acidTripEyes = config.getInt("acidTripEyes", "general", GooglyEyes.config.acidTripEyes, 0, 1, "Let them googly eyes be trippin'");
        GooglyEyes.config.googlyEyeChance = config.getInt("googlyEyeChance", "general", GooglyEyes.config.googlyEyeChance, 0, 100, "Googly Eye chance (in %)");

        if(config.hasChanged())
        {
            config.save();
        }

        eventHandler = new EventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);
    }
}
