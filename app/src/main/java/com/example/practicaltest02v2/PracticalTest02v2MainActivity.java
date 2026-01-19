package com.example.practicaltest02v2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.example.practicaltest02v2.network.MyServer;

public class PracticalTest02v2MainActivity extends AppCompatActivity {

    private EditText ipEditText, portEditText, operationEditText;
    private Button sendButton;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02v2_main);

        ipEditText = findViewById(R.id.ip_address_edit_text);
        portEditText = findViewById(R.id.port_edit_text);
        operationEditText = findViewById(R.id.operation_edit_text);
        sendButton = findViewById(R.id.send_button);
        resultTextView = findViewById(R.id.result_text_view);

        ipEditText.setText("localhost");
        portEditText.setText("5000");
        operationEditText.setText("add, 10, 25");

        sendButton.setOnClickListener(v -> {
            String ip = ipEditText.getText().toString();
            String portStr = portEditText.getText().toString();
            String operation = operationEditText.getText().toString();

            if (ip.isEmpty() || portStr.isEmpty() || operation.isEmpty()) {
                resultTextView.setText("Completează toate câmpurile!");
                return;
            }

            int port = Integer.parseInt(portStr);
            new ClientThread(ip, port, operation).start();
            new MyServer(port).startServer();
        });
    }

    class ClientThread extends Thread {
        private String ip;
        private int port;
        private String operation;

        public ClientThread(String ip, int port, String operation) {
            this.ip = ip;
            this.port = port;
            this.operation = operation;
        }

        @Override
        public void run() {
            try (Socket socket = new Socket(ip, port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println(operation);

                final String result = in.readLine();

                runOnUiThread(() -> resultTextView.setText("Server: " + result));

            } catch (Exception e) {
                e.printStackTrace();
                final String error = e.getMessage();
                runOnUiThread(() -> resultTextView.setText("Eroare: " + error));
            }
        }
    }
}