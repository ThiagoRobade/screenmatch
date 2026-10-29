package br.com.screenmatch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsultaGemini {

    public static String obterTraducao(String texto) {
        String apiKey = System.getenv("API_KEY_GEMINI");
        String geminiUrl = System.getenv("GEMINI_URL");

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("ERRO GEMINI: A variável de ambiente 'API_KEY_GEMINI' não foi encontrada ou está vazia.");
            return texto;
        }

        if (geminiUrl == null || geminiUrl.isBlank()) {
            System.err.println("ERRO GEMINI: A variável de ambiente 'GEMINI_URL' não foi encontrada ou está vazia.");
            return texto;
        }

        apiKey = apiKey.trim();
        geminiUrl = geminiUrl.trim();

        String separador = geminiUrl.contains("?") ? "&" : "?";
        String urlCompleta = geminiUrl + separador + "key=" + apiKey;

        ObjectMapper mapper = new ObjectMapper();
        String prompt = "Traduza a seguinte frase para o português. Retorne EXCLUSIVAMENTE o texto traduzido, sem introduções, sem explicações, sem opções alternativas e sem aspas: " + texto;

        var rootNode = mapper.createObjectNode();
        var contentsArray = rootNode.putArray("contents");
        var contentObj = contentsArray.addObject();
        var partsArray = contentObj.putArray("parts");
        partsArray.addObject().put("text", prompt);

        try {
            String jsonPayload = mapper.writeValueAsString(rootNode);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlCompleta))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("ERRO GEMINI - StatusCode: " + response.statusCode());
                System.err.println("ERRO GEMINI - Corpo da resposta: " + response.body());
                return texto;
            }

            JsonNode responseJson = mapper.readTree(response.body());
            JsonNode candidate = responseJson.path("candidates").get(0);

            if (candidate == null || candidate.isMissingNode() || candidate.isNull()) {
                return texto;
            }

            JsonNode textNode = candidate.path("content").path("parts").get(0).path("text");

            if (textNode == null || textNode.isMissingNode() || textNode.isNull()) {
                return texto;
            }

            String traducao = textNode.asText().trim();

            if (traducao.isBlank()) {
                return texto;
            }

            return traducao;

        } catch (IOException e) {
            System.err.println("ERRO GEMINI: Falha de I/O ao ligar à API do Gemini.");
            System.err.println("Mensagem: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("ERRO GEMINI: A chamada à API do Gemini foi interrompida.");
            System.err.println("Mensagem: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERRO GEMINI: Erro inesperado ao processar tradução.");
            System.err.println("Mensagem: " + e.getMessage());
        }

        return texto;
    }
}
