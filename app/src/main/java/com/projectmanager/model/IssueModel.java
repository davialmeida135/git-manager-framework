package com.projectmanager.model;



public class IssueModel {
    private String Titulo;
    private String Descricao;
    private int Id_criador;
    private int Id_projeto;
    private String Data_criacao;
    private String Prazo;
    
    public String getTitulo() {
        return Titulo;
    }
    public void setTitulo(String titulo) {
        Titulo = titulo;
    }
    public String getDescricao() {
        return Descricao;
    }
    public void setDescricao(String descricao) {
        Descricao = descricao;
    }
    public int getId_criador() {
        return Id_criador;
    }
    public void setId_criador(int id_criador) {
        Id_criador = id_criador;
    }
    public int getId_projeto() {
        return Id_projeto;
    }
    public void setId_projeto(int id_projeto) {
        Id_projeto = id_projeto;
    }
    public String getData_criacao() {
        return Data_criacao;
    }
    public void setData_criacao(String data_criacao) {
        Data_criacao = data_criacao;
    }
    public String getPrazo() {
        return Prazo;
    }
    public void setPrazo(String prazo) {
        Prazo = prazo;
    }

    
}