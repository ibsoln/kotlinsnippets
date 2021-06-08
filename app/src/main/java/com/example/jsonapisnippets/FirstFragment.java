package com.example.jsonapisnippets;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.couchbase.lite.CouchbaseLiteException;
import com.example.jsonapisnippets.databinding.FragmentFirstBinding;

import org.json.JSONException;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
//    private Context app = MainActivity.getApplicationContext();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void runQueries() {

        DatabaseManager dbm = DatabaseManager.getSharedInstance();

        try {
            dbm.seedDatabase();
            dbm.testQuerySyntaxCount();
            dbm.testQuerySyntaxJson();
        } catch (CouchbaseLiteException | JSONException e) {
            e.printStackTrace();
        }


    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);


                runQueries();




            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}