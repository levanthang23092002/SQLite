package com.example.sqlite;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class InsertComputerActivity extends Activity {

    SQLiteDatabase database=null;
    List<InforData>listBook=null;
    List<InforData>listAuthor=null;
    InforData authorData=null;
    MySimpleArrayAdapter adapter=null;
    int day,month,year;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_computer);
        Spinner pinner=(Spinner) findViewById(R.id.spinner1);
        listAuthor=new ArrayList<InforData>();
        InforData d1=new InforData();
        d1.setField1("_");
        d1.setField2("Show All");
        d1.setField3("_");
        listAuthor.add(d1);
        //Lệnh xử lý đưa dữ liệu là Tác giả và Spinner
        database=openOrCreateDatabase("mydata.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        if(database!=null)
        {

            Cursor cursor=database.query("tblAuthors", null, null, null, null, null, null);
            cursor.moveToFirst();
            while(cursor.isAfterLast()==false)
            {
                InforData d=new InforData();
                d.setField1(cursor.getInt(0));
                d.setField2(cursor.getString(1));
                d.setField3(cursor.getString(2));
                listAuthor.add(d);
                cursor.moveToNext();
            }
            cursor.close();
        }
        adapter=new MySimpleArrayAdapter(InsertComputerActivity.this, R.layout.my_layout_for_show_list_data,listAuthor);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pinner.setAdapter(adapter);

        pinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if(arg2==0)
                {

                    authorData=null;
                    loadAllListBook();
                }
                else
                {

                    authorData=listAuthor.get(arg2);
                    loadListBookByAuthor(authorData.getField1().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                authorData=null;
            }
        });

        setCurrentDateOnView();

        Button bChangeDate=(Button) findViewById(R.id.buttonDate);
        bChangeDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog(113);
            }
        });

        Button btnInsertBook =(Button) findViewById(R.id.buttonInsertBook);
        btnInsertBook.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(authorData==null)
                {
                    Toast.makeText(InsertComputerActivity.this, "Please choose an author to insert", Toast.LENGTH_LONG).show();
                    return;
                }
                EditText txtTitle=(EditText) findViewById(R.id.editTextTitle);
                ContentValues values=new ContentValues();
                values.put("title", txtTitle.getText().toString());
                Calendar c=Calendar.getInstance();
                c.set(year, month, day);
                SimpleDateFormat dfmt=new SimpleDateFormat("dd-MM-yyyy");
                values.put("dateadded",dfmt.format(c.getTime()));
                values.put("authorid", authorData.getField1().toString());
                long bId=database.insert("tblBooks", null, values);
                if(bId>0)
                {
                    Toast.makeText(InsertComputerActivity.this, "Insert Computer OK", Toast.LENGTH_LONG).show();
                    loadListBookByAuthor(authorData.getField1().toString());
                }
                else
                {
                    Toast.makeText(InsertComputerActivity.this, "Insert Computer Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void loadAllListBook()
    {
        Cursor cur=database.query("tblBooks", null, null, null, null, null, null);
        cur.moveToFirst();
        listBook=new ArrayList<InforData>();
        while(cur.isAfterLast()==false)
        {
            InforData d=new InforData();
            d.setField1(cur.getInt(0));
            d.setField2(cur.getString(1));
            d.setField3(cur.getString(2));
            listBook.add(d);
            cur.moveToNext();
        }
        cur.close();
        adapter=new MySimpleArrayAdapter(InsertComputerActivity.this, R.layout.my_layout_for_show_list_data, listBook);
        ListView lv=(ListView) findViewById(R.id.listViewBook);
        lv.setAdapter(adapter);
    }
    /**

     * @param authorid
     */
    public void loadListBookByAuthor(String authorid)
    {
        Cursor cur=database.query("tblBooks", null, "authorid=?", new String[]{authorid}, null, null, null);
        cur.moveToFirst();
        listBook=new ArrayList<InforData>();
        while(cur.isAfterLast()==false)
        {
            InforData d=new InforData();
            d.setField1(cur.getInt(0));
            d.setField2(cur.getString(1));
            d.setField3(cur.getString(2));
            listBook.add(d);
            cur.moveToNext();
        }
        cur.close();
        adapter=new MySimpleArrayAdapter(InsertComputerActivity.this, R.layout.my_layout_for_show_list_data, listBook);
        ListView lv=(ListView) findViewById(R.id.listViewBook);
        lv.setAdapter(adapter);
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if(id==113)
        {
            return new DatePickerDialog(this, dateChange, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dateChange= new OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year1, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            year=year1;
            month=monthOfYear;
            day=dayOfMonth;
            EditText eDate=(EditText) findViewById(R.id.editTextDate);
            eDate.setText(day+"-"+(month+1)+"-"+year);
        }
    };

    public void setCurrentDateOnView()
    {
        EditText eDate=(EditText) findViewById(R.id.editTextDate);
        Calendar cal=Calendar.getInstance();
        day=cal.get(Calendar.DAY_OF_MONTH);
        month=cal.get(Calendar.MONTH);
        year=cal.get(Calendar.YEAR);
        eDate.setText(day+"-"+(month+1)+"-"+year);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_insert_book, menu);
        return true;
    }
}
