package br.edu.ifma.openbyodmobileside.conexao;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import br.edu.ifma.openbyodmobileside.criptografia.Base64;
import okhttp3.OkHttpClient;

/**
 * Created by Windows on 04/06/2017.
 */

public class Conexao {
    public static OkHttpClient getCertificado() {
        OkHttpClient client;
        try {
            Certificate ca = getCertificadoServidor();
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            HostnameVerifier verificadorDeNomesDeHost = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    HostnameVerifier verificador =
                            HttpsURLConnection.getDefaultHostnameVerifier();
                    return verificador.verify("localhost", session);
                }
            };

            client = new OkHttpClient.Builder()
                    .readTimeout(40, TimeUnit.SECONDS)
                    .connectTimeout(40, TimeUnit.SECONDS)
                    .sslSocketFactory(sslContext.getSocketFactory())
                    .hostnameVerifier(verificadorDeNomesDeHost)
                    .build();

        } catch (Exception e) {
            Log.e("Exceção getCert()", e.getLocalizedMessage());
            return null;
        }

        return client;
    }

    private static X509Certificate getCertificadoServidor()
            throws Exception {

        byte[] cert = Base64.toHex(CERTIFICADO_SERVIDOR);
        ByteArrayInputStream bais = new ByteArrayInputStream(cert);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate verifyCert = (X509Certificate) cf.generateCertificate(bais);
        bais.close();
        return verifyCert;
    }

    private static final String CERTIFICADO_SERVIDOR =
            "3082039930820281a00302010202040d75898d300d06092a864886f70d01010b" +
            "0500307d310b3009060355040613025553311330110603550408130a43616c69666f726e69613114301" +
            "20603550407130b53616e746120436c617261311b3019060355040a13124f7261636c6520436f72706f" +
            "726174696f6e31123010060355040b1309476c6173734669736831123010060355040313096c6f63616" +
            "c686f7374301e170d3135303931393139303730385a170d3235303931363139303730385a307d310b30" +
            "09060355040613025553311330110603550408130a43616c69666f726e6961311430120603550407130" +
            "b53616e746120436c617261311b3019060355040a13124f7261636c6520436f72706f726174696f6e31" +
            "123010060355040b1309476c6173734669736831123010060355040313096c6f63616c686f737430820" +
            "122300d06092a864886f70d01010105000382010f003082010a028201010099de2ca3b513e081d8f466" +
            "c0fa1e7ce2930390f8c0d959b5ea091970e69c225d4fabc59214e719e85dbd7197437d9d7e182053a1a" +
            "00e1bbba7319e6fa7dbedfa08de7cb2cc85d805ff58a7458397be8b5df699984969bf811f5ed60e5486" +
            "474690628e7b6cee18a54b8a598db1f78a7427b19d59eaa6f3bc076f679859bf6ced2dd5395cc33512b" +
            "9a80889613e8c4290453f5b2c3cde2fcdb546e18dc42c17d91fb9ec7b4d8f2cf49d6f2cc6bda4b3092f" +
            "317762233680e706ab686ded1b1a80274e58264c9cf734e9921dd6ddab58ad0e7eb4062166e4b70e8cc" +
            "cff67801ac23cda1788948c5d03a641ed63f9830dba71f3da6670bffbbb6526db8ecd27f19102030100" +
            "01a321301f301d0603551d0e04160414e8c8ac7efb3862894f5c86209d5fc7853080475c300d06092a8" +
            "64886f70d01010b050003820101000f8f963e2e2b30d96fa63864e946f0ddad541ff62f541bfebcc14f" +
            "8e3036dcb29bee42c56c52f384d84c9eeb8afa3e873500a32bdaee454bad9f6d5339f1c8d58258592f2" +
            "06526f28f19e433ac81290bee11afd42026087add90d1cacbe0bb89d2e4e08366286de225093ae0423d" +
            "f50fffa7bf86323bd503760f0dbe02fa15cf250b217f98ec75f1591f9f542c9917114ee989c8a0047ba" +
            "505e5eee32b8e1e4591ee8347658f66035aeb199edf83bc7d9b018107bcda936450383c3511bc100891" +
            "e627b05ee084a1434f005d958fa164974964487f2f525134568fb04ea977718b82d19dc87dfe1671bf8" +
            "94a1d3f94f392a7bdbf69df019b57e910d3ffd8c7f1";
}
