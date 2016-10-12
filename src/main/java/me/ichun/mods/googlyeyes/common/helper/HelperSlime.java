package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.monster.EntitySlime;

public class HelperSlime extends HelperBase<EntitySlime>
{
    public float getHeadYaw(EntitySlime living, float partialTick, int eye)
    {
        return 0F;
    }

    public float getHeadPitch(EntitySlime living, float partialTick, int eye)
    {
        return 0F;
    }

    @Override
    public float[] getHeadJointOffset(EntitySlime living, int eye)
    {
        return new float[]{ 0F, -(19F/16F), 0F};
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntitySlime living, int eye)
    {
        return new float[]{ 0F, 0F, (4F/16F) };
    }
}
