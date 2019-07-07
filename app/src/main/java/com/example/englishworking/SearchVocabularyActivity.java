package com.example.englishworking;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class SearchVocabularyActivity extends AppCompatActivity {

    ListView vocabularyList;
    LinearLayout title_layout;

    private static String URL;

    ArrayList vocabularyArrayList;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_vocabulary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        vocabularyList = findViewById(R.id.vocabularyListView);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_vocabulary_fragment, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        searchView.setQueryHint("Vocabulary Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }

                countDownTimer = new CountDownTimer(500,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        URL  = "https://www.seslisozluk.net/"+ newText +"-nedir-ne-demek/" ;
                        getWebsite();
                    }
                };
                countDownTimer.start();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void getWebsite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();
                vocabularyArrayList = new ArrayList();
                try {
                    Document doc  = Jsoup.connect(URL).get();
                    Elements elements = doc.select("body > div > div > div > div > div > div > div > div > div > div > dl > dd > a");  // ilgili sayfanın açıklamasını almak için

                    for (int i = 0; i < elements.size() ; i++) {
                        vocabularyArrayList.add(elements.get(i).text());
                    }
                } catch (IOException e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        title_layout = (LinearLayout)findViewById(R.id.desc_layout);
                        title_layout.setVisibility(View.VISIBLE);
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,vocabularyArrayList);

                        //(C) adımı
                        vocabularyList.setAdapter(arrayAdapter);

                        vocabularyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                URL  = "https://www.seslisozluk.net/"+ vocabularyArrayList.get(position) +"-nedir-ne-demek/" ;
                                getWebsite();
                            }
                        });
                    }
                });
            }
        }).start();
    }

}
