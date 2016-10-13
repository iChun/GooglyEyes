package me.ichun.mods.googlyeyes.common;

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

    //TODO set up static fields for the helpers.

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
        ArrayList<RenderLivingBase> renderLivingBases = new ArrayList<>();
        for(Map.Entry< Class <? extends Entity> , Render <? extends Entity >> entry : Minecraft.getMinecraft().getRenderManager().entityRenderMap.entrySet())
        {
            if(entry.getValue() instanceof RenderLivingBase && !renderLivingBases.contains(entry.getValue()))
            {
                renderLivingBases.add((RenderLivingBase)entry.getValue());
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

                    //ZOMBIE WORKAROUND
                    if(render instanceof RenderZombie)
                    {
                        List<LayerRenderer> zombieLayers = ObfuscationReflectionHelper.getPrivateValue(RenderZombie.class, (RenderZombie)render, "field_177122_o", "defaultLayers"); //TODO AT THIS OUT IN ICHUNUTIL
                        List<LayerRenderer> villagerLayers = ObfuscationReflectionHelper.getPrivateValue(RenderZombie.class, (RenderZombie)render, "field_177121_n", "villagerLayers"); //TODO AT THIS OUT IN ICHUNUTIL
                        zombieLayers.add(layerGooglyEyes);
                        villagerLayers.add(layerGooglyEyes);
                    }

                    for(RenderLivingBase render1 : renderLivingBases)
                    {
                        if(render != render1 && render.getClass().isInstance(render1))
                        {
                            render1.addLayer(layerGooglyEyes);

                            //ZOMBIE WORKAROUND
                            if(render1 instanceof RenderZombie)
                            {
                                List<LayerRenderer> zombieLayers = ObfuscationReflectionHelper.getPrivateValue(RenderZombie.class, (RenderZombie)render1, "field_177122_o", "defaultLayers"); //TODO AT THIS OUT IN ICHUNUTIL
                                List<LayerRenderer> villagerLayers = ObfuscationReflectionHelper.getPrivateValue(RenderZombie.class, (RenderZombie)render1, "field_177121_n", "villagerLayers"); //TODO AT THIS OUT IN ICHUNUTIL
                                zombieLayers.add(layerGooglyEyes);
                                villagerLayers.add(layerGooglyEyes);
                            }
                        }
                    }
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
