package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.monster.EntityEnderman;

public class HelperEnderman extends HelperBase<EntityEnderman>
{
    public float[] headJointScreaming = new float[] { 0F, 18F/16F, 0F };

    public HelperEnderman()
    {
        headJoint = new float[] { 0F, 13F/16F, 0F };
        eyeOffset = new float[] { 0F, 3.5F/16F, 4F/16F };
        irisColour = new float[] { 0.88F, 0.47F, 0.98F };
        pupilColour = new float[] { 0.8F, 0F, 0.98F };
        halfInterpupillaryDistance = 2.5F/16F;
        eyeScale = 0.85F;
    }

    @Override
    public float[] getHeadJointOffset(EntityEnderman living, float partialTick, int eye)
    {
        if(living.isScreaming())
        {
            return headJointScreaming;
        }
        else
        {
            return headJoint;
        }
    }

    @Override
    public float getPupilScale(EntityEnderman living, float partialTick, int eye)
    {
        if(living.isScreaming())
        {
            return 0.4F;
        }
        return super.getPupilScale(living, partialTick, eye);
    }

    @Override
    public boolean affectedByInvisibility(EntityEnderman living, int eye)
    {
        return false;
    }

    @Override
    public boolean doesEyeGlow(EntityEnderman living, int eye)
    {
        return true;
    }
}
