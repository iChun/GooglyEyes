package me.ichun.mods.googlyeyes.common.core;

import me.ichun.mods.googlyeyes.common.GooglyEyes;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config extends ConfigBase
{
    @CategoryDivider(name = "clientOnly")
    @Prop
    public boolean acidTripEyes = false;

    @Prop
    public List<String> disabledGoogly = new ArrayList<>();

    @Prop(min = 0, max = 100)
    public int googlyEyeChance = 20; //default 20%

    @Prop(validator = "overrideChance")
    public List<String> entityOverrideChance = new ArrayList<>();

    @Prop
    public List<String> nameOverride = new ArrayList<>();

    public HashMap<ResourceLocation, Integer> entityOverrideChanceParsed = new HashMap<>();

    public boolean overrideChance(Object o)
    {
        if(o instanceof String)
        {
            String[] split = ((String)o).split(",");
            if(split.length == 2)
            {
                try
                {
                    new ResourceLocation(split[0]);
                    int chance = Integer.parseInt(split[1]);
                    return chance >= 0 && chance <= 100;
                }
                catch(ResourceLocationException | NumberFormatException ignored){}
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public String getModId()
    {
        return GooglyEyes.MOD_ID;
    }

    @Nonnull
    @Override
    public String getConfigName()
    {
        return GooglyEyes.MOD_NAME;
    }

    @Nonnull
    @Override
    public ModConfig.Type getConfigType()
    {
        return ModConfig.Type.CLIENT;
    }

    @Override
    public void onConfigLoaded()
    {
        Minecraft.getInstance().execute(this::parseOverrideChance);
    }

    public void parseOverrideChance()
    {
        entityOverrideChanceParsed.clear();

        for(String s : entityOverrideChance)
        {
            String[] split = s.split(",");
            entityOverrideChanceParsed.put(new ResourceLocation(split[0]), Integer.parseInt(split[1]));
        }
    }
}
