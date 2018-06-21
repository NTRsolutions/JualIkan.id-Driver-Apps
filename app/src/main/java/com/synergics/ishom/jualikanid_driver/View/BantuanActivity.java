package com.synergics.ishom.jualikanid_driver.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.synergics.ishom.jualikanid_driver.Controller.AppConfig;
import com.synergics.ishom.jualikanid_driver.Controller.RetroConfig.ApiClient;
import com.synergics.ishom.jualikanid_driver.Controller.RetroConfig.ApiInterface;
import com.synergics.ishom.jualikanid_driver.Model.Retrofit.ResponseBantuan;
import com.synergics.ishom.jualikanid_driver.Model.Retrofit.ResponseLogin;
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

public class BantuanActivity extends AppCompatActivity {

    //recycleview
    private SlimAdapter slimAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager grid;
    private ArrayList<Object> items = new ArrayList<>();

    //bundle
    Bundle bundle;
    private MaterialSearchView searchView;
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bantuan);

        bundle = getIntent().getExtras();

        setToolbar();

        //setting tampilan menu
        setContent();
    }

    private void setToolbar() {
        String title = "Bantuan";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarTitle = findViewById(R.id.toolbarTittle);
        toolbarTitle.setText(title);

        searchView = (MaterialSearchView) findViewById(R.id.searchView);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                toolbarTitle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSearchViewClosed() {

                TextView text = (TextView) findViewById(R.id.txtNotFound);
                text.setVisibility(TextView.INVISIBLE);

                toolbarTitle.setVisibility(View.VISIBLE);

                slimAdapter.updateData(items);
                slimAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(slimAdapter);
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText != null && !newText.isEmpty()){
                    ArrayList<Object> dataNew = new ArrayList<>();
                    getSearch(newText, dataNew);

//                    for (PersonilNew data : data1){
//                        if (data.getNama().toLowerCase().contains(newText.toLowerCase())){
//                            dataNew.add(data);
//                        }
//
//                    }
//
//                    dataAdapter = new PersonilNewAdapter(PolisiActivity.this, dataNew);
//                    dataAdapter.notifyDataSetChanged();
//                    recyclerView.setAdapter(dataAdapter);
                }else {

                    TextView text = (TextView) findViewById(R.id.txtNotFound);
                    text.setVisibility(TextView.INVISIBLE);

                    slimAdapter.updateData(items);
                    slimAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(slimAdapter);
                }
                return true;
            }
        });
    }

    private void getSearch(String newText, final ArrayList<Object> dataNew) {
        //get data from bundle intent
        String user_level = AppConfig.user_level;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reUserLevel = RequestBody.create(MediaType.parse("text/plain"), user_level);
        RequestBody reSearch = RequestBody.create(MediaType.parse("text/plain"), newText);

        Call call = apiInterface.serach_bantuan(reUserLevel, reSearch);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseBantuan res= (ResponseBantuan) response.body();

                    if (res.status){

                        List<ResponseBantuan.Bantuan> datas = res.data;

                        if (datas.size() == 0){

                            TextView text = (TextView) findViewById(R.id.txtNotFound);
                            text.setVisibility(TextView.VISIBLE);

                        }else {
                            TextView text = (TextView) findViewById(R.id.txtNotFound);
                            text.setVisibility(TextView.INVISIBLE);
                        }

                        for (ResponseBantuan.Bantuan data : datas){
                            dataNew.add(data);
                        }

                    }else {
                        Toast.makeText(getApplicationContext(), "Gagal Ambil Bantuan !", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(), "Register failed!", Toast.LENGTH_SHORT).show();
                }

                progressDialog.hide();

                slimAdapter.updateData(dataNew);
                recyclerView.setAdapter(slimAdapter);
                slimAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setContent() {
        //inisialisasi recyclwview
        recyclerView = findViewById(R.id.recycle);

        //setting grid layout
        grid = new GridLayoutManager(getApplicationContext(), 1);
        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return slimAdapter.getItem(position) instanceof ResponseLogin.Data ? 1 : 1;
            }
        });
        recyclerView.setLayoutManager(grid);

        //setting slimAdapter
        slimAdapter = SlimAdapter.create()
                //recycle in view pager
                .register(R.layout.layout_bantuan_item, new SlimInjector<ResponseBantuan.Bantuan>() {
                    @Override
                    public void onInject(final ResponseBantuan.Bantuan data, IViewInjector injector) {
                        injector.with(R.id.item, new IViewInjector.Action() {
                            @Override
                            public void action(View view) {
                                TextView title = view.findViewById(R.id.title);
                                title.setText(data.articel_name);

                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                                        intent.putExtra("bantuan_id", data.articel_id);
                                        intent.putExtra("web_name", data.articel_name);
                                        intent.putExtra("web_url", data.articel_url);
                                        startActivity(intent);
                                    }
                                });

                            }
                        });

                    }
                })
                .attachTo(recyclerView);

        //get Menu From Server
        getItemFromServer();
    }

    private void getItemFromServer() {
        //get data from bundle intent
        String user_level = AppConfig.user_level;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reUserLevel = RequestBody.create(MediaType.parse("text/plain"), user_level);

        Call call = apiInterface.bantuan(reUserLevel);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseBantuan res= (ResponseBantuan) response.body();

                    if (res.status){

                        List<ResponseBantuan.Bantuan> datas = res.data;

                        for (ResponseBantuan.Bantuan data : datas){
                            items.add(data);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fish_category, menu);
        MenuItem item = menu.findItem(R.id.search);
        searchView.setMenuItem(item);
        return true;
    }


    private String money(int d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);;
        formatter .applyPattern("#,###");
        return formatter.format(d);
    }
}
