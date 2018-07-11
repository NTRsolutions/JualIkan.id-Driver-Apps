package com.synergics.ishom.jualikanid_driver.View;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.synergics.ishom.jualikanid_driver.Controller.RetroConfig.ApiClient;
import com.synergics.ishom.jualikanid_driver.Controller.RetroConfig.ApiInterface;
import com.synergics.ishom.jualikanid_driver.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_driver.Model.Retrofit.ResponseSaldo;
import com.synergics.ishom.jualikanid_driver.R;

import net.idik.lib.slimadapter.SlimAdapter;
import net.idik.lib.slimadapter.SlimInjector;
import net.idik.lib.slimadapter.viewinjector.IViewInjector;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiwayatSaldoActivity extends AppCompatActivity {

    private TextView userSaldo, txtNotFound;

    private SlimAdapter slimAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private ArrayList<Object> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_saldo);

        txtNotFound = findViewById(R.id.txtNotFound);
        userSaldo = findViewById(R.id.userSaldo);

        recyclerView = findViewById(R.id.recycle);
        manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        slimAdapter = SlimAdapter.create()
                .register(R.layout.item_saldo, new SlimInjector<ResponseSaldo.Saldo>() {
                    @Override
                    public void onInject(final ResponseSaldo.Saldo data, IViewInjector injector) {
                        injector.with(R.id.item, new IViewInjector.Action() {
                            @Override
                            public void action(View view) {
                                TextView name = view.findViewById(R.id.name);
                                TextView saldo = view.findViewById(R.id.saldo);
                                name.setText(data.name);
                                saldo.setText(data.saldo);
                            }
                        });
                    }
                })
                .attachTo(recyclerView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reDriverId = RequestBody.create(MediaType.parse("text/plain"), new SQLiteHandler(getApplicationContext()).getUser().driver_id);

        Call call = apiInterface.saldo_history(reDriverId);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseSaldo res= (ResponseSaldo) response.body();

                    if (res.status){

                        List<ResponseSaldo.Saldo> datas = res.data.history;
                        userSaldo.setText(res.data.profile.saldo);

                        if (datas.size() != 0){
                            for (ResponseSaldo.Saldo data : datas){
                                items.add(data);
                            }
                        }else {
                            txtNotFound.setVisibility(View.VISIBLE);
                        }

                    }else {
                        Toast.makeText(getApplicationContext(), "Gagal Ambil Bantuan !", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(), "Register failed!", Toast.LENGTH_SHORT).show();
                }

                progressDialog.hide();

                slimAdapter.updateData(items);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
