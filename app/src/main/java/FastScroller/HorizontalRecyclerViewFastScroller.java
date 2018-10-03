package FastScroller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintSet;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.recyclerview_swipefastscroll.R;


/**
 * Created by tonychiu25 on 2018-10-02.
 */
public class HorizontalRecyclerViewFastScroller extends AbstractRecyclerViewFastScroller {

    private View mHandlerInfoView;

    public HorizontalRecyclerViewFastScroller(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, R.layout.horizontal_fast_scroll_layout);
    }

    @Override
    protected GestureDetector.OnGestureListener getOnGestureListener() {
        return new GestureDetector.SimpleOnGestureListener() {
            private float mPreviousX;

            @Override
            public boolean onDown(MotionEvent downEvent) {
                mPreviousX = downEvent.getRawX();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent downEvent, MotionEvent dragEvent, float distanceX, float distanceY) {
                float currentCursorX = dragEvent.getRawX();
                float cursorDeltaX = mPreviousX - currentCursorX;
                mPreviousX = currentCursorX;

                float scrollHandlerXPos = fScrollHandle.getX();
                boolean isScrollUp = cursorDeltaX > 0;
                if (isScrollUp) {
                    scrollHandlerXPos -= Math.abs(cursorDeltaX);
                    scrollHandlerXPos = Math.max(0, scrollHandlerXPos);
                } else {
                    scrollHandlerXPos += Math.abs(cursorDeltaX);
                    int scrollViewWidth = fScrollBar.getWidth();
                    int scrollHandleWidth = fScrollHandle.getWidth();

                    int scrollHandleMaxXPos = scrollViewWidth - scrollHandleWidth;
                    scrollHandlerXPos = Math.min(scrollHandleMaxXPos, scrollHandlerXPos);
                }
                fScrollHandle.setX(scrollHandlerXPos);

                // Set the handler info view, if set by caller.
                if (mHandlerInfoView != null) {
                    // Horizontally align both infoView and scrollHandler
                    float infoViewXPosCenter= scrollHandlerXPos + (fScrollHandle.getWidth() / 2);
                    float infoViewWidth = mHandlerInfoView.getWidth();
                    float infoViewXStart = infoViewXPosCenter - (infoViewWidth / 2);
                    mHandlerInfoView.setX(infoViewXStart);
                }

                return true;
            }
        };
    }

    @Override
    public void attachHandlerInfoView(View view, @HandlerInfoViewPlacement int placement) {
        mHandlerInfoView = view;
        switch (placement) {
            case TOP:
                attachViewTopOfHandler();
                break;
            case BOTTOM:
                attachViewBottomOfHandler();
                break;
            default:
                throw new IllegalStateException(String.format("Cannot attach scroll handler info view with " +
                                "placement %s.  Only top and bottom placements are allowed for %s.",
                        placement,
                        getClass().getSimpleName())
                );
        }

    }

    private void attachViewTopOfHandler() {
        ConstraintSet constraintSet = new ConstraintSet();
        fRootConstraintContainer.addView(mHandlerInfoView);
        constraintSet.clone(fRootConstraintContainer);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.BOTTOM, fScrollHandle.getId(), ConstraintSet.TOP);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.START, fScrollHandle.getId(), ConstraintSet.START);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.END, fScrollHandle.getId(), ConstraintSet.END);
        constraintSet.applyTo(fRootConstraintContainer);
    }

    private void attachViewBottomOfHandler() {
        ConstraintSet constraintSet = new ConstraintSet();
        fRootConstraintContainer.addView(mHandlerInfoView);
        constraintSet.clone(fRootConstraintContainer);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.TOP, fScrollHandle.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.START, fScrollHandle.getId(), ConstraintSet.START);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.END, fScrollHandle.getId(), ConstraintSet.END);
        constraintSet.applyTo(fRootConstraintContainer);
    }
}
