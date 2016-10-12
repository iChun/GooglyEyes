package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.monster.EntityCreeper;

public class HelperCreeper extends HelperBase<EntityCreeper>
{
    @Override
    public float[] getHeadJointOffset(EntityCreeper living, int eye)
    {
        return new float[] { 0F, -6F/16F, 0F };
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntityCreeper living, int eye)
    {
        return new float[] { 0F, 5F/16F, 4F/16F};
    }
}
