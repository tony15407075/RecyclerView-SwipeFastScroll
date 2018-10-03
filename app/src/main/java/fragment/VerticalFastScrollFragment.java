package fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.recyclerview_swipefastscroll.R;

import FastScroller.AbstractRecyclerViewFastScroller;
import FastScroller.VerticalRecyclerViewFastScroller;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerticalFastScrollFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private MyAdapter mAdapter;
    private String[] MY_DATA_SET = {
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
            "DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA","DATA",
    };
    private VerticalRecyclerViewFastScroller mFastScroller;

    public VerticalFastScrollFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_scroll_vertical, container, false);

        initVerticalFastScroller(inflater, container, rootView);
        initRecyclerView(inflater, container, rootView);
        return rootView;
    }

    private void initVerticalFastScroller(LayoutInflater inflater, ViewGroup container, View rootView) {
        View scrollerInfoView = inflater.inflate(R.layout.fast_scroller_info_bubble, container, false);
        mFastScroller = rootView.findViewById(R.id.fast_scroller);
        mFastScroller.attachHandlerInfoView(scrollerInfoView, AbstractRecyclerViewFastScroller.LEFT);
    }

    private void initRecyclerView(LayoutInflater inflater, ViewGroup container, View rootView) {

        mRecyclerView = rootView.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mFastScroller.bindRecyclerView(mRecyclerView);

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(MY_DATA_SET);
        mRecyclerView.setAdapter(mAdapter);
    }

}
