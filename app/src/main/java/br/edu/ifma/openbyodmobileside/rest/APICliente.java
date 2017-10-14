package br.edu.ifma.openbyodmobileside.rest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import br.edu.ifma.openbyodmobileside.conexao.Conexao;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ARTLAN-00 on 20/08/2017.
 */

public class APICliente {
    public static final String BASE_URL = "https://10.0.2.2:8181/OpenBYODREST/rest/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(Conexao.getCertificado())
                    .build();
        }
        return retrofit;
    }

    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        catch (Exception ex) {
            Log.e("Exceção isOnline()", ex.getLocalizedMessage());
            return false;
        }
    }
}
