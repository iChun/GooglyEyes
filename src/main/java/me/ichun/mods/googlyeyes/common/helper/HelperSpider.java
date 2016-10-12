package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.monster.EntitySpider;

public class HelperSpider extends HelperBase<EntitySpider>
{
    @Override
    public int getEyeCount(EntitySpider living)
    {
        return 6;
    }

    @Override
    public float[] getHeadJointOffset(EntitySpider living, int eye)
    {
        return new float[] { 0F, -15F/16F, 3F/16F }; //I love that I can use Tabula for this.
    }

    @Override
    public float getEyeScale(EntitySpider living, int eye)
    {
        return 0.8F;
    }

    @Override
    public float getEyeSideOffset(EntitySpider living, int eye)
    {
        if(eye <= 1)
        {
            return eye % 2 == 0 ? 1F / 16F : -1F / 16F;
        }
        else if(eye <= 3)
        {
            return eye % 2 == 0 ? 2F / 16F : -2F / 16F;
        }
        else
        {
            return eye % 2 == 0 ? 4F / 16F : -4F / 16F;
        }
    }

    @Override
    public float getEyeRotation(EntitySpider living, int eye)
    {
        if(eye >= 4)
        {
            return eye % 2 == 0 ? 45F : -45F;
        }
        return 0F;
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntitySpider living, int eye)
    {
        if(eye <= 1)
        {
            return new float[] { 0F, 0F, 8F / 16F };
        }
        else if(eye <= 3)
        {
            return new float[] { 0F, 3F/16F, 8F / 16F };
        }
        else
        {
            return new float[] { 0F, 1F/16F, 7.5F / 16F };
        }
    }

    @Override
    public float[] getIrisColours(EntitySpider living, int eye)
    {
        return new float[] { 0.8F, 0F, 0F };
    }

    @Override
    public boolean affectedByInvisibility(EntitySpider living, int eye)
    {
        return false;
    }

    @Override
    public boolean doesEyeGlow(EntitySpider living, int eye)
    {
        return true;
    }
}
