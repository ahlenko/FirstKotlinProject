package com.mycompany.testtask;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UsersScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_screen);

        LinearLayout container = findViewById(R.id.UserContainer);
        TextView stateProblem = findViewById(R.id.UserNotInListTitle);
        ArrayList<View> userList = new ArrayList<>();

        View.OnClickListener buttonToScreen = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextIntent = new Intent(UsersScreenActivity.this, CurrentUserInfoActivity.class);
                Bundle bundle = new Bundle();
                int userID = (int)v.getId() - 1;
                bundle.putInt("user_id", userID);
                nextIntent.putExtras(bundle);
                startActivity(nextIntent);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://jsonplaceholder.typicode.com/users");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close(); connection.disconnect();

                    JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();

                    for (int i = 0; i < jsonArray.size(); i++) {
                        View temp = getLayoutInflater().inflate(R.layout.fragment_user,
                                container, false);
                        TextView name = temp.findViewById(R.id.userName);
                        TextView email = temp.findViewById(R.id.userEmail);
                        TextView info = temp.findViewById(R.id.userInfo);
                        ImageView avatar = temp.findViewById(R.id.userAvatar);
                        ImageButton button = temp.findViewById(R.id.toThreeScreen);

                        JsonObject userObject = jsonArray.get(i).getAsJsonObject();

                        int userId = userObject.get("id").getAsInt();
                        name.setText(userObject.get("name").getAsString());
                        email.setText(userObject.get("email").getAsString());
                        String avatarUrl = "https://quizee.app/storage/avatars/" + userId + ".jpeg";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Picasso.get().load(avatarUrl).into(avatar);
                            }
                        });

                        JsonObject companyObject = userObject.getAsJsonObject("company");
                        info.setText(companyObject.get("catchPhrase").getAsString());
                        button.setOnClickListener(buttonToScreen); button.setId(i+1);
                        userList.add(temp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stateProblem.setVisibility(View.VISIBLE);
                            stateProblem.setText("ReadUserProblem");
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        if (!userList.isEmpty()){
                            for (View userView : userList) {
                                container.addView(userView);
                            }
                        } else {
                            stateProblem.setVisibility(View.VISIBLE);
                            stateProblem.setText("UserNotInList");
                        }
                    }
                });
            }
        }).start();
    }
}