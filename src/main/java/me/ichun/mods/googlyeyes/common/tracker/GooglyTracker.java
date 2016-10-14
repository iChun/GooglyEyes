package me.ichun.mods.googlyeyes.common.tracker;

import me.ichun.mods.googlyeyes.common.GooglyEyes;
import me.ichun.mods.googlyeyes.common.helper.HelperBase;
import net.minecraft.entity.EntityLivingBase;

import javax.annotation.Nonnull;
import java.util.Random;

public class GooglyTracker
{
    public final EntityLivingBase parent;
    public final HelperBase helper;
    public final Random rand;
    public final float renderChance;

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

    public double deltaX;
    public double deltaY;

    public GooglyTracker(@Nonnull EntityLivingBase parent, @Nonnull HelperBase helper)
    {
        this.parent = parent;
        this.helper = helper;
        this.rand = new Random(Math.abs(parent.hashCode()) * 8134);
        this.renderChance = rand.nextFloat();

        update();
    }

    public void update()
    {
        if(!shouldUpdate || shouldRender())
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

        prevRotationYaw = rotationYaw;
        prevRotationPitch = rotationPitch;

        rotationYaw = helper.getHeadYawForTracker(parent);
        rotationPitch = helper.getHeadPitchForTracker(parent);
    }

    public void requireUpdate()
    {
        shouldUpdate = true;
        lastUpdateRequest = parent.worldObj.getWorldTime();
    }

    public boolean shouldRender()
    {
        return renderChance < (GooglyEyes.config.googlyEyeChance / 100F);
    }
}
