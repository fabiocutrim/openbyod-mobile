package br.edu.ifma.openbyodmobileside.modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Windows on 25/08/2017.
 */

public class Usuario {
    private String nome;
    private String senha;
    private String email;
    private String enderecoMAC;
    private String foto;

    public Usuario(String email, String senha, String enderecoMAC) {
        this.senha = senha;
        this.email = email;
        this.enderecoMAC = enderecoMAC;
    }

    public Usuario(String nome, String foto) {
        this.nome = nome;
        this.foto = foto;
    }

    @Override
    public String toString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("senha", this.getSenha());
            jsonObject.put("email", this.getEmail());
            jsonObject.put("enderecoMAC", this.getEnderecoMAC());
            return jsonObject.toString();
        } catch (JSONException e) {
            return "Exceção Usuario.toString(): " + e.getLocalizedMessage();
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEnderecoMAC() {
        return enderecoMAC;
    }

    public void setEnderecoMAC(String enderecoMAC) {
        this.enderecoMAC = enderecoMAC;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
