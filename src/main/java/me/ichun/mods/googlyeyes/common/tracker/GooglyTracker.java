package me.ichun.mods.googlyeyes.common.tracker;

import net.minecraft.entity.EntityLivingBase;

import javax.annotation.Nonnull;

public class GooglyTracker
{
    public final EntityLivingBase parent;

    public boolean shouldUpdate = true;
    public long lastUpdateRequest;

    public double prevMotionX;
    public double prevMotionY;
    public double prevMotionZ;
    public double motionX;
    public double motionY;
    public double motionZ;
    public float prevRotationYaw;
    public float rotationYaw;
    public float prevRotationPitch;
    public float rotationPitch;

    public GooglyTracker(@Nonnull EntityLivingBase parent)
    {
        this.parent = parent;

        update();
    }

    public void update()
    {
        if(!shouldUpdate)
        {
            return;
        }
        shouldUpdate = false;

        prevMotionX = motionX;
        prevMotionY = motionY;
        prevMotionZ = motionZ;

        motionX = parent.posX - parent.prevPosX;
        motionY = parent.posY - parent.prevPosY;
        motionZ = parent.posZ - parent.prevPosZ;

        //TODO remember to account for ghasts.

        prevRotationYaw = parent.prevRotationYawHead;
        rotationYaw = parent.rotationYaw;

        prevRotationPitch = parent.prevRotationPitch;
        rotationPitch = parent.rotationPitch;
    }

    public void requireUpdate()
    {
        shouldUpdate = true;
        lastUpdateRequest = parent.worldObj.getWorldTime();
    }
}
