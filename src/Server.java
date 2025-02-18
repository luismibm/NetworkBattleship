import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) throws IOException {

        var server = new ServerSocket(6069);
        var player1 = server.accept();
        var player2 = server.accept();
        Scanner sc = new Scanner(System.in);

        try(var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            var player1Writer = new PrintWriter(player1.getOutputStream(), true);
            var player2Writer = new PrintWriter(player2.getOutputStream(), true);
            var player1Reader = new BufferedReader(new InputStreamReader(player1.getInputStream()));
            var player2Reader = new BufferedReader(new InputStreamReader(player2.getInputStream()));

            // Recibe y retransmite mensajes de player1 a player2
            executor.submit(() -> {
                while (true) {
                    var msg = player1Reader.readLine();
                    if (msg != null) {
                        System.out.println("Player 1: " + msg);
                        player2Writer.println("Player 1: " + msg);
                    }
                }
            });

            // Recibe y retransmite mensajes de player2 a player1
            executor.submit(() -> {
                while (true) {
                    var msg = player2Reader.readLine();
                    if (msg != null) {
                        System.out.println("Player 2: " + msg);
                        player1Writer.println("Player 2: " + msg);
                    }
                }
            });

        } catch (Exception e) {
            System.out.println("Exception on Server");
        }
        sc.close();

    }
}