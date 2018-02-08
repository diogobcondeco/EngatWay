package com.engatway.classes;

/**
 * Created by diogobcondeco on 18/01/2018.
 */

public class Intriga {

    //Data Variables
    private String id_intriga; // added this
    private String id_sujeito; // added this
    private String sujeito;
    private String mensagem;
    private String id_alvo; // added this
    private String alvo;
    private String descricao;

    // Depois tirar
    /*public Intriga(String sujeito, String mensagem, String alvo, String descricao) {
        this.sujeito = sujeito;
        this.mensagem = mensagem;
        this.alvo = alvo;
        this.descricao = descricao;
    }

    public Intriga () {

    }*/

    //Getters and Setters
    public String getId_intriga() {
        return id_intriga;
    }

    public void setId_intriga(String id_intriga) {
        this.id_intriga = id_intriga;
    }

    public String getSujeito() {
        return sujeito;
    }

    public void setSujeito(String sujeito) {
        this.sujeito = sujeito;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getAlvo() {
        return alvo;
    }

    public void setAlvo(String alvo) {
        this.alvo = alvo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getId_sujeito() {
        return id_sujeito;
    }

    public void setId_sujeito(String id_sujeito) {
        this.id_sujeito = id_sujeito;
    }

    public String getId_alvo() {
        return id_alvo;
    }

    public void setId_alvo(String id_alvo) {
        this.id_alvo = id_alvo;
    }
}