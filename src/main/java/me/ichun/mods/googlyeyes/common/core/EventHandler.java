package me.ichun.mods.googlyeyes.common.core;

import me.ichun.mods.googlyeyes.common.GooglyEyes;
import me.ichun.mods.ichunutil.client.core.event.RendererSafeCompatibilityEvent;
import me.ichun.mods.ichunutil.client.entity.head.HeadBase;
import me.ichun.mods.googlyeyes.common.layerrenderer.LayerGooglyEyes;
import me.ichun.mods.googlyeyes.common.tracker.GooglyTracker;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class EventHandler
{
    @SubscribeEvent
    public void onWorldTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            if(Minecraft.getMinecraft().world != null && !Minecraft.getMinecraft().isGamePaused())
            {
                Iterator<Map.Entry<EntityLivingBase, GooglyTracker>> ite = trackers.entrySet().iterator();
                while(ite.hasNext())
                {
                    Map.Entry<EntityLivingBase, GooglyTracker> e = ite.next();
                    GooglyTracker tracker = e.getValue();
                    if(iChunUtil.eventHandlerClient.ticks - tracker.lastUpdateRequest > 3)
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
            if(tracker.parent.getEntityWorld() == event.getWorld())
            {
                ite.remove();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRendererSafeCompatibilityEvent(RendererSafeCompatibilityEvent event)
    {
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

        for(Map.Entry< Class <? extends Entity> , Render<? extends Entity >> entry : Minecraft.getMinecraft().getRenderManager().entityRenderMap.entrySet())
        {
            if(EntityPlayer.class.isAssignableFrom(entry.getKey()))
            {
                continue;
            }
            if(EntityLivingBase.class.isAssignableFrom(entry.getKey()) && RenderLivingBase.class.isAssignableFrom(entry.getValue().getClass())) //is a living entity with a living entity renderer
            {
                boolean addLayer = true;
                net.minecraftforge.fml.common.registry.EntityEntry entEntry = net.minecraftforge.fml.common.registry.EntityRegistry.getEntry(entry.getKey());
                if(entEntry != null)
                {
                    String entName = entEntry.getName();
                    for(String s : GooglyEyes.config.disabledGoogly)
                    {
                        if(s.equalsIgnoreCase(entName))
                        {
                            addLayer = false;
                            break;
                        }
                    }
                }
                if(addLayer)
                {
                    for(Map.Entry<Class<? extends EntityLivingBase>, HeadBase> e : HeadBase.modelOffsetHelpers.entrySet())
                    {
                        if(e.getKey().isAssignableFrom(entry.getKey()))
                        {
                            RenderLivingBase renderLiving = (RenderLivingBase)entry.getValue();
                            addGooglyLayer(renderLiving, layerGooglyEyes);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void addGooglyLayer(RenderLivingBase render, LayerGooglyEyes layerGooglyEyes)
    {
        render.addLayer(layerGooglyEyes);
    }

    public GooglyTracker getGooglyTracker(EntityLivingBase living, HeadBase helper)
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
