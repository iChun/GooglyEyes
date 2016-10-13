package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.monster.EntityGuardian;

public class HelperGuardian extends HelperBase<EntityGuardian>
{
    public float[] irisColourElder = new float[] { 203F / 255F, 177F / 255F, 165F / 255F };
    public float[] pupilColourElder = new float[] { 237F / 255F, 228F / 255F, 224F / 255F };

    public HelperGuardian()
    {
        headJoint = new float[]{ 0F, 0F, 0F };
        eyeOffset = new float[]{ 0F, -16F/16F, 8F/16F };
        irisColour = new float[] { 214F / 255F, 211F / 255F, 203F / 255F };
        pupilColour = new float[] { 101F / 255F, 35F / 255F, 31F / 255F };
        halfInterpupillaryDistance = 0F;
        eyeScale = 2F;
    }

    @Override
    public int getEyeCount(EntityGuardian living)
    {
        return 1;
    }

    @Override
    public float[] getIrisColours(EntityGuardian living, float partialTick, int eye)
    {
        return living.isElder() ? irisColourElder : irisColour;
    }

    @Override
    public float[] getPupilColours(EntityGuardian living, float partialTick, int eye)
    {
        return living.isElder() ? pupilColourElder : pupilColour;
    }
}
