package FastScroller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.recyclerview_swipefastscroll.R;

/**
 * Created by tonychiu25 on 2018-10-02.
 */
public class VerticalRecyclerViewFastScroller extends AbstractRecyclerViewFastScroller {

    private View mHandlerInfoView;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private @HandlerInfoVerticalPlacement int mHandlerInfoVerticalPlacement;

    public VerticalRecyclerViewFastScroller(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, R.layout.vertical_fast_scroll_layout);
    }

    @Override
    protected GestureDetector.OnGestureListener getHandlerGestureListener() {
        return new GestureDetector.SimpleOnGestureListener() {
            private float mCurrentCursorY;

            @Override
            public boolean onDown(MotionEvent downEvent) {
                mCurrentCursorY = downEvent.getRawY();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent downEvent, MotionEvent dragEvent, float distanceX, float distanceY) {
                float cursorDeltaY = mCurrentCursorY - dragEvent.getRawY();
                mCurrentCursorY -= cursorDeltaY;

                float scrollHandlerYPos = calculateHandlerYPosition(cursorDeltaY);
                fScrollHandler.setY(scrollHandlerYPos);

                syncHandlerInfoViewPosition(scrollHandlerYPos);
                syncRecyclerViewPosition(scrollHandlerYPos);
                return true;
            }
        };
    }

    // Sync the recycler view's position with the handler's current position.
    @Override
    protected void syncRecyclerViewPosition(float handlerCurrentY) {
        float recyclerViewScrollYPos = mRecyclerView.computeVerticalScrollOffset();
        float recyclerViewScrollableHeight = mRecyclerView.computeVerticalScrollRange() - mRecyclerView.computeVerticalScrollExtent();

        float yScrolledPercentage = handlerCurrentY / getHandlerMaxScrollableHeight();

        float recyclerViewNewScrollYPos = recyclerViewScrollableHeight * yScrolledPercentage;
        float dy = recyclerViewNewScrollYPos - recyclerViewScrollYPos;

        mRecyclerView.scrollBy(0, (int) dy);
    }

    // Sync the handler info view's position with the handler's current position.
    @Override
    protected void syncHandlerInfoViewPosition(float handlerCurrentY) {
        if (mHandlerInfoView == null) {
            return;
        }

        // Horizontally align both infoView and scrollHandler
        float infoViewHeight = mHandlerInfoView.getHeight();
        int handlerHeight = fScrollHandler.getHeight();
        float infoViewYTop;
        switch (mHandlerInfoVerticalPlacement) {
            case TOP:
                infoViewYTop = handlerCurrentY - infoViewHeight;
                break;
            case CENTER:
                float infoViewYPosCenter = handlerCurrentY + (handlerHeight / 2);
                infoViewYTop = infoViewYPosCenter - (infoViewHeight / 2);
                break;
            case BOTTOM:
                infoViewYTop = handlerCurrentY + handlerHeight;
                break;
            default:
                throw new IllegalStateException("Handler info view has illegal vertical placement, " +
                        "can only be one of {TOP, CENTER, BOTTOM}");
        }

        float infoViewTopMin = 0.0f;
        float infoViewTopMax = fRootConstraintContainer.getHeight() - infoViewHeight;

        // Should not be less than infoViewTopMin
        if (infoViewYTop < infoViewTopMin) {
            mHandlerInfoView.setY(infoViewTopMin);
        }
        // Should not be larger than infoViewTopMax
        else if (infoViewYTop > infoViewTopMax) {
            mHandlerInfoView.setY(infoViewTopMax);
        }
        // Within range of [min, max], can then safely set that value.
        else {
            mHandlerInfoView.setY(infoViewYTop);
        }
    }

    /**
     * Attaches a auxiliary info view next to the handler for displaying information while scrolling.
     *
     * @param view : the info view to be attachment.
     * @param horizontalPlacement : the horizontal placement of that view relative to the position of the scroll handler.
     *                              {@link HandlerInfoHorizontalPlacement}
     * @param verticalPlacement : the vertical placement of that view relative to the position of the scroll handler.
     *                              {@link HandlerInfoVerticalPlacement}
     *
     */
    @Override
    public void attachHandlerInfoView(View view,
                                      @HandlerInfoHorizontalPlacement int horizontalPlacement,
                                      @HandlerInfoVerticalPlacement int verticalPlacement) {
        mHandlerInfoView = view;
        mHandlerInfoVerticalPlacement = verticalPlacement;

        switch (horizontalPlacement) {
            case LEFT:
                attachViewLeftOfHandler();
                break;
            case RIGHT:
                attachViewRightOfHandler();
                break;
            default:
                throw new IllegalStateException(String.format("Cannot attach scroll handler info view with " +
                        "placement %s.  Only left and right placements are allowed for %s.",
                        horizontalPlacement,
                        VerticalRecyclerViewFastScroller.class.getSimpleName())
                );
        }
    }

    // Listens to recycler view onScroll event.
    @Override
    public RecyclerView.OnScrollListener getRecyclerViewScrollListener() {
        if (mOnScrollListener == null) {
            mOnScrollListener = new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_IDLE:
                            if (fIsAutoShowHide) {
                                hide(fHideAnimationResId);
                            }
                            break;
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            if (fIsAutoShowHide) {
                                show(fShowAnimationResId);
                            }
                            break;
                        default:
                            super.onScrollStateChanged(recyclerView, newState);
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    float recyclerViewScrollYPos = recyclerView.computeVerticalScrollOffset();
                    float recyclerViewScrollableHeight = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent();

                    float yScrolledPercentage = recyclerViewScrollYPos / recyclerViewScrollableHeight;
                    float scrollHandleYPos = yScrolledPercentage * getHandlerMaxScrollableHeight();

                    fScrollHandler.setY(scrollHandleYPos);
                    syncHandlerInfoViewPosition(scrollHandleYPos);

                    propagateHandlerScrollEvent();
                }
            };
        }
        return mOnScrollListener;
    }

    /**
     * @param cursorDeltaY : the delta Y, in pixels, of the scroll bar handler.
     * @return The new position of the scroll handler for a given vertical displacement of cursorDeltaY
     */
    private float calculateHandlerYPosition(float cursorDeltaY) {
        float scrollHandlerYPos = fScrollHandler.getY();
        boolean isScrollUp = cursorDeltaY > 0;
        if (isScrollUp) {
            scrollHandlerYPos -= Math.abs(cursorDeltaY);
            scrollHandlerYPos = Math.max(0, scrollHandlerYPos);
        } else {
            scrollHandlerYPos += Math.abs(cursorDeltaY);
            float scrollHandleMaxYPos = getHandlerMaxScrollableHeight();
            scrollHandlerYPos = Math.min(scrollHandleMaxYPos, scrollHandlerYPos);
        }
        return scrollHandlerYPos;
    }

    private void attachViewLeftOfHandler() {
        ConstraintSet constraintSet = new ConstraintSet();
        fRootConstraintContainer.addView(mHandlerInfoView);
        constraintSet.clone(fRootConstraintContainer);
        constraintSet.clear(R.id.scroll_handle, ConstraintSet.START);
        constraintSet.connect(fScrollHandler.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.TOP, fScrollHandler.getId(), ConstraintSet.TOP);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.BOTTOM, fScrollHandler.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.END, fScrollHandler.getId(), ConstraintSet.START);
        constraintSet.applyTo(fRootConstraintContainer);
    }

    private void attachViewRightOfHandler() {
        ConstraintSet constraintSet = new ConstraintSet();
        fRootConstraintContainer.addView(mHandlerInfoView);
        constraintSet.clone(fRootConstraintContainer);
        constraintSet.clear(R.id.scroll_handle, ConstraintSet.END);
        constraintSet.connect(fScrollHandler.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.TOP, fScrollHandler.getId(), ConstraintSet.TOP);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.BOTTOM, fScrollHandler.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(mHandlerInfoView.getId(), ConstraintSet.START, fScrollHandler.getId(), ConstraintSet.END);
        constraintSet.applyTo(fRootConstraintContainer);
    }
}
