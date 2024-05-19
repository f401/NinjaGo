package net.yx.ninjago.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.yx.ninjago.NinjaGo;
import net.yx.ninjago.world.entity.projectile.BoomerangEntity;

// Made with Blockbench 4.9.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class BoomerangModel extends EntityModel<BoomerangEntity> {
    // This layer location should be baked with EntityRendererProvider.Context in
    // the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(NinjaGo.MOD_ID, "boomerang_entity"), "main");
    private final ModelPart bb_main;

    public BoomerangModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(0.0F, -6.25F, -1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(1.0F, -5.75F, -1.0F, 0.5F, 3.5F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(0.0F, -7.375F, -1.125F, 0.5F, 4.5F, 1.25F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r1 = bb_main.addOrReplaceChild("cube_r1",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-0.4314F, 0.514F, -0.625F, 4.5F, 0.5F, 1.25F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-0.9314F, 1.514F, -0.5F, 3.5F, 0.5F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-0.9314F, 0.514F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.3442F, -3.182F, -0.5F, 0.0F, 0.0F, 0.3927F));

        PartDefinition cube_r2 = bb_main.addOrReplaceChild("cube_r2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.0066F, -1.9355F, -0.5F, 3.5F, 0.5F, 1.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.3442F, -3.182F, -0.5F, 0.0F, 0.0F, -0.7854F));

        PartDefinition cube_r3 = bb_main.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-1.4355F, -1.4934F, -0.5F, 1.0F, 4.5F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-0.9355F, -0.8684F, -0.625F, 0.5F, 4.875F, 1.25F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.3442F, -3.182F, -0.5F, 0.0F, 0.0F, 0.7854F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void setupAnim(BoomerangEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
