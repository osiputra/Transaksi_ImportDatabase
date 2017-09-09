package com.aplikasipasardemo.view;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aplikasipasardemo.CustomSwipeAdapter;
import com.aplikasipasardemo.Product;
import com.aplikasipasardemo.ProductListAdapter;
import com.aplikasipasardemo.R;
import com.aplikasipasardemo.SQLiteHelper;
import com.aplikasipasardemo.Transaction;
import com.aplikasipasardemo.TransactionListAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class MenuSlidingPanelActitvity extends AppCompatActivity {

    private final static String TAG = "Menu Sliding Panel";

    private GridView gridView;
    private ListView listView;
    private EditText searchEdt;
    private TextView nameTxt;
    private TextView totalTxt;
    private Button prosesBtn;
    private WebView webView;

    ViewPager viewPager;
    CustomSwipeAdapter customSwipeAdapter;

    private ProductListAdapter productListAdapter;
    private TransactionListAdapter testListAdapter;

    private ArrayList<Product> productList;
    private ArrayList<Transaction> transList = new ArrayList<>();

    public static MenuSlidingPanelActitvity mainActivity;
    public static SQLiteHelper sqLiteHelper;

    private String[] daftar;
    private int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_sliding_panel_actitvity);

        init();

        mainActivity = this;
        sqLiteHelper = new SQLiteHelper(this);

        AdView mAdView = (AdView) findViewById(R.id.iklanAds);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                .build();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });
        mAdView.loadAd(adRequest);

        Log.i(TAG, "onOptionsItemSelected: Import Item");
        if (sqLiteHelper.checkDB()){
            if (sqLiteHelper.importDB()){
                Toast.makeText(this, "DB Has Been Imported!", Toast.LENGTH_LONG).show();
                refreshListSearch();
                sqLiteHelper.deleteAllRecordTrans();
            }else{
                Toast.makeText(this, "DB Not Imported!", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "DB Not Created!", Toast.LENGTH_LONG).show();
        }
    }

    // Start Build Listenner

    private View.OnClickListener mProses = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String data = String.valueOf(totalTxt.getText());
            String dataTemp = "";
            if (data.length() > 4){
                int totalTransaksi = listView.getCount();
                String[] split = data.split(" ");
                dataTemp = split[1];
                if (dataTemp.length() <= 3){
                    data = dataTemp;
                }else if (dataTemp.length() > 3 && dataTemp.length() <= 7){
                    split = dataTemp.split("\\.");
                    data = split[0] + split[1];
                }else if (dataTemp.length() > 7 && dataTemp.length() <= 11){
                    split = dataTemp.split("\\.");
                    data = split[0] + split[1] + split [2];
                }else {
                    data = "Total Harga Terlalu Panjang";
                }

                showToast(totalTransaksi + "-" + data);
            }
        }
    };

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            Log.i(TAG, "onItemLongClick: "+position);
            final String selection = daftar[position];
            Cursor cursor = sqLiteHelper.mDataBase.rawQuery("SELECT * FROM PRODUCT WHERE Id = '" + selection + "'", null);
            cursor.moveToFirst();
            if (cursor.getCount()>0){

                int idi = cursor.getInt(0);
                String name = cursor.getString(1);
                String unit = cursor.getString(2);
                String price = cursor.getString(3);
                byte[] image = cursor.getBlob(4);

                Cursor mCursor = sqLiteHelper.mDataBase.rawQuery("SELECT COUNT(*) FROM PRODUCTTEMP", null);
                mCursor.moveToFirst();
                int count = mCursor.getInt(0);
                if (count > 0){
                    mCursor = sqLiteHelper.mDataBase.rawQuery("SELECT * FROM PRODUCTTEMP WHERE name = '"+ name +"'", null);
                    mCursor.moveToFirst();
                    if (mCursor.getCount() > 0){
                        int qty = Integer.parseInt(mCursor.getString(2));
                        idi = mCursor.getInt(0);

                        if (qty > 1){
                            qty = qty - 1;
                            MenuSlidingPanelActitvity.sqLiteHelper.updateDataTrans(
                                    name.toString().trim(),
                                    String.valueOf(qty),
                                    price.toString().trim(),
                                    String.valueOf(idi)
                            );
                        }else{
                            MenuSlidingPanelActitvity.sqLiteHelper.deleteDataTrans(String.valueOf(idi));
                        }
                    }
                }
            }

            refreshListTrans();
            return true;
        }
    };

    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long idi) {

            Log.i(TAG, "onItemClick: "+position);

            final String selection = daftar[position];

            Cursor cursor = sqLiteHelper.mDataBase.rawQuery("SELECT * FROM PRODUCT WHERE Id = '" + selection + "'", null);

            cursor.moveToFirst();
            if (cursor.getCount()>0){

                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String unit = cursor.getString(2);
                String price = cursor.getString(3);
                byte[] image = cursor.getBlob(4);

                cursor = sqLiteHelper.mDataBase.rawQuery("SELECT * FROM PRODUCTTEMP WHERE name = '"+ name +"'", null);
                cursor.moveToFirst();
                if (cursor.getCount() > 0){
                    id  = cursor.getInt(0);
                    name = cursor.getString(1);
                    int qty = Integer.parseInt(cursor.getString(2));

                    qty = qty + 1;

                    MenuSlidingPanelActitvity.sqLiteHelper.updateDataTrans(
                            name.toString().trim(),
                            String.valueOf(qty),
                            price.toString().trim(),
                            String.valueOf(id)
                    );


                }else{
                    MenuSlidingPanelActitvity.sqLiteHelper.insertDataTrans(
                            name.toString().trim(),
                            String.valueOf(1),
                            price.toString().trim()

                    );
                }


            }

            refreshListTrans();
        }
    };

    public void refreshListTrans(){

        Cursor cursor = sqLiteHelper.mDataBase.rawQuery("SELECT a.id, a.name, a.qty, b.price FROM PRODUCTTEMP a, PRODUCT b WHERE a.name = b.name", null);
        cursor.moveToFirst();
        transList.clear();
        total = 0;
        for (int cc=0; cc < cursor.getCount(); cc++){
            cursor.moveToPosition(cc);

            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String qty = cursor.getString(2);
            String price = cursor.getString(3);
            int a = Integer.parseInt(qty) * Integer.parseInt(price);
            total = total + a;

            transList.add(new Transaction(name, qty, String.valueOf(a), id));

        }

        listView = (ListView) findViewById(R.id.item_Lis);
        testListAdapter = new TransactionListAdapter(this, R.layout.trans_list_item, transList);
        listView.setAdapter(testListAdapter);

        testListAdapter.notifyDataSetChanged();

//        totalTxt.setText("Rp. "+total);
        setAmount(String.valueOf(total));
    }

    public void refreshListSearch(){

        productList = new ArrayList<>();
//        Cursor cursor = sqLiteHelper.getData("SELECT * FROM PRODUCT ORDER BY name");
        Cursor cursor = sqLiteHelper.mDataBase.rawQuery("SELECT * FROM PRODUCT ORDER BY name", null);
        daftar = new String[cursor.getCount()];

        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i);
            daftar[i] = cursor.getString(0).toString();

            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String unit = cursor.getString(2);
            String price = cursor.getString(3);
            byte[] image = cursor.getBlob(4);

            char data[] = price.toCharArray();
            price = "";
            int pos = 0;
            for (int j = data.length - 1; j >= 0; j--){
                if (pos == 3){
                    price = price + ",";
                    price = price + String.valueOf(data[j]);
                    pos = 0;
                }else{
                    price = price + String.valueOf(data[j]);
                    pos++;
                }
            }
            data = price.toCharArray();
            price = "";
            for (int j = data.length - 1; j >= 0; j--){
                price = price + data[j];
            }
            price = "Rp. " + price;

            productList.add(new Product(name, unit, price, image, id));
        }

        gridView = (GridView) findViewById(R.id.searchLis);
        productListAdapter = new ProductListAdapter(this, R.layout.search_grid_item, productList);
        gridView.setAdapter(productListAdapter);

        productListAdapter.notifyDataSetChanged();

        //  Register item click
        gridView.setOnItemClickListener(onItemClick);
        gridView.setOnItemLongClickListener(onItemLongClickListener);

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

//                Log.i(TAG, "beforeTextChanged: ");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, "onTextChanged: ");
                searchList(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {
//                Log.i(TAG, "afterTextChanged: ");
            }
        });
    }

    private void searchList(String result){
        productList = new ArrayList<>();
        Cursor cursor = sqLiteHelper.mDataBase.rawQuery("SELECT * FROM PRODUCT WHERE name LIKE '%" + result + "%' ORDER BY name", null);
        daftar = new String[cursor.getCount()];

        cursor.moveToFirst();
        for (int cc=0; cc < cursor.getCount(); cc++){
            cursor.moveToPosition(cc);
            daftar[cc] = cursor.getString(0).toString();

            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String unit = cursor.getString(2);
            String price = cursor.getString(3);
            byte[] image = cursor.getBlob(4);

            char data[] = price.toCharArray();
            price = "";
            int pos = 0;
            for (int i = data.length - 1; i >= 0; i--){
                if (pos == 3){
                    price = price + ",";
                    price = price + String.valueOf(data[i]);
                    pos = 0;
                }else{
                    price = price + String.valueOf(data[i]);
                    pos++;
                }
            }
            data = price.toCharArray();
            price = "";
            for (int i = data.length - 1; i >= 0; i--){
                price = price + data[i];
            }
            price = "Rp. " + price;

            productList.add(new Product(name, unit, price, image, id));
        }

        productListAdapter = new ProductListAdapter(this, R.layout.search_grid_item, productList);
        gridView.setAdapter(productListAdapter);

        productListAdapter.notifyDataSetChanged();

        //  Register item click
        gridView.setOnItemClickListener(onItemClick);
        gridView.setOnItemLongClickListener(onItemLongClickListener);

    }

    private void init() {

        totalTxt = (TextView) findViewById(R.id.totalTxt);
        nameTxt = (TextView) findViewById(R.id.nameTxt);

        searchEdt = (EditText) findViewById(R.id.searchEdt);

        prosesBtn = (Button) findViewById(R.id.prosesBtn);
        prosesBtn.setOnClickListener(mProses);

        gridView = (GridView) findViewById(R.id.searchLis);
        listView = (ListView) findViewById(R.id.item_Lis);
    }

    private void setAmount(String amount){
        char[] amountArr = amount.toCharArray();
        if (amount.length() <= 3){
            totalTxt.setText("Rp. " + amount);
        }else if (amountArr.length > 3){
            amount = "";
            int x = 0;
            for (int i = amountArr.length - 1; i >= 0; i--){
                if ((x % 3) == 0 && x != 0){
                    amount = amount + ".";
                    amount = amount + amountArr[i];
                }else{
                    amount = amount + amountArr[i];
                }
                x++;
            }

            amountArr = amount.toCharArray();
            amount = "";
            for (int i = amountArr.length - 1; i >= 0; i --){
                amount = amount + amountArr[i];
            }

            totalTxt.setText("Rp. " + amount);
        }
    }

    private void showToast(String toast){
        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
    }

}
