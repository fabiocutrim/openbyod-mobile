package br.edu.ifma.openbyodmobileside.rest;

import br.edu.ifma.openbyodmobileside.modelo.Resource;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by ARTLAN-00 on 20/08/2017.
 */

public interface APIInterface {
    // Autentica o Usuário
    @POST("usuario/autentica")
    Call<Resource> autentica(@Body Resource resource,
                             @Header("chave") String chave);

    // Lista os Contatos de um Usuário
    @GET("contato/lista")
    Call<Resource> listaContatos(
            @Header("token") String token,
            @Header("chave") String chave
    );

    // Insere um novo Contato
    @POST("contato/insere")
    Call<Resource> insereContato(@Body Resource resource,
                                 @Header("token") String token,
                                 @Header("chave") String chave);

    // Altera os dados de um Contato
    @PUT("contato/atualiza")
    Call<Resource> atualizaContato(@Body Resource resource,
                                   @Header("token") String token,
                                   @Header("chave") String chave);

    // Insere uma chamada
    @POST("chamada/salva")
    Call<Resource> salvaChamada(@Body Resource resource,
                                 @Header("token") String token,
                                 @Header("chave") String chave);

    // Recupera os e-mails da caixa de entrada do usuário
    @GET("email/inbox")
    Call<Resource> getCaixaDeEntrada(
            @Header("token") String token,
            @Header("chave") String chave
    );

    // Envia um e-mail
    @POST("email/envia")
    Call<Resource> enviaEmail(@Body Resource resource,
                                @Header("token") String token,
                                @Header("chave") String chave);
}
