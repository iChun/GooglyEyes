package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.HashMap;

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
        return 1F;
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
