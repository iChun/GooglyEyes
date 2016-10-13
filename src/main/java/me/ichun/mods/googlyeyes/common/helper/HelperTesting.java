package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.EntityLivingBase;

public class HelperTesting extends HelperBase<EntityLivingBase>
{
    @Override
    public float getEyeSideOffset(EntityLivingBase living, float partialTick, int eye)
    {
        halfInterpupillaryDistance = 2F / 16F;
        return super.getEyeSideOffset(living, partialTick, eye);
    }

    @Override
    public float getEyeScale(EntityLivingBase living, float partialTick, int eye)
    {
        return 0.65F;
    }

    @Override
    public float[] getHeadJointOffset(EntityLivingBase living, float partialTick, int eye)
    {
        return new float[] { 1F/16F, -13.5F/16F, 7F/16F };
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntityLivingBase living, float partialTick, int eye)
    {
        return new float[] { -1F/16F, 0.5F/16F, 2F/16F };
    }
}
