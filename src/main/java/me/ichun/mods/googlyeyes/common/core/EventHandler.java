package me.ichun.mods.googlyeyes.common.core;

import me.ichun.mods.googlyeyes.common.GooglyEyes;
import me.ichun.mods.googlyeyes.common.layer.LayerGooglyEyes;
import me.ichun.mods.googlyeyes.common.model.ModelRendererDragonHook;
import me.ichun.mods.googlyeyes.common.tracker.GooglyTracker;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import me.ichun.mods.ichunutil.client.head.HeadHandler;
import me.ichun.mods.ichunutil.client.head.entity.HeadDragon;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class EventHandler
{
    protected WeakHashMap<LivingEntity, GooglyTracker> trackers = new WeakHashMap<>();

    @SubscribeEvent
    public void onWorldTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            if(Minecraft.getInstance().world != null && !Minecraft.getInstance().isGamePaused())
            {
                Iterator<Map.Entry<LivingEntity, GooglyTracker>> ite = trackers.entrySet().iterator();
                while(ite.hasNext())
                {
                    Map.Entry<LivingEntity, GooglyTracker> e = ite.next();
                    GooglyTracker tracker = e.getValue();
                    if(iChunUtil.eventHandlerClient.ticks - tracker.lastUpdateRequest > 10)
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
        if(event.getWorld().isRemote())
        {
            Iterator<Map.Entry<LivingEntity, GooglyTracker>> ite = trackers.entrySet().iterator();
            while(ite.hasNext())
            {
                Map.Entry<LivingEntity, GooglyTracker> e = ite.next();
                GooglyTracker tracker = e.getValue();
                if(tracker.parent.getEntityWorld() == event.getWorld())
                {
                    ite.remove();
                }
            }
        }
    }

    public GooglyTracker getGooglyTracker(LivingEntity living, HeadBase helper)
    {
        GooglyTracker tracker = trackers.get(living);
        if(tracker == null)
        {
            tracker = new GooglyTracker(living, helper);
            trackers.put(living, tracker);
        }
        return tracker;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addLayers()
    {
        LayerGooglyEyes layerGooglyEyes = new LayerGooglyEyes();

        HashSet<LivingRenderer> addedRenderers = new HashSet<>();

        EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
        if(!(GooglyEyes.config.disabledGoogly.contains("minecraft:player") || GooglyEyes.config.disabledGoogly.contains("player")))
        {
            Map<String, PlayerRenderer> skinMap = renderManager.getSkinMap();
            for(Map.Entry<String, PlayerRenderer> e : skinMap.entrySet())
            {
                e.getValue().addLayer(layerGooglyEyes);
                addedRenderers.add(e.getValue());
            }
        }
        renderManager.renderers.forEach((entityType, entityRenderer) -> {
            if(addedRenderers.contains(entityRenderer))
            {
                return;
            }

            ResourceLocation rl = entityType.getRegistryName();
            for(String s : GooglyEyes.config.disabledGoogly)
            {
                ResourceLocation disabled = new ResourceLocation(s);
                if(disabled.equals(rl))
                {
                    return;
                }
            }

            if(entityRenderer instanceof LivingRenderer)
            {
                LivingRenderer renderer = (LivingRenderer)entityRenderer;
                renderer.addLayer(layerGooglyEyes);
            }
            else if(entityRenderer instanceof EnderDragonRenderer)
            {
                EnderDragonRenderer dragonRenderer = (EnderDragonRenderer)entityRenderer;
                dragonRenderer.model.head.addChild(new ModelRendererDragonHook(dragonRenderer.model));
            }
        });
    }
}
