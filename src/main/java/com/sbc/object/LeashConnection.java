package com.sbc.object;

import net.minecraft.entity.Entity;

public class LeashConnection {
    private Entity holder;
    private Entity leashedEntity;

    public LeashConnection(Entity holder, Entity leashedEntity){
        this.holder = holder;
        this.leashedEntity = leashedEntity;
    }

    public Entity getHolder() {
        return holder;
    }

    public Entity getLeashedEntity() {
        return leashedEntity;
    }
}
