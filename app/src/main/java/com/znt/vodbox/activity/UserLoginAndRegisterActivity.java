package com.znt.vodbox.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.znt.vodbox.R;
import com.znt.vodbox.fragment.first.LoginFragment;

public class UserLoginAndRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login_and_register);
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.usermainfragment,new LoginFragment()).commit();
    }
}
