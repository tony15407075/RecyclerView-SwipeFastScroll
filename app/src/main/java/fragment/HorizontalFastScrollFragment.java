package fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.recyclerview_swipefastscroll.R;

import FastScroller.AbstractRecyclerViewFastScroller;
import FastScroller.HorizontalRecyclerViewFastScroller;

/**
 * A simple {@link Fragment} subclass.
 */
public class HorizontalFastScrollFragment extends Fragment {


    public HorizontalFastScrollFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View scrollerInfoView = inflater.inflate(R.layout.fast_scroller_info_bubble, container, false);
        View rootView = inflater.inflate(R.layout.fragment_scroll_horizontal, container, false);

        HorizontalRecyclerViewFastScroller fastScroller = rootView.findViewById(R.id.fast_scroller);

        return rootView;
    }

}
