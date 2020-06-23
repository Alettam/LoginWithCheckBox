package com.example.loginwithcheckbox;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button loginBtn;
    private Button registrationBtn;
    private CheckBox checkBox;
    private EditText loginEdTxt;
    private EditText passwordEdTxt;
    private String login;
    private String password;

    private static String STORAGE_FILE = "storage_file.txt";
    private File file;
    private SharedPreferences saveTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initViews();
    }

    private void initViews() {
        loginEdTxt = findViewById(R.id.loginEdTxt);
        passwordEdTxt = findViewById(R.id.passwordEdTxt);
        checkBox = findViewById(R.id.checkBox);
        saveTxt = getSharedPreferences("PREFS_NAME", 0);
        checkBox.setChecked(saveTxt.getBoolean("isChecked", false));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = saveTxt.edit();
                editor.putBoolean("isChecked", isChecked);
                editor.apply();
            }
        });


        registrationBtn = findViewById(R.id.regBtn);
        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkData()) {
                    registrationIn();
                    Toast.makeText(MainActivity.this, "Регистрация прошла успешно", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Введите данные", Toast.LENGTH_LONG).show();
                }
            }
        });
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkData()) {
                    singIn();
                } else {
                    Toast.makeText(MainActivity.this, "Введите данные", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void registrationIn() {
        if (checkBox.isChecked()) {
            saveExternalFile(login, password);
        } else {
            saveFile("login", login, password);
        }
    }

    private void saveExternalFile(String login, String password) {
        file = new File(getExternalFilesDir(null), STORAGE_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            writer.write(login + ";" + password);
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void singIn() {
        if (checkBox.isChecked()) {
            if(readExternalFile(login, password)){
                Toast.makeText(MainActivity.this, "Вход выполнен", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(MainActivity.this, "Введены неправильный логин или пароль", Toast.LENGTH_LONG).show();
            }
        } else {
            if (readFile("login", login, password)) {
                Toast.makeText(MainActivity.this, "Вход выполнен", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Введены неправильный логин или пароль", Toast.LENGTH_LONG).show();
            }

        }
    }

    private boolean readExternalFile(String login, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            try {
                String line = reader.readLine();
                while (line != null) {
                    String [] data = line.split(";");
                    Log.d("Tag", line);
                    return (data[0].equals(login) && data[1].equals(password));
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean readFile(String fileName, String login, String password) {
        try(FileInputStream fileInputStream = openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader)) {
            try {
                String line = reader.readLine();
                String[] data = line.split(";");
                Log.d("Tag", line);
                return (data[0].equals(login) && data[1].equals(password));
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void saveFile(String fileName, String login, String password) {
        try (FileOutputStream fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
             BufferedWriter bw = new BufferedWriter(outputStreamWriter)) {
            bw.write(login + ";" + password);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private boolean checkData() {
        login = loginEdTxt.getText().toString();
        password = passwordEdTxt.getText().toString();
        return !TextUtils.isEmpty(login) && !TextUtils.isEmpty(password);
    }
}
