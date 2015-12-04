package nl.sterrenkunde.zonno.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.sterrenkunde.zonno.R;

/**
 * Created by mint on 1-12-15.
 */
public class ProFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pro_fragment, container, false);
        return view;
    }
}
