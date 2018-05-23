package com.synergics.ishom.jualikanid_driver.Model.TrackMaps;

/**
 * Created by asmarasusanto on 10/2/17.
 */

public class Line {
    private int lineId;
    private float lineLength;
    private int lineTime;
    private float linePrice;

    public Line(int lineId, float lineLength, int lineTime, float linePrice) {
        this.lineId = lineId;
        this.lineLength = lineLength;
        this.lineTime = lineTime;
        this.linePrice = linePrice;
    }

    public float getLineLength() {
        return lineLength;
    }

    public float getLinePrice() {
        return linePrice;
    }
}
