package br.edu.ifma.openbyodmobileside.modelo;

import android.graphics.drawable.Drawable;

/**
 * Created by Windows on 25/08/2017.
 */

public class ItemHome {
    private String titulo;
    private String descricao;
    private Drawable imagem;

    public ItemHome() {

    }

    public ItemHome(String titulo, String descricao, Drawable imagem) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.imagem = imagem;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Drawable getImagem() {
        return imagem;
    }

    public void setImagem(Drawable imagem) {
        this.imagem = imagem;
    }
}
