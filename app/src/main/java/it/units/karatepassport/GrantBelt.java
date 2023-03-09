package it.units.karatepassport;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;

public class GrantBelt extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_grant_belt, container,false);
        return view;
    }
}