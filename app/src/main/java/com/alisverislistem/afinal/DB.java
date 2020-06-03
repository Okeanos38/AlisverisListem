package com.alisverislistem.afinal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper{

    final static String name = "proje";
    final static int version = 1;

    public DB(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE `liste` (\n" +
                "\t`lid`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`baslik`\tTEXT NOT NULL,\n" +
                "\t`aciklama`\tTEXT NOT NULL,\n" +
                "\t`tarih`\tTEXT,\n" +
                "\t`durum`\tINTEGER\n" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists liste");
        onCreate(sqLiteDatabase);
    }
}
