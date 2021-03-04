package me.ichun.mods.googlyeyes.common.tracker;

import me.ichun.mods.googlyeyes.common.GooglyEyes;
import me.ichun.mods.ichunutil.common.head.HeadInfo;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.util.Random;

@SuppressWarnings({"rawtypes", "unchecked"})
public class GooglyTracker
{
    public final LivingEntity parent;
    public final HeadInfo helper;
    public final Random rand;
    public final float renderChance;

    public boolean shouldUpdate = true;
    public int lastUpdateRequest;

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

            rotationYaw = helper.getHeadYaw(parent.parent, 1F, eye);
            rotationPitch = helper.getHeadPitch(parent.parent, 1F, eye);
            rotationRoll = helper.getHeadRoll(parent.parent, 1F, eye);

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


    public GooglyTracker(@Nonnull LivingEntity parent, @Nonnull HeadInfo helper)
    {
        this.parent = parent;
        this.helper = helper;
        this.rand = new Random(Math.abs(parent.getUniqueID().hashCode()) * 8134L);
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

        motionX = parent.getPosX() - parent.prevPosX;
        motionY = parent.getPosY() - parent.prevPosY;
        motionZ = parent.getPosZ() - parent.prevPosZ;

        for(int i = 0; i < eyes.length; i++)
        {
            eyes[i].update(i, this, motionX, motionY, motionZ);
        }
    }

    public void setLastUpdateRequest()
    {
        lastUpdateRequest = iChunUtil.eventHandlerClient.ticks;
    }

    public void requireUpdate()
    {
        shouldUpdate = true;
    }

    public boolean shouldRender()
    {
        String name = parent.getName().getUnformattedComponentText();
        for(String s : GooglyEyes.config.nameOverride)
        {
            if(s.equals(name))
            {
                return true;
            }
        }

        if(GooglyEyes.config.entityOverrideChanceParsed.containsKey(parent.getType().getRegistryName()))
        {
            return renderChance < GooglyEyes.config.entityOverrideChanceParsed.get(parent.getType().getRegistryName()) / 100F;
        }

        return renderChance < (GooglyEyes.config.googlyEyeChance / 100F);
    }
}
