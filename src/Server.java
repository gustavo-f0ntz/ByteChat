// Server.java
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static int PORT;
    private static String CHAT_HISTORY_FILE;
    private static final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        loadConfigurations();

        System.out.println("Chat server started on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());

                    ClientHandler handler = new ClientHandler(clientSocket);
                    clients.add(handler);
                    handler.start();
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to start the server on port " + PORT + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void loadConfigurations() {
        try (InputStream input = new FileInputStream("server_config.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            PORT = Integer.parseInt(prop.getProperty("port", "12345"));
            CHAT_HISTORY_FILE = prop.getProperty("chatHistoryFile", "chat_history.txt");
        } catch (FileNotFoundException e) {
            System.err.println("Configuration file 'server_config.properties' not found. Using default values.");
        } catch (IOException e) {
            System.err.println("Error reading configuration file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number in configuration file. Using default port 12345.");
        } finally {
            if (PORT == 0) PORT = 12345;
            if (CHAT_HISTORY_FILE == null) CHAT_HISTORY_FILE = "chat_history.txt";
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);

                // Recebe o nome de usuário
                out.println("Enter your username:");
                username = in.readLine();
                if (username == null || username.trim().isEmpty()) {
                    username = "Anonymous";
                }
                broadcast("🔔 " + username + " joined the chat");

                String message;
                while ((message = in.readLine()) != null) {
                    broadcast(username + ": " + message);
                }
            } catch (IOException e) {
                System.err.println("Connection error with client " + socket.getInetAddress() + ": " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket for client " + username + ": " + e.getMessage());
                }

                clients.remove(this);
                broadcast("❌ " + username + " left the chat");
            }
        }

        private void broadcast(String message) {
            for (ClientHandler client : clients) {
                client.out.println(message);
            }
            saveMessageToTxt(message);
        }

        private void saveMessageToTxt(String message) {
            try {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String logLine = "[" + timestamp + "] " + message + System.lineSeparator();

                Files.write(
                        Paths.get(CHAT_HISTORY_FILE),
                        logLine.getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND
                );
            } catch (IOException e) {
                System.err.println("Failed to save message to file '" + CHAT_HISTORY_FILE + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}