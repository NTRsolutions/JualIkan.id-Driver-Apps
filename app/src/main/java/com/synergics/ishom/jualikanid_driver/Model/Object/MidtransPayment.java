package com.synergics.ishom.jualikanid_driver.Model.Object;

/**
 * Created by asmarasusanto on 3/12/18.
 */

public class MidtransPayment {
    private DetailTransaksi transaction_details;
    private CustomerDetail customer_details;

    public MidtransPayment(DetailTransaksi detailTransaksi, CustomerDetail customerDetail) {
        this.transaction_details = detailTransaksi;
        this.customer_details = customerDetail;
    }

    public DetailTransaksi getDetailTransaksi() {
        return transaction_details;
    }

    public void setDetailTransaksi(DetailTransaksi detailTransaksi) {
        this.transaction_details = detailTransaksi;
    }

    public CustomerDetail getCustomerDetail() {
        return customer_details;
    }

    public void setCustomerDetail(CustomerDetail customerDetail) {
        this.customer_details = customerDetail;
    }
}
