package me.ichun.mods.googlyeyes.common.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.ichun.mods.googlyeyes.common.GooglyEyes;
import me.ichun.mods.googlyeyes.common.model.ModelGooglyEye;
import me.ichun.mods.googlyeyes.common.tracker.GooglyTracker;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import me.ichun.mods.ichunutil.client.head.HeadHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

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
        HeadBase helper = HeadHandler.getHelperBase(living.getClass());
        if(helper != null)
        {
            EntityRenderer<?> render = Minecraft.getInstance().getRenderManager().getRenderer(living);
            if(!(render instanceof LivingRenderer))
            {
                return;
            }
            LivingRenderer<?, ?> renderer = (LivingRenderer<?, ?>)render;
            helper.setHeadModel(renderer);
            if(helper.headModel == null)
            {
                return;
            }

            GooglyTracker tracker = GooglyEyes.eventHandler.getGooglyTracker(living, helper);
            tracker.setLastUpdateRequest();
            if(!tracker.shouldRender())
            {
                return;
            }
            tracker.requireUpdate();

            int eyeCount = helper.getEyeCount(living);

            for(int i = 0; i < eyeCount; i++)
            {
                if(living.isInvisible() && helper.affectedByInvisibility(living, i))
                {
                    continue;
                }

                float eyeScale = helper.getEyeScale(living, stack, partialTicks, i) + helper.maxEyeSizeGrowth(living, i);

                if(eyeScale <= 0F)
                {
                    continue;
                }

                stack.push();

                // thepatcat: Creatures only get googly eyes in adulthood. It's science.
                helper.preChildEntHeadRenderCalls(living, stack, renderer);

                float[] joint = helper.getHeadJointOffset(living, stack, partialTicks, i);
                stack.translate(-joint[0], -joint[1], -joint[2]);

                stack.rotate(Vector3f.ZP.rotationDegrees(helper.getHeadRoll(living, stack, partialTicks, i)));
                stack.rotate(Vector3f.YP.rotationDegrees(helper.getHeadYaw(living, stack, partialTicks, i)));
                stack.rotate(Vector3f.XP.rotationDegrees(helper.getHeadPitch(living, stack, partialTicks, i)));

                float[] eyes = helper.getEyeOffsetFromJoint(living, stack, partialTicks, i);
                stack.translate(-(eyes[0] + helper.getEyeSideOffset(living, stack, partialTicks, i)), -eyes[1], -eyes[2]);

                stack.rotate(Vector3f.YP.rotationDegrees(helper.getEyeRotation(living, stack, partialTicks, i)));
                stack.rotate(Vector3f.XP.rotationDegrees(helper.getEyeTopRotation(living, stack, partialTicks, i)));

                stack.scale(eyeScale, eyeScale, eyeScale * 0.5F);

                IVertexBuilder buffer = bufferIn.getBuffer(RENDER_TYPE);

                int overlay = LivingRenderer.getPackedOverlay(living, 0.0F);

                float[] irisColours = helper.getIrisColours(living, stack, partialTicks, i);
                modelGooglyEye.renderIris(stack, buffer, packedLightIn, overlay, irisColours[0], irisColours[1], irisColours[2], 1F);

                float[] pupilColours = helper.getPupilColours(living, stack, partialTicks, i);

                float pupilScale = helper.getPupilScale(living, stack, partialTicks, i);
                stack.push();
                stack.scale(pupilScale, pupilScale, 1F);
                modelGooglyEye.movePupil(tracker.eyes[i].prevDeltaX + (tracker.eyes[i].deltaX - tracker.eyes[i].prevDeltaX) * partialTicks, tracker.eyes[i].prevDeltaY + (tracker.eyes[i].deltaY - tracker.eyes[i].prevDeltaY) * partialTicks, pupilScale);
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

                stack.pop();
            }
        }
    }
}
