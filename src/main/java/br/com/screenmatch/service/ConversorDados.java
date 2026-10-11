package br.com.screenmatch.service;

import tools.jackson.databind.ObjectMapper;

public class ConversorDados implements IConversorDados {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T obterDados(String json, Class<T> classe) {
        try {
            return objectMapper.readValue(json, classe);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter dados", e);
        }
    }
}
