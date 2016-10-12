package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;

public class HelperEnderman extends HelperBase<EntityEnderman>
{
    @Override
    public float[] getHeadJointOffset(EntityEnderman living, int eye)
    {
        if(living.isScreaming())
        {
            return new float[] { 0F, 18F/16F, 0F };
        }
        else
        {
            return new float[] { 0F, 13F/16F, 0F };
        }
    }

    @Override
    public float getEyeScale(EntityEnderman living, int eye)
    {
        return 0.85F;
    }

    @Override
    public float getEyeSideOffset(EntityEnderman living, int eye)
    {
        return eye == 0 ? 2.5F/16F : -2.5F/16F;
    }

    @Override
    public float getPupilScale(EntityEnderman living, int eye)
    {
        if(living.isScreaming())
        {
            return 0.4F;
        }
        return super.getPupilScale(living, eye);
    }

    @Override
    public float[] getIrisColours(EntityEnderman living, int eye)
    {
        return new float[] { 0.88F, 0.47F, 0.98F };
    }

    @Override
    public float[] getPupilColours(EntityEnderman living, int eye)
    {
        return new float[] { 0.8F, 0F, 0.98F };
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntityEnderman living, int eye)
    {
        return new float[] { 0F, 3.5F/16F, 4F/16F};
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
