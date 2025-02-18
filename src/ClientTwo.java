import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class ClientTwo {
    public static void main(String[] args) throws IOException {

        var clientTwoServerConnection = new Socket("localhost", 6069);
        var clientTwoWriter = new PrintWriter(clientTwoServerConnection.getOutputStream(), true);
        var clientTwoReader = new BufferedReader(new InputStreamReader(clientTwoServerConnection.getInputStream()));
        var sc = new Scanner(System.in);

        // Leer mensajes del servidor
        new Thread(() -> {
            try {
                String msg;
                while ((msg = clientTwoReader.readLine()) != null) {
                    System.out.println(msg);
                }
            } catch (IOException e) {
                System.out.println("Error reading from server: " + e.getMessage());
            }
        }).start();

        // Enviar mensajes al servidor
        while (true) {
            var msg = sc.nextLine();
            clientTwoWriter.println(msg);
        }

    }
}