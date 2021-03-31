package com.example.apppdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    final String fileName = "pdfPrueba.pdf";
    TextView prueba;
    Button descargar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        prueba = findViewById(R.id.tvPrueba);
        descargar = findViewById(R.id.btnDescargar);

        descargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearPdf();
            }
        });

        validarPermisos();


    }


    void validarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Verifica permisos para Android 6.0+
            int permissionCheck = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Log.i("Mensaje", "No se tiene permiso para leer.");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
            } else {
                Log.i("Mensaje", "Se tiene permiso para leer!");
            }
        }
    }

    public void crearPdf() {
        Rect bounds = new Rect();
        int pageWidth = 300;
        int pageheight = 470;
        int pathHeight = 2;


        String ruta = "/sdcard/" + fileName;
        PdfDocument myPdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint paint2 = new Paint();
        Path path = new Path();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageheight, 1).create();
        PdfDocument.Page documentPage = myPdfDocument.startPage(myPageInfo);
        Canvas canvas = documentPage.getCanvas();
        int y = 25;
        int x = 10;


        //Traer valores de campos Edditext o TextView
        paint.getTextBounds(prueba.getText().toString(), 0, prueba.getText().toString().length(), bounds);
        x = (canvas.getWidth() / 2) - (bounds.width() / 2);
        canvas.drawText(prueba.getText().toString(), x, y, paint);


        //Espacios en blanco
        y += paint.descent() - paint.ascent();
        canvas.drawText("", x, y, paint);

        //Linea Horizontal
        path.lineTo(pageWidth, pathHeight);
        paint2.setColor(Color.GRAY);
        paint2.setStyle(Paint.Style.STROKE);
        path.moveTo(x, y);

        canvas.drawLine(0, y, pageWidth, y, paint2);


        //Texto manual
        y += paint.descent() - paint.ascent();
        x = 10;
        canvas.drawText("Este es un texto enviado desde la clase", x, y, paint);


        //Agregar una imagen desde Res
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.car);
        Bitmap b = (Bitmap.createScaledBitmap(bitmap, 100, 50, false));
        canvas.drawBitmap(b, x, y, paint);
        y += 25;
        canvas.drawText(getString(R.string.app_name), 120, y, paint);


        myPdfDocument.finishPage(documentPage);

        File file = new File(ruta);
        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        myPdfDocument.close();
        verPdf();
    }

    public void verPdf() {
        String ruta = "/sdcard/"+fileName;
        File file = new File(ruta);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

}