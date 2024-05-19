package net.yx.ninjago.client.renderer;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.yx.ninjago.client.model.BoomerangModel;
import net.yx.ninjago.world.entity.projectile.BoomerangEntity;

public class BoomerangRenderer extends EntityRenderer<BoomerangEntity> {
    private EntityModel<BoomerangEntity> model;

    public BoomerangRenderer(Context pContext) {
        super(pContext);
        model = new BoomerangModel(pContext.bakeLayer(BoomerangModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(BoomerangEntity pEntity) {
        return new ResourceLocation("textures/item/boomerang.png");
    }

    @Override
    public void render(BoomerangEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack,
            MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();

        pPoseStack.scale(1.6f, 1.6f, 1.6f);
        
        if (!pEntity.isInGround()) {
            pEntity.spin += 20 * pPartialTick;
        }
        
        pPoseStack.mulPose(Axis.YP.rotationDegrees(pEntity.spin));
        
        pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot()) + 90.0F));

        // rotate it into a horizontal position
        pPoseStack.mulPose(Axis.of(new Vector3f(1f, 0f, 1f)).rotationDegrees(90f));

        VertexConsumer vertexconsumer = pBuffer.getBuffer(this.model.renderType(getTextureLocation(pEntity)));

        this.model.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,
                1.0F);

        pPoseStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }
}
