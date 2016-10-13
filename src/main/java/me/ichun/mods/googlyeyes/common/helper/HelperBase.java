package me.ichun.mods.googlyeyes.common.helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Random;

public class HelperBase<E extends EntityLivingBase>
{
    //Defaults. Works on Bipeds
    public float[] headJoint = new float[3];
    public float[] eyeOffset = new float[] { 0F, 4F/16F, 4F/16F}; //I love that I can use Tabula for this.
    public float[] irisColour = new float[] { 0.9F, 0.9F, 0.9F };
    public float[] pupilColour = new float[] { 0.0F, 0.0F, 0.0F };
    public float halfInterpupillaryDistance = 2F/16F;
    public float eyeScale = 0.75F;

    public Random livingRand = new Random();
    public int[] acidTime;

    public HelperBase setHeadJoint(float jointX, float jointY, float jointZ)
    {
        headJoint = new float[] { jointX, jointY, jointZ };
        return this;
    }

    public HelperBase setEyeOffset(float offsetX, float offsetY, float offsetZ)
    {
        eyeOffset = new float[] { offsetX, offsetY, offsetZ };
        return this;
    }

    public HelperBase setHalfInterpupillaryDistance(float dist)
    {
        halfInterpupillaryDistance = dist;
        return this;
    }

    public HelperBase setEyeScale(float scale)
    {
        eyeScale = scale;
        return this;
    }

    public float[] getHeadJointOffset(E living, float partialTick, int eye)
    {
        return headJoint;
    }

    public float[] getEyeOffsetFromJoint(E living, float partialTick, int eye)
    {
        return eyeOffset;
    }

    public int getEyeCount(E living)
    {
        return 2;
    }

    public float maxEyeSizeGrowth(E living, int eye)
    {
        return 0F;
    }

    public float getEyeSideOffset(E living, float partialTick, int eye)
    {
        return eye == 0 ? halfInterpupillaryDistance : -halfInterpupillaryDistance;
    }

    public float getEyeScale(E living, float partialTick, int eye)
    {
        return eyeScale;
    }

    public float getEyeRotation(E living, float partialTick, int eye)
    {
        return 0F;
    }

    public float getPupilScale(E living, float partialTick, int eye) //TODO trigger acid on config or if the entity has a potion effect
    {
        livingRand.setSeed(Math.abs(living.hashCode()) * 1000);
        int eyeCount = getEyeCount(living);
        if(acidTime == null || acidTime.length < eyeCount)
        {
            acidTime = new int[eyeCount];
        }
        for(int i = 0; i < eyeCount; i++)
        {
            acidTime[i] = 20 + livingRand.nextInt(20);
        }
        return 0.3F + ((float)Math.sin(Math.toRadians((living.ticksExisted + partialTick) / acidTime[eye] * 360F)) + 1F) / 2F;

        //        return 1F;
    }

    public float[] getIrisColours(E living, float partialTick, int eye)
    {
        return irisColour;
    }

    public float[] getPupilColours(E living, float partialTick, int eye)
    {
        return pupilColour;
    }

    public float getHeadYaw(E living, float partialTick, int eye)
    {
        return (living.prevRotationYawHead + ((living.rotationYawHead - living.prevRotationYawHead) * partialTick)) - (living.prevRenderYawOffset + ((living.renderYawOffset - living.prevRenderYawOffset) * partialTick));
    }

    public float getHeadPitch(E living, float partialTick, int eye)
    {
        return living.prevRotationPitch + ((living.rotationPitch - living.prevRotationPitch) * partialTick);
    }

    public float getHeadRoll(E living, float partialTick, int eye)
    {
        return 0;
    }

    public boolean affectedByInvisibility(E living, int eye)
    {
        return true;
    }

    public boolean doesEyeGlow(E living, int eye)
    {
        return false;
    }

    //TODO handle child entities?
    //TODO dragons...?
    //TODO Silverfish/Endermites?
    //TODO iron golems?
    //TODO shulkers and polar bears?
    public static HashMap<Class<? extends EntityLivingBase>, HelperBase> modelOffsetHelpers = new HashMap<Class<? extends EntityLivingBase>, HelperBase>() {{
        put(EntityPlayer.class, new HelperPlayer());

//        put(EntityBat.class, new HelperBat()); //Bats will need their own helpers.

        put(EntityBlaze.class, new HelperBase().setEyeOffset(0F, 0F, 4F/16F));
        put(EntityChicken.class, new HelperBase().setHeadJoint(0F, -15F/16F, 4F/16F).setEyeOffset(0F, 4.5F/16F, 2F/16F).setHalfInterpupillaryDistance(1.5F / 16F).setEyeScale(0.375F));
        put(EntityCow.class, new HelperBase().setHeadJoint(0F, -4F/16F, 8F/16F).setEyeOffset(0F, 1F/16F, 6F/16F).setHalfInterpupillaryDistance(3F / 16F));
        put(EntityCreeper.class, new HelperBase().setHeadJoint(0F, -6F/16F, 0F).setEyeOffset(0F, 5F/16F, 4F/16F));
        put(EntityEnderman.class, new HelperEnderman());
        put(EntityGhast.class, new HelperGhast());

//        put(EntityGuardian.class, new HelperGuardian());
//        put(EntityHorse.class, new HelperHorse());

        put(EntityMagmaCube.class, new HelperMagmaCube());
        put(EntityOcelot.class, new HelperOcelot());
        put(EntityPig.class, new HelperBase().setHeadJoint(0F, -12F/16F, 6F/16F).setEyeOffset(0F, 0.5F/16F, 8F/16F).setHalfInterpupillaryDistance(3F/16F));
        put(EntityRabbit.class, new HelperBase().setHeadJoint(0F, -16F/16F * 0.6F - (1F * 0.6F), 1F/16F * 0.6F).setEyeOffset(0F, 3F/16F * 0.6F, 5F/16F * 0.6F).setHalfInterpupillaryDistance(1F / 16F * 0.6F).setEyeScale(0.6F  * 0.6F)); //Bunnie scaling is annoying AF

//        put(EntitySheep.class, new HelperSheep());

        put(EntitySkeleton.class, new HelperBase());
        put(EntitySlime.class, new HelperSlime());
        put(EntitySnowman.class, new HelperBase().setHeadJoint(0F, -4F/16F, 0F/16F).setEyeOffset(0F, 7.5F/16, 5F/16F).setHalfInterpupillaryDistance(1.5F / 16F).setEyeScale(1F));
        put(EntitySpider.class, new HelperSpider());

        put(EntitySquid.class, new HelperTesting());
        put(EntityVillager.class, new HelperTesting());
        put(EntityWitch.class, new HelperTesting());

//        put(EntityWitch.class, new HelperWither());

        put(EntityWolf.class, new HelperTesting());

        put(EntityZombie.class, new HelperBase()); //TODO villager zombies?
    }};

    @Nullable
    public static HelperBase getHelperBase(Class<? extends EntityLivingBase> clz)
    {
        HelperBase helper = modelOffsetHelpers.get(clz);
        Class clzz = clz.getSuperclass();
        while(helper == null && clzz != EntityLivingBase.class)
        {
            helper = getHelperBase(clzz);
            if(helper != null)
            {
                modelOffsetHelpers.put(clzz, helper);
            }
            clzz = clzz.getSuperclass();
        }
        return helper;
    }
}
