package com.leon.saintsdragons.client.model.npc;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.server.entity.npc.IvyTheDragonMerchant;
import net.minecraft.util.Mth;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
// TODO(geckolib5-port): GeckoLib 5 removed AnimationState as-is (render-state rewrite). Check com.geckolib.animation.state.* (AnimationTest, ControllerState, KeyFrameEvent) or the new AnimationController predicate signature and update this import + all AnimationState usages below.
import software.bernie.geckolib.core.animation.AnimationState;
import com.geckolib.model.DefaultedEntityGeoModel;
// TODO(geckolib5-port): GeckoLib 5 moved model-data off GeoModel onto the new GeoRenderState objects. Check com.geckolib.renderer.base.GeoRenderState / GeoModel API and update.
import software.bernie.geckolib.model.data.EntityModelData;

public class IvyTheDragonMerchantModel extends DefaultedEntityGeoModel<IvyTheDragonMerchant> {
    public IvyTheDragonMerchantModel() {
        super(SaintsDragonsCommon.rl("ivy_oleander/ivy_oleander"));
    }

    @Override
    public void setCustomAnimations(IvyTheDragonMerchant entity, long instanceId, AnimationState<IvyTheDragonMerchant> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        EntityModelData modelData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        if (modelData == null) {
            return;
        }

        float headYawRad = Mth.clamp(modelData.netHeadYaw(), -45.0f, 45.0f) * Mth.DEG_TO_RAD;
        float headPitchRad = Mth.clamp(modelData.headPitch(), -25.0f, 25.0f) * Mth.DEG_TO_RAD;
        float deviationRad = (float) (entity.bodyRotDeviation.get(animationState.getPartialTick()) * Mth.DEG_TO_RAD);

        GeoBone head = getBoneOrNull("head");
        if (head != null && entity.shouldApplyHeadTracking()) {
            head.setRotY(head.getRotY() + headYawRad);
            head.setRotX(head.getRotX() + headPitchRad);
        }

        GeoBone body = getBoneOrNull("waist");
        if (body == null) {
            body = getBoneOrNull("body");
        }
        if (body != null) {
            body.setRotY(body.getRotY() - deviationRad);
        }
    }

    private GeoBone getBoneOrNull(String name) {
        var bone = getBone(name);
        return bone.isPresent() ? bone.get() : null;
    }
}