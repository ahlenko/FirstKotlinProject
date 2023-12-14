package com.mycompany.testtask;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrentUserInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user_info);

        Bundle receivedBundle = getIntent().getExtras();
        int userID = 0; if (receivedBundle != null) {
            userID = receivedBundle.getInt("user_id");
        } else {
            Toast.makeText(this, "NotEnableUserData", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        int finalUserID = userID;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://jsonplaceholder.typicode.com/users");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    TextView name = findViewById(R.id.UserName);
                    Button email = findViewById(R.id.buttonEmail);
                    Button phone = findViewById(R.id.buttonPhone);
                    Button adress = findViewById(R.id.buttonAdress);
                    Button site = findViewById(R.id.buttonSite);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        response.append(line);}

                    reader.close(); connection.disconnect();

                    JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();
                    JsonObject userObject = jsonArray.get(finalUserID).getAsJsonObject();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            name.setText(userObject.get("name").getAsString());
                            email.setText(userObject.get("email").getAsString());
                            phone.setText(userObject.get("phone").getAsString());
                            site.setText(userObject.get("website").getAsString());
                        }
                    });

                    JsonObject adressOBJ = userObject.getAsJsonObject("address");

                    String adr = adressOBJ.get("city").getAsString() + ", " + adressOBJ.get("street").getAsString() + ", " + adressOBJ.get("suite").getAsString() ;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adress.setText(adr);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CurrentUserInfoActivity.this, "NotEnableUserData", Toast.LENGTH_SHORT).show();
                            CurrentUserInfoActivity.this.finish();
                        }
                    });
                }


            }
        }).start();
    }

    public void Button(View view) {
        this.finish();
    }
}