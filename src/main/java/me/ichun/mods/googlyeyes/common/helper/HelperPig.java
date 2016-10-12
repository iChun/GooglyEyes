package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.passive.EntityPig;

public class HelperPig extends HelperBase<EntityPig>
{
    @Override
    public float getEyeSideOffset(EntityPig living, int eye)
    {
        return eye == 0 ? 3F/16F : -3F/16F;
    }

    @Override
    public float[] getHeadJointOffset(EntityPig living, int eye)
    {
        return new float[] { 0F, -12F/16F, 6F/16F};
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntityPig living, int eye)
    {
        return new float[] { 0F, 0.5F/16F, 8F/16F};
    }
}
