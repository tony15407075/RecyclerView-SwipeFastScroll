/**
 * Created by tonychiu25 on 2018-10-03.
 */
package FastScroller;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

interface RecyclerViewFastScroller {
    /**
     * Binds a {@link RecyclerView} to RecyclerViewFastScroller, thereby synchronising their scroll
     * events.
     *
     * @param recyclerView : a {@link RecyclerView}
     */
    void bindRecyclerView(@NonNull RecyclerView recyclerView);

    /**
     * Unbinds the current {@link RecyclerView} from the RecyclerViewFastScroller
     */
    void unbindRecyclerView();

    /**
     * Show the {@link RecyclerViewFastScroller}
     * @param animationResId(Optional) : animation during the show progress.
     */
    void show(int ... animationResId);

    /**
     * Hide the {@link RecyclerViewFastScroller}
     * @param animationResId(Optional) : animation during the hide progress.
     */
    void hide(int ... animationResId);

    /**
     * Set listeners to listen for scroll events.
     * @param fastScrollHandlerListener : a {@link FastScrollHandlerListener}
     */
    void setScrollHandlerListener(FastScrollHandlerListener fastScrollHandlerListener);
}
