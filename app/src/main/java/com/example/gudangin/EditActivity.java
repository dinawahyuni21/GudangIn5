package com.example.gudangin;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

//import android.support.v7.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity {
    DatabaseHelper helper;
    EditText KodeBarang, NamaBarang, Harga, Jumlah, TglExp;
    Spinner SpJB;
    long id;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbarEdit);
        setSupportActionBar(toolbar);

        helper = new DatabaseHelper(this);

        id = getIntent().getLongExtra(DatabaseHelper.row_id, 0);


        KodeBarang = (EditText)findViewById(R.id.KodeBarang_Edit);
        NamaBarang = (EditText)findViewById(R.id.NamaBarang_Edit);
        Harga = (EditText)findViewById(R.id.Harga_Edit);
        Jumlah = (EditText)findViewById(R.id.Jumlah_Edit);
        TglExp = (EditText)findViewById(R.id.TglExp_Edit);
        SpJB = (Spinner)findViewById(R.id.spJB_Edit);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        TglExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });
        getData();
    }

    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                TglExp.setText(dateFormatter.format(newDate.getTime()));
            }
        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void getData(){
        Cursor cursor = helper.oneData(id);
        if(cursor.moveToFirst()){
            String kodeBarang = cursor.getString(cursor.getColumnIndex(DatabaseHelper.row_kodeBarang));
            String namaBarang = cursor.getString(cursor.getColumnIndex(DatabaseHelper.row_namaBarang));
            String jp = cursor.getString(cursor.getColumnIndex(DatabaseHelper.row_jb));
            String harga = cursor.getString(cursor.getColumnIndex(DatabaseHelper.row_harga));
            String jumlah = cursor.getString(cursor.getColumnIndex(DatabaseHelper.row_jumlah));
            String tglExp = cursor.getString(cursor.getColumnIndex(DatabaseHelper.row_tanggalExp));

            KodeBarang.setText(kodeBarang);
            NamaBarang.setText(namaBarang);

            if (jp.equals("Makanan")){
                SpJB.setSelection(0);
            }else if(jp.equals("Minuman")){
                SpJB.setSelection(1);
            }

            Harga.setText(harga);
            Jumlah.setText(jumlah);
            TglExp.setText(tglExp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_edit:
                String kodeBarang = KodeBarang.getText().toString().trim();
                String namaBarang = NamaBarang.getText().toString().trim();
                String jb = SpJB.getSelectedItem().toString().trim();
                String harga = Harga.getText().toString().trim();
                String jumlah = Jumlah.getText().toString().trim();
                String tglExp = TglExp.getText().toString().trim();

                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.row_kodeBarang, kodeBarang);
                values.put(DatabaseHelper.row_namaBarang, namaBarang);
                values.put(DatabaseHelper.row_jb, jb);
                values.put(DatabaseHelper.row_harga, harga);
                values.put(DatabaseHelper.row_jumlah, jumlah);
                values.put(DatabaseHelper.row_tanggalExp, tglExp);


                if (kodeBarang.equals("") || namaBarang.equals("") || harga.equals("") || jumlah.equals("") || tglExp.equals("")){
                    Toast.makeText(EditActivity.this, "Data tidak boleh kosong", Toast.LENGTH_SHORT);
                }else{
                    helper.updateData(values, id);
                    Toast.makeText(EditActivity.this, "Data Tersimpan", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
        switch (item.getItemId()){
            case R.id.delete_edit:
                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                builder.setMessage("Data ini akan dihapus.");
                builder.setCancelable(true);
                builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        helper.deleteData(id);
                        Toast.makeText(EditActivity.this, "Data Terhapus", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}

