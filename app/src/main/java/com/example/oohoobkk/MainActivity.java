package com.example.oohoobkk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private AccountHelper accountHelper;
    EditText pat;
    EditText tUsrn;
    EditText tPwd;
    Spinner sQuest;
    EditText tAns;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch paten;
    RecyclerView list;
    List<String> uList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accountHelper = new AccountHelper(this, "account.db", null, 1);
        pat = findViewById(R.id.txt_pat);
        Button bAdd = findViewById(R.id.btn_add);
        Button bList = findViewById(R.id.btn_list);
        Button bDelete = findViewById(R.id.btn_delete);
        Button bUpdate = findViewById(R.id.btn_update);
        tUsrn = findViewById(R.id.txt_usrn);
        tPwd = findViewById(R.id.txt_pwd);
        sQuest = findViewById(R.id.spinner_q);
        tAns = findViewById(R.id.txt_ans);
        paten = findViewById(R.id.switch_p);
        list = findViewById(R.id.lst_all);
        uList = new ArrayList<>();
        updateAccounts();
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new AccountAdapter(uList));
        list.setItemAnimator(new DefaultItemAnimator());
        paten.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pat.setEnabled(isChecked);
            }
        });
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountHelper.signup(tUsrn.getText().toString(),
                        MainActivity.encrypt(tPwd.getText().toString()),
                        Integer.parseInt(sQuest.getSelectedItem().toString()),
                        tAns.getText().toString(),
                        paten.isChecked() ? 1 : 0,
                        MainActivity.encrypt(pat.getText().toString())
                        );
            }
        });
        bList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.updateAccounts();
                Objects.requireNonNull(list.getAdapter()).notifyDataSetChanged();
            }
        });
        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountHelper.delete(tUsrn.getText().toString());
            }
        });
        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountHelper.update(tUsrn.getText().toString(),
                        MainActivity.encrypt(tPwd.getText().toString()),
                        Integer.parseInt(sQuest.getSelectedItem().toString()),
                        tAns.getText().toString(),
                        paten.isChecked() ? 1 : 0,
                        MainActivity.encrypt(pat.getText().toString())
                        );
            }
        });
    }

    public void updateAccounts() {
        Cursor c = accountHelper.listall();
        uList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                String usr = c.getString(c.getColumnIndex("username"));
                String pwd = c.getString(c.getColumnIndex("password"));
                int qst = c.getInt(c.getColumnIndex("question"));
                String ans = c.getString(c.getColumnIndex("answer"));
                int pte = c.getInt(c.getColumnIndex("patternenabled"));
                String ptn = c.getString(c.getColumnIndex("pattern"));
                List<String> sl = new ArrayList<>();
                sl.add(usr);
                sl.add(MainActivity.encrypt(pwd));
                sl.add(String.valueOf(qst));
                sl.add(ans);
                sl.add(String.valueOf(pte));
                sl.add(MainActivity.encrypt(pwd));
                uList.add(sl.toString());
            } while (c.moveToNext());
        }
        list.setAdapter(new AccountAdapter(uList));
    }

    public static String encrypt(String str) {
        byte[] encrypted = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            encrypted = md5.digest(str.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new BigInteger(1, encrypted).toString(16);
    }

}