package tech.skot.components

import kotlinx.coroutines.*
import tech.skot.contract.modelcontract.MutablePoker
import tech.skot.contract.modelcontract.Poker
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface ScreenParent {
    fun remove(aScreen: Screen<*>)
}

abstract class Screen<V : ScreenView> : Component<V>(), ScreenParent {

    companion object {
        private var root: Screen<out ScreenView>? = null
            set(value) {
                field?.let { oldRootScreen ->
                    if (value != null) {
                        oldRootScreen.view.openScreenWillFinish(value.view)
                    }
                    oldRootScreen.onRemove()
                }
                field = value

                value?.let { screenOnTop = it }
            }

        var screenOnTop: Screen<*>? = null
    }

    fun push(screen: Screen<*>) {
        (parent as? Stack<*>)?.push(screen)
                ?: throw IllegalStateException("This screen is not in a Stack !!!")
    }

    fun replaceWith(screen: Screen<*>) {
        parent?.let { currenParent ->
            when {
                currenParent == null -> screen.setAsRoot()
                currenParent is Screen<*> -> currenParent.onTop = screen
                currenParent is Frame<*> -> currenParent.screen = screen
                currenParent is Stack<*> -> {
                    currenParent.popScreen()
                    currenParent.push(screen)
                }
                currenParent is FrameKeepingScreens<*> -> currenParent.screen = screen
            }
        }

    }

    fun finish() {
        if (root == this) {
            onRemove()
        } else {
            parent?.remove(this)
        }
    }


    override fun remove(aScreen: Screen<*>) {
        onTop = null
    }

    override fun onRemove() {
        onTop?.onRemove()
        super.onRemove()
    }

    private val _ontopChanged = MutablePoker()
    protected val onTopChanged: Poker = _ontopChanged

    var onTop: Screen<out ScreenView>? = null
        set(value) {
            if (value != null) {
                screenOnTop = value
            } else {
                screenOnTop = this
            }

            field?.onRemove()
            field = value
            value?._parent = this
            view.onTop = value?.view
            _ontopChanged.poke()
        }

    internal var _parent: ScreenParent? = null
    val parent: ScreenParent?
        get() = _parent

    fun setAsRoot() {
        root = this
    }

    fun setAsInitial(): Screen<V> {
        setAsRoot()
        return this
    }

    private var loadingCounter = 0
        set(value) {
            field = value
            view.loading = value > 0
        }

    protected fun launchWithLoadingAndError(
            context: CoroutineContext = EmptyCoroutineContext,
            start: CoroutineStart = CoroutineStart.DEFAULT,
            block: suspend CoroutineScope.() -> Unit): Job =
            launch(context, start) {
                loadingCounter++
                try {
                    block()
                } catch (ex: Exception) {
                    if (ex !is CancellationException) {
                        treatError(ex)
                    }
                } finally {
                    loadingCounter--
                }

            }


    protected suspend fun CoroutineScope.parrallel(vararg blocks: suspend CoroutineScope.() -> Unit) {
        val deffereds =
                blocks.map {
                    async {
                        try {
                            it()
                        } catch (ex: Exception) {
                            if (ex !is CancellationException) {
                                treatError(ex)
                            }
                        }
                    }
                }

        deffereds.forEach { it.await() }
    }


    protected open fun treatError(ex: Exception) {
        logE(ex.message ?: "Erreur inconnue", ex)
    }

}
