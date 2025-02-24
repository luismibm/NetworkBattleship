import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class Server {

    // Por razones de tiempo no me da tiempo a hacer el Battleship, he tenido que hacer un juego más simple
    // Problemas actuales del juego:
    // 1. No respeta turnos
    // 2. Al empezar, se muestra en terminal el mensaje de selección y luego tu asignación de jugador
    // 3. El mensaje de victoria/derrota solo se muestra al perdedor (por como se maneja el opponentWriter)

    static char[][] board = {
            {' ', ' ', ' '},
            {' ', ' ', ' '},
            {' ', ' ', ' '}
    };

    static char currentPlayer = 'X';

    // handlePlayer pide que board y currentPlayer variables sean static

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

            /* Recibe y retransmite mensajes de player1 a player2
            executor.submit(() -> {
                while (true) {
                    var msg = player1Reader.readLine();
                    if (msg != null) {
                        System.out.println("Player 1: " + msg);
                        player2Writer.println("Player 1: " + msg);
                    }
                }
            });
            */

            /* Recibe y retransmite mensajes de player2 a player1
            executor.submit(() -> {
                while (true) {
                    var msg = player2Reader.readLine();
                    if (msg != null) {
                        System.out.println("Player 2: " + msg);
                        player1Writer.println("Player 2: " + msg);
                    }
                }
            });
            */

            player1Writer.println("Assigned player 1 (X)");
            player2Writer.println("Assigned player 2 (O)");

            executor.submit(() -> {
                try {
                    handlePlayer(player1Reader, player2Writer, 'X');
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            executor.submit(() -> {
                try {
                    handlePlayer(player2Reader, player1Writer, 'O');
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // Por alguna razón Java pide los try-catch aquí arriba aunque el propio handlePlayer lance IOException

        } catch (Exception e) {
            System.out.println("Sabrías que ha pasado si no fuera una Exception general, crack");
        }
        sc.close();

    }

    // TODO: Ahora mismo solo el rival recibe el mensaje de juego finalizado
    static void handlePlayer(BufferedReader reader, PrintWriter opponentWriter, char player) throws IOException {
        String msg;
        while ((msg = reader.readLine()) != null) {
            int move = Integer.parseInt(msg);
            int row = (move - 1) / 3;
            int col = (move - 1) % 3;

            if (board[row][col] == ' ') {
                board[row][col] = player;
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                printBoard();
                opponentWriter.println("Player " + player + " moved to " + move);
                if (checkWin(player)) {
                    opponentWriter.println("Player " + player + " wins!");
                    break;
                }
                if (isBoardFull()) {
                    opponentWriter.println("It's a draw!");
                    break;
                }
            } else {
                opponentWriter.println("Invalid move, try again.");
            }
        }
    }

    static void printBoard() {
        for (char[] row : board) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    static boolean checkWin(char player) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) return true;
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) return true;
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) return true;
        return false;
    }

    static boolean isBoardFull() {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == ' ') return false;
            }
        }
        return true;
    }

    // handlePlayer pide que todos estos métodos sean static

}