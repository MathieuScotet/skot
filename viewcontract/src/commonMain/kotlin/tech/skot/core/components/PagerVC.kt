package tech.skot.core.components

interface PagerVC: ComponentVC {
    val screens:List<ScreenVC>
    var selectedPageIndex:Int
    val onSwipeToPage:((index:Int)->Unit)?
}