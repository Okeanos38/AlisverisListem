package com.alisverislistem.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.onesignal.OneSignal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Button btnEkle, about;
    EditText txtBaslik, txtAciklama;
    ListView listData;

    ArrayList<String> titles = new ArrayList<String>();
    ArrayList<ProList> prls = new ArrayList<ProList>();

    DB db = new DB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        btnEkle = findViewById(R.id.btnEkle);
        about = findViewById(R.id.about);
        txtBaslik = findViewById(R.id.txtBaslik);
        txtAciklama = findViewById(R.id.txtAciklama);
        listData = findViewById(R.id.listData);

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,about.class);
                startActivity(i);
            }
        });

        btnEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String b = txtBaslik.getText().toString().trim(); //trim sağında solunda boşluk veya istenmeyen karakter olmasın
                String a = txtAciklama.getText().toString().trim(); //string olarak a ya atıyoruz.
                SQLiteDatabase yaz = db.getWritableDatabase();

                String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

                //inster operation
                ContentValues con = new ContentValues(); //değer kümesini con içine depoluyoruz
                con.put("baslik", b); //Başlığı b diye atadık yukarda, burda çekiyoruz ve con ile values değerini tutuyoruz
                con.put("aciklama", a); //Açıklamayı a diye atamıştık burda çekiyoruz
                con.put("tarih", timeStamp);
                con.put("durum", 0);

                long yazSonuc = yaz.insert("liste", null, con);
                if(yazSonuc > 0){
                    dataGetir(); //insert ile veri eklendiğinde, verileri bu şekilde tekrar güncelliyoruz
                    Toast.makeText(MainActivity.this, txtBaslik.getText() + " öğesi eklendi", Toast.LENGTH_SHORT).show();
                    txtBaslik.setText(""); // textbox ların içini boşaltıyoruz. Veri eklendiğinde içindeki veri silinsin null.
                    txtAciklama.setText(""); // textbox ların içini boşaltıyoruz. Veri eklendiğinde içindeki veri silinsin null.
                    txtBaslik.requestFocus();

                }else{
                    Toast.makeText(MainActivity.this, "Yazma hatası!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dataGetir(); //altta yazdığımız class ı çağırıyoruz. Kayıt eklendikten sonra bu classı çalıştırıcak, her kayıt eklemede
    }

    public void dataGetir(){
        SQLiteDatabase oku = db.getReadableDatabase();
        Cursor cr = oku.query("liste", null, null, null, null, null, null);
        titles.clear();
        prls.clear();
        while(cr.moveToNext()){
            String title = cr.getString(cr.getColumnIndex("baslik"));
            String durum = cr.getString(cr.getColumnIndex("durum"));
            Log.d("title", title);
            titles.add(title);

            ProList pr = new ProList();
            pr.setAciklama(cr.getString(cr.getColumnIndex("aciklama")));
            pr.setBaslik(cr.getString(cr.getColumnIndex("baslik")));
            pr.setDurum(cr.getInt(cr.getColumnIndex("durum")));
            pr.setLid(cr.getInt(cr.getColumnIndex("lid")));
            pr.setTarih(cr.getString(cr.getColumnIndex("tarih")));
            prls.add(pr);
        }

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, titles);
        listData.setAdapter(adp);

        oku.close();



        listData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Detail.pr = prls.get(i);
                Intent in = new Intent(MainActivity.this, Detail.class); //Intent => Activity ler arası geçiş için kullanılıyor
                startActivity(in); //Detaylar Activity e git
            }
        });

        listData.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

                AlertDialog.Builder uyari = new AlertDialog.Builder(MainActivity.this);
                uyari.setTitle("Veri Silme");
                uyari.setMessage("Veriyi silmek istediğinize emin misiniz?");
                uyari.setCancelable(false);
                uyari.setIcon(R.mipmap.warning);
                uyari.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        SQLiteDatabase sil = db.getWritableDatabase();
                        int sDurum = sil.delete("liste", "lid = " + prls.get(i).getLid(), null);
                        if(sDurum > 0){
                            dataGetir();
                            Toast.makeText(MainActivity.this, "Silme işlemi başarılı!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Silme işlemi başarısız!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                uyari.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Silme işlemi ipal edildi.", Toast.LENGTH_SHORT).show();
                    }
                });

                uyari.create().show();

                return true; // değeri true olarak döndürerek iki basımında çakışmamasını sağlıyoruz.
            }
        });
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        dataGetir();
    }

}


