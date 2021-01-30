package com.example.studentmarkscalculator;

import android.content.Intent;

import com.example.studentmarkscalculator.integration.R;


public class NavigationActionBarActivity  {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

   
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       
        int id = item.getItemId();

        switch (id){
           
        }

        return super.onOptionsItemSelected(item);
    }

    private void go(Class c){
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
