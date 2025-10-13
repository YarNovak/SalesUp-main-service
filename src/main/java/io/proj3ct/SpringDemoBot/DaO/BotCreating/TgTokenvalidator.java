package io.proj3ct.SpringDemoBot.DaO.BotCreating;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Service
public class TgTokenvalidator {


        public static boolean isValidTelegramToken(String token) {
            // 1. Перевірка формату токена
            if (!token.matches("^\\d{5,15}:[A-Za-z0-9_-]{30,}$")) {
                return false;
            }

            try {
                // 2. Виклик getMe
                String urlString = "https://api.telegram.org/bot" + token + "/getMe";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // 3. Читаємо відповідь
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                // 4. Перевіряємо, чи є "ok":true
                return response.toString().contains("\"ok\":true");

            } catch (Exception e) {
                return false; // якщо помилка мережі або токен неправильний
            }
        }


    public static DaO printBotInfo(String token) {

            DaO botInfo = new DaO();
        try {
            String urlString = "https://api.telegram.org/bot" + token + "/getMe";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            if (json.getBoolean("ok")) {
                JSONObject result = json.getJSONObject("result");
                String firstName = result.getString("first_name");
                String username = result.getString("username");

                System.out.println("Bot name: " + firstName);
                System.out.println("Bot username: @" + username);

                botInfo.setFirstName(firstName);
                botInfo.setUserName(username);



            } else {
                System.out.println("❌ Невірний токен або бот недоступний");
            }
        } catch (Exception e) {
            System.out.println("❌ Помилка: " + e.getMessage());
        }

        return botInfo;
    }


    @Setter
    @Getter
    public static class DaO{

        String UserName;
        String FirstName;

        public DaO(String UsetName, String FirstName) {
            this.UserName = UsetName;
            this.FirstName = FirstName;
        }
        public DaO(){}

    }



}
