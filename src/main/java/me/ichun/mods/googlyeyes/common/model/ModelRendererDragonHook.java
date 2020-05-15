package me.ichun.mods.googlyeyes.common.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.ichun.mods.googlyeyes.common.GooglyEyes;
import me.ichun.mods.googlyeyes.common.tracker.GooglyTracker;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import me.ichun.mods.ichunutil.client.head.HeadHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class ModelRendererDragonHook extends ModelRenderer
{
    private static final ResourceLocation TEX_GOOGLY_EYE = new ResourceLocation("googlyeyes","textures/model/modelgooglyeye.png");
    private static final RenderType RENDER_TYPE = RenderType.getEntityCutout(TEX_GOOGLY_EYE);
    private static final RenderType RENDER_TYPE_EYES = RenderType.getEyes(TEX_GOOGLY_EYE);
    private static final RenderType RENDER_TYPE_RESET = RenderType.getEyes(new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png"));
    private final ModelGooglyEye modelGooglyEye;


    public EnderDragonRenderer.EnderDragonModel parentModel;
    public int renderCount;
    public float lastPartialTick;

    public ModelRendererDragonHook(EnderDragonRenderer.EnderDragonModel model)
    {
        super(model);
        this.modelGooglyEye = new ModelGooglyEye();

        parentModel = model;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render(MatrixStack stack, IVertexBuilder bufferInUnused, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        if(parentModel.dragonInstance == null)
        {
            return;
        }

        if(lastPartialTick != parentModel.partialTicks) // new render
        {
            lastPartialTick = parentModel.partialTicks;
            renderCount = 0;
        }
        renderCount++;

        boolean render = renderCount == 3;
        if(renderCount == 2 && !(parentModel.dragonInstance.deathTicks > 0))
        {
            render = true;
        }

        if(!render)
        {
            return;
        }

        HeadBase helper = HeadHandler.getHelperBase(parentModel.dragonInstance.getClass());
        if(helper == null)
        {
            return;
        }

        helper.headModel = new ModelRenderer[] { parentModel.head };

        GooglyTracker tracker = GooglyEyes.eventHandler.getGooglyTracker(parentModel.dragonInstance, helper);
        if(!tracker.shouldRender())
        {
            return;
        }
        tracker.requireUpdate();

        LivingEntity living = parentModel.dragonInstance;

        int eyeCount = helper.getEyeCount(living);

        for(int i = 0; i < eyeCount; i++)
        {
            float eyeScale = helper.getEyeScale(living, stack, lastPartialTick, i) + helper.maxEyeSizeGrowth(living, i);

            if(eyeScale <= 0F)
            {
                continue;
            }

            stack.push();

            float[] eyes = helper.getEyeOffsetFromJoint(living, stack, lastPartialTick, i);
            stack.translate(-(eyes[0] + helper.getEyeSideOffset(living, stack, lastPartialTick, i)), -eyes[1], -eyes[2]);

            stack.rotate(Vector3f.YP.rotationDegrees(helper.getEyeRotation(living, stack, lastPartialTick, i)));

            stack.scale(eyeScale, eyeScale, eyeScale * 0.5F);

            IRenderTypeBuffer.Impl bufferIn = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

            IVertexBuilder buffer = bufferIn.getBuffer(RENDER_TYPE);

            int overlay = LivingRenderer.getPackedOverlay(living, 0.0F);

            float[] irisColours = helper.getIrisColours(living, stack, lastPartialTick, i);
            modelGooglyEye.renderIris(stack, buffer, packedLightIn, overlay, irisColours[0], irisColours[1], irisColours[2], 1F);

            float[] pupilColours = helper.getPupilColours(living, stack, lastPartialTick, i);

            float pupilScale = helper.getPupilScale(living, stack, lastPartialTick, i);
            stack.push();
            stack.scale(pupilScale, pupilScale, 1F);
            modelGooglyEye.movePupil(tracker.eyes[i].prevDeltaX + (tracker.eyes[i].deltaX - tracker.eyes[i].prevDeltaX) * lastPartialTick, tracker.eyes[i].prevDeltaY + (tracker.eyes[i].deltaY - tracker.eyes[i].prevDeltaY) * lastPartialTick, pupilScale);
            modelGooglyEye.renderPupil(stack, buffer, packedLightIn, overlay, pupilColours[0], pupilColours[1], pupilColours[2], 1F);
            stack.pop();

            if(helper.doesEyeGlow(living, i))
            {
                buffer = bufferIn.getBuffer(RENDER_TYPE_EYES);
                modelGooglyEye.renderIris(stack, buffer, packedLightIn, overlay, irisColours[0], irisColours[1], irisColours[2], 1F);

                stack.push();
                stack.scale(pupilScale, pupilScale, 1F);
                modelGooglyEye.renderPupil(stack, buffer, packedLightIn, overlay, pupilColours[0], pupilColours[1], pupilColours[2], 1F);
                stack.pop();
            }

            bufferIn.getBuffer(RENDER_TYPE_RESET);
            stack.pop();
        }
    }
}
