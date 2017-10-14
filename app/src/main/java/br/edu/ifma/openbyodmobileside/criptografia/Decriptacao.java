package br.edu.ifma.openbyodmobileside.criptografia;

/**
 * Created by Windows on 23/09/2017.
 */

public class Decriptacao {

    // Decriptografa uma mensagem criptografada com o algoritmo AES
    public static String decriptografa(String mensagem, byte[] chave) {
        return AES.decriptografa(mensagem, chave);
    }
}
