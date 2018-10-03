package fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.recyclerview_swipefastscroll.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HorizontalFastScrollFragment extends Fragment {


    public HorizontalFastScrollFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_horizontal_fast_scroll, container, false);
    }

}
