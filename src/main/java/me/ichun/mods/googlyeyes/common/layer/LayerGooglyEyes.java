package me.ichun.mods.googlyeyes.common.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.ichun.mods.googlyeyes.common.GooglyEyes;
import me.ichun.mods.googlyeyes.common.model.ModelGooglyEye;
import me.ichun.mods.googlyeyes.common.tracker.GooglyTracker;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import me.ichun.mods.ichunutil.common.head.HeadHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

@SuppressWarnings("unchecked")
public class LayerGooglyEyes<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M>
{
    private static final ResourceLocation TEX_GOOGLY_EYE = new ResourceLocation("googlyeyes","textures/model/modelgooglyeye.png");
    private static final RenderType RENDER_TYPE = RenderType.getEntityCutout(TEX_GOOGLY_EYE);
    private static final RenderType RENDER_TYPE_EYES = RenderType.getEyes(TEX_GOOGLY_EYE);
    private final ModelGooglyEye modelGooglyEye;

    public LayerGooglyEyes()
    {
        super((IEntityRenderer<T, M>)Minecraft.getInstance().getRenderManager().playerRenderer); // nonnull, we'll just pass the player renderer
        this.modelGooglyEye = new ModelGooglyEye();
    }

    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn, LivingEntity living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        HeadInfo parentHelper = HeadHandler.getHelper(living.getClass());
        if(parentHelper != null)
        {
            EntityRenderer<?> render = Minecraft.getInstance().getRenderManager().getRenderer(living);
            if(!(render instanceof LivingRenderer))
            {
                return;
            }
            LivingRenderer<?, ?> renderer = (LivingRenderer<?, ?>)render;

            if(!parentHelper.setup(living, renderer))
            {
                return;
            }

            GooglyTracker tracker = GooglyEyes.eventHandler.getGooglyTracker(living, parentHelper);
            tracker.setLastUpdateRequest();
            if(!tracker.shouldRender())
            {
                return;
            }
            tracker.requireUpdate();

            int headCount = parentHelper.getHeadCount(living);

            for(int headIndex = 0; headIndex < headCount; headIndex++)
            {
                HeadInfo helper = parentHelper.getHeadInfo(living, headIndex);
                
                if(helper.noFaceInfo)
                {
                    continue;
                }

                helper.setHeadModel(living, renderer);
                if(helper.headModel == null)
                {
                    continue;
                }

                int eyeCount = helper.getEyeCount(living);

                for(int eyeIndex = 0; eyeIndex < eyeCount; eyeIndex++)
                {
                    if(living.isInvisible() && helper.affectedByInvisibility(living, eyeIndex))
                    {
                        continue;
                    }

                    float eyeScale = helper.getEyeScale(living, stack, partialTicks, eyeIndex);

                    if(eyeScale <= 0F)
                    {
                        continue;
                    }

                    stack.push();

                    // thepatcat: Creatures only get googly eyes in adulthood. It's science.
                    helper.preChildEntHeadRenderCalls(living, stack, renderer);

                    float[] joint = helper.getHeadJointOffset(living, stack, partialTicks, headIndex);
                    stack.translate(-joint[0], -joint[1], -joint[2]);

                    stack.rotate(Vector3f.ZP.rotationDegrees(helper.getHeadRoll(living, stack, partialTicks, headIndex, eyeIndex)));
                    stack.rotate(Vector3f.YP.rotationDegrees(helper.getHeadYaw(living, stack, partialTicks, headIndex, eyeIndex)));
                    stack.rotate(Vector3f.XP.rotationDegrees(helper.getHeadPitch(living, stack, partialTicks, headIndex, eyeIndex)));

                    helper.postHeadTranslation(living, stack, partialTicks);

                    float[] eyes = helper.getEyeOffsetFromJoint(living, stack, partialTicks, eyeIndex);
                    stack.translate(-(eyes[0] + helper.getEyeSideOffset(living, stack, partialTicks, eyeIndex)), -eyes[1], -eyes[2]);

                    stack.rotate(Vector3f.YP.rotationDegrees(helper.getEyeRotation(living, stack, partialTicks, eyeIndex)));
                    stack.rotate(Vector3f.XP.rotationDegrees(helper.getEyeTopRotation(living, stack, partialTicks, eyeIndex)));

                    stack.scale(eyeScale, eyeScale, eyeScale * 0.4F);

                    //rendering the eyes
                    IVertexBuilder buffer = bufferIn.getBuffer(RENDER_TYPE);

                    int overlay = LivingRenderer.getPackedOverlay(living, 0.0F);

                    float[] corneaColours = helper.getCorneaColours(living, stack, partialTicks, eyeIndex);
                    modelGooglyEye.renderCornea(stack, buffer, packedLightIn, overlay, corneaColours[0], corneaColours[1], corneaColours[2], 1F);

                    float[] irisColours = helper.getIrisColours(living, stack, partialTicks, eyeIndex);

                    float irisScale = helper.getIrisScale(living, stack, partialTicks, eyeIndex);
                    stack.push();
                    stack.scale(irisScale, irisScale, 1F);
                    modelGooglyEye.moveIris(tracker.eyes[headIndex][eyeIndex].prevDeltaX + (tracker.eyes[headIndex][eyeIndex].deltaX - tracker.eyes[headIndex][eyeIndex].prevDeltaX) * partialTicks, tracker.eyes[headIndex][eyeIndex].prevDeltaY + (tracker.eyes[headIndex][eyeIndex].deltaY - tracker.eyes[headIndex][eyeIndex].prevDeltaY) * partialTicks, irisScale);
                    modelGooglyEye.renderIris(stack, buffer, packedLightIn, overlay, irisColours[0], irisColours[1], irisColours[2], 1F);
                    stack.pop();

                    if(helper.doesEyeGlow(living, eyeIndex))
                    {
                        buffer = bufferIn.getBuffer(RENDER_TYPE_EYES);
                        modelGooglyEye.renderCornea(stack, buffer, packedLightIn, overlay, corneaColours[0], corneaColours[1], corneaColours[2], 1F);

                        stack.push();
                        stack.scale(irisScale, irisScale, 1F);
                        modelGooglyEye.renderIris(stack, buffer, packedLightIn, overlay, irisColours[0], irisColours[1], irisColours[2], 1F);
                        stack.pop();
                    }
                    //end rendering the eyes

                    stack.pop();
                }
            }
        }
    }
}
