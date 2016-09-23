package com.example.ricardo.cotacaoapi;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Model.APIResponse;
import Model.Currency;

public class MainActivity extends AppCompatActivity {
    ListView currencyList;
    APIResponse response;
    private static final int PERMISSION_REQUEST = 1;
    private static final String currencies[] = {"USD","EUR","ARS","GBP","BTC"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        currencyList = (ListView) findViewById(R.id.currencyList);

        //Creates Listener to check clicks on the ListView
        currencyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Check if the app has Internet Permission
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.INTERNET)
                        == PackageManager.PERMISSION_GRANTED) {

                    //Check if the user has connection with the internet
                    if(isNetworkAvailable()) {
                        GetValuesAsyncTask getValues = new GetValuesAsyncTask();
                        getValues.execute(position);
                    }
                    //If not, send a toast to the user
                    else{
                        Toast.makeText(MainActivity.this, "Não há conexão com a internet",
                                Toast.LENGTH_LONG).show();
                    }
                }

                //If not, requests permission from the user
                else{
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.INTERNET},
                            PERMISSION_REQUEST);
                }
            }
        });

    }

    //Check if there is internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return (activeNetworkInfo != null) && (activeNetworkInfo.isConnected());
    }

    //AssynTask to request currencia value from the API
    public class GetValuesAsyncTask extends AsyncTask<Integer, String, String> {
        Currency currency;
        protected String doInBackground(Integer... params) {
            try{
                String directionsUrl = "http://api.promasters.net.br/cotacao/v1/valores?moedas="+currencies[params[0]]+"&alt=json";

                //Connection
                URL url = new URL(directionsUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection(); conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-length", "0");
                conn.setUseCaches(false);
                conn.setAllowUserInteraction(false);
                conn.connect();

                //read content
                InputStream is = conn.getInputStream();
                BufferedReader reader;
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String data;
                String content = "";
                while ((data = reader.readLine()) != null) {
                    content += data + "\n";
                }

                parseJson(content,params[0]);

                return content;
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        //Create an AlertDialog after receiving and parsing the JSON
        protected void onPostExecute(String result) {
            System.out.println("status: "+ currency.getNome());
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Cotação")
                    .setMessage("Nome: "+ currency.getNome()+"\n"+
                                "Valor: "+currency.getValor()+"\n"+
                                "Fonte: "+currency.getFonte())
                    .setNegativeButton("Voltar",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

        //Parse the JSON received from the API
        public void parseJson(String json,int currenciesToParse){

            JsonElement jelement = new JsonParser().parse(json);
            JsonObject jobject = jelement.getAsJsonObject().getAsJsonObject("valores");
            JsonObject jCurrency = jobject.getAsJsonObject(currencies[currenciesToParse]);

            Gson gson = new Gson();
            currency = gson.fromJson(jCurrency,Currency.class);
        }
    }
}
