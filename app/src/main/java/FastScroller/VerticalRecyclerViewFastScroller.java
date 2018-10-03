package FastScroller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintSet;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.recyclerview_swipefastscroll.R;

/**
 * Created by tonychiu25 on 2018-10-02.
 */
public class VerticalRecyclerViewFastScroller extends AbstractRecyclerViewFastScroller {

    private View mHandlerInfoView;

    public VerticalRecyclerViewFastScroller(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, R.layout.vertical_fast_scroll_layout);
    }

    @Override
    protected GestureDetector.OnGestureListener getOnGestureListener() {
        return new GestureDetector.SimpleOnGestureListener() {
            private float mPreviousCursorY;

            @Override
            public boolean onDown(MotionEvent downEvent) {
                Log.i("TONY -- Func_s", " onDown() .(VerticalRecyclerViewFastScroller.java:32)");
                mPreviousCursorY = downEvent.getRawY();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent downEvent, MotionEvent dragEvent, float distanceX, float distanceY) {
                Log.i("TONY -- Func_s", " onScroll() .(VerticalRecyclerViewFastScroller.java:40)");
                float currentCursorY = dragEvent.getRawY();
                float cursorDeltaY = mPreviousCursorY - currentCursorY;
                mPreviousCursorY = currentCursorY;

                float scrollHandlerYPos = fScrollHandle.getY();
                boolean isScrollUp = cursorDeltaY > 0;
                if (isScrollUp) {
                    scrollHandlerYPos -= Math.abs(cursorDeltaY);
                    scrollHandlerYPos = Math.max(0, scrollHandlerYPos);
                } else {
                    scrollHandlerYPos += Math.abs(cursorDeltaY);
                    int scrollViewHeight = fScrollBar.getHeight();
                    int scrollHandleHeight = fScrollHandle.getHeight();

                    int scrollHandleMaxYPos = scrollViewHeight - scrollHandleHeight;
                    scrollHandlerYPos = Math.min(scrollHandleMaxYPos, scrollHandlerYPos);
                }
                fScrollHandle.setY(scrollHandlerYPos);

                // Set the handler info view, if set by caller.
                if (mHandlerInfoView != null) {
                    // Horizontally align both infoView and scrollHandler
                    float infoViewYPosCenter= scrollHandlerYPos + (fScrollHandle.getHeight() / 2);
                    float infoViewHeight = mHandlerInfoView.getHeight();
                    float infoViewYTop = infoViewYPosCenter - (infoViewHeight / 2);
                    mHandlerInfoView.setY(infoViewYTop);
                }

                return true;
            }
        };

    }

    /**
     * Attaches a auxiliary info view next to the handler for displaying information while scrolling.
     *
     * @param view : the info view to be attachment.
     * @param placement : the placement of that view relative to the position of the scroll handler.
     *                    {
     *                      @link AbstractRecyclerViewFastScroller.HandlerInfoViewPlacement.LEFT or
     *                      @link AbstractRecyclerViewFastScroller.HandlerInfoViewPlacement.RIGHT
     *                    }
     */
    @Override
    public void attachHandlerInfoView(View view, @HandlerInfoViewPlacement int placement) {
        mHandlerInfoView = view;
        switch (placement) {
            case LEFT:
                attachViewLeftOfHandler();
                break;
            case RIGHT:
                attachViewRightOfHandler();
                break;
            default:
                throw new IllegalStateException(String.format("Cannot attach scroll handler info view with " +
                        "placement %s.  Only left and right placements are allowed for %s.",
                        placement,
                        VerticalRecyclerViewFastScroller.class.getSimpleName())
                );
        }
    }

    private void attachViewRightOfHandler() {
        ConstraintSet constraintSet = new ConstraintSet();
        fRootConstraintContainer.addView(mHandlerInfoView);
        constraintSet.clone(fRootConstraintContainer);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.TOP, fScrollHandle.getId(), ConstraintSet.TOP);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.BOTTOM, fScrollHandle.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.END, fScrollHandle.getId(), ConstraintSet.START);
        constraintSet.applyTo(fRootConstraintContainer);
    }

    private void attachViewLeftOfHandler() {
        ConstraintSet constraintSet = new ConstraintSet();
        fRootConstraintContainer.addView(mHandlerInfoView);
        constraintSet.clone(fRootConstraintContainer);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.TOP, fScrollHandle.getId(), ConstraintSet.TOP);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.BOTTOM, fScrollHandle.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.START, fScrollHandle.getId(), ConstraintSet.END);
        constraintSet.applyTo(fRootConstraintContainer);
    }
}
