// Client.java
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        final String SERVER_IP = "127.0.0.1"; // Endereço do servidor (localhost)
        final int SERVER_PORT = 12345;        // Mesma porta do servidor

        try {
            // Conecta ao servidor
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to the server!");

            // Cria canal para enviar mensagens para o servidor
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            // Cria canal para receber mensagens do servidor
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Para ler o que o usuário digita no terminal
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

            String userMessage;
            String serverResponse;

            // Loop principal: envia mensagens digitadas pelo usuário e mostra a resposta do servidor
            while (true) {
                System.out.print("You: ");
                userMessage = keyboard.readLine();

                if (userMessage == null || userMessage.equalsIgnoreCase("exit")) {
                    break; // Sai do chat digitando "exit"
                }

                // Envia mensagem ao servidor
                output.println(userMessage);

                // Lê e mostra a resposta do servidor
                serverResponse = input.readLine();
                System.out.println("Server: " + serverResponse);
            }

            // Fecha conexões
            output.close();
            input.close();
            keyboard.close();
            socket.close();

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}
