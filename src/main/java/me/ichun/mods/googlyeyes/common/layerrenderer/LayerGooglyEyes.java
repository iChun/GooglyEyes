package me.ichun.mods.googlyeyes.common.layerrenderer;

import me.ichun.mods.googlyeyes.common.GooglyEyes;
import me.ichun.mods.googlyeyes.common.helper.HelperBase;
import me.ichun.mods.googlyeyes.common.model.ModelGooglyEye;
import me.ichun.mods.googlyeyes.common.tracker.GooglyTracker;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("unchecked")
public class LayerGooglyEyes
        implements LayerRenderer<EntityLivingBase>
{
    public static final ResourceLocation texGooglyEye = new ResourceLocation("googlyeyes","textures/model/modelgooglyeye.png");
    private ModelGooglyEye modelGooglyEye;
    private TextureManager textureManager;

    public LayerGooglyEyes(TextureManager manager)
    {
        modelGooglyEye = new ModelGooglyEye();
        textureManager = manager;
    }

    @Override
    public void doRenderLayer(EntityLivingBase living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        //TODO do not track/render if no helper exists for it.
        GooglyTracker tracker = GooglyEyes.eventHandler.getGooglyTracker(living);
        tracker.requireUpdate();

        HelperBase helper = HelperBase.getHelperBase(living.getClass());
        if(helper != null)
        {
            int eyeCount = helper.getEyeCount(living);

            for(int i = 0; i < eyeCount; i++)
            {
                float[] joint = helper.getHeadJointOffset(living, i);
                float[] eyes = helper.getEyeOffsetFromJoint(living, i);

                GlStateManager.pushMatrix();

                GlStateManager.translate(joint[0], joint[1], joint[2]);

                GlStateManager.rotate(helper.getHeadYaw(living, partialTicks, i), 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(helper.getHeadPitch(living, partialTicks, i), 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(helper.getHeadRoll(living, partialTicks, i), 0.0F, 0.0F, -1.0F);

                //TODO where do I do eye scaling?

                GlStateManager.translate(eyes[0] + helper.getEyeSideOffset(living, i), eyes[1], eyes[2]);

                GlStateManager.rotate(helper.getEyeRotation(living, i), 0.0F, 1.0F, 0.0F);

                float eyeScale = helper.getEyeScale(living, i);

                GlStateManager.scale(eyeScale, eyeScale, eyeScale * 0.5F);

                textureManager.bindTexture(texGooglyEye);

                //                modelGooglyEye.movePupilAndRender(0, 0, 0.0625F);
                GlStateManager.color(1F, 1F, 1F);
                modelGooglyEye.renderIris(0.0625F);

                GlStateManager.color(0F, 0F, 0F);
                modelGooglyEye.renderPupil(0.0625F);

                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}
