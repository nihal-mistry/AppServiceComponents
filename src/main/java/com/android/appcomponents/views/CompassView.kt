package com.android.appcomponents.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import com.android.appcomponents.R
import com.android.appcomponents.util.CompassUtility

/**
 * Custom View class to display a Compass which shows directions using CompassUtility
 */
class CompassView : FrameLayout, Observer<Float> {

    private var compassUtility: CompassUtility? = null
    private var compassBgView: ImageView? = null
    private var compassNeedleView: ImageView? = null

    private var compassBgDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_compass_bg, null)
    private var compassNeedleDrawable =  ResourcesCompat.getDrawable(resources, R.drawable.ic_compass_needle, null)

    /**
     * Constructor Used when instantiating Views via xml.
     *
     */
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context, attrs, defStyle
    ) {
        initView(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        initView(attrs)
    }

    /*
    * Constructor used to instantiate view programmatically.
    * */
    constructor(context: Context?) : super(context!!) {
        initView(null)
    }

    /*
    * Function used to instantiate CompassUtility and compass's image views.
    * @param attrs : AttributeSet containing compass background and needle drawables.
    * */
    private fun initView(attrs: AttributeSet?) {
        compassUtility = CompassUtility.getInstance(context)
        compassUtility?.getCompassAngleLiveData()?.observeForever(this)
        val view = inflate(context, R.layout.compass_view, this)
        compassBgView = view.findViewById(R.id.iv_compass_bg)
        compassNeedleView = view.findViewById(R.id.iv_compass_needle)

        attrs?.let {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CompassView)
            compassBgDrawable = a.getDrawable(R.styleable.CompassView_compassBackgroundSrc) ?: compassBgDrawable
            compassNeedleDrawable = a.getDrawable(R.styleable.CompassView_compassNeedleSrc) ?: compassNeedleDrawable
            a.recycle()
        }
        compassBgView?.setImageDrawable(compassBgDrawable)
        compassNeedleView?.setImageDrawable(compassNeedleDrawable)
    }

    /*
    * Set the Drawable to use for compass background image view
    * */
    fun setCompassBackgroundDrawable(drawable: Drawable) {
        compassBgDrawable = drawable
        compassBgView?.setImageDrawable(compassBgDrawable)
    }

    /*
    * Set the Drawable to use for needle image view
    * */
    fun setCompassNeedleDrawable(drawable: Drawable) {
        compassNeedleDrawable = drawable
        compassNeedleView?.setImageDrawable(compassNeedleDrawable)
    }

    /*
    * Start listening to compass events
    * */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        compassUtility?.startListening()
    }

    /*
    * Stop listening to compass events
    * */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        compassUtility?.getCompassAngleLiveData()?.removeObserver(this)
        compassUtility?.stopListening()
    }

    /*
    * Callback to receive compass update event
    * */
    override fun onChanged(t: Float?) {
        t?.let {
            compassBgView?.rotation = it
        }
    }
}