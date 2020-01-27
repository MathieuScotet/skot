package tech.skot.generator.utils

import tech.skot.components.ComponentView
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure

var _views: ViewNode? = null

fun initGenerator(views: ViewNode) {
    _views = views
}

val actualComponents: Set<KClass<out ComponentView>> by lazy {
    _views!!.allActualComponents()
}

val allComponents: Set<KClass<out ComponentView>> by lazy {
    _views!!.allComponents()
}

val componentsFromApp: List<KClass<out ComponentView>> by lazy {
    allComponents.fromApp()
}

fun KClass<out ComponentView>.isActualComponent() = actualComponents.contains(this)


val actions by lazy {
    allComponents.flatMap { it.supertypes.filter { !it.isComponentView() && !(it.isAny()) } }.toSet().map { it.jvmErasure }
}

val actionsFromApp: List<KClass<*>> by lazy {
    actions.fromApp()
}

