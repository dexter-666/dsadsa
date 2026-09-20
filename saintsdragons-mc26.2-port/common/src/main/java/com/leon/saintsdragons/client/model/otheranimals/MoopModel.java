package com.leon.saintsdragons.client.model.otheranimals;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import com.leon.saintsdragons.server.entity.otheranimals.Moop;
import com.geckolib.model.DefaultedEntityGeoModel;

public class MoopModel extends DefaultedEntityGeoModel<Moop> {
    public MoopModel() {
        super(SaintsDragonsCommon.rl("moop/moop"));
    }
}
