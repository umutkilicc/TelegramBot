package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MyAmazingBot extends TelegramLongPollingBot {

    private static final String API_KEY = "your ethereum gas api key";
    private static final String API_URL = "your ethereum gas api url";
    @Override
    public void onUpdateReceived(Update update) {



        String command = update.getMessage().getText();
        if(command.equals("/ethereum")) {

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", API_KEY)
                    .build();
            String blockPrices = null;

            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(response.body());
                    blockPrices = jsonNode.get("blockPrices").get(0).get("baseFeePerGas").asText();

                } else {
                    System.out.println("Error: " + response.statusCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(blockPrices);

            try {
                execute(response);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        // TODO
        return "your  telegram bot name";
    }

    @Override
    public String getBotToken() {
        // TODO
        return "your telegram bot token";
    }


}
