package tech.skot.core.components

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import tech.skot.core.SKLog
import tech.skot.view.live.MutableSKLiveData
import tech.skot.view.live.SKMessage

class SKListViewProxy(private val vertical:Boolean, private val reverse:Boolean, private val nbColumns:Int?) : SKComponentViewProxy<RecyclerView>(), SKListVC {

    private val itemsLD: MutableSKLiveData<List<SKComponentViewProxy<*>>> = MutableSKLiveData(emptyList())

    override var items: List<SKComponentVC>
        get() = itemsLD.value
        set(newVal) {
//            val newProxyItems = newVal as List<SKComponentViewProxy<*>>
//            itemsLD.value.lastOrNull()?.let {
//                if (newProxyList.lastOrNull() != it && newProxyList.contains(it)) {
//                    it.saveState()
//                }
//            }
            SKLog.d("SKListViewProxy set new Value: $newVal")
            itemsLD.postValue(newVal as List<SKComponentViewProxy<*>>)
        }

    private val saveSignal: SKMessage<Unit> = SKMessage()
    private var _state: Parcelable? = null

    override fun saveState() {
//        SKLog.d("SKListViewProxy saveState")
        saveSignal.post(Unit)
    }

    override fun bindTo(activity: SKActivity, fragment: Fragment?, binding: RecyclerView, collectingObservers:Boolean) =
            SKListView(vertical, reverse, nbColumns, activity, fragment, binding).apply {
                collectObservers = collectingObservers
                itemsLD.observe {
                    onItems(it)
                }
                saveSignal.observe {
//                    SKLog.d("SKListViewProxy receive Save Signal")
                    _state = saveState()
                }
                _state?.let {
//                    SKLog.d("SKListViewProxy restoreState")
                    restoreState(it)
                }
            }




}