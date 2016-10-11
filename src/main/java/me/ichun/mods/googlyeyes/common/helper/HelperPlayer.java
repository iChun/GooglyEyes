package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.player.EntityPlayer;

public class HelperPlayer extends HelperBase<EntityPlayer>
{
    //TODO fucking elytra
    @Override
    public float[] getHeadJointOffset(EntityPlayer living, int eye)
    {
        return new float[3];
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntityPlayer living, int eye)
    {
        return new float[] { 0F, -4F/16F, -4F/16F};
    }
}
