package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.player.EntityPlayer;

public class HelperPlayer extends HelperBase<EntityPlayer>
{
    //TODO fucking elytra
    public float[] headJoint = new float[3];
    public float[] eyeOffset = new float[] { 0F, 4F/16F, 4F/16F};

    @Override
    public float[] getHeadJointOffset(EntityPlayer living, int eye)
    {
        return headJoint;
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntityPlayer living, int eye)
    {
        return eyeOffset;
    }
}
