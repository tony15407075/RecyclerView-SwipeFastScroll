package FastScroller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.android.recyclerview_swipefastscroll.R;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.CLASS;


/**
 * Created by tonychiu25 on 2018-10-02.
 */

public abstract class AbstractRecyclerViewFastScroller extends FrameLayout
        implements View.OnTouchListener, RecyclerViewFastScroller {

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int TOP = 2;
    public static final int BOTTOM = 3;
    @IntDef({LEFT, RIGHT, TOP, BOTTOM})
    @Retention(CLASS)
    public @interface HandlerInfoViewPlacement {}

    private static final int[] STYLEABLE = R.styleable.AbstractRecyclerViewFastScroller;

    protected final boolean fIsShowHideWhenScroll;      // Automatically show/hide the fast scroller on scroll begin & end
    protected final ConstraintLayout fRootConstraintContainer;
    protected final View fScrollBar;
    protected final ImageView fScrollHandler;
    protected final GestureDetectorCompat fHandlerGestureDetector;

    protected RecyclerView mRecyclerView;
    private FastScrollHandlerListener mFastScrollHandlerListener;

    public AbstractRecyclerViewFastScroller(@NonNull Context context, @Nullable AttributeSet attrs, int layoutResource) {
        super(context, attrs);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(layoutResource, this, true);
        fHandlerGestureDetector = new GestureDetectorCompat(context, getHandlerGestureListener());
        fRootConstraintContainer = findViewById(R.id.scroll_bar_container);

        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, STYLEABLE, 0, 0);
        try {
            // Initialise scrollbar
            fScrollBar = findViewById(R.id.scroll_bar);
            fScrollBar.setBackgroundColor(Color.GRAY);

            // Initialise scrollbar handle
            fScrollHandler = findViewById(R.id.scroll_handle);
            fScrollHandler.setOnTouchListener(this);
            Drawable handlerDrawable = attributes.getDrawable(R.styleable.AbstractRecyclerViewFastScroller_scroll_handler_background);
            if (handlerDrawable != null) {
                int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;
                int handlerWidth = (int) attributes.getDimension(R.styleable.AbstractRecyclerViewFastScroller_scroll_handler_width, wrapContent);
                int handlerHeight = (int) attributes.getDimension(R.styleable.AbstractRecyclerViewFastScroller_scroll_handler_height, wrapContent);

                fScrollHandler.setImageDrawable(handlerDrawable);
                fScrollHandler.getLayoutParams().width = handlerWidth;
                fScrollHandler.getLayoutParams().height = handlerHeight;
            }

            fIsShowHideWhenScroll = attributes.getBoolean(R.styleable.AbstractRecyclerViewFastScroller_auto_show_hide_when_scrolled, true);
            if (fIsShowHideWhenScroll) {
                fRootConstraintContainer.setVisibility(View.INVISIBLE);
            }
        } finally {
            attributes.recycle();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                unbindRecyclerView();
                mRecyclerView.stopScroll();
                fRootConstraintContainer.clearAnimation();

                if (mFastScrollHandlerListener != null) {
                    mFastScrollHandlerListener.onHandlerScrollStart();
                }
                break;
            case MotionEvent.ACTION_UP:
                bindRecyclerView(mRecyclerView);

                if (mFastScrollHandlerListener != null) {
                    mFastScrollHandlerListener.onHandlerScrollEnd();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                propagateHandlerScrollEvent();
                break;
            default:
                break;
        }

        return fHandlerGestureDetector.onTouchEvent(event);
    }

    @Override
    public void show(int... animationResId) {
        if (fRootConstraintContainer.getVisibility() == View.VISIBLE) {
            return;
        }

        fRootConstraintContainer.setVisibility(View.VISIBLE);
        if (animationResId.length > 0) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), animationResId[0]);
            fRootConstraintContainer.startAnimation(animation);
        }
    }

    @Override
    public void hide(int... animationResId) {
        boolean isHidden = fRootConstraintContainer.getVisibility() != View.VISIBLE;
        if (isHidden) {
            return;
        }

        if (animationResId.length > 0) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), animationResId[0]);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override public void onAnimationStart(Animation animation) {}
                @Override public void onAnimationRepeat(Animation animation) {}
                @Override public void onAnimationEnd(Animation animation) {
                    fRootConstraintContainer.setVisibility(View.INVISIBLE);
                }
            });
            fRootConstraintContainer.startAnimation(animation);
        } else {
            fRootConstraintContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setScrollHandlerListener(FastScrollHandlerListener fastScrollHandlerListener) {
        mFastScrollHandlerListener = fastScrollHandlerListener;
    }

    @Override
    public void bindRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mRecyclerView.addOnScrollListener(getRecyclerViewScrollListener());
    }

    @Override
    public void unbindRecyclerView() {
        if (mRecyclerView != null) {
            mRecyclerView.removeOnScrollListener(getRecyclerViewScrollListener());
        }
    }

    /**
     * Propagate the scroll event back to the caller who initially setup the listener
     * via the setter {@link #setScrollHandlerListener(FastScrollHandlerListener)}
     */
    protected void propagateHandlerScrollEvent() {
        if (mFastScrollHandlerListener != null) {
            int itemPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            mFastScrollHandlerListener.onHandlerScrolling(itemPosition);
        }
    }

    /**
     * @return : returns the maximum height, in pixels, that the scroller can scroll.
     */
    protected float getHandlerMaxScrollableHeight() {
        return fScrollBar.getHeight() - fScrollHandler.getHeight();
    }

    /**
     * Get a OnScrollListener to listen for bounded recycler view's onScroll event.
     * @return recycler view onScrollListener
     */
    protected abstract RecyclerView.OnScrollListener getRecyclerViewScrollListener();

    /**
     * @return the Gesture listener implementation with respect to the orientation (vertical | horizontal) of
     * the child implementation.
     */
    protected abstract GestureDetector.OnGestureListener getHandlerGestureListener();

    /**
     * Sync the recycler view's position with the handler's current position.
     * Typically to be called every time user scrolls to a new handler position.
     * @param handlerCurrentPosition : the current position of the scroll bar handler.
     */
    protected abstract void syncRecyclerViewPosition(float handlerCurrentPosition);

    /**
     * Sync the handler info view's position with the handler's current position.
     * Typically to be called every time user scrolls to a new handler position.
     * @param handlerCurrentPosition : the current position of the scroll bar handler.
     */
    protected abstract void syncHandlerInfoViewPosition(float handlerCurrentPosition);

    /**
     * Attaches a auxiliary info view next to the handler for displaying information while scrolling.
     *
     * @param view : the info view to be attachment.
     * @param @HandlerInfoViewPlacement : the placement of that view relative to the position of the scroll handler.
     */
    public abstract void attachHandlerInfoView(View view, @HandlerInfoViewPlacement int placement);
}
