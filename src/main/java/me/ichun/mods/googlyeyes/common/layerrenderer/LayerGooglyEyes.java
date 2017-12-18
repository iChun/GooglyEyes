package me.ichun.mods.googlyeyes.common.layerrenderer;

import me.ichun.mods.googlyeyes.common.GooglyEyes;
import me.ichun.mods.googlyeyes.common.model.ModelGooglyEye;
import me.ichun.mods.googlyeyes.common.tracker.GooglyTracker;
import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import me.ichun.mods.ichunutil.client.entity.head.HeadHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
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
        HeadBase helper = HeadHandler.getHelperBase(living.getClass());
        if(helper != null)
        {
            Render render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(living);
            if(!(render instanceof RenderLivingBase))
            {
                return;
            }
            RenderLivingBase renderer = (RenderLivingBase)render;
            HeadHandler.setHeadModel(helper, renderer);
            if(helper.headModel == null)
            {
                return;
            }

            GooglyTracker tracker = GooglyEyes.eventHandler.getGooglyTracker(living, helper);
            if(!tracker.shouldRender())
            {
                return;
            }
            tracker.requireUpdate();

            int eyeCount = helper.getEyeCount(living);

            GlStateManager.enableNormalize();
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);

            for(int i = 0; i < eyeCount; i++)
            {
                if(living.isInvisible() && helper.affectedByInvisibility(living, i))
                {
                    continue;
                }

                float eyeScale = helper.getEyeScale(living, partialTicks, i) + helper.maxEyeSizeGrowth(living, i);

                if(eyeScale <= 0F)
                {
                    continue;
                }

                GlStateManager.pushMatrix();

                // thepatcat: Creatures only get googly eyes in adulthood. It's science.
                helper.preChildEntHeadRenderCalls(living, renderer);

                float[] joint = helper.getHeadJointOffset(living, partialTicks, i);
                GlStateManager.translate(-joint[0], -joint[1], -joint[2]);

                GlStateManager.rotate(helper.getHeadYaw(living, partialTicks, i), 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(helper.getHeadPitch(living, partialTicks, i), 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(helper.getHeadRoll(living, partialTicks, i), 0.0F, 0.0F, 1.0F);

                float[] eyes = helper.getEyeOffsetFromJoint(living, partialTicks, i);
                GlStateManager.translate(-(eyes[0] + helper.getEyeSideOffset(living, partialTicks, i)), -eyes[1], -eyes[2]);

                GlStateManager.rotate(helper.getEyeRotation(living, partialTicks, i), 0.0F, 1.0F, 0.0F);

                GlStateManager.scale(eyeScale, eyeScale, eyeScale * 0.5F);

                textureManager.bindTexture(texGooglyEye);

                float[] irisColours = helper.getIrisColours(living, partialTicks, i);
                GlStateManager.color(irisColours[0], irisColours[1], irisColours[2]);
                modelGooglyEye.renderIris(0.0625F);

                float[] pupilColours = helper.getPupilColours(living, partialTicks, i);
                GlStateManager.color(pupilColours[0], pupilColours[1], pupilColours[2]);

                float pupilScale = helper.getPupilScale(living, partialTicks, i);
                GlStateManager.pushMatrix();
                GlStateManager.scale(pupilScale, pupilScale, 1F);
                modelGooglyEye.movePupil(tracker.eyes[i].prevDeltaX + (tracker.eyes[i].deltaX - tracker.eyes[i].prevDeltaX) * partialTicks, tracker.eyes[i].prevDeltaY + (tracker.eyes[i].deltaY - tracker.eyes[i].prevDeltaY) * partialTicks, pupilScale);
                modelGooglyEye.renderPupil(0.0625F);
                GlStateManager.popMatrix();

                if(helper.doesEyeGlow(living, i))
                {
                    GlStateManager.enableBlend();
                    GlStateManager.disableAlpha();
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

                    GlStateManager.color(irisColours[0], irisColours[1], irisColours[2]);
                    modelGooglyEye.renderIris(0.0625F);

                    GlStateManager.color(pupilColours[0], pupilColours[1], pupilColours[2]);

                    GlStateManager.pushMatrix();
                    GlStateManager.scale(pupilScale, pupilScale, 1F);
                    modelGooglyEye.renderPupil(0.0625F);
                    GlStateManager.popMatrix();

                    GlStateManager.disableBlend();
                    GlStateManager.enableAlpha();
                }

                GlStateManager.popMatrix();
            }
            GlStateManager.color(1F, 1F, 1F, 1F);
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return true;
    }
}
