package br.edu.ifma.openbyodmobileside.criptografia;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Windows on 23/09/2017.
 */

public class RSA {

    private static PublicKey getChavePublicaServidor() {
        PublicKey publicKey = null;
        try {
            byte[] chavePublicaBytes = Base64.toHex(CHAVE_PUBLICA_SERVIDOR);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(chavePublicaBytes);
            publicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (Exception e) {
            Log.e("Exceção Chave Pública",  e.getLocalizedMessage());
        }
        return publicKey;
    }

    static String criptografa(byte[] chaveSimetrica) {
        byte[] chaveCriptografada = null;
        try {
            PublicKey chavePublica = getChavePublicaServidor();
            Cipher cipher = Cipher.getInstance("RSA");
            // Criptografa a chave simétrica usando a chave Pública do servidor
            cipher.init(Cipher.ENCRYPT_MODE, chavePublica);
            chaveCriptografada = cipher.doFinal(chaveSimetrica);
        } catch (Exception e) {
            Log.e("Exceção Cripto RSA", e.getLocalizedMessage());
        }
        return Base64.fromHex(chaveCriptografada);
    }

    private static final String CHAVE_PUBLICA_SERVIDOR =
            "30820122300d06092a864886f70d01010105000382010f003082010a028201" +
                    "0100c2734c469104eed94074ebe285f579e58fca877f088413ce4e2a6253ac79a601b1b286148f5e04b" +
                    "b2822d688087cbebb7c5569372872bbab75eeefdd840b9e0968a2e20e340a0bc659fe23b3f63e2be450" +
                    "5ca2994875f49398679e26efb42d528b26760ecf3cc930fb41302417e9c204258ebd8977ecf80536910" +
                    "b670f6dfb4d2b814655f2096768b85966398b0e48906e629c7d4f9ef4c2f8e017e0bf197167bb87a32c" +
                    "5a47b94cd1d1e737737e0d7947dd5eddda1548b8cbc4967254a750f86c39fedd126afe456b0c1258548" +
                    "299281d632e6237a7502dd71980dc4d1b62bc8526792083e693428384c1408a5f581435d106cdde30d6" +
                    "86ddb9ee3565362f7d0203010001";
}
