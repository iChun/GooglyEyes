package me.ichun.mods.googlyeyes.common.tracker;

import me.ichun.mods.googlyeyes.common.GooglyEyes;
import me.ichun.mods.ichunutil.client.entity.head.HeadBase;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.util.Random;

public class GooglyTracker
{
    public final EntityLivingBase parent;
    public final HeadBase helper;
    public final Random rand;
    public final float renderChance;

    public boolean shouldUpdate = true;
    public long lastUpdateRequest;

    public double motionX;
    public double motionY;
    public double motionZ;

    public EyeInfo[] eyes;

    public class EyeInfo
    {
        public float prevRotationYaw;
        public float rotationYaw;
        public float prevRotationPitch;
        public float rotationPitch;
        public float prevRotationRoll;
        public float rotationRoll;

        //Deltas are capped to -1 to 1
        public float prevDeltaX;
        public float prevDeltaY;
        public float deltaX;
        public float deltaY;
        public float momentumX;
        public float momentumY;

        public EyeInfo()
        {
            prevDeltaY = deltaY = -1F;
        }

        public void update(int eye, GooglyTracker parent, double motionX, double motionY, double motionZ)
        {
            prevRotationYaw = rotationYaw;
            prevRotationPitch = rotationPitch;
            prevRotationRoll = rotationRoll;

            rotationYaw = helper.getHeadYawForTracker(parent.parent, eye);
            rotationPitch = helper.getHeadPitchForTracker(parent.parent, eye);
            rotationRoll = helper.getHeadRollForTracker(parent.parent, eye);

            prevDeltaX = deltaX;
            prevDeltaY = deltaY;

            //calculate momentums
            float yawDiff = rotationYaw - prevRotationYaw;
            float pitchDiff = rotationPitch - prevRotationPitch;
            float rollDiff = rotationRoll - prevRotationRoll;

            momentumY += motionY * 1.5F + (motionX + motionZ) * rand.nextGaussian() * (0.75F) + (pitchDiff / 45F) + (yawDiff / 180F) + rollDiff * rand.nextGaussian() * (0.05F);
            momentumX -= (motionX + motionZ) * rand.nextGaussian() * 0.4F + (yawDiff / 45F) + rollDiff * rand.nextGaussian() * (0.05F);

            //Physics based!
            float momentumLoss = 0.9F;
            float newDeltaX = deltaX + momentumX;
            float newDeltaY = deltaY + momentumY;
            if(newDeltaX < -1F || newDeltaX > 1F)
            {
                float newMo = momentumX * -momentumLoss;
                float randFloat = 0.8F + rand.nextFloat() * 0.2F;
                momentumX = newMo * randFloat;
                momentumY += newMo * (randFloat) * (rand.nextFloat() > 0.5F ? 1F : -1F);
            }
            if(newDeltaY < -1F || newDeltaY > 1F)
            {
                float newMo = momentumY * -momentumLoss;
                float randFloat = 0.8F + rand.nextFloat() * 0.2F;
                momentumY = newMo * randFloat;
                momentumX += newMo * (1F - randFloat) * (rand.nextFloat() > 0.5F ? 1F : -1F);
            }
            else
            {
                momentumY -= MathHelper.clamp(1F + deltaY, 0F, 0.1999F);
            }

            momentumX *= 0.95F;
            deltaX *= 0.95F;

            if(Math.abs(momentumX) < 0.03F)
            {
                momentumX = 0F;
            }
            if(Math.abs(deltaX) < 0.03F)
            {
                deltaX = 0F;
            }

            float maxMomentum = 1.3F;
            momentumX = MathHelper.clamp(momentumX, -maxMomentum, maxMomentum);
            momentumY = MathHelper.clamp(momentumY, -maxMomentum, maxMomentum);

            deltaX += momentumX;
            deltaY += momentumY;
            deltaX = MathHelper.clamp(deltaX, -1F, 1F);
            deltaY = MathHelper.clamp(deltaY, -1F, 1F);
        }
    }


    public GooglyTracker(@Nonnull EntityLivingBase parent, @Nonnull HeadBase helper)
    {
        this.parent = parent;
        this.helper = helper;
        this.rand = new Random(Math.abs(parent.hashCode()) * 8134);
        this.renderChance = rand.nextFloat();
        this.eyes = new EyeInfo[helper.getEyeCount(parent)];
        for(int i = 0; i < eyes.length; i++)
        {
            this.eyes[i] = new EyeInfo();
        }

        update();
    }

    public void update()
    {
        if(!shouldUpdate || !shouldRender())
        {
            return;
        }
        shouldUpdate = false;

        motionX = parent.posX - parent.prevPosX;
        motionY = parent.posY - parent.prevPosY;
        motionZ = parent.posZ - parent.prevPosZ;

        for(int i = 0; i < eyes.length; i++)
        {
            eyes[i].update(i, this, motionX, motionY, motionZ);
        }
    }

    public void requireUpdate()
    {
        shouldUpdate = true;
        lastUpdateRequest = iChunUtil.eventHandlerClient.ticks;
    }

    public boolean shouldRender()
    {
        return renderChance < (GooglyEyes.config.googlyEyeChance / 100F);
    }
}
