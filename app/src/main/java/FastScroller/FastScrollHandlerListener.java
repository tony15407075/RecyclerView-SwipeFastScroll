/**
 * Created by tonychiu25 on 2018-10-05.
 */
package FastScroller;

import android.support.v7.widget.LinearLayoutManager;

// Interface to listen for fast scroll handler scroll events.
public interface FastScrollHandlerListener {

    /**
     * When user initiates a scroll handler drag event; usually coincides with MotionEvent.ACTION_DOWN
     */
    void onHandlerScrollStart();

    /**
     * When user finished a scroll handler drag event, usually coincides with MotionEvent.ACTION_UP
     */
    void onHandlerScrollEnd();

    /**
     * When user is currently scrolling the handler.
     * @param firstCompletelyVisibleItemPosition:
     *          The adapter position of the first fully visible view.  This position
     *          does not include adapter changes that were dispatched after the last
     *          layout pass. Refer to {@link LinearLayoutManager#findFirstCompletelyVisibleItemPosition()}
     *          for detail.
     */
    void onHandlerScrolling(int firstCompletelyVisibleItemPosition);

}
