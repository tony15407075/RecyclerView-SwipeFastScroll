package FastScroller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.android.recyclerview_swipefastscroll.R;


/**
 * Created by tonychiu25 on 2018-10-02.
 */

public abstract class AbstractRecyclerViewFastScroller extends FrameLayout implements View.OnTouchListener {

    // Placement of the info bubble view, relative to the scroll handler.
    public enum HandlerInfoViewPlacement { LEFT, RIGHT, TOP, BOTTOM }

    private static final int[] STYLEABLE = R.styleable.AbstractRecyclerViewFastScroller;

    protected final ConstraintLayout fRootConstraintContainer;
    protected final View fScrollBar;
    protected final ImageView fScrollHandle;
    protected final GestureDetectorCompat fGestureDetector;

    public AbstractRecyclerViewFastScroller(@NonNull Context context, @Nullable AttributeSet attrs, int layoutResource) {
        super(context, attrs);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(layoutResource, this, true);
        fGestureDetector = new GestureDetectorCompat(context, getOnGestureListener());
        fRootConstraintContainer = findViewById(R.id.scroll_bar_container);

        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, STYLEABLE, 0, 0);
        try {
            // Initialise scrollbar
            fScrollBar = findViewById(R.id.scroll_bar);
            fScrollBar.setBackgroundColor(Color.GRAY);

            // Initialise scrollbar handle
            fScrollHandle = findViewById(R.id.scroll_handle);
            fScrollHandle.setOnTouchListener(this);
            Drawable handlerDrawable = attributes.getDrawable(R.styleable.AbstractRecyclerViewFastScroller_scroll_handler_background);
            if (handlerDrawable != null) {
                int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;
                int handlerWidth = (int) attributes.getDimension(R.styleable.AbstractRecyclerViewFastScroller_scroll_handler_width, wrapContent);
                int handlerHeight = (int) attributes.getDimension(R.styleable.AbstractRecyclerViewFastScroller_scroll_handler_height, wrapContent);

                fScrollHandle.setImageDrawable(handlerDrawable);
                fScrollHandle.getLayoutParams().width = handlerWidth;
                fScrollHandle.getLayoutParams().height = handlerHeight;
            }
        } finally {
            attributes.recycle();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i("TONY -- Func_s", " onTouch() .(AbstractRecyclerViewFastScroller.java:73)");
        return fGestureDetector.onTouchEvent(event);
    }

    /**
     * @return the Gesture listener implementation with respect to the orientation (vertical | horizontal) of
     * the child implementation.
     */
    protected abstract GestureDetector.OnGestureListener getOnGestureListener();

    /**
     * Attaches a auxiliary info view next to the handler for displaying information while scrolling.
     *
     * @param view : the info view to be attachment.
     * @param placement : the placement of that view relative to the position of the scroll handler.
     */
    public abstract void attachHandlerInfoView(View view, HandlerInfoViewPlacement placement);
}
