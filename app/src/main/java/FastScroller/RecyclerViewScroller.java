package FastScroller;

import android.support.v7.widget.RecyclerView;

/**
 * Created by tonychiu25 on 2018-10-03.
 */
interface RecyclerViewScroller {
    void bindRecyclerView(RecyclerView recyclerView);
    void unbindRecyclerView();
}
