package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.player.EntityPlayer;

public class HelperPlayer extends HelperBase<EntityPlayer>
{
    //TODO fucking elytra
    public HelperPlayer()
    {
        headJoint = new float[3];
        eyeOffset = new float[] { 0F, 4F/16F, 4F/16F};
    }

    @Override
    public float[] getHeadJointOffset(EntityPlayer living, float partialTick, int eye)
    {
        return headJoint;
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntityPlayer living, float partialTick, int eye)
    {
        return eyeOffset;
    }
}
