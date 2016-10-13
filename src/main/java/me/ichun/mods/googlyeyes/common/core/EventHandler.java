package me.ichun.mods.googlyeyes.common.core;

import me.ichun.mods.googlyeyes.common.tracker.GooglyTracker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

public class EventHandler
{
    @SubscribeEvent
    public void onWorldTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
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

    public GooglyTracker getGooglyTracker(EntityLivingBase living)
    {
        GooglyTracker tracker = trackers.get(living);
        if(tracker == null)
        {
            tracker = new GooglyTracker(living);
            trackers.put(living, tracker);
        }
        return tracker;
    }

    protected WeakHashMap<EntityLivingBase, GooglyTracker> trackers = new WeakHashMap<>();
}
