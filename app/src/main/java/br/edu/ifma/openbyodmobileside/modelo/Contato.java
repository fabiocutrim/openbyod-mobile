package br.edu.ifma.openbyodmobileside.modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Windows on 07/05/2017.
 */

public class Contato {
    private String nome;
    private String telefone;
    private String email;
    private String empresa;
    private int id;

    public Contato(int id, String nome, String telefone, String email,
                   String empresa) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.empresa = empresa;
    }

    public Contato(String nome, String telefone, String email,
                   String empresa) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.empresa = empresa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    @Override
    public String toString() {
        try {
            JSONObject jsonObject = new JSONObject();
            if (this.getId() != 0) {
                jsonObject.put("id", this.getId());
            }
            jsonObject.put("nome", this.getNome());
            jsonObject.put("telefone", this.getTelefone());
            jsonObject.put("email", this.getEmail());
            jsonObject.put("empresa", this.getEmpresa());
            return jsonObject.toString();
        } catch (JSONException e) {
            return "Exceção Contato.toString(): " + e.getLocalizedMessage();
        }
    }
}
