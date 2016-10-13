package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.passive.EntityHorse;

public class HelperHorse extends HelperBase<EntityHorse>
{
    public HelperHorse()
    {
        headJoint = new float[] { 0F, -11F/16F, -9F/16F };
        eyeOffset = new float[] { 0F, 6F/16F, 5F/16F };
        halfInterpupillaryDistance = 3F/16F;
        eyeScale = 0.9F;
    }

    @Override
    public float getHeadYaw(EntityHorse living, float partialTick, int eye)
    {
        return 180F;
    }

    @Override
    public float getHeadPitch(EntityHorse living, float partialTick, int eye)
    {
        return (float)Math.toDegrees(living.getRearingAmount(partialTick) * ((float)Math.PI / 4F));
    }

}
