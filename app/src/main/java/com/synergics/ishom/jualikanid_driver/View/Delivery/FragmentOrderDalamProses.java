package com.synergics.ishom.jualikanid_driver.View.Delivery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.synergics.ishom.jualikanid_driver.Controller.RetroConfig.ApiClient;
import com.synergics.ishom.jualikanid_driver.Controller.RetroConfig.ApiInterface;
import com.synergics.ishom.jualikanid_driver.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_driver.Model.Retrofit.ResponseListDelivery;
import com.synergics.ishom.jualikanid_driver.Model.Retrofit.ResponseMainMenu;
import com.synergics.ishom.jualikanid_driver.R;

import net.idik.lib.slimadapter.SlimAdapter;
import net.idik.lib.slimadapter.SlimInjector;
import net.idik.lib.slimadapter.viewinjector.IViewInjector;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by asmarasusanto on 3/16/18.
 */

public class FragmentOrderDalamProses extends Fragment {

    private View viewFragment;

    //recycleview
    private SlimAdapter slimAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private ArrayList<Object> items = new ArrayList<>();

    public FragmentOrderDalamProses() {}

    public static FragmentOrderDalamProses newInstance() {
        FragmentOrderDalamProses fragment = new FragmentOrderDalamProses();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        viewFragment = inflater.inflate(R.layout.fragment_order_dalam_proses, container, false);

        inisialRecycle();

        return viewFragment;
    }

    private void inisialRecycle() {
        //inisialisasi recyclwview
        recyclerView = viewFragment.findViewById(R.id.recycle);

        //setting layout manager
        manager = new LinearLayoutManager(viewFragment.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        //setting slimAdapter
        slimAdapter = SlimAdapter.create()
                .register(R.layout.layout_list_order_proses, new SlimInjector<ResponseMainMenu.Last_Delivery>() {
                    @Override
                    public void onInject(final ResponseMainMenu.Last_Delivery data, IViewInjector injector) {
                        injector.text(R.id.idDelivery, data.code)
                                .text(R.id.tanggalDelivery, getJam(data.date_time) +  " " + getTanggal(data.date_time))
                                .text(R.id.jumlahDelivery,  data.order_count + " User")
                                .text(R.id.waktuDelivery, data.time)
                                .text(R.id.jarakDelivery, data.distance)
                                .with(R.id.item, new IViewInjector.Action() {
                                    @Override
                                    public void action(View view) {

                                        TextView status = view.findViewById(R.id.waktuHoursDelivery);
                                        setStatusColor(data.status, status);

                                        view.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (data.status == 0){
                                                    Intent intent = new Intent(viewFragment.getContext(), NotfiDetailDeliveryActivity.class);
                                                    intent.putExtra("delivery_id", data.id);
                                                    startActivity(intent);
                                                }else {
                                                    Intent intent = new Intent(viewFragment.getContext(), TrackDetailDeliveryActivity.class);
                                                    intent.putExtra("delivery_id", data.id);
                                                    startActivity(intent);
                                                }
                                            }
                                        });   
                                    }
                                });

                    }
                })
                .attachTo(recyclerView);
        
        getDataFromDatabase();
        
    }

    private void getDataFromDatabase() {

        SQLiteHandler db = new SQLiteHandler(viewFragment.getContext());
        String id_user = db.getUser().driver_id;

        final ProgressDialog progressDialog = new ProgressDialog(viewFragment.getContext());
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reIdUser = RequestBody.create(MediaType.parse("text/plain"), id_user);

        Call call = apiInterface.deliveryProcessedHistory(reIdUser);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseListDelivery res= (ResponseListDelivery) response.body();

                    if (res.status){

                        if (res.data.size() == 0){
                            TextView text = viewFragment.findViewById(R.id.txtNotFound);
                            text.setVisibility(TextView.VISIBLE);
                        }else {
                            TextView text = viewFragment.findViewById(R.id.txtNotFound);
                            text.setVisibility(TextView.INVISIBLE);
                        }

                        items.clear();
                        for (ResponseMainMenu.Last_Delivery item : res.data){
                            items.add(item);
                        }

                    }else {
                        Toast.makeText(viewFragment.getContext(), res.message, Toast.LENGTH_SHORT).show();
                        TextView text = viewFragment.findViewById(R.id.txtNotFound);
                        text.setVisibility(TextView.VISIBLE);
                    }

                }else {
                    Toast.makeText(viewFragment.getContext(), "Failed to connect server", Toast.LENGTH_SHORT).show();
                    TextView text = viewFragment.findViewById(R.id.txtNotFound);
                    text.setVisibility(TextView.VISIBLE);
                }

                progressDialog.hide();

                slimAdapter.updateData(items);
                recyclerView.setAdapter(slimAdapter);
                slimAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    private String money(int d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);;
        formatter .applyPattern("#,###");
        return formatter.format(d);
    }

    private String statusChecker(int status){
        if (status == 0){
            return "Belum di Bayar";
        }else if (status == 1){
            return "Sedang di Konfirmasi";
        }else if (status == 5){
            return "Expired";
        }else if (status == 3){
            return "Finished";
        }else if (status == 2){
            return "Sedang di Kirim";
        }
        else {
            return "";
        }
    }

    private void setStatusColor(int status, TextView textView){
        if (status == 2){
            textView.setText("Finished");
            textView.setTextColor(viewFragment.getContext().getResources().getColor(R.color.green));
        }else {
            textView.setText("Sedang Delivery");
            textView.setTextColor(viewFragment.getContext().getResources().getColor(R.color.blueFont));
        }
    }
    
    private String dateConvert(String date){
        String[] dates = date.split("\\s+");
        String[] tanggals = dates[0].split("-");
        tanggals[1] = monthConvert(Integer.parseInt(tanggals[1]));
        return tanggals[2] + " " + tanggals[1] + " " + tanggals[0];
    }

    private String monthConvert (int month){
        if (month == 1){
            return "January";
        }
        if (month == 2){
            return "Febuary";
        }
        if (month == 3){
            return "March";
        }
        if (month == 4){
            return "April";
        }
        if (month == 5){
            return "May";
        }
        if (month == 6){
            return "June";
        }
        if (month == 7){
            return "July";
        }
        if (month == 8){
            return "August";
        }
        if (month == 9){
            return "September";
        }
        if (month == 10){
            return "October";
        }
        if (month == 11){
            return "November";
        }
        if (month == 12){
            return "December";
        }
        return "";
    }

    private String getTanggal(String date){
        String array[] = date.split("\\s+");
        String tanggal[] = array[0].split("-");
        return tanggal[2] + " " + bulanFormat(tanggal[1]) + " " + tanggal[0];
    }

    private String bulanFormat(String bulan){
        if (bulan.equals("01")){
            return "Januari";
        }else if (bulan.equals("02")){
            return "Februari";
        }else if (bulan.equals("03")){
            return "Maret";
        }else if (bulan.equals("04")){
            return "April";
        }else if (bulan.equals("05")){
            return "Mei";
        }else if (bulan.equals("06")){
            return "Juni";
        }else if (bulan.equals("07")){
            return "Juli";
        }else if (bulan.equals("08")){
            return "Agustus";
        }else if (bulan.equals("09")){
            return "September";
        }else if (bulan.equals("10")){
            return "Oktober";
        }else if (bulan.equals("11")){
            return "November";
        }else {
            return "Desember";
        }
    }

    private String getJam(String date){
        String array[] = date.split("\\s+");
        return array[1] + " WIB";
    }
}
