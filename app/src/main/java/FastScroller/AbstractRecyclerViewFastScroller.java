package FastScroller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
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

    public static final int ORIENTATION_VERTICAL = 0;
    public static final int ORIENTATION_HORIZONTAL = 1;
    @IntDef({ORIENTATION_VERTICAL, ORIENTATION_HORIZONTAL})
    @Retention(CLASS)
    @interface Orientation {}

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int TOP = 2;
    public static final int BOTTOM = 3;
    @IntDef({LEFT, RIGHT, TOP, BOTTOM})
    @Retention(CLASS)
    @interface HandlerInfoViewPlacement {}

    private static final int[] STYLEABLE = R.styleable.AbstractRecyclerViewFastScroller;

    protected final boolean fIsShowHideWhenScroll;      // Automatically show/hide the fast scroller on scroll begin & end
    protected final ConstraintLayout fRootConstraintContainer;
    protected final View fSliderBar;
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
            // Initialise slider bar
            fSliderBar = findViewById(R.id.scroll_bar);
            int sliderBarColor = attributes.getColor(R.styleable.AbstractRecyclerViewFastScroller_slider_bar_color, Color.GRAY);
            fSliderBar.setBackgroundColor(sliderBarColor);

            int defaultThickness = (int) getResources().getDimension(R.dimen.slider_bar_default_thickness);
            int sliderThickness = attributes.getDimensionPixelSize(R.styleable.AbstractRecyclerViewFastScroller_slider_bar_thickness, defaultThickness);

            int orientation = getOrientation();
            switch (orientation) {
                case ORIENTATION_VERTICAL:
                    fSliderBar.getLayoutParams().width = sliderThickness;
                    break;
                case ORIENTATION_HORIZONTAL:
                    // TODO: 2018-10-09 Test horizontal case
                    fSliderBar.getLayoutParams().height = sliderThickness;
                    break;
                default:
                    throw new IllegalStateException(String.format("Invalid orientation %s", orientation));
            }

            // Initialise scrollbar handle
            fScrollHandler = findViewById(R.id.scroll_handle);
            fScrollHandler.setOnTouchListener(this);
            Drawable customHandlerDrawable = attributes.getDrawable(R.styleable.AbstractRecyclerViewFastScroller_scroll_handler_background);

            int handlerWidth;
            int handlerHeight;
            if (customHandlerDrawable != null) {
                fScrollHandler.setImageDrawable(customHandlerDrawable);
                int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;
                handlerWidth = (int) attributes.getDimension(R.styleable.AbstractRecyclerViewFastScroller_scroll_handler_width, wrapContent);
                handlerHeight = (int) attributes.getDimension(R.styleable.AbstractRecyclerViewFastScroller_scroll_handler_height, wrapContent);

                int handlerBackGroundColor = attributes.getColor(R.styleable.AbstractRecyclerViewFastScroller_scroll_handler_background_color, Color.TRANSPARENT);
                fScrollHandler.setBackgroundColor(handlerBackGroundColor);
            } else {
                int handlerBackGroundColor = attributes.getColor(R.styleable.AbstractRecyclerViewFastScroller_scroll_handler_background_color, Color.GRAY);
                GradientDrawable defaultHandler = (GradientDrawable) getResources().getDrawable(R.drawable.vertical_handler);
                defaultHandler.mutate();
                defaultHandler.setColor(handlerBackGroundColor);
                fScrollHandler.setImageDrawable(defaultHandler);

                int defaultWidth = fScrollHandler.getLayoutParams().width;
                int defaultHeight = fScrollHandler.getLayoutParams().height;

                handlerWidth = (int) attributes.getDimension(R.styleable.AbstractRecyclerViewFastScroller_scroll_handler_width, defaultWidth);
                handlerHeight = (int) attributes.getDimension(R.styleable.AbstractRecyclerViewFastScroller_scroll_handler_height, defaultHeight);
            }

            fScrollHandler.getLayoutParams().width = handlerWidth;
            fScrollHandler.getLayoutParams().height = handlerHeight;

            fIsShowHideWhenScroll = attributes.getBoolean(R.styleable.AbstractRecyclerViewFastScroller_auto_show_hide_when_scrolled, true);
            if (fIsShowHideWhenScroll) {
                fRootConstraintContainer.setVisibility(View.INVISIBLE);
            }
        } finally {
            attributes.recycle();
        }
    }

    // Listener called when interacts with the scroll handlers
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onHandlerDownPress();
                break;
            case MotionEvent.ACTION_UP:
                onHandlerUp();
                break;
            case MotionEvent.ACTION_MOVE:
                propagateHandlerScrollEvent();
                break;
            default:
                break;
        }

        return fHandlerGestureDetector.onTouchEvent(event);
    }

    private void onHandlerUp() {
        bindRecyclerView(mRecyclerView);

        // Auto hide the fast scroller if set to true.
        if (fIsShowHideWhenScroll) {
            hide(R.anim.right_slide_out);
        }

        if (mFastScrollHandlerListener != null) {
            mFastScrollHandlerListener.onHandlerScrollEnd();
        }
    }

    private void onHandlerDownPress() {
        unbindRecyclerView();
        mRecyclerView.stopScroll();
        fRootConstraintContainer.clearAnimation();

        if (mFastScrollHandlerListener != null) {
            mFastScrollHandlerListener.onHandlerScrollStart();
        }
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
        mRecyclerView.setVerticalScrollBarEnabled(false);
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
        return fSliderBar.getHeight() - fScrollHandler.getHeight();
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

    /**
     * @return: the scroller's {@link Orientation}.
     */
    protected abstract @Orientation int getOrientation();
}
