package me.ichun.mods.googlyeyes.common.core;

import me.ichun.mods.googlyeyes.common.GooglyEyes;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;

import java.io.File;

public class Config extends ConfigBase
{
    @ConfigProp(changeable = false)
    public String[] disabledGoogly = new String[0];

    @ConfigProp
    @IntBool
    public int acidTripEyes = 0;

    @ConfigProp
    @IntMinMax(min = 0, max = 100)
    public int googlyEyeChance = 20; //default 20%

    public Config(File file)
    {
        super(file);
    }

    @Override
    public String getModId()
    {
        return GooglyEyes.MOD_ID;
    }

    @Override
    public String getModName()
    {
        return "Googly Eyes";
    }
}
