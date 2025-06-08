package com.example.rotatorazimuthapp;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText inputIP, inputPort, inputAzimuth;
    private TextView resultView;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputIP = findViewById(R.id.inputIP);
        inputPort = findViewById(R.id.inputPort);
        inputAzimuth = findViewById(R.id.inputAzimuth);
        resultView = findViewById(R.id.resultView);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(view -> {
            String ip = inputIP.getText().toString().trim();
            int port = Integer.parseInt(inputPort.getText().toString().trim());
            String az = inputAzimuth.getText().toString().trim();

            if (az.length() == 3) {
                sendCommand(ip, port, "C" + az + "\n");
            } else {
                resultView.setText("Enter a 3-digit azimuth value (e.g., 090)");
            }
        });
    }

    private void sendCommand(String ip, int port, String command) {
        new Thread(() -> {
            try (Socket socket = new Socket(ip, port)) {
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream();
                out.write(command.getBytes());
                out.flush();

                byte[] buffer = new byte[128];
                int len = in.read(buffer);
                String response = new String(buffer, 0, len);

                runOnUiThread(() -> resultView.setText("Response: " + response));
            } catch (Exception e) {
                runOnUiThread(() -> resultView.setText("Error: " + e.getMessage()));
            }
        }).start();
    }
}
