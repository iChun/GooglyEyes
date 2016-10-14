package me.ichun.mods.googlyeyes.common.core;

import me.ichun.mods.googlyeyes.common.GooglyEyes;
import me.ichun.mods.googlyeyes.common.helper.HelperBase;
import me.ichun.mods.googlyeyes.common.layerrenderer.LayerGooglyEyes;
import me.ichun.mods.googlyeyes.common.tracker.GooglyTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class EventHandler
{
    private boolean hasShownFirstGui = false;

    @SubscribeEvent
    public void onWorldTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            if(Minecraft.getMinecraft().theWorld != null && !Minecraft.getMinecraft().isGamePaused())
            {
                Iterator<Map.Entry<EntityLivingBase, GooglyTracker>> ite = trackers.entrySet().iterator();
                while(ite.hasNext())
                {
                    Map.Entry<EntityLivingBase, GooglyTracker> e = ite.next();
                    GooglyTracker tracker = e.getValue();
                    if(tracker.parent.worldObj.getWorldTime() - tracker.lastUpdateRequest > 3) //If the tracker hasn't been updated for 3 ticks, assume the entity has despawned
                    {
                        ite.remove();
                    }
                    else
                    {
                        tracker.update();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event)
    {
        Iterator<Map.Entry<EntityLivingBase, GooglyTracker>> ite = trackers.entrySet().iterator();
        while(ite.hasNext())
        {
            Map.Entry<EntityLivingBase, GooglyTracker> e = ite.next();
            GooglyTracker tracker = e.getValue();
            if(tracker.parent.worldObj == event.getWorld())
            {
                ite.remove();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post event)
    {
        //TODO switch this over to the render-safe event in iChunUtil
        if(!hasShownFirstGui)
        {
            hasShownFirstGui = true;

            LayerGooglyEyes layerGooglyEyes = new LayerGooglyEyes(Minecraft.getMinecraft().getTextureManager());
            ArrayList<RenderLivingBase> addedRenderers = new ArrayList<>();

            boolean doPlayer = true;
            for(String s : GooglyEyes.config.disabledGoogly)
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
                    addedRenderers.add(e.getValue());
                }
            }
            ArrayList<RenderLivingBase> renderLivingBases = new ArrayList<>();
            for(Map.Entry< Class <? extends Entity> , Render<? extends Entity >> entry : Minecraft.getMinecraft().getRenderManager().entityRenderMap.entrySet())
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
                for(String s : GooglyEyes.config.disabledGoogly)
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
                        addGooglyLayer(renderLiving, layerGooglyEyes);

                        for(RenderLivingBase render1 : renderLivingBases)
                        {
                            if(render != render1 && render.getClass().isInstance(render1) && !addedRenderers.contains(render1))
                            {
                                addLayer = true;

                                entName = EntityList.getEntityStringFromClass(clz);
                                for(String s : GooglyEyes.config.disabledGoogly)
                                {
                                    if(s.equalsIgnoreCase(entName))
                                    {
                                        addLayer = false;
                                        break;
                                    }
                                }
                                if(addLayer)
                                {
                                    addGooglyLayer(render1, layerGooglyEyes);
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

    public void addGooglyLayer(RenderLivingBase render, LayerGooglyEyes layerGooglyEyes)
    {
        render.addLayer(layerGooglyEyes);

        //ZOMBIE WORKAROUND
        if(render instanceof RenderZombie)
        {
            List<LayerRenderer> zombieLayers = ObfuscationReflectionHelper.getPrivateValue(RenderZombie.class, (RenderZombie)render, "field_177122_o", "defaultLayers"); //TODO AT THIS OUT IN ICHUNUTIL
            List<LayerRenderer> villagerLayers = ObfuscationReflectionHelper.getPrivateValue(RenderZombie.class, (RenderZombie)render, "field_177121_n", "villagerLayers"); //TODO AT THIS OUT IN ICHUNUTIL
            zombieLayers.add(layerGooglyEyes);
            villagerLayers.add(layerGooglyEyes);
        }
    }


    public GooglyTracker getGooglyTracker(EntityLivingBase living, HelperBase helper)
    {
        GooglyTracker tracker = trackers.get(living);
        if(tracker == null)
        {
            tracker = new GooglyTracker(living, helper);
            trackers.put(living, tracker);
        }
        return tracker;
    }

    protected WeakHashMap<EntityLivingBase, GooglyTracker> trackers = new WeakHashMap<>();
}
