package me.ichun.mods.googlyeyes.common;

import me.ichun.mods.googlyeyes.common.core.Config;
import me.ichun.mods.googlyeyes.common.core.EventHandler;
import me.ichun.mods.googlyeyes.common.helper.HelperBase;
import me.ichun.mods.googlyeyes.common.layerrenderer.LayerGooglyEyes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod(name = GooglyEyes.name, modid = GooglyEyes.modid, version = GooglyEyes.version,
        clientSideOnly = true,
        acceptableRemoteVersions = "*",
        dependencies = "required-after:Forge@[12.18.2.2099,)"
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
