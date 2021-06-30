package tech.skot.core.components.presented

import tech.skot.core.components.SKComponent
import tech.skot.core.components.SKScreen
import tech.skot.core.di.coreViewInjector

class SKBottomSheet : SKComponent<SKBottomSheetVC>() {

    override val view = coreViewInjector.bottomSheet()

    var shownScreen:SKScreen<*>? = null

    fun show(screen: SKScreen<*>) {
        shownScreen?.presenter = null
        view.state = SKBottomSheetVC.Shown(screen = screen.view)
        screen.presenter = this
        shownScreen = screen
    }

    fun dismiss() {
        shownScreen?.presenter = null
        shownScreen = null
        view.state = null
    }
}