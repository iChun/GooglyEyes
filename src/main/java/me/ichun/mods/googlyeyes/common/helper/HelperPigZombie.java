package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.monster.EntityPigZombie;

public class HelperPigZombie extends HelperBase<EntityPigZombie>
{
    public float[] eyeOffsetSkin = new float[]{ -0.35F/16F, 4.5F/16F, 4.5F/16F };
    public float eyeScaleSkin = 0.65F;

    public HelperPigZombie()
    {
        headJoint = new float[]{ 0F, 0F, 0F };
        eyeOffset = new float[]{ 0F, 4.5F/16F, 4F/16F };
        halfInterpupillaryDistance = 3F/16F;
        eyeScale = 0.8F;
    }

    @Override
    public float getEyeScale(EntityPigZombie living, float partialTick, int eye)
    {
        if(eye == 1 && !living.isChild())
        {
            return eyeScaleSkin;
        }
        return eyeScale;
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntityPigZombie living, float partialTick, int eye)
    {
        if(eye == 1 && !living.isChild())
        {
            return eyeOffsetSkin;
        }
        return eyeOffset;
    }
}
