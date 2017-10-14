package br.edu.ifma.openbyodmobileside.modelo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Windows on 23/05/2017.
 */

public class EmailUsuario {
    private String destinatario;
    private String remetente;
    private String data;
    private String assunto;
    private String conteudo;

    public EmailUsuario(String destinatario, String remetente, String data, String assunto,
                        String corpo) {
        super();
        this.destinatario = destinatario;
        this.remetente = remetente;
        this.data = data;
        this.assunto = assunto;
        this.conteudo = corpo;
    }

    public EmailUsuario(String destinatario, String remetente, String assunto,
                        String corpo) {
        super();
        this.destinatario = destinatario;
        this.remetente = remetente;
        this.assunto = assunto;
        this.conteudo = corpo;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getRemetente() {
        return remetente;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    @Override
    public String toString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("destinatario", this.getDestinatario());
            jsonObject.put("remetente", this.getRemetente());
            jsonObject.put("data", this.getData());
            jsonObject.put("assunto", this.getAssunto());
            jsonObject.put("conteudo", this.getConteudo());
            return jsonObject.toString();
        } catch (JSONException e) {
            return "Exceção EmailUsuario.toString(): " + e.getLocalizedMessage();
        }
    }
}
