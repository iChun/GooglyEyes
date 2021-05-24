package me.ichun.mods.googlyeyes.common.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.ichun.mods.googlyeyes.common.GooglyEyes;
import me.ichun.mods.googlyeyes.common.tracker.GooglyTracker;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import me.ichun.mods.ichunutil.api.common.head.HeadInfoDelegate;
import me.ichun.mods.ichunutil.common.head.HeadHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

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

        HeadInfo helper = HeadHandler.getHelper(parentModel.dragonInstance.getClass());
        if(helper == null || helper.noFaceInfo || helper instanceof HeadInfoDelegate) //Dragons are special, do not allow HeadInfoDelegate.
        {
            return;
        }

        helper.headModel = parentModel.head;

        GooglyTracker tracker = GooglyEyes.eventHandler.getGooglyTracker(parentModel.dragonInstance, helper);
        tracker.setLastUpdateRequest();
        if(!tracker.shouldRender())
        {
            return;
        }
        tracker.requireUpdate();

        LivingEntity living = parentModel.dragonInstance;

        int headCount = helper.getHeadCount(living);

        for(int headIndex = 0; headIndex < headCount; headIndex++)
        {
            stack.push();

            helper.correctPosition(living, stack, lastPartialTick);

            int eyeCount = helper.getEyeCount(living);

            for(int i = 0; i < eyeCount; i++)
            {
                float eyeScale = helper.getEyeScale(living, stack, lastPartialTick, i);

                if(eyeScale <= 0F)
                {
                    continue;
                }

                stack.push();

                float[] eyes = helper.getEyeOffsetFromJoint(living, stack, lastPartialTick, i);
                stack.translate(-(eyes[0] + helper.getEyeSideOffset(living, stack, lastPartialTick, i)), -eyes[1], -eyes[2]);

                stack.rotate(Vector3f.YP.rotationDegrees(helper.getEyeRotation(living, stack, lastPartialTick, i)));
                stack.rotate(Vector3f.XP.rotationDegrees(helper.getEyeTopRotation(living, stack, lastPartialTick, i)));

                stack.scale(eyeScale, eyeScale, eyeScale * 0.5F);

                IRenderTypeBuffer.Impl bufferIn = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

                IVertexBuilder buffer = bufferIn.getBuffer(RENDER_TYPE);

                int overlay = LivingRenderer.getPackedOverlay(living, 0.0F);

                float[] corneaColours = helper.getCorneaColours(living, stack, lastPartialTick, i);
                modelGooglyEye.renderCornea(stack, buffer, packedLightIn, overlay, corneaColours[0], corneaColours[1], corneaColours[2], 1F);

                float[] irisColours = helper.getIrisColours(living, stack, lastPartialTick, i);

                float irisScale = helper.getIrisScale(living, stack, lastPartialTick, i);
                stack.push();
                stack.scale(irisScale, irisScale, 1F);
                modelGooglyEye.moveIris(tracker.eyes[0][i].prevDeltaX + (tracker.eyes[0][i].deltaX - tracker.eyes[0][i].prevDeltaX) * lastPartialTick, tracker.eyes[0][i].prevDeltaY + (tracker.eyes[0][i].deltaY - tracker.eyes[0][i].prevDeltaY) * lastPartialTick, irisScale);
                modelGooglyEye.renderIris(stack, buffer, packedLightIn, overlay, irisColours[0], irisColours[1], irisColours[2], 1F);
                stack.pop();

                if(helper.doesEyeGlow(living, i))
                {
                    buffer = bufferIn.getBuffer(RENDER_TYPE_EYES);
                    modelGooglyEye.renderCornea(stack, buffer, packedLightIn, overlay, corneaColours[0], corneaColours[1], corneaColours[2], 1F);

                    stack.push();
                    stack.scale(irisScale, irisScale, 1F);
                    modelGooglyEye.renderIris(stack, buffer, packedLightIn, overlay, irisColours[0], irisColours[1], irisColours[2], 1F);
                    stack.pop();
                }

                bufferIn.getBuffer(RENDER_TYPE_RESET);
                stack.pop();
            }

            stack.pop();
        }
    }
}
