import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class ClientOne {
    public static void main(String[] args) throws IOException {

        var clientOneServerConnection = new Socket("localhost", 6069);
        var clientOneWriter = new PrintWriter(clientOneServerConnection.getOutputStream(), true);
        var clientOneReader = new BufferedReader(new InputStreamReader(clientOneServerConnection.getInputStream()));
        var sc = new Scanner(System.in);

        // Leer mensajes del servidor
        new Thread(() -> {
            try {
                String msg;
                while ((msg = clientOneReader.readLine()) != null) {
                    System.out.println(msg);
                }
            } catch (IOException e) {
                System.out.println("Error reading from server: " + e.getMessage());
            }
        }).start();

        // Enviar mensajes al servidor
        while (true) {
            var msg = sc.nextLine();
            clientOneWriter.println(msg);
        }

    }
}