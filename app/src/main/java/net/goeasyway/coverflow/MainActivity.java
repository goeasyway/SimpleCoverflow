package net.goeasyway.coverflow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CoverflowView coverflowView = (CoverflowView) findViewById(R.id.coverflowview);
        coverflowView.setAdapter(new MyAdapter());
//        coverflowView.setSelection(3);
    }

     private class MyAdapter extends BaseAdapter {

         @Override
         public int getCount() {
             return 5;
         }

         @Override
         public Object getItem(int position) {
             return position;
         }

         @Override
         public long getItemId(int position) {
             return position;
         }

         @Override
         public View getView(int position, View convertView, ViewGroup parent) {
             View view = null;
             if (convertView == null) {
                 view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_layout, null);
             } else {
                 view = convertView;
             }
             return view;
         }
     }
}
