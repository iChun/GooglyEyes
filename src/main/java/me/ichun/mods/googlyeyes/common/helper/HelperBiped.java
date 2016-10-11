package me.ichun.mods.googlyeyes.common.helper;


import net.minecraft.entity.monster.EntityMob;

public class HelperBiped extends HelperBase<EntityMob>
{
    @Override
    public float[] getHeadJointOffset(EntityMob living, int eye)
    {
        return new float[3];
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntityMob living, int eye)
    {
        return new float[] { 0F, -4F/16F, -4F/16F};
    }
}
