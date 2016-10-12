package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.monster.EntityMob;

public class HelperBiped extends HelperBase<EntityMob>
{
    public float[] headJoint = new float[3];
    public float[] eyeOffset = new float[] { 0F, 4F/16F, 4F/16F};

    @Override
    public float[] getHeadJointOffset(EntityMob living, int eye)
    {
        return headJoint;
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntityMob living, int eye)
    {
        return eyeOffset;
    }
}
