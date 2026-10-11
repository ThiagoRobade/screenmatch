package br.com.screenmatch.service;

public interface IConversorDados {

    <T> T obterDados(String json, Class<T> classe);
}
