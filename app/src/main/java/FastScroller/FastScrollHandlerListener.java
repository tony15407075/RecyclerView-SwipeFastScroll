/**
 * Created by tonychiu25 on 2018-10-05.
 */
package FastScroller;

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
}
