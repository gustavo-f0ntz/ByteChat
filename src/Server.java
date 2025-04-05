// Server.java
import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server {
    private static final int PORT = 12345;
    private static final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("Chat server started on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    // Classe interna que representa um cliente conectado
    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ) {
                out = new PrintWriter(socket.getOutputStream(), true);

                // Recebe o nome de usuário
                out.println("Enter your username:");
                username = in.readLine();
                broadcast("🔔 " + username + " joined the chat");

                String message;
                while ((message = in.readLine()) != null) {
                    broadcast(username + ": " + message);
                }

            } catch (IOException e) {
                System.out.println("Connection error with " + username);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {}

                clients.remove(this);
                broadcast("❌ " + username + " left the chat");
            }
        }

        // Envia uma mensagem para todos os clientes conectados
        private void broadcast(String message) {
            for (ClientHandler client : clients) {
                client.out.println(message);
            }
            saveMessageToTxt(message);
        }

        // Salva a mensagem no arquivo chat_history.txt com data/hora
        private void saveMessageToTxt(String message) {
            try {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String logLine = "[" + timestamp + "] " + message + System.lineSeparator();

                Files.write(
                        Paths.get("chat_history.txt"),
                        logLine.getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND
                );
            } catch (IOException e) {
                System.out.println("Failed to save message to .txt: " + e.getMessage());
            }
        }
    }
}
