package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Random;

public abstract class HelperBase<E extends EntityLivingBase>
{
    public float[] defaultIrisColour = new float[] { 0.9F, 0.9F, 0.9F };
    public float[] defaultPupilColour = new float[] { 0.0F, 0.0F, 0.0F };
    public float halfInterpupillaryDistance = 2F/16F;

    public abstract float[] getHeadJointOffset(E living, int eye);
    public abstract float[] getEyeOffsetFromJoint(E living, int eye);

    public int getEyeCount(E living)
    {
        return 2;
    }

    public float getEyeSideOffset(E living, int eye)
    {
        return eye == 0 ? halfInterpupillaryDistance : -halfInterpupillaryDistance;
    }

    public float getEyeScale(E living, int eye)
    {
        return 0.75F;
    }

    public float maxEyeSizeGrowth(E living, int eye)
    {
        return 0F;
    }

    public float getEyeRotation(E living, int eye)
    {
        return 0F;
    }

    public float getPupilScale(E living, int eye)
    {
        Random rand = new Random(Math.abs(living.hashCode()) * 1000);
        int eyeCount = getEyeCount(living);
        int[] times = new int[eyeCount];
        for(int i = 0; i < eyeCount; i++)
        {
            times[i] = 20 + rand.nextInt(20);
        }
        return 0.3F + (float)Math.sin(Math.toRadians((float)living.ticksExisted / times[eye] * 180F % 180F));

//        return 1F;
    }

    public float[] getIrisColours(E living, int eye)
    {
        return defaultIrisColour;
    }

    public float[] getPupilColours(E living, int eye)
    {
        return defaultPupilColour;
    }

    public float getHeadYaw(E living, float partialTick, int eye)
    {
        return (living.prevRotationYawHead + ((living.rotationYawHead - living.prevRotationYawHead) * partialTick)) - (living.prevRenderYawOffset + ((living.renderYawOffset - living.prevRenderYawOffset) * partialTick));
    }

    public float getHeadPitch(E living, float partialTick, int eye)
    {
        return living.prevRotationPitch + ((living.rotationPitch - living.prevRotationPitch) * partialTick);
    }

    public float getHeadRoll(E living, float partialTick, int eye)
    {
        return 0;
    }

    public boolean affectedByInvisibility(E living, int eye)
    {
        return true;
    }

    public boolean doesEyeGlow(E living, int eye)
    {
        return false;
    }

    public static HashMap<Class<? extends EntityLivingBase>, HelperBase> modelOffsetHelpers = new HashMap<Class<? extends EntityLivingBase>, HelperBase>() {{
        put(EntityPlayer.class, new HelperPlayer());
        put(EntityCreeper.class, new HelperCreeper());
        put(EntityEnderman.class, new HelperEnderman());
        put(EntityPig.class, new HelperPig());
        put(EntitySkeleton.class, new HelperBiped());
        put(EntitySlime.class, new HelperSlime());
        put(EntitySpider.class, new HelperSpider());
        put(EntityZombie.class, new HelperBiped());
    }};

    @Nullable
    public static HelperBase getHelperBase(Class<? extends EntityLivingBase> clz)
    {
        HelperBase helper = modelOffsetHelpers.get(clz);
        Class clzz = clz.getSuperclass();
        while(helper == null && clzz != EntityLivingBase.class)
        {
            helper = getHelperBase(clzz);
            if(helper != null)
            {
                modelOffsetHelpers.put(clzz, helper);
            }
            clzz = clzz.getSuperclass();
        }
        return helper;
    }
}
