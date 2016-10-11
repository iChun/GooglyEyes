package me.ichun.mods.googlyeyes.common;

import me.ichun.mods.googlyeyes.common.core.EventHandler;
import me.ichun.mods.googlyeyes.common.helper.HelperBase;
import me.ichun.mods.googlyeyes.common.layerrenderer.LayerGooglyEyes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
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
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Map;

@Mod(name = GooglyEyes.name, modid = GooglyEyes.modid, version = GooglyEyes.version,
        clientSideOnly = true,
        acceptableRemoteVersions = "*",
        dependencies = "required-after:Forge@[12.18.2.2103,)"
)
public class GooglyEyes
{
    public static final String version = "6.0.0";

    public static final String name = "GooglyEyes";
    public static final String modid = "googlyeyes";

    public static EventHandler eventHandler;

    //Config stuff

    public static String[] disabledGoogly = new String[0];

    //End config stuff

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        disabledGoogly = config.getStringList("disabledGoogly", "general", disabledGoogly, "To disable Googly Eyes on a specific entity, put their registry name in here.\nFor Players, put \"player\"");

        if(config.hasChanged())
        {
            config.save();
        }

        eventHandler = new EventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);
    }

    @SuppressWarnings("unchecked")
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        //TODO switch this over to the render-safe event in iChunUtil

        LayerGooglyEyes layerGooglyEyes = new LayerGooglyEyes(Minecraft.getMinecraft().getTextureManager());

        boolean doPlayer = true;
        for(String s : disabledGoogly)
        {
            if(s.equalsIgnoreCase("player"))
            {
                doPlayer = false;
                break;
            }
        }
        if(doPlayer)
        {
            Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
            for(Map.Entry<String, RenderPlayer> e : skinMap.entrySet())
            {
                e.getValue().addLayer(layerGooglyEyes);
            }
        }
        for(Map.Entry<Class<? extends EntityLivingBase>, HelperBase> e : HelperBase.modelOffsetHelpers.entrySet())
        {
            boolean addLayer = true;
            Class<? extends EntityLivingBase> clz = e.getKey();
            if(clz == EntityPlayer.class)
            {
                continue;
            }
            String entName = EntityList.getEntityStringFromClass(clz);
            for(String s : disabledGoogly)
            {
                if(s.equalsIgnoreCase(entName))
                {
                    addLayer = false;
                    break;
                }
            }
            if(addLayer)
            {
                Render render = Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(clz);
                if(render instanceof RenderLivingBase)
                {
                    RenderLivingBase renderLiving = (RenderLivingBase)render;
                    renderLiving.addLayer(layerGooglyEyes);
                }
                else
                {
                    //TODO LOGGER
                    System.out.println("ASDLKHASKLDHKLASJDLKASJDLKJ SOMETHING WRONG: " + entName);
                }
            }
        }
    }
}
