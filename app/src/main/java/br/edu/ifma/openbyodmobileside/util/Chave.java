package br.edu.ifma.openbyodmobileside.util;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by Windows on 18/05/2017.
 */

public class Chave {

    public static void salvaEnderecoMAC(Context contexto, String enderecoMAC) {
        try {
            FileOutputStream fos = contexto.openFileOutput("MAC_Arquivo_Texto", Context.MODE_PRIVATE);
            fos.write(enderecoMAC.getBytes());
            fos.close();
        } catch (Exception ex) {
            Log.e("Exceção Salva MAC", ex.getLocalizedMessage());
        }
    }

    public static String getEnderecoMAC(Context contexto) {
        String enderecoMAC = "";
        try {
            FileInputStream fis = contexto.openFileInput("MAC_Arquivo_Texto");
            int c;
            while ((c = fis.read()) != -1) {
                enderecoMAC = enderecoMAC + Character.toString((char) c);
            }
            fis.close();
        } catch (Exception ex) {
            Log.e("Exceção Get MAC", ex.getLocalizedMessage());
        }
        return enderecoMAC;
    }

    public static boolean verificaArquivoEnderecoMAC(Context contexto) {
        try {
            FileInputStream fis = contexto.openFileInput("MAC_Arquivo_Texto");
            int c;
            String enderecoMAC = "";
            while ((c = fis.read()) != -1) {
                enderecoMAC = enderecoMAC + Character.toString((char) c);
            }
            fis.close();
        } catch (Exception ex) {
            Log.e("Exceção Chave MAC", ex.getLocalizedMessage());
            return false;
        }
        return true;
    }
}
