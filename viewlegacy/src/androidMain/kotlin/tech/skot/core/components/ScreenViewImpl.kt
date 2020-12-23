package tech.skot.view.legacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import tech.skot.components.ComponentViewImpl
import tech.skot.components.ComponentViewProxy
import tech.skot.core.SKLog
import tech.skot.core.components.SKActivity
import tech.skot.core.components.SKFragment
import tech.skot.core.components.ScreenView
import tech.skot.core.components.ScreensManager
import tech.skot.view.extensions.updatePadding
import tech.skot.view.live.MutableSKLiveData


abstract class ScreenViewProxy<B : ViewBinding> : ComponentViewProxy<B>(), ScreenView {

    val key = ScreensManager.addScreen(this)

    private val onBackPressedLD = MutableSKLiveData<(() -> Unit)?>(null)
    override var onBackPressed by onBackPressedLD

    abstract override fun bindTo(activity: SKActivity, fragment: SKFragment?, layoutInflater: LayoutInflater, binding: B): ScreenViewImpl<B>

    fun bindTo(activity: SKActivity, fragment: SKFragment?, layoutInflater: LayoutInflater): View {
        val binding = inflate(layoutInflater)
        bindTo(activity, fragment, layoutInflater, binding).apply {
            onBackPressedLD.observe {
                setOnBackPressed(it)
            }
        }
        return binding.root
    }


    abstract fun inflate(layoutInflater: LayoutInflater): B

    fun createFragment(): SKFragment =
            SKFragment().apply {
                arguments = Bundle().apply {
                    putLong(ScreensManager.SK_ARGUMENT_VIEW_KEY, key)
                }
            }

}

abstract class ScreenViewImpl<B : ViewBinding>(activity: SKActivity, fragment: SKFragment?, binding: B) : ComponentViewImpl<B>(activity, fragment, binding) {
    val view: View = binding.root

    open fun windowInsetPaddingTop() = false

    private var onBackPressed: (() -> Unit)? = null
    fun setOnBackPressed(onBackPressed: (() -> Unit)?) {
        this.onBackPressed = onBackPressed
    }

    init {
        ScreensManager.backPressed.observe(this) {
            onBackPressed?.invoke()
        }

        if (windowInsetPaddingTop()) {
            val loadedInsets = activity.window?.decorView?.rootWindowInsets
            if (loadedInsets != null) {
                view.updatePadding(top = view.paddingTop + loadedInsets.systemWindowInsetTop)
            } else {

                view.setOnApplyWindowInsetsListener { view, windowInsets ->
                    view.updatePadding(top = view.paddingTop + windowInsets.systemWindowInsetTop)
                    windowInsets
                }
            }
        }

    }

}