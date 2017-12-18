package me.ichun.mods.googlyeyes.common;

import me.ichun.mods.googlyeyes.common.core.Config;
import me.ichun.mods.googlyeyes.common.core.EventHandler;
import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.function.BooleanSupplier;

@Mod(name = GooglyEyes.MOD_NAME, modid = GooglyEyes.MOD_ID,
        version = GooglyEyes.VERSION,
        clientSideOnly = true,
        acceptableRemoteVersions = "*",
        guiFactory = iChunUtil.GUI_CONFIG_FACTORY,
        dependencies = "required-after:ichunutil@[" + iChunUtil.VERSION_MAJOR +".2.0," + (iChunUtil.VERSION_MAJOR + 1) + ".0.0)",
        acceptedMinecraftVersions = iChunUtil.MC_VERSION_RANGE
)
public class GooglyEyes
{
    public static final String VERSION = iChunUtil.VERSION_MAJOR + ".0.2";

    public static final String MOD_NAME = "GooglyEyes";
    public static final String MOD_ID = "googlyeyes";

    public static EventHandler eventHandler;

    public static Config config;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        eventHandler = new EventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);

        UpdateChecker.registerMod(new UpdateChecker.ModVersionInfo(MOD_NAME, iChunUtil.VERSION_OF_MC, VERSION, true));

        BooleanSupplier oldAcidEyesBooleanSupplier = HeadBase.acidEyesBooleanSupplier;
        HeadBase.acidEyesBooleanSupplier = () -> (GooglyEyes.config.acidTripEyes == 1 || oldAcidEyesBooleanSupplier.getAsBoolean());

        iChunUtil.config.reveal("aggressiveHeadTracking", "horseEasterEgg");
    }
}
