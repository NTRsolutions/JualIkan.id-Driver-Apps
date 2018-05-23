package com.synergics.ishom.jualikanid_driver.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.synergics.ishom.jualikanid_driver.Model.Retrofit.ResponseLogin;

/**
 * Created by asmarasusanto on 9/27/17.
 */

public class SQLiteHandler extends SQLiteOpenHelper{

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    private static final int DB_VERSION = 1;

    //nama database
    public static final String DATABASE_NAME = "Synergics.WiraWiri";

    //nama tabel
    public static final String TABEL_USER = "tbUser";

    public static final String KEY_USER_ID= "userId";               //0
    public static final String KEY_USER_ID_SERVER= "driverIdServer";  //1
    public static final String KEY_USER_FULL_NAME= "driverName";      //2
    public static final String KEY_USER_IMAGE= "driverImage";         //3
    public static final String KEY_USER_PHONE= "driverPhone";         //4
    public static final String KEY_USER_EMAIL= "driverEmail";         //5
    public static final String KEY_USER_PASSWORD= "driverPassword";   //6
    public static final String KEY_USER_DEVICE_ID= "driverDeviceId";  //7
    public static final String KEY_USER_KOTA_ID= "driverKotaId";      //8
    public static final String KEY_USER_ADDRESS= "drvierAddress";     //9
    public static final String KEY_USER_SALDO= "driverSaldo";         //10

    private  static  final String CREATE_TB_USER = "CREATE TABLE " + TABEL_USER + "("
            + KEY_USER_ID+ " INTEGER PRIMARY KEY,"
            + KEY_USER_ID_SERVER+ " TEXT,"
            + KEY_USER_FULL_NAME+ " TEXT,"
            + KEY_USER_IMAGE+ " TEXT,"
            + KEY_USER_PHONE+ " TEXT,"
            + KEY_USER_EMAIL+ " TEXT,"
            + KEY_USER_PASSWORD+ " TEXT,"
            + KEY_USER_DEVICE_ID+ " TEXT,"
            + KEY_USER_KOTA_ID+ " TEXT,"
            + KEY_USER_ADDRESS+ " TEXT,"
            + KEY_USER_SALDO+ " TEXT" + ")";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TB_USER);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABEL_USER);

        // Create tables again
        onCreate(db);
    }


    //user
    public void addUser(ResponseLogin.Data user) {
        SQLiteDatabase db = this.getWritableDatabase();

        //menginisialisasi value string dengan kolom tabel
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID_SERVER, user.driver_id);
        values.put(KEY_USER_FULL_NAME, user.driver_full_name);
        values.put(KEY_USER_IMAGE, user.driver_image);
        values.put(KEY_USER_PHONE, user.driver_phone);
        values.put(KEY_USER_EMAIL, user.driver_email);
        values.put(KEY_USER_PASSWORD, user.driver_password);
        values.put(KEY_USER_DEVICE_ID, user.driver_device_id);
        values.put(KEY_USER_KOTA_ID, user.driver_koperasi_id);
        values.put(KEY_USER_ADDRESS, user.driver_address);
        values.put(KEY_USER_SALDO, user.driver_saldo);

        // masukan ke tabel
        long id = db.insert(TABEL_USER, null, values);
    }

    public ResponseLogin.Data getUser(){
        String selectQuery = "SELECT * FROM " + TABEL_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        ResponseLogin.Data user = new ResponseLogin.Data();
        user.driver_id = cursor.getString(1);
        user.driver_full_name = cursor.getString(2);
        user.driver_image = cursor.getString(3);
        user.driver_phone = cursor.getString(4);
        user.driver_email = cursor.getString(5);
        user.driver_password = cursor.getString(6);
        user.driver_device_id = cursor.getString(7);
        user.driver_koperasi_id = cursor.getString(8);
        user.driver_address = cursor.getString(9);
        user.driver_saldo = cursor.getString(10);
        return user;
    }

    public void hapusUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABEL_USER, null, null);
        db.close(); // Closing database connection
    }

}
