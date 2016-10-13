package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.monster.EntityZombie;

public class HelperZombie extends HelperBase<EntityZombie>
{
    public float[] eyeOffsetVillager = new float[]{ 0F, 3.2F/16F, 4F/16F };
    public float halfInterpupillaryDistanceVillager = 1.9F / 16F;
    public float eyeScaleVillager = 0.7F;

    @Override
    public float[] getEyeOffsetFromJoint(EntityZombie living, float partialTick, int eye)
    {
        if(living.isVillager())
        {
            return eyeOffsetVillager;
        }
        return eyeOffset;
    }

    @Override
    public float getEyeSideOffset(EntityZombie living, float partialTick, int eye)
    {
        if(living.isVillager())
        {
            return eye == 0 ? halfInterpupillaryDistanceVillager : -halfInterpupillaryDistanceVillager;
        }
        return eye == 0 ? halfInterpupillaryDistance : -halfInterpupillaryDistance;
    }

    @Override
    public float getEyeScale(EntityZombie living, float partialTick, int eye)
    {
        if(living.isVillager())
        {
            return eyeScaleVillager;
        }
        return eyeScale;
    }
}
