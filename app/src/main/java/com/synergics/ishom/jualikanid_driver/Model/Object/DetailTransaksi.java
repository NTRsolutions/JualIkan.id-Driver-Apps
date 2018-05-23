package com.synergics.ishom.jualikanid_driver.Model.Object;

/**
 * Created by asmarasusanto on 3/12/18.
 */

public class DetailTransaksi {

    private String order_id;
    private int gross_amount;

    public DetailTransaksi(String order_id, int gross_amount) {
        this.order_id = order_id;
        this.gross_amount = gross_amount;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public int getGross_amount() {
        return gross_amount;
    }

    public void setGross_amount(int gross_amount) {
        this.gross_amount = gross_amount;
    }
}
