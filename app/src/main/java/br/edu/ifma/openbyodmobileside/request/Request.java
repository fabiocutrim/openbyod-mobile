package br.edu.ifma.openbyodmobileside.request;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import br.edu.ifma.openbyodmobileside.activity.ActivityCriptografia;
import br.edu.ifma.openbyodmobileside.activity.ActivityPrincipal;
import br.edu.ifma.openbyodmobileside.criptografia.Base64;
import br.edu.ifma.openbyodmobileside.criptografia.Decriptacao;
import br.edu.ifma.openbyodmobileside.criptografia.Encriptacao;
import br.edu.ifma.openbyodmobileside.modelo.Resource;
import br.edu.ifma.openbyodmobileside.rest.APICliente;
import br.edu.ifma.openbyodmobileside.rest.APIInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Windows on 27/09/2017.
 */

public abstract class Request {
    ProgressDialog progressDialog;

    Intent intent;
    String tokenEnviado;
    byte[] chaveSimetrica;
    String chaveCriptografada;
    String requisicao;
    long tempoRespostaRequisicao;
    long tempoGeracaoChaveSimetrica;
    long tempoCriptografiaRequisicao;
    long tempoCriptografiaChave;
    int statusCode;
    String mensagemResposta = "";
    String mensagemOriginal = "";
    String mensagemDecriptografada = "";
    String tokenRecebido = "";

    public Request() {
        // Recupera o token atual
        getToken();
        // Gera a chave simétrica
        getChaveSimetrica();
        // Criptografa a chave simétrica
        getChaveCriptografada();
    }

    APIInterface apiService =
            APICliente.getClient().create(APIInterface.class);

    abstract Call getRequest(Resource resource, String token, String chaveCriptografada);

    abstract void exibeMensagemSucesso();

    abstract void executaAcaoPosterior(String mensagemDecriptografada);

    String getToken() {
        tokenEnviado = System.getProperty("token");
        return tokenEnviado;
    }

    byte[] getChaveSimetrica() {
        long tempoInicioGeracaoChave = System.nanoTime();
        chaveSimetrica = Encriptacao.getChaveSimetrica();
        tempoGeracaoChaveSimetrica = (System.nanoTime() - tempoInicioGeracaoChave);
        return chaveSimetrica;
    }

    String getChaveCriptografada() {
        // Criptografa a chave
        long tempoInicioCriptografiaChave = System.nanoTime();
        chaveCriptografada = Encriptacao.criptografa(chaveSimetrica);
        tempoCriptografiaChave = (System.nanoTime() - tempoInicioCriptografiaChave);
        return chaveCriptografada;
    }

    Resource getResource(String entidadeToString) {
        // Criptografa a mensagem
        long tempoInicioCriptografiaRequisicao = System.nanoTime();
        mensagemOriginal = entidadeToString;
        requisicao = Encriptacao.criptografa(entidadeToString, chaveSimetrica);
        tempoCriptografiaRequisicao = (System.nanoTime() - tempoInicioCriptografiaRequisicao);
        Resource resource = new Resource(requisicao);
        return resource;
    }

    void efetuaRequisicao(String entidadeToString, final ActivityPrincipal activityPrincipal) {
        // Criptografa a requisição
        Resource resource = getResource(entidadeToString);
        // "Chama" o a requisição a ser executada
        Call<Resource> request = getRequest(resource,
                tokenEnviado, chaveCriptografada);

        // Checa se há conexão com a Internet
        if (!APICliente.isOnline(activityPrincipal)) {
            // Fecha o ProgressDialog
            progressDialog.dismiss();
            Toast.makeText(activityPrincipal, "Não há conexão com a Internet!",
                    Toast.LENGTH_LONG).show();
            activityPrincipal.logoff();

        } else {

            final long tempoInicio = System.nanoTime();

            // Executa a comunicação com o servidor
            request.enqueue(new Callback<Resource>() {
                @Override
                public void onResponse(Call<Resource> call, Response<Resource> response) {

                    tempoRespostaRequisicao = System.nanoTime() - tempoInicio;

                    statusCode = response.code();
                    // Fecha o ProgressDialog
                    progressDialog.dismiss();

                    // Processa as respostas de erro enviadas pelo servidor
                    processaErroResponse(statusCode, activityPrincipal, response);
                }

                @Override
                public void onFailure(Call<Resource> call, Throwable t) {
                    // Log error here since request failed
                    call.cancel();
                    progressDialog.dismiss();
                    Toast.makeText(activityPrincipal,
                            "Erro na comunicação com o servidor." +
                                    "\nTente novamente mais tarde!",
                            Toast.LENGTH_SHORT).show();
                    activityPrincipal.logoff();
                }
            });
        }
    }

    void processaErroResponse(int statusCode, ActivityPrincipal activityPrincipal,
                              Response<Resource> response) {
        switch (statusCode) {

            case 403: // Sessão expirada

                Toast.makeText(activityPrincipal,
                        "Sessão Expirada!" +
                                "\nEfetue Login Novamente!",
                        Toast.LENGTH_SHORT).show();

                // startActivityCriptografia(activityPrincipal);

                break;

            case 500: // Erro interno do servidor

                Toast.makeText(activityPrincipal,
                        "Erro temporário do sistema." +
                                "\nTente novamente mais tarde!",
                        Toast.LENGTH_LONG).show();

                // startActivityCriptografia(activityPrincipal);

                break;

            case 400: // Falha na checagem da validade do token

                Toast.makeText(activityPrincipal,
                        "Não foi possível processar suas informações no momento." +
                                "\nTente novamente mais tarde!",
                        Toast.LENGTH_LONG).show();

                // startActivityCriptografia(activityPrincipal);

                break;
        }

        if (statusCode == 400 || statusCode == 403 || statusCode == 500) {
            // Encerra a aplicação
            activityPrincipal.logoff();
            activityPrincipal.startActivity(intent);
        } else {
            // Processa as respostas de sucesso enviadas pelo servidor
            processaSucessoResponse(statusCode, response, activityPrincipal);
            // startActivityCriptografia(activityPrincipal);
        }
    }

    private void processaSucessoResponse(int statusCode, Response<Resource> response,
                                         ActivityPrincipal activityPrincipal) {
        // Processa a resposta
        Resource resposta = response.body();
        mensagemResposta = resposta.getResource();
        tokenRecebido = resposta.getToken();
        /*
            REQUISIÇÃO POST E PUT
        */
        if (statusCode == 201) {
            exibeMensagemSucesso();
            executaAcaoPosterior("");
        }
        /*
            REQUISIÇÃO GET
        */
        // Lista vazia
        else if (statusCode == 206) {

            exibeMensagemSucesso();

            // Lista populada
        } else if (statusCode == 200) {
            // Decriptografa e processa os contatos para exibição
            mensagemDecriptografada = Decriptacao.decriptografa(resposta.getResource(), chaveSimetrica);
            executaAcaoPosterior(mensagemDecriptografada);
        }
        // Atualiza o token
        System.setProperty("token", tokenRecebido);
    }

    private void startActivityCriptografia(ActivityPrincipal activityPrincipal) {
        intent = new Intent(activityPrincipal, ActivityCriptografia.class);
        System.setProperty("chaveSimetrica", Base64.fromHex(chaveSimetrica));
        System.setProperty("chaveCriptografada", chaveCriptografada);
        System.setProperty("requisicao", requisicao);
        System.setProperty("tempoRespostaRequisicao", String.valueOf(tempoRespostaRequisicao));
        System.setProperty("tempoGeracaoChaveSimetrica", String.valueOf(tempoGeracaoChaveSimetrica));
        System.setProperty("tempoCriptografiaRequisicao", String.valueOf(tempoCriptografiaRequisicao));
        System.setProperty("tempoCriptografiaChave", String.valueOf(tempoCriptografiaChave));
        System.setProperty("tokenEnviado", tokenEnviado);
        System.setProperty("tokenRecebido", tokenRecebido);
        System.setProperty("statusCode", String.valueOf(statusCode));
        System.setProperty("mensagemResposta", mensagemResposta);
        System.setProperty("mensagemOriginal", mensagemOriginal);
        System.setProperty("mensagemDecriptografada", mensagemDecriptografada);
    }
}
