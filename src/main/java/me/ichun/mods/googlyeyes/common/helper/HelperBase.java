package me.ichun.mods.googlyeyes.common.helper;

import me.ichun.mods.googlyeyes.common.GooglyEyes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
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
    public float[] eyeOffset = new float[] { 0F, 4F/16F, 4F/16F }; //I love that I can use Tabula for this.
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

    public float getPupilScale(E living, float partialTick, int eye)
    {
        if(GooglyEyes.config.acidTripEyes == 1 || !living.getActivePotionEffects().isEmpty())
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
        }
        return 1F;
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
        return interpolateRotation(living.prevRotationYawHead, living.rotationYawHead, partialTick) - interpolateRotation(living.prevRenderYawOffset, living.renderYawOffset, partialTick);
    }

    public float getHeadPitch(E living, float partialTick, int eye)
    {
        return interpolateRotation(living.prevRotationPitch, living.rotationPitch, partialTick);
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

    public float getHeadYawForTracker(E living, int eye)
    {
        return getHeadYawForTracker(living);
    }

    public float getHeadPitchForTracker(E living, int eye)
    {
        return getHeadPitchForTracker(living);
    }

    public float getHeadYawForTracker(E living)
    {
        return living.rotationYawHead;
    }

    public float getHeadPitchForTracker(E living)
    {
        return living.rotationPitch;
    }

    public static HashMap<Class<? extends EntityLivingBase>, HelperBase> modelOffsetHelpers = new HashMap<Class<? extends EntityLivingBase>, HelperBase>() {{
        put(EntityPlayer.class, new HelperPlayer());
        put(EntityBat.class, new HelperBat());
        put(EntityBlaze.class, new HelperBase().setEyeOffset(0F, 0F, 4F/16F));
        put(EntityChicken.class, new HelperBase().setHeadJoint(0F, -15F/16F, 4F/16F).setEyeOffset(0F, 4.5F/16F, 2F/16F).setHalfInterpupillaryDistance(1.5F / 16F).setEyeScale(0.375F));
        put(EntityCow.class, new HelperBase().setHeadJoint(0F, -4F/16F, 8F/16F).setEyeOffset(0F, 1F/16F, 6F/16F).setHalfInterpupillaryDistance(3F / 16F));
        put(EntityCreeper.class, new HelperBase().setHeadJoint(0F, -6F/16F, 0F).setEyeOffset(0F, 5F/16F, 4F/16F)); //make creeper maaaaaaad with narrowing pupils
        put(EntityDragon.class, new HelperDragon());
        put(EntityDonkey.class, new HelperHorse());
        put(EntityEnderman.class, new HelperEnderman());
        put(EntityEndermite.class, new HelperEndermite());
        put(EntityGhast.class, new HelperGhast());
        put(EntityGuardian.class, new HelperGuardian());
        put(EntityHorse.class, new HelperHorse());
        put(EntityHusk.class, new HelperBase());
        put(EntityIronGolem.class, new HelperBase().setHeadJoint(0F, 7F/16F, 2F/16F).setEyeOffset(0F, 6F/16F, 5.5F/16F));
        put(EntityMagmaCube.class, new HelperMagmaCube());
        put(EntityMooshroom.class, new HelperBase().setHeadJoint(0F, -4F/16F, 8F/16F).setEyeOffset(0F, 1F/16F, 6F/16F).setHalfInterpupillaryDistance(3F / 16F));
        put(EntityOcelot.class, new HelperOcelot());
        put(EntityPig.class, new HelperBase().setHeadJoint(0F, -12F/16F, 6F/16F).setEyeOffset(0F, 0.5F/16F, 8F/16F).setHalfInterpupillaryDistance(3F/16F));
        put(EntityPigZombie.class, new HelperPigZombie());
        put(EntityPolarBear.class, new HelperBase().setHeadJoint(0F, -10F/16F, 16F/16F).setEyeOffset(0F, -0.5F/16F, 3F/16F).setEyeScale(0.4F));
        put(EntityRabbit.class, new HelperRabbit());
        put(EntitySheep.class, new HelperSheep());
        put(EntityShulker.class, new HelperShulker());
        put(EntitySilverfish.class, new HelperSilverfish());
        put(EntitySkeleton.class, new HelperBiped());
        put(EntitySkeletonHorse.class, new HelperHorse());
        put(EntityStray.class, new HelperBiped());
        put(EntitySlime.class, new HelperSlime());
        put(EntitySnowman.class, new HelperBase().setHeadJoint(0F, -4F/16F, 0F/16F).setEyeOffset(0F, 7.5F/16, 5F/16F).setHalfInterpupillaryDistance(1.5F / 16F).setEyeScale(1F));
        put(EntitySpider.class, new HelperSpider());
        put(EntitySquid.class, new HelperSquid());
        put(EntityVillager.class, new HelperBase().setEyeOffset(0F, 3.2F/16F, 4F/16F).setHalfInterpupillaryDistance(1.9F / 16F).setEyeScale(0.7F));
        put(EntityVindicator.class, new HelperBase().setEyeOffset(0F, 3.2F/16F, 4F/16F).setHalfInterpupillaryDistance(1.9F / 16F).setEyeScale(0.7F));
        put(EntityVex.class, new HelperBiped());
        put(EntityWitch.class, new HelperBase().setEyeOffset(0F, 3.2F/16F, 4F/16F).setHalfInterpupillaryDistance(1.9F / 16F).setEyeScale(0.7F));
        put(EntityWither.class, new HelperWither());
        put(EntityWolf.class, new HelperWolf());
        put(EntityZombie.class, new HelperBase());
        put(EntityZombieHorse.class, new HelperHorse());
        put(EntityZombieVillager.class, new HelperBase().setEyeOffset(0F, 3.2F/16F, 4F/16F).setHalfInterpupillaryDistance(1.9F / 16F).setEyeScale(0.7F));
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
                break;
            }
            clzz = clzz.getSuperclass();
        }
        return helper;
    }

    public float interpolateRotation(float prevAngle, float nextAngle, float partialTick)
    {
        float f = nextAngle - prevAngle;
        while(f < -180.0F) { f += 360.0F; }
        while(f >= 180.0F) { f -= 360.0F; }
        return prevAngle + partialTick * f;
    }
}
