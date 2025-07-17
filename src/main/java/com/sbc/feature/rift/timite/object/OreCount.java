package com.sbc.feature.rift.timite.object;

public class OreCount {
    public final OreType type;
    public int count;
    public int ratio;
    public int sets;

    public OreCount(OreType type, int count, int ratio) {
        this.type = type;
        this.count = count;
        this.ratio = ratio;
        if (this.ratio == 0){
            this.sets = Integer.MAX_VALUE;
        }
        else {
            this.sets = this.count / this.ratio;
        }
    }

    public void update(int count, int ratio) {
        this.count = count;
        this.ratio = ratio;
        if (this.ratio == 0){
            this.sets = Integer.MAX_VALUE;
        }
        else {
            this.sets = this.count / this.ratio;
        }
    }
}
