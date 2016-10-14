package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

public class HelperPlayer extends HelperBase<EntityPlayer>
{
    //TODO fucking elytra
    //TODO HelperBiped for sneaking...?
    public HelperPlayer()
    {
        headJoint = new float[3];
        eyeOffset = new float[] { 0F, 4F/16F, 4F/16F};
    }

    @Override
    public float[] getHeadJointOffset(EntityPlayer living, float partialTick, int eye)
    {
        if(living.isSneaking())
        {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
            return new float[] { 0F, -1F/16F, 0F};
        }
        return headJoint;
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntityPlayer living, float partialTick, int eye)
    {
        return eyeOffset;
    }
}
