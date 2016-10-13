package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.EntityLivingBase;

public class HelperTesting extends HelperBase<EntityLivingBase>
{
    @Override
    public float getEyeSideOffset(EntityLivingBase living, float partialTick, int eye)
    {
        halfInterpupillaryDistance = 2.F / 16F;
        return super.getEyeSideOffset(living, partialTick, eye);
    }

    @Override
    public float getEyeScale(EntityLivingBase living, float partialTick, int eye)
    {
        return 1F;
    }

    @Override
    public float[] getHeadJointOffset(EntityLivingBase living, float partialTick, int eye)
    {
        return new float[] { 0F, -4F/16F, 0F/16F };
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntityLivingBase living, float partialTick, int eye)
    {
        return new float[] { 0F, 7.5F/16, 5F/16F };
    }
}
