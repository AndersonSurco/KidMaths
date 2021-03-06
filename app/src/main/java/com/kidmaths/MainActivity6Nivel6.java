package com.kidmaths;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity6Nivel6 extends AppCompatActivity {

    private TextView tv_nombre, tv_score;
    private ImageView iv_Auno, iv_Ados, iv_vidas, iv_signo;
    private EditText et_respuesta;
    private MediaPlayer mp,mp_great,mp_bad;

    int score, numAleatorio_uno ,numAleatorio_dos, signoaAleatorio, resultado, vidas;
    String nombre_jugador, string_score, string_vidas;

    String numero[]= {"cero","uno","dos","tres","cuatro","cinco","seis","siete","ocho","nueve"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity6_nivel6);

        Toast.makeText(this,"Nivel 6", Toast.LENGTH_SHORT).show();

        tv_nombre = (TextView) findViewById(R.id.textView_nombre);
        tv_score = (TextView) findViewById(R.id.textView_score);
        iv_Auno = (ImageView) findViewById(R.id.imageView_NumUno);
        iv_Ados = (ImageView) findViewById(R.id.imageView_NumDos);
        iv_signo = (ImageView) findViewById(R.id.imageView_signo);
        iv_vidas = (ImageView) findViewById(R.id.imageView_vidas);
        et_respuesta = (EditText) findViewById(R.id.editTextNumber);

        nombre_jugador = getIntent().getStringExtra("jugador");
        string_score = getIntent().getStringExtra("score");
        string_vidas = getIntent().getStringExtra("vidas");

        tv_nombre.setText("Jugador:" +nombre_jugador);
        tv_score.setText("Score: "+string_score);

        score = Integer.parseInt(string_score);
        vidas = Integer.parseInt(string_vidas);
        vidas(vidas);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        mp = MediaPlayer.create(this,R.raw.goats);
        mp.start();
        mp.setLooping(true);

        mp_great = MediaPlayer.create(this,R.raw.wonderful);
        mp_bad = MediaPlayer.create(this,R.raw.bad);
        NumAleatorio();
    }

    public  void NumAleatorio(){

            resultado = 0;
            numAleatorio_uno = (int)(Math.random() * 10);
            numAleatorio_dos = (int)(Math.random() * 10);
            signoaAleatorio = (int) (Math.random() * 3);

            if(signoaAleatorio == 0){
                int id = getResources().getIdentifier("adicion","drawable",getPackageName());
                iv_signo.setImageResource(id);
                resultado = numAleatorio_uno + numAleatorio_dos;
            }else if(signoaAleatorio == 1){
                int id = getResources().getIdentifier("resta","drawable",getPackageName());
                iv_signo.setImageResource(id);
                resultado = numAleatorio_uno - numAleatorio_dos;
            }else if(signoaAleatorio == 2){
                int id = getResources().getIdentifier("multiplicacion","drawable",getPackageName());
                iv_signo.setImageResource(id);
                resultado = numAleatorio_uno * numAleatorio_dos;
            }

            if(resultado > 0){
                for(int i=0; i<numero.length; i++){
                    int id = getResources().getIdentifier(numero[i],"drawable",getPackageName());
                    if(numAleatorio_uno == i){
                        iv_Auno.setImageResource(id);
                    }
                    if(numAleatorio_dos == i){
                        iv_Ados.setImageResource(id);
                    }
                }
            }else{
                NumAleatorio();
            }


    }

    public void Comparar(View view){
        String respuesta = et_respuesta.getText().toString();
        if(!respuesta.equals("")){
            int respuesta_jugador = Integer.parseInt(respuesta);
            if(respuesta_jugador == resultado){
                mp_great.start();
                score++;
                tv_score.setText("Score: "+score);
                et_respuesta.setText("");
                BaseDeDatos();
            }else{
                mp_bad.start();
                vidas--;
                BaseDeDatos();
                vidas(vidas);
                et_respuesta.setText("");
            }
            NumAleatorio();
        }else{
            Toast.makeText(this,"Escribe tu respuesta",Toast.LENGTH_SHORT).show();
        }
    }

    public void BaseDeDatos(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"db",null,1);
        SQLiteDatabase BD = admin.getWritableDatabase();

        Cursor consulta = BD.rawQuery("select * from puntaje where score=(select max(score) from puntaje)",null);

        if(consulta.moveToFirst()){
            String temp_nombre = consulta.getString(0);
            String temp_score = consulta.getString(1);
            int bestScore = Integer.parseInt(temp_score);
            if(score>bestScore){
                ContentValues modificacion = new ContentValues();
                modificacion.put("nombre",nombre_jugador);
                modificacion.put("score",score);
                BD.update("puntaje",modificacion,"score="+bestScore,null);
            }
            BD.close();
        }else{
            ContentValues insertar = new ContentValues();
            insertar.put("nombre",nombre_jugador);
            insertar.put("score",score);
            BD.insert("puntaje",null,insertar);
            BD.close();
        }
    }

    public void vidas(int vidas){
        switch (vidas){
            case 3: iv_vidas.setImageResource(R.drawable.tresvidas);break;
            case 2:
                Toast.makeText(this,"Te quedan dos manzanas",Toast.LENGTH_SHORT).show();
                iv_vidas.setImageResource(R.drawable.dosvidas);break;
            case 1:
                Toast.makeText(this,"Te quedan una manzana",Toast.LENGTH_SHORT).show();
                iv_vidas.setImageResource(R.drawable.unavida);break;
            case 0:
                Toast.makeText(this,"Haz perdido todas tus manzanas",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
                mp.stop();
                mp.release();break;
        }
    }
}