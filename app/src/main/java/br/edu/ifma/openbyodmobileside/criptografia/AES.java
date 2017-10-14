package br.edu.ifma.openbyodmobileside.criptografia;

import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Windows on 12/08/2017.
 */

public class AES {

    static String criptografa(String mensagem, byte[] chaveSimetrica) {
        String mensagemEncriptada = "";
        try {
            // Criptografa a mensagem
            Cipher encripta = Cipher.getInstance("AES");
            SecretKeySpec key = new SecretKeySpec(chaveSimetrica, "AES");
            encripta.init(Cipher.ENCRYPT_MODE, key);
            byte[] textoBytes = Base64.nullPadString(mensagem).getBytes("UTF-8");
            byte[] textoEncriptado = encripta.doFinal(textoBytes);
            mensagemEncriptada = Base64.fromHex(textoEncriptado);
        } catch (Exception ex) {
            Log.e("Exceção Criptografia", ex.getLocalizedMessage());
        }
        return mensagemEncriptada;
    }

    static String decriptografa(String mensagemCriptografada, byte[] chaveSimetrica) {
        String mensagemOriginal = "";
        try {
            // Decriptografa a mensagem
            Cipher decripta = Cipher.getInstance("AES");
            SecretKeySpec key = new SecretKeySpec(chaveSimetrica, "AES");
            decripta.init(Cipher.DECRYPT_MODE, key);
            byte[] textoBytes = Base64.toHex(mensagemCriptografada);
            byte[] textoDecriptado = decripta.doFinal(textoBytes);
            mensagemOriginal = new String(textoDecriptado, "UTF-8").trim();
        } catch (Exception e) {
            Log.e("Exceção Decriptografia", e.getLocalizedMessage());
        }
        return mensagemOriginal;
    }
}
