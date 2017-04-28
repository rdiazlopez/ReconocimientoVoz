package com.cice.rdl.reconocimientovoz;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    Button btnvoz, btntexto;
    TextView txtresultado;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnvoz = (Button)findViewById(R.id.btnvoz);
        btntexto = (Button) findViewById(R.id.btntexto);
        txtresultado = (TextView)findViewById(R.id.txtresultado);

        PackageManager pm = getPackageManager();
        //LLAMAMOS A LA ACTIVIDAD QUE SE ENCARGA DE
        //GESTIONAR EL MICROFONO
        List<ResolveInfo> actividadmicro = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        if (actividadmicro.size() != 0) {
            btnvoz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    capturarVoz();
                }
            });
        }else{
            txtresultado.setText("NO HAY MICROFONO ACTIVO");
        }

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.ENGLISH);
                }
            }
        });

        btntexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = txtresultado.getText().toString();
                Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // API 21 y siguientes
                    String utteranceId=this.hashCode() + "";
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null,utteranceId);
                } else {
                    // API anterior a 21
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

    }
    private void capturarVoz() {
        //CREAMOS UN INTENT PARA PODER LANZAR EL MICROFONO
        //DEL TELEFONO, ENVIANDO EL LENGUAJE DEL PROPIO
        //MOVIL
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        //TEXTO OPCIONAL PARA MOSTRAR AL USUARIO CUANDO SE LE PIDE QUE HABLE.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "");

        //LANZAMOS UN HILO QUE SE ENCARGARÁ DE RECUPERAR LA
        //RESPUESTA CUANDO HAYAMOS TERMINADO DE HABLAR
        //E IRÁ AL MÉTODO ONACTIVITYRESULT
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //ALGUNOS TELEFONOS PIDEN CLAVE DE ACCESO PARA
        //PODER COMUNICARSE CON EL MICRO,
        //DEPENDE DE CADA USUARIO Y DE LA
        //CONFIGURACIÓN DEL TELEFONO
        //TAMBIÉN PREGUNTAMOS SI EL RESULTADO ES OK
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            //CAPTURAMOS LAS POSIBLES COINCIDENCIAS
            //QUE EL MICROFONO NOS HA PROPORCIONADO
            ArrayList<String> palabras = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            //Y AHORA SIMPLEMENTE TENDREMOS QUE MOSTRAR
            //LOS RESULTADOS OBTENIDOS DENTRO DE
            //NUESTRO CONTROL TEXTVIEW
            String resultado = "";
//            for(int i = 0; i < palabras.size(); i++){
//                resultado += palabras.get(i) + "/n ";
//            }
            resultado = palabras.get(0);
            txtresultado.setText(resultado);
        }
    }
}

