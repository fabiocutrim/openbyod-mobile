package br.edu.ifma.openbyodmobileside.criptografia;

import android.util.Log;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.security.MessageDigest;
import java.util.Random;

/**
 * Created by Windows on 23/09/2017.
 */

public class Encriptacao {
    // Criptografa uma mensagem através do algoritmo AES
    public static String criptografa(String mensagem, byte[] chave) {
        return AES.criptografa(mensagem, chave);
    }

    // Criptografa a chave simétrica
    public static String criptografa(byte[] chave) {
        // Criptografa a chave simétrica com o algoritmo assimétrico
        String chaveEncriptada = RSA.criptografa(chave);
        return chaveEncriptada;
    }

    public static byte[] getChaveSimetrica() {
        byte[] chave = null;
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128);
            SecretKey sk = kg.generateKey();
            chave = sk.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            Log.e("Exceção Get Chave", e.getLocalizedMessage());
        }
        return chave;
    }

    public static byte[] getChaveSimetricaString() {
        byte[] chaveSimetrica = null;
        try {
            chaveSimetrica = getRandomString(16).getBytes("UTF-8");
        } catch (Exception e) {
            Log.e("Exceção Get Chave", e.getLocalizedMessage());
        }
        return chaveSimetrica;
    }

    public static String getRandomString(int tamanho) {
        String caracteres = "abcdefghijklmnopqrstuvwxyz"
                + "ABCDEFGHIJKLMNOPQRSTUVYWXZ1234567890";
        Random random = new Random();
        String randomString = "";
        int index;
        for (int i = 0; i < tamanho; i++) {
            index = random.nextInt(caracteres.length());
            randomString += caracteres.substring(index, index + 1);
        }
        return randomString;
    }

    public static byte[] MD5(String string) {
        byte[] hashMd5 = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes("UTF-8"));
            hashMd5 = md.digest();
        } catch (Exception ex) {
            Log.e("Exceção MD5", ex.getLocalizedMessage());
        }
        return hashMd5;
    }

    public static byte[] SHA3(String string) {
        byte[] digest = null;
        try {
            SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
            digest = digestSHA3.digest(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Log.e("Exceção SHA-3", ex.getLocalizedMessage());
        }
        return digest;
    }

    public static byte[] getChaveCriptografiaSenha(String email) {

        return MD5(email);
    }

    public static String criptografaSenha(String email, String senha) {
        byte[] chave = getChaveCriptografiaSenha(email);
        return AES.criptografa(senha, chave);
    }
}
