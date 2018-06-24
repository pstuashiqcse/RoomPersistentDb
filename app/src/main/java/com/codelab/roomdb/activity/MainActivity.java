package com.codelab.roomdb.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.codelab.roomdb.R;
import com.codelab.roomdb.adapter.UserAdapter;
import com.codelab.roomdb.data.database.entity.UserDbModel;
import com.codelab.roomdb.data.database.helpers.AppDb;
import com.codelab.roomdb.data.database.helpers.DaoHelper;
import com.codelab.roomdb.data.database.helpers.DbLoaderInterface;
import com.codelab.roomdb.data.database.loader.UserDbLoader;
import com.codelab.roomdb.listeners.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etPhone, etEmail, etAddress;
    private Button btnSearch, btnEdit, btnSave;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<UserDbModel> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initFunctionality();
        initListener();
        loadData();

    }

    private void initView() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etName = (EditText) findViewById(R.id.etName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etAddress = (EditText) findViewById(R.id.etAddress);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnSave = (Button) findViewById(R.id.btnSave);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    private void initFunctionality() {
        arrayList = new ArrayList<>();
        userAdapter = new UserAdapter(getApplicationContext(), arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(userAdapter);
    }

    private void initListener() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(etName.getText().toString());
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDbModel userDbModel = new UserDbModel(
                        etName.getText().toString(),
                        etPhone.getText().toString(),
                        etEmail.getText().toString(),
                        etAddress.getText().toString()
                );
                userDbModel.setId(arrayList.get(arrayList.size()-1).getId());
                edit(userDbModel);// edit last one
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData(
                        etName.getText().toString(),
                        etPhone.getText().toString(),
                        etEmail.getText().toString(),
                        etAddress.getText().toString()
                );
            }
        });

        userAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(final int position, View view) {
                Snackbar.make(recyclerView, getString(R.string.delete_message), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.yes), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteItem(arrayList.get(position));
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_clear:
                clear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void insertData(String name, String phone, String email, String address) {

        // you can add multiple data
        ArrayList<UserDbModel> dataList = new ArrayList<>();
        dataList.add(new UserDbModel(name, phone, email, address)); // adding single data here

        UserDbLoader userDbLoader = new UserDbLoader(getApplicationContext());
        userDbLoader.setDbLoaderInterface(new DbLoaderInterface() {
            @Override
            public void onFinished(Object object) {
                Snackbar.make(recyclerView, getString(R.string.inserted), Snackbar.LENGTH_SHORT).show();
                loadData();
            }
        });
        userDbLoader.execute(DaoHelper.INSERT_ALL, dataList);
    }

    private void loadData() {

        UserDbLoader userDbLoader = new UserDbLoader(getApplicationContext());
        userDbLoader.setDbLoaderInterface(new DbLoaderInterface() {
            @Override
            public void onFinished(Object object) {
                if (object != null) {
                    List<UserDbModel> userDbModels = (List<UserDbModel>) object;
                    arrayList.clear();
                    arrayList.addAll(userDbModels);
                    userAdapter.notifyDataSetChanged();
                }
            }
        });
        userDbLoader.execute(DaoHelper.FETCH_ALL);

    }

    private void deleteItem(UserDbModel userDbModel) {
        UserDbLoader userDbLoader = new UserDbLoader(getApplicationContext());
        userDbLoader.setDbLoaderInterface(new DbLoaderInterface() {
            @Override
            public void onFinished(Object object) {
                loadData();
            }
        });
        userDbLoader.execute(DaoHelper.DELETE, userDbModel);
    }

    private void search(String name) {
        UserDbLoader userDbLoader = new UserDbLoader(getApplicationContext());
        userDbLoader.setDbLoaderInterface(new DbLoaderInterface() {
            @Override
            public void onFinished(Object object) {
                if (object != null) {
                    List<UserDbModel> userDbModels = (List<UserDbModel>) object;
                    if(userDbModels.isEmpty()) {
                        Snackbar.make(recyclerView, getString(R.string.no_search_data), Snackbar.LENGTH_SHORT).show();
                    } else {
                        arrayList.clear();
                        arrayList.addAll(userDbModels);
                        userAdapter.notifyDataSetChanged();
                    }
                } else {
                    Snackbar.make(recyclerView, getString(R.string.no_search_data), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        userDbLoader.execute(DaoHelper.SEARCH, name);
    }

    private void edit(UserDbModel userDbModel) {
        UserDbLoader userDbLoader = new UserDbLoader(getApplicationContext());
        userDbLoader.setDbLoaderInterface(new DbLoaderInterface() {
            @Override
            public void onFinished(Object object) {
                loadData();
            }
        });
        userDbLoader.execute(DaoHelper.EDIT, userDbModel);
    }

    private void clear() {
        Snackbar.make(recyclerView, getString(R.string.delete_message_all), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserDbLoader userDbLoader = new UserDbLoader(getApplicationContext());
                        userDbLoader.setDbLoaderInterface(new DbLoaderInterface() {
                            @Override
                            public void onFinished(Object object) {
                                arrayList.clear();
                                userAdapter.notifyDataSetChanged();
                                Snackbar.make(recyclerView, getString(R.string.deleted_all), Snackbar.LENGTH_LONG).show();
                            }
                        });
                        userDbLoader.execute(DaoHelper.DELETE_ALL);
                    }
                })
                .show();

    }
}
