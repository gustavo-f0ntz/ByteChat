// ChatClientGUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClientGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private PrintWriter output;
    private BufferedReader input;
    private Socket socket;
    private String username;

    public ChatClientGUI(String serverIP, int serverPort) {
        setTitle("Java Chat Client");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Pergunta o nome do usuário
        username = JOptionPane.showInputDialog(this, "Enter your username:", "Welcome", JOptionPane.PLAIN_MESSAGE);
        if (username == null || username.trim().isEmpty()) {
            username = "Anonymous"; // valor padrão
        }

        // Área de chat
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Campo de texto e botão
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // Ação de enviar
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        // Conectar ao servidor
        try {
            socket = new Socket(serverIP, serverPort);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            // Envia o nome do usuário como primeira mensagem
            output.println(username);

            // Thread para receber mensagens
            new Thread(() -> {
                try {
                    String message;
                    while ((message = input.readLine()) != null) {
                        chatArea.append(message + "\n");
                    }
                } catch (IOException ex) {
                    chatArea.append("Connection closed.\n");
                }
            }).start();

        } catch (IOException ex) {
            chatArea.append("Unable to connect to server.\n");
        }

        setVisible(true);
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            output.println(message);
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClientGUI("127.0.0.1", 12345));
    }
}
