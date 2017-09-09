package com.aplikasipasardemo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aplikasipasardemo.view.MenuSlidingPanelActitvity;

import java.util.ArrayList;

/**
 * Created by Putra_Osi_PC on 6/21/2017.
 */

public class TransactionListAdapter extends BaseAdapter{

    private final static String TAG = "Transaction List Adapter";

    private Context context;
    private  int layout;
    private ArrayList<Transaction> transactionList;
    private CustomKeyboard mCustomKeyboard;

    public TransactionListAdapter(Context context, int layout, ArrayList<Transaction> transactionList) {
        this.context = context;
        this.layout = layout;
        this.transactionList = transactionList;
    }

    @Override
    public int getCount() {
        return transactionList.size();
    }

    @Override
    public Object getItem(int position) {
        return transactionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView nameTxt,qtyTxt, txtPrice;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.nameTxt = (TextView) row.findViewById(R.id.nameTxt);
            holder.qtyTxt = (TextView) row.findViewById(R.id.qtyTxt);
            holder.txtPrice = (TextView) row.findViewById(R.id.priceTxt);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        final Transaction transaction = transactionList.get(position);

        holder.nameTxt.setText(transaction.getName());
        holder.qtyTxt.setText(transaction.getQty());
        holder.txtPrice.setText(transaction.getPrice());

        holder.nameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MenuSlidingPanelActitvity.mainActivity);
                builder.setTitle("Informasi");
                builder.setMessage("Untuk Mengahapus silahkan tekan yang lama pada nama produk");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        holder.nameTxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final CharSequence[] dialogItem = {
                        "Yes",
                        "No"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MenuSlidingPanelActitvity.mainActivity);
                builder.setTitle("Apakah anda yakin ingin menghapus?");
                builder.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                    switch(item){
                        case 0 :
                            Log.i(TAG, "onClick: YES "+transaction.getId());
                            MenuSlidingPanelActitvity.sqLiteHelper.deleteDataTrans(String.valueOf(transaction.getId()));
                            MenuSlidingPanelActitvity.mainActivity.refreshListTrans();
                            break;
                        case 1 :
                            Log.i(TAG, "onClick: NO "+transaction.getId());
                            break;
                    }
                    }
                });
                builder.show();

                return true;
            }
        });

        holder.qtyTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MenuSlidingPanelActitvity.mainActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.ubah_data);

                final EditText qtyEdt = (EditText) dialog.findViewById(R.id.qtyEdt);
                qtyEdt.setText(transaction.getQty());

                Button ubahBtn = (Button) dialog.findViewById(R.id.ubahBtn);
                // if button is clicked, close the custom dialog
                ubahBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int price = 0;
                        Cursor cursor = MenuSlidingPanelActitvity.sqLiteHelper.mDataBase.rawQuery("SELECT * FROM PRODUCT WHERE name = '"+ transaction.getName()+"'", null);
                        cursor.moveToFirst();
                        if (cursor.getCount() > 0){
                            price = Integer.parseInt(cursor.getString(3));
                        }
                        int total = Integer.parseInt(String.valueOf(qtyEdt.getText())) * price;

                        MenuSlidingPanelActitvity.sqLiteHelper.updateDataTrans(
                                transaction.getName().trim(),
                                String.valueOf(qtyEdt.getText()),
                                String.valueOf(total),
                                String.valueOf(transaction.getId()));
                        MenuSlidingPanelActitvity.mainActivity.refreshListTrans();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        return row;
    }


}
