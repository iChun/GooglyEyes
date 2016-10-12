package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Random;

public abstract class HelperBase<E extends EntityLivingBase>
{
    public abstract float[] getHeadJointOffset(E living, int eye);
    public abstract float[] getEyeOffsetFromJoint(E living, int eye);

    public int getEyeCount(E living)
    {
        return 2;
    }

    public float getEyeSideOffset(E living, int eye)
    {
        return eye == 0 ? 2F/16F : -2F/16F;
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
        int time1 = 20 + rand.nextInt(20);
        int time2 = 20 + rand.nextInt(20);
        float eye0 = (float)Math.sin(Math.toRadians((float)living.ticksExisted / time1 * 180F % 180F));
        float eye1 = (float)Math.sin(Math.toRadians((float)living.ticksExisted / time2 * 180F % 180F));
        if(eye == 0)
        {
            return 0.3F + eye0;
        }
        else
        {
            return 0.3F + eye1;
        }

//        return 1F;
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

    public static HashMap<Class<? extends EntityLivingBase>, HelperBase> modelOffsetHelpers = new HashMap<Class<? extends EntityLivingBase>, HelperBase>() {{
        put(EntityPlayer.class, new HelperPlayer());
        put(EntitySlime.class, new HelperSlime());
        put(EntitySkeleton.class, new HelperBiped());
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
