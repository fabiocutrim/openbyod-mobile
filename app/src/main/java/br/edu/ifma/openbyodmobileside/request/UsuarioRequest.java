package br.edu.ifma.openbyodmobileside.request;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import br.edu.ifma.openbyodmobileside.R;
import br.edu.ifma.openbyodmobileside.activity.ActivityCriptografia;
import br.edu.ifma.openbyodmobileside.activity.ActivityPrincipal;
import br.edu.ifma.openbyodmobileside.criptografia.Base64;
import br.edu.ifma.openbyodmobileside.criptografia.Decriptacao;
import br.edu.ifma.openbyodmobileside.criptografia.Encriptacao;
import br.edu.ifma.openbyodmobileside.modelo.Resource;
import br.edu.ifma.openbyodmobileside.modelo.Usuario;
import br.edu.ifma.openbyodmobileside.rest.APICliente;
import br.edu.ifma.openbyodmobileside.rest.APIInterface;
import br.edu.ifma.openbyodmobileside.util.Chave;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ARTLAN-00 on 20/08/2017.
 */

public class UsuarioRequest {
    private Context activityLogin;
    private ProgressDialog progress;
    private String emailUsuario;

    private Intent intent;
    private byte[] chaveSimetrica;
    private String chaveCriptografada;
    private String requisicao;
    private long tempoRespostaRequisicao;
    private long tempoGeracaoChaveSimetrica;
    private long tempoCriptografiaRequisicao;
    private long tempoCriptografiaChave;
    private String tokenRecebido = "";
    private int statusCode;
    private String mensagemResposta = "";
    private String mensagemOriginal = "";
    private String mensagemDecriptografada = "";

    public UsuarioRequest(Context activityLogin) {
        this.activityLogin = activityLogin;
        // Carrega o ProgressDialog enquanto processa o login
        progress = new ProgressDialog(this.activityLogin);
        progress.setTitle("Validando seus dados");
        progress.setMessage("Aguarde...");
        progress.setProgressStyle(R.style.ProgressBar);
        progress.show();
    }

    public void efetuaRequisicao(String email, String senha) {
        // Obtém endereço MAC do dispositivo
        String enderecoMAC = getEnderecoMAC();
        enderecoMAC = "5F:92:DC:77:55:F7";

        // Verifica se obteve o endereço MAC
        if (enderecoMAC.equals("") || enderecoMAC.equals("02:00:00:00:00:00")) {

            // Primeiro acesso, que deve ser feito de uma rede sem fio
            if (!Chave.verificaArquivoEnderecoMAC(activityLogin)) {
                // Fecha o ProgressDialog
                progress.dismiss();

                Toast.makeText(activityLogin, "Não foi possível verificar o endereço MAC do seu dispositivo!",
                        Toast.LENGTH_SHORT).show();

                Toast.makeText(activityLogin, "Efetue o acesso através de uma rede sem fio!",
                        Toast.LENGTH_LONG).show();

                return;

            } else {

                enderecoMAC = Chave.getEnderecoMAC(activityLogin);

            }

            /* Salva o endereço MAC em um diretório privado
            */
        } else {

            if (!Chave.verificaArquivoEnderecoMAC(activityLogin)) {

                Chave.salvaEnderecoMAC(activityLogin, enderecoMAC);
            }
        }

        emailUsuario = email;

        // Criptografa a requisição
        criptografaRequisicao(email, senha, enderecoMAC);
        // Executa a comunicação com o servidor
        efetuaRequisicao();
    }

    private void efetuaRequisicao() {
        APIInterface apiService =
                APICliente.getClient().create(APIInterface.class);

        // Executa a comunicação com o servidor
        Resource resource = new Resource(requisicao);
        Call<Resource> request = apiService.autentica(resource,
                chaveCriptografada);

        // Checa se há conexão com a Internet
        if (!APICliente.isOnline(activityLogin)) {
            // Fecha o ProgressDialog
            progress.dismiss();
            Toast.makeText(activityLogin, "Não há conexão com a Internet!",
                    Toast.LENGTH_LONG).show();

            // Efetua a requisição
        } else {

            final long tempoInicio = System.nanoTime();

            request.enqueue(new Callback<Resource>() {
                @Override
                public void onResponse(Call<Resource> call, Response<Resource> response) {

                    tempoRespostaRequisicao = (System.nanoTime() - tempoInicio);

                    // Recebe o status de resposta do servidor
                    statusCode = response.code();
                    // Fecha o ProgressDialog
                    progress.dismiss();

                    // Processa resposta de erro enviada pelo servidor
                    processaErroResponse(statusCode, response);
                }

                @Override
                public void onFailure(Call<Resource> call, Throwable t) {
                    // Log error here since request failed
                    call.cancel();
                    progress.dismiss();
                    Toast.makeText(activityLogin,
                            "Erro na comunicação com o servidor." +
                                    "\nTente novamente mais tarde!",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void criptografaRequisicao(String email, String senha, String enderecoMAC) {
        // Monta o JSON
        Usuario usuario = new Usuario(email, Encriptacao.criptografaSenha(email, senha), enderecoMAC);
        mensagemOriginal = usuario.toString();

        // Gera a chave simétrica
        long tempoInicioGeracaoChave = System.nanoTime();
        chaveSimetrica = Encriptacao.getChaveSimetrica();
        tempoGeracaoChaveSimetrica = (System.nanoTime() - tempoInicioGeracaoChave);

        // Criptografa a mensagem
        long tempoInicioCriptografiaRequisicao = System.nanoTime();
        requisicao = Encriptacao.criptografa(usuario.toString(), chaveSimetrica);
        tempoCriptografiaRequisicao = (System.nanoTime() - tempoInicioCriptografiaRequisicao);

        // Criptografa a chave
        long tempoInicioCriptografiaChave = System.nanoTime();
        chaveCriptografada = Encriptacao.criptografa(chaveSimetrica);
        tempoCriptografiaChave = (System.nanoTime() - tempoInicioCriptografiaChave);
    }

    private void processaErroResponse(int statusCode, Response<Resource> response) {
        switch (statusCode) {

            case 401: // Credenciais incorretas

                Toast.makeText(activityLogin, "Credenciais incorretas!",
                        Toast.LENGTH_LONG).show();

                //  startActivityCriptografia();
                activityLogin.startActivity(intent);

                break;

            case 403: // MAC não validado

                Toast.makeText(activityLogin, "Este dispositivo não está registrado para este usuário!",
                        Toast.LENGTH_LONG).show();

                // startActivityCriptografia();
                activityLogin.startActivity(intent);

                break;

            case 500: // Erro temporário do sistema

                Toast.makeText(activityLogin, "Erro temporário do sistema!",
                        Toast.LENGTH_LONG).show();

                // startActivityCriptografia();
                activityLogin.startActivity(intent);

                break;

            case 202: // Usuário autenticado com sucesso

                // Processa resposta de sucesso enviada pelo servidor
                processaSucessoResponse(response);
                // startActivityCriptografia();

                break;
        }
    }

    private void processaSucessoResponse(Response<Resource> response) {
        // Recebe a resposta do servidor e decriptografa
        Resource resposta = response.body();
        mensagemDecriptografada = Decriptacao.decriptografa(resposta.getResource(), chaveSimetrica);
        // "Seta" as propriedades a serem usadas pela aplicação
        tokenRecebido = resposta.getToken();
        mensagemResposta = resposta.getResource();
        setaPropriedades(mensagemDecriptografada);
        Intent intent = new Intent(activityLogin, ActivityPrincipal.class);
        activityLogin.startActivity(intent);
        // "Chama" a activity principal
        Toast.makeText(activityLogin, "Bem-Vindo(a), " +
                        System.getProperty("nome") + "!",
                Toast.LENGTH_LONG).show();
    }

    private String getEnderecoMAC() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }

                return res1.toString();
            }
        } catch (Exception e) {
            Log.e("Exceção Get MAC", e.getLocalizedMessage());
        }
        return "02:00:00:00:00:00";
    }

    private void setaPropriedades(String json) {
        try {
            JSONObject jsonDecriptografado = new JSONObject(json);
            String nome = jsonDecriptografado.getString("nome");
            String foto = jsonDecriptografado.getString("fotoPerfil");
            Usuario usuario = new Usuario(nome, foto);

            Uri urlFotoPerfil = montaFotoPerfil(usuario.getFoto());
            Uri urlImagemFundoPerfil = montaImagemFundoPerfil();

            System.setProperty("token", tokenRecebido);
            System.setProperty("email", emailUsuario);
            System.setProperty("nome", usuario.getNome());
            System.setProperty("urlFotoPerfil", urlFotoPerfil.getPath());
            System.setProperty("urlImagemFundoPerfil", urlImagemFundoPerfil.getPath());

        } catch (IOException e) {
            Log.e("Exceção Arquivo Foto", e.getLocalizedMessage());

        } catch (JSONException e) {
            Log.e("Exceção JSON", e.getLocalizedMessage());
        }
    }

    private Uri montaFotoPerfil(String foto) throws JSONException, IOException {
        byte[] fotoPerfil = Base64.toHex(foto);
        File file = new File(this.activityLogin.getCacheDir(), emailUsuario + "_fotoPerfil.jpg");
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(fotoPerfil);
        Uri urlFotoPerfil = Uri.fromFile(file);
        outputStream.close();
        return urlFotoPerfil;
    }

    private Uri montaImagemFundoPerfil() throws IOException {
        byte[] imagemFundoPerfil = Base64.toHex(IMAGEM_FUNDO_PERFIL);
        File file = new File(this.activityLogin.getCacheDir(), "imagemFundoPerfil.jpg");
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(imagemFundoPerfil);
        Uri urlImagemFundoPerfil = Uri.fromFile(file);
        outputStream.close();
        return urlImagemFundoPerfil;
    }

    private void startActivityCriptografia() {
        intent = new Intent(activityLogin, ActivityCriptografia.class);
        System.setProperty("chaveSimetrica", Base64.fromHex(chaveSimetrica));
        System.setProperty("chaveCriptografada", chaveCriptografada);
        System.setProperty("requisicao", requisicao);
        System.setProperty("tempoRespostaRequisicao", String.valueOf(tempoRespostaRequisicao));
        System.setProperty("tempoGeracaoChaveSimetrica", String.valueOf(tempoGeracaoChaveSimetrica));
        System.setProperty("tempoCriptografiaRequisicao", String.valueOf(tempoCriptografiaRequisicao));
        System.setProperty("tempoCriptografiaChave", String.valueOf(tempoCriptografiaChave));
        System.setProperty("tokenRecebido", tokenRecebido);
        System.setProperty("statusCode", String.valueOf(statusCode));
        System.setProperty("mensagemResposta", mensagemResposta);
        System.setProperty("mensagemOriginal", mensagemOriginal);
        System.setProperty("mensagemDecriptografada", mensagemDecriptografada);
    }

    private static final String IMAGEM_FUNDO_PERFIL =
            "ffd8ffe000104a46494600010200006400640000ffec00114475636b79000100"
                    + "040000003c0000ffee000e41646f62650064c000000001ffdb00840006040"
                    + "40405040605050609060506090b080606080b0c0a0a0b0a0a0c100c0c0c0c"
                    + "0c0c100c0e0f100f0e0c1313141413131c1b1b1b1c1f1f1f1f1f1f1f1f1f1"
                    + "f010707070d0c0d181010181a1511151a1f1f1f1f1f1f1f1f1f1f1f1f1f1f"
                    + "1f1f1f1f1f1f1f1f1f1f1f1f1f1f1f1f1f1f1f1f1f1f1f1f1f1f1f1f1f1f1"
                    + "f1f1f1f1fffc0001108018c02bc03011100021101031101ffc400a3000100"
                    + "0105010000000000000000000000000401020305060701010001050100000"
                    + "0000000000000000000050203040607011001000201020403050604050402"
                    + "030000000102031104213151054161122232132306b1c14252621471a1d1c"
                    + "28191e1334382e22415f04472a21611010002010301060405020701010100"
                    + "000001020311040531215161b1c11241223213f081e1426252a271a1d1f17"
                    + "22333148216ffda000c03010002110311003f00e2c0000000000000000000"
                    + "063dc6df0ee314e2cb5f5527fce27ac79839cdd6d73ec73456fed62b7fb79"
                    + "3c27ca7cc197166d60122b6d417000a4c031df1c4821e7c13aeb1c2d1c626"
                    + "39ea0daf6beedf1a636fb99d33f2a5fc2ffea0da000000000000000000000"
                    + "00000000000000002ccd9b1e1c739324e958fe7e500d0ee73db3e69cb6888"
                    + "9d3488e911ca018c000000000000000000000000000000000000000000000"
                    + "0000000001d284c68000000000000000000000b33e0c59f15b165afaa96e7"
                    + "1f7c039cde6cf36c32c44ccdb0da7e5e4fba7cc17e1cd1300935b6a0b8005"
                    + "26018ef489043cf83c63fc241b3ed7dda6f31b6dd4e997963c93f8bca7cc1"
                    + "b6000000000000000000000000000000000059972d31639bde74ac0347bbd"
                    + "ddf7193d53c291ee57a030800000000000000000000000000000000000000"
                    + "000000000000000000e8ef4b60b693c69f62376fb8f6f64f46d5c9f17f73e"
                    + "7a7d7e7faae8989e31c92512d5a626274907800000000000000000002ccb8"
                    + "b1e6c76c792b16a5a34b5641ceef7639b61935e37dbda7d8c9d3cade60bb0"
                    + "e6898049adb505c001300c57a44821e7c1ac48363dabbb4eb1b6dd4fb5cb1"
                    + "e59f1f2b7983700000000000000000000000000000000b7264a63a4def3a5"
                    + "6bce41a3de6eefb8c9acf0c71ee57effe20c0000000000000000000000000"
                    + "0000000000000000000000000000000000eeb75b48b44f06bb5b3a764c6d4"
                    + "64c76c169e1ad3a74676df71edec9e8d7b92e32327cd5ecbf9ae89898d638"
                    + "c24e2756a76acc4e93d47af000000000000000000005b931d3252d8f2562d"
                    + "4b4696acf29073bbfd865d8e4f5d75b6dad3ecdbc6be56030e6898e60935b"
                    + "6a0bc0026018ef489043cfb78989e009ddafbb4d66bb6dd5b872c5967ecb7"
                    + "f506e800000000000000000000000000016def4a526f79d2b5e33320d26f7"
                    + "797dc5fa62afbb5fbe411c000000000000000000000000000000000000000"
                    + "000000000000000000007a564a44c35987579840dd6d62d13c172b663e4c7"
                    + "ab519715f05b846b49e70cedbee3dbd93d1aff0025c64658d63b2fe7fe244"
                    + "c4c6b1c6252713131ac352bd26b3a4c6930abd52000000000000000000029"
                    + "7a52f59a5e22d4b4696acf298073bdc3b764d95fe263d6db699e7ce693d27"
                    + "cbcc16e1cda82556da82f00098063bd35044cf822639025f6beed38a6bb6d"
                    + "d5bd8e58f2cf8795bcbcc1bc000000000000000000000000052d6ad2b36b4"
                    + "e958e33320d26f77b6dc5f48e18abeed7af9c823000000000000000000000"
                    + "00000000000000000000000000000000000000003d3e61ac3ad31de9130f6"
                    + "2544c206eb6b1689e0b95b31f26369f2e1be0b4cc46b59e70cdc1b89aff00"
                    + "820b91e3632c6b1d978f8ffa913131ac7294a56d131ac350c98ed4b4d6d1a"
                    + "4c2af5400000000000000000000a5ab5b566b6889acc69313ca601cff0071"
                    + "edb7d9da7361d6db69e71e34fe3e40c7873c4c02556da82f000063bd35045"
                    + "cf82263902476ceeb6dbcc6df733ae1e54c93f87ca7c81bee131ac7189e52"
                    + "0000000000000000000000a5ad5ad66d69d2b1c66641a5df6fadb8b7a6bc3"
                    + "0c728ebe7208a000000000000000000000000000000000000000000000000"
                    + "000000000000003d41ac3ada9303c63bd2261ec299841dced62d13c15d6cc"
                    + "7c98da7cd82f86f3358d6be30ccc19e6b3e083e438eae68eeb474952b68b4"
                    + "6b1c92b5b45a35869f9715b1da6b68d261554b60000000000000000000131"
                    + "1313131ac4f0989073fdcbb5db6b339f6f1aede7dea78d3fed061c39e2639"
                    + "825d6fa82f0000597a6b00899f06be00cddb7b9db6b318371333b7e55bf39"
                    + "a7fda0e82262622627589e31300000000000000000000a4cc444ccce911c6"
                    + "6641a6dfefa73dbd14e1863ffda7a82200000000000000000000000000000"
                    + "000000000000000000000000000000000003d41ac3ad80a4c0f18ef48987a"
                    + "a6610b73b68b44f05712c7be369f3edef86f36ac70f1865e0cf359f042f21"
                    + "c7d7357bad1d256d6d168d612d5b45a35869d9b0db1dbdb68d25554b40000"
                    + "0000000000000001f60343dcfb55b6f33b8db46b879e4c71f87ce3c811b06"
                    + "7d7c412e97d4190000165aba822e6c3131c8193b7772beced18736b6db4f2"
                    + "9e734fe1e40e82b6adab16acc5ab31ac4c729805400000000000000266223"
                    + "59e111ce41a7ee1bf9cd3f0f1ce98a39cfe69fe8084000000000000000000"
                    + "000000000000000000000000000000000000000000000003d3dac3ad2a3d0"
                    + "14981e2cbd225ea99842dceda2d13c15c4ac5e8d36e36f7c579b523f8c756"
                    + "5e1cf359436ff615cd5d27afc256d6d168d63fc52d4bc5a35869b9f05b15b"
                    + "db6eaaaa5900000000000000000000068fba7699c533b9dad7d8e79314787"
                    + "9d7c8113067898804ba5f50640000596aea08b9b0c4c02eedfdc726caff0f"
                    + "26b6db4cf2f1af9c7f407454bd2f48bd262d4b46b5b472980540000000000"
                    + "001a8ee1bff8b338b14fca8f7adf9bfd01040000000000000000000000000"
                    + "000000000000000000000000000000000000000001ea0d61d6c0000526078"
                    + "b2d48987aa6610b73b68b44f05712b17a34fb9db5f15e6f48fe31d5958734"
                    + "d6758446fb635cb5d27f29ee595b45a358ff184b63c9168d61a66e36f6c56"
                    + "f6d9556b0000000000000000000000d2f74ed3359b6e76b5f3cb863edaff0"
                    + "04041c19e263982652f120c9120000b2d5d411b361d6240d86ff2ec727a6d"
                    + "adb6d69f6abe35f3aff407458f253252b931da2d4b46b5b47882e00000000"
                    + "006a7b86ffe26b8714fb1caf68f1f28f20400000000000000000000000000"
                    + "00000000000000000000000000000000000000000001ea0d61d6c00000149"
                    + "81e2cb56261ea99843dc6da2d13c15c4acde9ab4fb9dadf1dbd74e7e31d59"
                    + "38734d675844ef7655cb5d2dfecc55bc5a3a4c738e896c7922f1ac34bdced"
                    + "af86dedb7fbae5c6380000000000000000000034fdd3b4ccccee76b5f6f9e"
                    + "4c51e3e75f306bf06e3504ba5f50648905400596aea08f9b0c4c02dd96fb2"
                    + "ec3269c6fb7b4fb78fa79d7cc1d162cb8f363ae4c568b52dc62d00bc00000"
                    + "01abee3dc3d5ae1c33ecf2bde3c7ca01ae000000000000000000000000000"
                    + "00000000000000000000000000000000000000000007a835875b000000014"
                    + "1e2cb5757aa6611371b78b44f05512b37a34fbada5a96f5d384c3271659ac"
                    + "eb08bde6ceb96bedb430d2f168e931ce12f8b2c5e358697bada5b0db4b74f"
                    + "84f7ae5c62800000000000000000000353dd3b4fae6773b58d32f3c98e3f1"
                    + "79c798359833f84f09f1804ca5f5806489054005b6aea08d9b0ea0b369bcc"
                    + "db0cbac7b586dfee63fbe3cc1d160cf8b3e2ae5c56f552dca7ee906400006"
                    + "b3b8f70e7830cf95ef1f64035a00000000000000000000000000000000000"
                    + "0000000000000000000000000000000000003d41ac3ad800000000283c5b6"
                    + "ac4bd533089b8dbc5a2782a895abd1a7dded2d5b7ae9c261918b2cd675846"
                    + "6ef695c959ada3b18297f5709e168e7097c5962f0d2b79b3b60b693d3e12b"
                    + "97588000000000000000000000d5f74ed3f1a6771b68d33c71bd3c2ff00ea"
                    + "0d4e1cf313a5b84c70989e71209b8f244c0324482a002db575047cb8a2601"
                    + "8b6bbacfb0cd37a7b58adfee63f09f38f30745b7dce1dc618cb86deaa4ff9"
                    + "c4f49f306506bbb8f70d35c38678f2bde3c3ca01ab0000000000000000000"
                    + "0000000000000000000000000000000000000000000000000000007a83587"
                    + "5b0000000000141e2db56261ebc9845cfb78b44f05512b37a34fbbd9cd67d"
                    + "55e1686463c9359d611bbadad72566b68d611e97d784c6968e7096c39a2f1"
                    + "e2d2f7bb2b60b76f6d7e12b97984000000000000000000000d6f74ed51b8d"
                    + "73e088aee239c728bff00a834f8734c4cd6dad6d59d2d59e71300998f26b0"
                    + "0cb120a800b6d1c01133d2349044c1dc33f6fcff00131fb549ff00731cf2b"
                    + "47f506ef3f78c79b6f49db4cc7c48d66d31a4d627c3f8835e000000000000"
                    + "0000000000000000000000000000000000000000000000000000000000000"
                    + "ddfff00caefff00f5bfb9ff00ec7bdfb6d38fa3f8fe6f2617ff00753dfedf"
                    + "877a7bff00e7f37d8fb9fbfafb7c3fd7c3d5daa15be800000000000283c5b"
                    + "6aeaf5e4c22e7c1168e4aa256af46a379b3989f55784c7295fc7926275847"
                    + "6e76d5bd66b68d6251a97d784f0b47384be1cd178f1697bed8db05bbebf09"
                    + "ff55cbcc00000000000000000000006bfba76aaee63e2e2d2bb98ff002bc7"
                    + "49fea0d2e2cb6a5e69789adeb3a5ab3ce241331e489806589054165eda021"
                    + "ee32c444822e3dbfc6b7aef1f2e39475ff404c00000000000000000000000"
                    + "00000000000000000000000000000000000000000000000000759f4dfd3df"
                    + "0bd3bdde57e6f3c3867f0feab79f4e889ddeef5f96bd3e2dcb84e1bd9a66c"
                    + "b1f37ed8eef19f1f27488e6d2000000000000000a0f16dababd79308d9f04"
                    + "5a25544ad5e9ab51bcd9cc4faabc26394af52f313ac30371b78bc4c4c6b12"
                    + "8b5bcccfa6dc2d1fcd2f873c5e3c5a5eff00616c13ac76d3f1d572fa38000"
                    + "00000000000000000103b9f6ba6eabf131e94dc563d9b785a3a581a4a64be"
                    + "3bce3c9135bd674b5679c4825e3c9ac02f9bc6808d9b344402352939adeab"
                    + "7fb7e1e609200000000000000000000000000000000000000000000000000"
                    + "00000000000000000000000ea7e9bfa7bd3e8df6f29ed7bd830dbc3a5ed1f"
                    + "6422f79bbfdb5fce5b7f09c369a66cb1ff1afacfa43a74636c47fdfecff00"
                    + "7bfb1f8d5fddfa7d7f075f6bd3ff00cf057f6ededf769d8b1ffd38fee7dbf"
                    + "747bf4d744850be000000000000000a0f14b55ebc9846cd822d1c9544ad5a"
                    + "ad46f365e31c26394af52f30c1cf822d13131ac21d6d3afa6fc2d1fcd2d83"
                    + "3c5fb27ab4ce438f9c33ac76d3c97b211800000000000000000000085dcbb"
                    + "663ddd3d55d29b8ac7b17ebe56f20683d7930e4b62cb1e8c94e16ac82eb6e"
                    + "634e60c54adb35bd56ff6e3f9824c4444691c815000000000000000000000"
                    + "000000000000000000000000000000000000000000000000001d37d37f4f7"
                    + "abd1bede57d9f7b0619f1e96b47d908cde6eff6d7f36d7c270dae99b2c767"
                    + "edafacfa43aa45b70693ea4fa8b1f6cc5f070697dfe48f62bce2913f8edf7"
                    + "432f6bb69c93acfd285e5f968db57db5edcb3d3c3c67d1c27ab73f17f75f1"
                    + "6dfbaf57c4f8dafb5eaebaa6bd91a69a76342fbd7f7fbf59f7ebaebf1d5ea"
                    + "cd69d5c00000000000000005078a4d5ebc9847cd862d0f6256ed5d5a8de6c"
                    + "b5e311c7c257a97d1839b0c4c692855b4c5bd17e16f09ea96c1b8f7f64f56"
                    + "9bc8f1d3867dd5fa3c97b251400000000000000000000085dcfb662dee2e7"
                    + "e8cf58f9793ee9f207314d96ea33df16e2b38e31ce96f3fe1e5e609d11111"
                    + "1111a44728054000000000000000000000000000000000000000000000000"
                    + "00000000000000000000000747f4e7d3df1a6bbdde57e4c71c3867f17eab7"
                    + "974ea8eddeef4f96bd7e2da384e1bdfa66cb1f2fed8eff19f0f375a896e6d"
                    + "37d45f50e2ed587d18f4c9becb1f2b1f8563f3dbcba75656db6d3927f8a23"
                    + "95e56bb5ae91db927a47acfe3b5c24466cf9af9f3da7265c93eabdedce665"
                    + "395ac44691d1cfb264b5ed36b4eb6949f871e8d347aa1e96d61d6c0000000"
                    + "0000000000141e29303c9847cd86261544addabab53bcd96bacc42f56cc2c"
                    + "b8626106b6b56de8bfbde13d52b8371eeec9ead3b91e36714fba9f4797e8b"
                    + "d948800000000000000000001177dbdaede9e9af1cb6f763a79c834b6b5ad"
                    + "69b5a75b4f199905000000000000000000000000000000000000000000000"
                    + "000000000000000000000000000741f4e7d3dfb99aef3775ffc78e38b1cfe"
                    + "39eb3fa7ed47eef77edf96bd7c9b2f0bc37ddd32e58f93e11fd5fa79baf44"
                    + "3756abea0eff87b56de2234c9bcc91f270ff75bf4c7f364edf6f3927f8a2f"
                    + "94e4ebb5a77e49e91eb3e1e6e0a673ee73df71b8bce4cd927d57bcf8ca72b"
                    + "58ac691d1cf72e5b64b4ded3ada52b1e3d2152db2e9c01e88d61d6c000000"
                    + "00000000000005078a4c0f261833618b42a895bb55a9de6ca262782ed6cc3"
                    + "cb8b54089b56de8bf3f0b754aedf71eeec9ead4392e3271eb7a7d3f18eefd"
                    + "17b2d0a0000000000000000236f7795dbd3871cb6f76bf7c83497bdaf79bd"
                    + "e75b5b8ccc828000000000000000000000000000000000000000000000000"
                    + "0000000000000000000000037bf4efd3f3bcb46eb755d36959f6293ff24c7"
                    + "f6fdac1ddeebd9f2d7eaf26c3c2f0ff007a7ee648ff00afe11fd5fa79bb28"
                    + "88888888d223844421dbcc4359dfbbeedfb56dbd53a64dd64d7e060eb3f9a"
                    + "dd2b0bfb7dbce49f047727c9536b4d67b6f3d23f1f07017c9b9ddee6fb9dc"
                    + "de7266c93adad3f6474884ed29158d23a39de7cf7cb79bde75b4a4e3c7a42"
                    + "a5a66880541e86d61d6c00000000000000000000141e29303c9860cb8a261"
                    + "544a8b57569bbae3dbedf6f7cf9ed14c54e3369fb23cd7f1eb69d23aa3f75"
                    + "34c759b5a74ac39ded9dfebb8cf6c39e9f0a2d3f22f33ce3a5bcd3948988d"
                    + "27b65cfb3deb6bccd63db5ee6e552d000000000000008fbcde536d4eb927d"
                    + "cafdf3e40d264c97c979bde75b5b9c82d0000000000000000000000000000"
                    + "00000000000000000000000000000000000000000001bbfa7bb05b7d78dce"
                    + "e22636759e11ca724c7847e9eac2ddeebd9f2c7d5e49ee1b889dc4fdcbffe"
                    + "51fddfa77bb4ad6b5ac56b115ad634ad63844447821a65be44444691d1afe"
                    + "f7def6ddab6bf1327b79efac60c11ced3e7d2b1e32bd8304e49d23a30391e"
                    + "469b5a7ba7b6d3d23bff00479f66cdbadf6eafbadd5fd79b24f19f088f088"
                    + "8f0884ee3c7148d23a39d6e3717cd79bde75b4a462c7a42b596688054007a"
                    + "1b5875b00000000000000000000000060de6eb6fb4dbdf71b8bc530e38d6d"
                    + "69fe511d6655d29369d23aac67cd4c549bde74ac3cffbaf72dcf77dd464b4"
                    + "4e3db639f91879e9faadd6d29cdbede31c78b9ef25c95b757d7a523a47acf"
                    + "8a267c349a4d623fc74e7a3211a9bdafbb4d66bb6dd5bcb1e59fb2dfd41ba"
                    + "0000000000018377bba6df1eb3c6f3ee57a8347972df2de6f79d6d3ce4168"
                    + "0000000000000000000000000000000000000000000000000000000000000"
                    + "0000000000dc760ec37ee193e36689aece93c6794de63f0c7975961eeb75f"
                    + "6e348fabc939c3f113b9b7befd98a3fbbc23d65db5294a52b4a562b4ac695"
                    + "ac708888f0842ccebdb2df6b58ac4444691085de7bc6d7b56d27366f6b25b"
                    + "861c31ef5edfd3acaee1c3392da430f7fbfa6db1fbadd7e11dff8f8bcf771"
                    + "b9dd770dddf75bab7ab2dfc3c2b5f0ad63c2213d8f1c5234873add6eaf9ef"
                    + "37bcf6cff00978433e3c7a42b63b344680a80003d0dac3ad8000000000000"
                    + "0000000000c3bbddedf69b7bee37178c7871c6b6b4fd91d6655d29369d23a"
                    + "ace7cf4c549bde74ac382eebdd773de37317bc4e3dae39f9183a7eab75b4a"
                    + "736fb78c71e2e7bc9f277dd5fba91d23d67c56531c561908c52f4d410f3e0"
                    + "8989e009bdafbb4e298db6eadec72c7967c3cade5e60dd800000000c3badd"
                    + "63dbe3f55b8da7ddaf8cc83479b2e4cb92725e75b4ff002f2805800000000"
                    + "0000000000000000000000000000000000000000000000000000000000000"
                    + "0036dd87b164ee397e264d69b3a4fb76e5369fcb5fbe589badcc638d23ea4"
                    + "cf13c4db736f75bb31475f1f08f57718f1e3c58eb8f1d6298e91a56b1c222"
                    + "210933333acb7fa52b4ac56b1a4422776eedb5ed9b49dc679d667862c51ef"
                    + "5edd23ef95cc386725b48636fb7b4db63f7dff28ef979e6ef77bbee5bbb6e"
                    + "b753adedc2b58f7695f0ad7c93d8b1452ba439ceef777dc649bdfaf947743"
                    + "2e2c5a2e3199e234054000007a1b5875b00000000000000000000061ddeef"
                    + "6db4dbdf71b9c918f0e38d6d79555acda748eab59b3571d66d79d2b0e0fbb"
                    + "776cfde375179d69b3c7fec619e1ff55bf54ff24e6db6f18e3f939ef29c9d"
                    + "b757eec71d23d67c58e948ac32516bc0063bd2241133e08989e0093db3bad"
                    + "b6f31b7dcceb87963c93f87ca7c81be0000018b73b9c7b7c7ebbf3fc35f19"
                    + "9068b3e7c99b24e4c93accf28f088e900b000000000000000000000000000"
                    + "0000000000000000000000000000000000000000000006cfb1f64cbdcb37a"
                    + "adad36949f9993acfe5af9fd8c5dcee631c7f24b715c5db756d67b31c759f"
                    + "48fc763bac387161c55c58ab14c748d2b58e51084b5a66759eae838f1d695"
                    + "8ad634ac23774ee7b5edbb4b6e7713c2385291ef5ede15aabc58a6f6d2163"
                    + "79bca6df1cdeffef3dd0f3cdf6fb77dd3793badccf1e58f1c7bb4afe584ee"
                    + "1c318eba439d6f77b7dce4f7dff28ee864c58a2217588cf100a80000003d0"
                    + "dac3ad80000000000000000000c3badd6df6b82f9f3de298a91adad3f72aa"
                    + "526d3a42d66cd4c559b5e74ac3cfbbcf71de77ade45b244e3d8629d7060eb"
                    + "faadfabec4e6df6f18e3c5cfb94e52fbab77523a47acab8f1c52b111e0c94"
                    + "52f0000018ef4d411736089f0067edbdd2db598c1b8999dbf2a5fc69ff006"
                    + "837f131311313ac4f18980018b71b8c7831cdeffe11e333d01a3dc67c99f2"
                    + "4e4bcf1f08f088e900c600000000000000000000000000000000000000000"
                    + "000000000000000000000000000000363d97b366ee59f4e34db527e6e5fed"
                    + "af9b1b71b88c71e294e2f8cb6eafdd48eb3e91e2eef060c3b7c34c386b14c"
                    + "548d2b5841dad369d67aba161c35c758a5234ac30f71ee3b5edfb4bee7736"
                    + "f4d2bc22b1ef5ade15ac759558f1cde7485bdd6ea9831cdef3d9e7e10f3ce"
                    + "e3dc777ddb793b9dc70ac70c38a3dda57a479f594ee1c318eba439d6ff7d7"
                    + "dce4f75ba7c23ba15c58b485e6133d63405c000000003d0dac3ad80000000"
                    + "000000000030eeb75836b82f9f3de298a91adad2aa949b4e90b59b3571566"
                    + "d69d2b0e1bb9f73dc777dc45efae3da639f9387fbade69cdbede31c78b9f7"
                    + "29ca5b736eea4748f596288888d23932514a800000000b2d4d4117360d601"
                    + "93b6f73beced18736b6db4f29f1a7fa0379977387161f8b6b6b498d6b31c7"
                    + "d5af2d01a4dcee326e327aeff00f4d7c22018800000000000000000000000"
                    + "0000000000000000000000000000000000000000000000004fecfd9f3f72d"
                    + "c7a6bec60a7fbd97a4748f3963ee37118e3c525c6f1b7dd5f48eca4759fc7"
                    + "c5de6db6d836d82983057d18a91a563ef9f341def369d67aba1e0c14c548a"
                    + "5234ac2cdf6fb6db1dadf73b9bfa3153fce67c2b58f19931e39bce91d54ee"
                    + "7734c349bde74ac7e3479df74ee9baeefbbf8f9bd9c55e1830c72a57facf8"
                    + "ca7b0608c71a47573be4390beeb27badd958e91ddfa98b14442f3019e2ba0"
                    + "2e00000000007a1b5875b000000000000000018775bac1b5c17cf9ef14c54"
                    + "8d6d69554a4da7485acd9ab8ab36b4e95870ddd3ba6e3bbee3d7789c7b3c7"
                    + "3f270ff75bcd39b7dbc638f173ee5394b6e6ddd48e91eb2c511111a432514"
                    + "a800000000000b2d5d4117360898047ae5c98b4a5e667146be98fcbaf4067"
                    + "89898d638c02a000000000000000000000000000000000000000000000000"
                    + "00000000000000000000009bda7b4ee3b96e3e1e3f671578e5cbe158feb3e"
                    + "0b19f3c638d67ab3f8ee3efbabfb6bd958eb3ddfabbdda6d36fb4dbd76f82"
                    + "be9c74ff399f1999eb282c9926f3acf5745db6de986914a469585379bcdb6"
                    + "cf6d7dcee6fe8c38e35b4f8f944478cc94a4da748eaf73e7a62a4def3a561"
                    + "e79ddfbb6ebbc6efe2e4d69b7a6b1b7c1e158eb3d6d3e29ddbede31c69f17"
                    + "3be4b92beeafacf6523a47e3e2b3162885f472456ba02e000000000001e86"
                    + "d61d6c0000000000000018775bac1b5c17cf9ef14c548d6d69554a4da7485"
                    + "acd9ab8eb36b4e95870ddd3ba6e3bbee22f689c7b3c73f270f5fd56f34e6d"
                    + "f6f18e3c5cfb94e52db9b77523a47acb144444690c9452a00000000000000"
                    + "2cb575046cd86260116b6b61b693c71cff00204889898d627589e520a8000"
                    + "0000000000000000000000000000000000000000000000000000000000000"
                    + "0025f6bed7b8ee3b98c38b85638e5cb3cab5febd21673668c75d659bb0d85"
                    + "f7393db5e9f19ee877bb2d96df65b6aedf057d34af39f1b4f8ccf9a0b2649"
                    + "bceb2e8bb5dad30522948ec8ff003f195fbadd60daedefb8dc5e31e1c71ad"
                    + "ed3ff00ce6a6b59b4e91d573366ae2a4def3a561e79de7bcee7bc6ea2d313"
                    + "8f698e7e460e9faadfaa53bb7dbc638f173ce4f93b6eafdd48e91eb3e2c58"
                    + "7144432118915ae80b80000000000001e86d61d6c000000000000062dd6eb"
                    + "06d705f3e7bc5315235b5a5556b369d216b366ae3acdad3a561c2f74ee9b8"
                    + "eefb88b5b5c7b3c73f270f5fd56f34e6df6f18e3c5cfb94e52db9b77523a4"
                    + "7acb144444690c9452a000000000000000002cb575046cd87504589b61b69"
                    + "cf1cf38e9fc0122262622627589e520a80000000000000000000000000000"
                    + "000000000000000000000000000000000002576deddb8ee1b98c1863cf264"
                    + "9e55af595acd9a31d759666cb657dce4f653f39ee877bdbf61b7d8edabb7c"
                    + "11a5638dad3ced6eb281cb966f6d65d1767b3a6df1c529d3ce7be59771b8c"
                    + "3b7c37cf9ef18f0e38f55ef6e510a6b59b4e91d57b2e5ae3acdad3a561e7b"
                    + "df3bde7ef1b988889c7b2c73f27178cfebb79fd89cdb6da31c7f273ee5794"
                    + "b6eafa476638e91eb3e3e48f87168c944a4d6ba02e000000000000001e86d"
                    + "61d6c0000000000018b73b9c1b5c17cf9ef14c548d6d69555acda7485acd9"
                    + "ab8eb36b4e95870bdd3ba6e3bc6e3d56d71ecf1cfc9c3d7f55bcd39b6db46"
                    + "38f173fe5394b6e6ddd8e3a47acb144444690c944aa000000000000000000"
                    + "002cb575047cd875804489b61b72d693ce3ef8048ada2d11313ac4f290540"
                    + "0000000000000000000000000000000000000000000000000000000000012"
                    + "3b7ec371bedcd76f8235b4f1b5a7956bd656f2e58a5759656cf677dc648a5"
                    + "3fda3be5def6deddb7edfb68c1863cf25e79dadd65039b34e4b6b2e8bb2d9"
                    + "536d8fd94fce7be523366c58715f366bc63c58e26d7bdb844442dc44cce90"
                    + "c9bdeb4acdad3a443cfbbff7dcdddf3fc3c7ad3618e75c58e784de7f3dbee"
                    + "8f04e6d76d18e359fa9cff96e56db9b7b6bd98a3a477f8cfa7722e1c3a432"
                    + "90e935ae80b80000000000000001e86d61d6c000000000062dcee706d705f"
                    + "3e7bc5315235b5a5556b369d216b366ae3acdad3a561c2f74ee9b8ef1b8f5"
                    + "5a271ecf1cfc9c5d7f55bcd39b6db4638fe4e7fca7296dcdb48ecc71d23d6"
                    + "58a2222348e4c944aa000000000000000000000002db575046cd8624113db"
                    + "c36d638d279d4122b6adab16aceb120a80000000000000000000000000000"
                    + "0000000000000000000000000000033ecb65b8deee6bb7c15f55edce67956"
                    + "3c6d3e4b7932452359646d76b7cf78a523b67fcbc65def6bed7b7eddb68c3"
                    + "8b8da78e5cb3ced6fe9d21059b34e4b6b2e8bb0d8536d8fdb5ebf19ef94ac"
                    + "9931e3c76c992d14c7489b5ef69d222239ccad446bd90ccb5a2b13333a443"
                    + "80fa87bfe5eed9be06099a76fc73ecd794e498fc56f2e909adaed631c6b3f"
                    + "5341e5f969dcdbdb5ecc51fe7e33e9083870c4433108955ae80b800000000"
                    + "0000000007a1b5875b00000000062dcee706db05f3e7bc5315235b5a5556b"
                    + "369d216b2e5ae3acdad3a561c2f74eebb8ef1b88b5b5c7b3c73f2b175fd56"
                    + "f34e6db6d18e3f939ff0029ca5b736d23b31c748f596288888d23932512a8"
                    + "00000000000000000000000000b6d5d411f2e1898043d2f86dac71acfbd50"
                    + "67adab6ac5ab3ac482e000000000000000000000000000000000000000000"
                    + "000000000000066da6d371bbdc536f82beac97ff00288f199f28519324523"
                    + "59e8bfb6dbdf35e2948d6d2ef3b4f69dbf6ddb7c3c7ed65b71cb97c6d3fd2"
                    + "3c1059f3ce49d67a3a271dc7d36b4f6d7b6d3d67bff44cb5ab4acdef68ad2"
                    + "b1336b4f08888e7332b311ab3e6622359e8e0bea3fa86fdd32fed76d335ed"
                    + "f49e7ca72cc7e29fd3d2133b4dafb3b67eaf2687cc72f3b89f653ff28feef"
                    + "d3b9afc1874866a0526b5d01780000000000000000003d0dac3ad80000000"
                    + "c5b9dce0db60be7cf78a62a46b6b4aaad66d3a42d65cb5c759b5a74ac386e"
                    + "ebdd771ddf71aceb8f658e7e562ebfaade7f62736db68c71fc9cff94e52db"
                    + "9b691d98e3a47acb0c4444691c99289540000000000000000000000000000"
                    + "05b6aea08f970eb0087317c37f5578d67dea833d2f5bd7d559d6017000000"
                    + "0000000000000000000000000000000000000000000000326db6d9f759e98"
                    + "3057d796f3a563ef9f2537bc56359e8bb8305f2de2948d6d2ef3b3f67c1db"
                    + "76fe8afb79efc7365eb3d23ca105b8dc4e49f0744e378da6d69a476de7acf"
                    + "e3e09f3311136b4c456235999e11110b09199d3ab84fa97ea3b772c93b3d9"
                    + "da636359f6ef1c272cc7f6f4ea98da6d7d9f35beaf268dccf31f7e7ede39f"
                    + "f00ae3fbbf4f36af0e1886735e4aad7405e000000000000000000003d0dac"
                    + "3ad800000316e773836d82f9f3de298b1c6b6b4aaad66d3a42d65cb5c759b"
                    + "5a74ac386eebdd771de371aceb8f658e7e562ebfaade69bdb6da31c7f273f"
                    + "e5794b6e6da476638e91eb2c311111a472652255000000000000000000000"
                    + "0000000000016daba823e5c513120876adf0dfd55e5e35ea0cf4bd6f5f557"
                    + "902e00000000000000000000000000000000000000000000000005f83066d"
                    + "c66a61c359be5bce95ac29b5a2b1acf45cc386d92d14a46b69777d97b361e"
                    + "db834e17dcde3e6e5fedaf9420f71b89c93e0e87c5f195dad3bef3d67d23c"
                    + "1b1e111acf088e72c649b87fa9bea4b6fef6d86cada6ceb3a66cb1ff2cc78"
                    + "47e8fb531b4dafb7e6b75f2693cd733f775c58a7e4f8cff57e9e6d3e0c3a4"
                    + "33dada5d6ba02f00000000000000000000007a1b5875b000018b73b9c1b6c"
                    + "17cf9ef14c548d6d69555acda7485bcb96b8eb36b4e95870ddd7baee3bc6e"
                    + "22675c7b2c73f2b175fd56f34dedb6d18e3f939f729cadb736d23b31c748f"
                    + "596188888d23932912a800000000000000000000000000000000000b6d5d4"
                    + "1832e289042b52f8afeaaff008c75067c792b7aeb5ff18e80b80000000000"
                    + "000000000000000000000000000000000001761c397365ae2c559be4bce95"
                    + "ac7399796b4446b3d15e3c76bda2b58d6d2eebb1f64c5db70faada5f77923"
                    + "e6e4e91f96be483dcee6724ff1741e2b8baed6bacf6e49eb3e91f8ed6d18a"
                    + "97713f547d4b3bbb5bb7ec2ff00f8d1c3719ebf8e7c6b59fcbd7aa5b69b4d"
                    + "3e6b75f834be6f99f7eb8b14fcbfba7bfc23c3cda4c182222122d612e95d0"
                    + "17800000000000000000000000f436b0eb6000c5b8dc61db61be7cf78a62a"
                    + "46b6b4aaad66d3a42de5cb5c759b5a74ac386eebdd73f78dc44ceb8f658e7"
                    + "e5629f1fd56f34dedb6d18e3f939ff2bcadb736d23b31c748f596188888d2"
                    + "3932910a800000000000000000000000000000000000000b6d5060cb8a260"
                    + "10af4be2bfaa9cfc63a83363c95bd758ff18e80bc00000000000000000000"
                    + "00000000000000000000001763c7932e4ae3c759be4bce95ac71999979331"
                    + "11acaaa52d7b456b1accbb8ec3d8b1f6ec5f132697de5e3dbb78563f2d7ef"
                    + "9426eb73392748fa5d0389e26bb6afbaddb967af87847ab6cc44cb8dfaa3e"
                    + "a59cd37edddbeff002bdddcee2bf8bad293d3aca5767b4d3e6b7e4d3f9be6"
                    + "7ddae1c53d9fbade91eae7f0608888e0926a8994a680bc000000000000000"
                    + "0000000001e86d61d6c062dc6e30edb05f3e7bc531638d6d69555acda7485"
                    + "bcb96b8eb36b4e95870dddbbae7ef1b8f1c7b1c73ae2c5f9a7f35bcd37b6d"
                    + "b4638fe4e7fcaf2b6dcdb48ecc71d23d65862222348e4ca442a0000000000"
                    + "000000000000000000000000000000029300c19716b0085931df1dfd74e7e"
                    + "31d41971e4ae4aeb1c2639c7405e000000000000000000000000000000000"
                    + "0000000ad297bdeb4a566d7b4e95ac719999f087933a76caaad66d31111ac"
                    + "cbb6ec1d869dbf1fc6cd116de5e38cf38a44fe18f3eb285dd6ebee4e91f4b"
                    + "7de1f888db57df7edcb3fdbe11eb2dcb0d38e43ea9fa9a6d37edbdbefc7dd"
                    + "dcee2bfce949fb6527b3da7eeb7e4d4b9be67ae1c53ff002b7a47acb9cc18"
                    + "22239251a8a5d29a4032000000000000000000000000000f436b0eb6c5b8d"
                    + "c61dbe1be6cf78a62a46b6b4f842aad66d3a42de5cb5c759b5a74ac386eed"
                    + "ddb3f78cfe38f638e7e562f1b4fe6b79a6f6db68c71fc9cff95e56db9b691"
                    + "d98e3a47acb0c4444691c9948854000000000000000000000000000000000"
                    + "0000000000052601832e289042c98ed4b7ae9c260197165ae48e1c2639d41"
                    + "7800000000000000000000000000000000000015adad68ad6266d69d22238"
                    + "cccc932f62266748eaed7e9eec15d8d2373b888b6f2d1c239c6389f08f3ea"
                    + "85ddeebdfd91f4f9b7be1b878dbc7dcbffeb3fdbfaf7b76c24fb93faa7ea6"
                    + "9a4dfb776fbfcdf77739ebf87ad2b3d7acf824b67b4d7e6b74f8355e6f99f"
                    + "6eb8714fcdfba7bbc23c7bfb9cc6df04444704ab4e4ca5340640000000000"
                    + "00000000000000000077fb8dc61dbe1be6cd78a62a46b6b4f2886b55acda7"
                    + "48757cb96b8eb36b4e95870bddbbb67ef19fc71ec71cfcac5e369fcd6f34d"
                    + "edb6d18e3f939ff2bcadb736d23b31c748f596188888d23932910a8000000"
                    + "00000000000000000000000000000000000000000a4c6a0c1931ea0859715"
                    + "a96f5d384c03262cb1923a5a39c0320000000000000000000000000000000"
                    + "0001113331111accf0888e732111abb2fa77e9f8d9d6375baaebbbb47b149"
                    + "ff008e27fb90dbbdd7bfe5afd3e6de785e1fecc7dcc91ff67c23fa7f5f26f"
                    + "982d89cbfd53f534e0f576fd85bff00267867cd5ff8e27f0d7f57d890da6d"
                    + "3ddf35ba359e6b99fb7ae2c53f3fc67bbc3fc7c9ca60c1a25da526529a032"
                    + "000000000000000000000000000000cbddbbb67ef19fc71ec71ceb8b1f8da"
                    + "7f3598bb6db4638fe497e5795b6e6da476638e91eb2c311111a4726522150"
                    + "00000000000000000000000000000000000000000000000001498061c98f5"
                    + "8042cb86d5b7aabc263c417e2cb178d2785e39d4190000000000000000000"
                    + "0000000000000075ff4e7d3dfb68aef3775ff00c99e38b1cfe089f19fd5f6"
                    + "22377bbf77cb5e9e6ddb85e1bed69972c7cff08fe9fd7c9d0a3db2b9afaa3"
                    + "ea6fda45b63b1b6bbcb469972c7fc513e11fafec67ed36beff9adf4f9b5ce"
                    + "6b98fb3138b1cffd9f19fe9fd7c9c860c13ce78ccf1999e729868e9b4a680"
                    + "c9100000000000000000000000000000000a56b111a472054000000000000"
                    + "000000000000000000000000000000000000000005260187263898042cd86"
                    + "d5b7aabc2d1ca417e2cb178d2785e39c03200000000000000000000000000"
                    + "0000eb3e9cfa7be0fa77bbcafce9e38714fe1fd56f3e9d113bbddebf2d7a7"
                    + "c5b9f09c37b34cb963e6fdb1dde33e3e4e911cda1cefd4ff52c6c6b3b3d9d"
                    + "a277d78f6afce3144f8fff009748676d36beff009adf4f9b5fe67988c11f6"
                    + "f1ffe93fdbfab8dc3866666d699b5ad3adad3c66667c66532d1666667594c"
                    + "a522078cb1000000000000000000000000000000000000000000000000000"
                    + "00000000000000000000000000000000000000029300c5931c4820e6c3313"
                    + "eaaf0b472905d8b37afd9b70bc738ebfc0194000000000000000000000000"
                    + "00753f4dfd3de9f4efb794f6b9e0c36f0e96b47d908bde6ef5f96bf9cb6fe"
                    + "1386d34cd963b7f6d7d67d21d3a31b6341f537d495edd4fdaed662dbfc91f"
                    + "c63144fe2b79f48666d76bf72759fa7cd05cc72f1b78f653b72cff6f8ff00"
                    + "8f7438ac58ef7bcdef336bda66d6b5b8ccccf39994d4468d0ad69b4eb3db3"
                    + "2994a68f5e324402a00000000000000000000000000000000000000000000"
                    + "00000000000000000000000000000000000000000000000a4c03164c7a821"
                    + "66c1313eaaf098e3120ae2cdeaf66dc2f1fcc194000000000000000000000"
                    + "0074df4dfd3deaf46fb794f67dec186de3d2f68fb2119bcddfedafe72db38"
                    + "4e1b5d33658ff008d7d67d21d522db7b47f527d474ed98be060d2fbfc91ec"
                    + "579c6389fc76fba197b5db4e49d67e942f2fcb46dabedaf6e59ff2f19f487"
                    + "0f8e993264b65cb69be4bcfaaf7b719999f194dc4444690d02f79b4cdad3a"
                    + "cca5e3c7a43d52cb100a80000000000000000000000000000000000000000"
                    + "0000000000000000000000000000000000000000000000000000029300c59"
                    + "31eb00859f04ebac7098e520ae1cdeaf62fc2f1fcc1940000000000000000"
                    + "0000747f4dfd3df166bbdde57e5471c1867f17eab474e88ede6ef4f96bd7e"
                    + "2da384e1bdfa66cb1f2fed8eff0019f0eeef75a896e6d37d45f50e2ed787e"
                    + "1e3d326fb2c7cac7e158fcf6f2e91e2cadb6da724ff00143f2dcad76b5d23"
                    + "b724f48eef19fc76b84ac66cd9af9b35e7265c93eabdedc66665395ac4469"
                    + "1d1cff264b5ed36b4eb694bc78e21ea865880540000000000000000000000"
                    + "0000000000000000000000000000000000000000000000000000000000000"
                    + "00000000000001498063be389042cfb7f18e13d414c39a667d17e17f09ea0"
                    + "cc000000000000000003a0fa73e9efdccd779bbaff00e3c71c58a7f1cf59f"
                    + "d3f6a3f77bbf6fcb5ebe4d9785e1beee9972c7c9f08feafd3cdd8221bab51"
                    + "f5077fc3dab044562326f32c7c9c3fdf6fd31fcd93b6dbce49fe28be5394a"
                    + "ed69df927a47acf879b8399cfb9cf7dc6e2f3933649f55ef3ce65395ac563"
                    + "48e8e7b972db25a6d69d6d2958f1e8a96d962015000000000000000000000"
                    + "0000000000000000000000000000000000000000000000000000000000000"
                    + "000000000000000000526018ef8f5042cfb7f1053166999f45fdef09ea0cc"
                    + "0000000000000037bf4efd3f3bbb46ef755d36b59f6293ff24c7f6fdac1dd"
                    + "eebd9f2d7eaf26c5c2f0ff007a632648ff00afe11fd5fa79bb2888888888d"
                    + "2238444219bc44359dfbbee0ed3b6f54e993759227e060eb3f9add2b0c8db"
                    + "ede724f823793e4a9b5a6b3db79e91eb3e0e02f7dc6ef717dcee6f3933649"
                    + "d6f69fb23a44276948ac691d1cf33e6be5bcdef3ada5271e388854b4cd100"
                    + "a800000000000000000000000000000000000000000000000000000000000"
                    + "000000000000000000000000000000000000000000029300c77a44821e7c1"
                    + "120b31669d7d193def0b75067000000000001bbfa7bb05b7d78dcee626367"
                    + "59e11ca724c7847e9eac2ddeebd9f2d7eaf24ff000dc3ce79fb993ff28fee"
                    + "fd3bdda56b5ad62b5888ac469111c222210d32dee2222348e8d777cef7b6e"
                    + "d5b5f897f6f3df58c183c6d3d67a563c657b0609c93a47447f23c8d36b4f7"
                    + "4f6da7a477fe8f3fcb9773bddd5f75babfc4cd927599f088f0888f0884ed2"
                    + "9148d23a39dee3717cd79bde75b4a463c710ad659a2015000000000000000"
                    + "0000000000000000000000000000000000000000000000000000000000000"
                    + "000000000000000000000000000000526018ef4d6010f3e089f00598b34c4"
                    + "fa3273fc36ebfc419c000000006e3b0761b6ff0027c7cf135d9d278f84de6"
                    + "3f0c7975961eeb75f6e348fabc939c3f113b99f7dfb3147f77847acbb6a52"
                    + "94a5694ac56958d2b58e11111e108599d5bed6b1588888d22107bcf79db76"
                    + "ada7c6cbed64b70c386278dedfd23c65770619c96d2185c86fe9b6c7eeb75"
                    + "f8477fe3e2f3edc6e375dc3757dd6eadebcb7ff2ac7856b1e1109ec78e291"
                    + "a439deeb737cf79bde7599fc690cf8f1c442b63b34402a000000000000000"
                    + "0000000000000000000000000000000000000000000000000000000000000"
                    + "0000000000000000000000000000000004c031de9120879f6fac72062c596"
                    + "6b318f27fd369fbc120000006dbb0f62c9dc72fc4cbad36749f6edca6d3f9"
                    + "6bf7cb1375b98c71a47d499e2389b6e6deeb76628ebe3e11eaee31e3c78f1"
                    + "d71e3ac531d234ad638444421266667596ff004a45622b58d2210fbbf76da"
                    + "f6bda4ee33ceb69e18b147bd7b748fbe5770e19c96d218bbedf536d8fdf6f"
                    + "ca3be5e7bbaddeefb96f2dbbdd5b5bdb856b1eed6be15ac744ee2c514ae90"
                    + "e75bbdddf71926f79edf2f0865c58f45c6333c402a0000000000000000000"
                    + "0000000000000000000000000000000000000000000000000000000000000"
                    + "00000000000000000000000000000000a483164f4e8081b9f87a4ea0bb6df"
                    + "17e1fb7cbf0ebcf4f30660017e0f81f1f1fee3d5f03d51f17d1ef7a7c7453"
                    + "6d749d3aae62f67be3dfafb35edd3ae8f47da7ed7f6b8bf6be9fdb7a63e17"
                    + "a797a5aee4f77ba7ddd5d436ff006fedd7ede9ecd3b34ee6650bef38fa93f"
                    + "f0065ff00b9c9ff00b1f7bffafe9ff6fe16bc3d1f7f9a7769ecf67cbf9ff8"
                    + "b9d735f7ff00fa27eeff00f9eef6f87af8b061f468ca44a4d7405c0000000"
                    + "00000000000000000000000000000000000003fffd9";
}
