package com.example.postmessege;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button post;
    private Button getnumber;
    private Button chooseExcel;
    private EditText content;
    private TextView shownumber;
    private ArrayList<String> list;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        post = findViewById(R.id.post);
        getnumber = findViewById(R.id.getnumber);
        chooseExcel = findViewById(R.id.chooseExcel);
        content = findViewById(R.id.content);
        shownumber = findViewById(R.id.shownumber);

        list = new ArrayList<String>();

        //选择表格事件绑定
        chooseExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });
//        获取号码事件绑定
        getnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                从输入框获取地址
                readExcel(path);
            }
        });


//        确认发送事件绑定
        post.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

//                //动态申请权限
//                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) !=
//                        PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
//                            Manifest.permission.SEND_SMS}, 1);
//                }

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {


                String string = content.getText().toString();
                Log.d("string",string);

                String a = list.toString();
                a = a.replace("[","");
                a = a.replace("]","");
                a = a.replace(",",";");
                Log.d("a",a);
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+a));
//                intent.putExtra("address", a);
                intent.putExtra("sms_body", string);
                startActivity(intent);
//                SmsManager smsManager = SmsManager.getDefault();
//                for (int z = 0; z < 50; z++) {
//                    Log.d("list", list.get(z));
//                    smsManager.sendTextMessage(list.get(z), null, string, null, null);
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                }


//                    }
//                }).start();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                path = uri.getPath();
//            tv.setText(path);
                Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
//                tv.setText(path);
                Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
            }


        }
    }

    //读取表格号码
    public int readExcel(String string) {
        int k;
        try {


            /**
             * 后续考虑问题,比如Excel里面的图片以及其他数据类型的读取
             **/
            InputStream is = new FileInputStream(path);

            Workbook book = new HSSFWorkbook(is);
//            Workbook book = WorkbookFactory.create(new File(path));
            book.getNumberOfSheets();
            // 获得第一个工作表对象
            Sheet sheet = book.getSheetAt(0);
//            int Rows = sheet.getRows();
//            int Cols = sheet.getColumns();
            Log.d("工作表", sheet.getSheetName());
            System.out.println("当前工作表的名字:" + sheet.getSheetName());

//            System.out.println("总行数:" + Rows);
//            System.out.println("总列数:" + Cols);
            for (int i = 153; i < 208; i++) {
//                for (int j = 0; j < Rows; ++j) {
//                    // getCell(Col,Row)获得单元格的值
//                    System.out.print((sheet.getRow(1).getCell(i)));
                list.add(sheet.getRow(i).getCell(1).toString());

//                }
//                System.out.print("\n");
            }
            Log.d("list", list.toString());
            Log.d("list", String.valueOf(list.size()));
            shownumber.setText(list.toString());
            Log.d("listttttt", list.get(0));
            // 得到第一列第一行的单元格
//            Cell cell1 = sheet.getCell(0, 0);
//            String result = cell1.getContents();
//            System.out.println(result);
            book.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;
    }




    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * 179      * @param uri The Uri to check.
     * 180      * @return Whether the Uri authority is DownloadsProvider.
     * 181
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * 187      * @param uri The Uri to check.
     * 188      * @return Whether the Uri authority is MediaProvider.
     * 189
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


}
