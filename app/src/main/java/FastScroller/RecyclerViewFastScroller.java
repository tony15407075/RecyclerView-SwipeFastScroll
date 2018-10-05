package FastScroller;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

// TODO: 2018-10-05 Comment code.
/**
 * Created by tonychiu25 on 2018-10-03.
 */
interface RecyclerViewFastScroller {
    void bindRecyclerView(@NonNull RecyclerView recyclerView);
    void unbindRecyclerView();
    void show(int ... animationResId);
    void hide(int ... animationResId);
    void setScrollHandlerListener(ScrollHandlerListener scrollHandlerListener);
}
