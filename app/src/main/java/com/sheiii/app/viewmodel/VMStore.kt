package com.sheiii.app.viewmodel

import androidx.activity.ComponentActivity
import androidx.lifecycle.*

/**
 * @author created by Zhenqing He on  09:57
 * @description view model 商店，用来确定viewmodel作用域，让一个viewmodel在多个 activity 中都可以使用
 */
private val vMStores = HashMap<String, VMStore>()//作用域对应的商店

//反射注入view model
fun ComponentActivity.injectViewModel() {
    //根据作用域创建商店
    this::class.java.declaredFields.forEach { field ->
        field.getAnnotation(VMScope::class.java)?.also { scope ->//获取作用域
            val element = scope.scopeName
            var store: VMStore
            if (vMStores.keys.contains(element)) {//如果该作用域存在缓存，则从缓存中获取view model商店
                store = vMStores[element]!!
            } else {//如果不存在则创建一个新的商店
                store = VMStore()
                vMStores[element] = store
            }
            store.bindHost(this)
            val clazz = field.type as Class<ViewModel>
            val vm = ViewModelProvider(store, VMFactory()).get(clazz)
            //给view model赋值
            field.set(this, vm)
        }
    }
}

class VMStore : ViewModelStoreOwner {
    private val bindTargets = ArrayList<LifecycleOwner>()
    private var vmStore: ViewModelStore? = null

    //绑定
    fun bindHost(host: LifecycleOwner) {
        if (!bindTargets.contains(host)) {
            bindTargets.add(host)
            //绑定生命周期
            host.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        bindTargets.remove(host)
                        //如果当前商店没有关联对象，则释放内存
                        if (bindTargets.isEmpty()) {
                            vMStores.entries.find { it.value == this@VMStore }?.also {
                                vmStore?.clear()
                                vMStores.remove(it.key)
//                                "clear vMStores:${vMStores.size}".p()
                            }
                        }
                    }
                }
            })
        }
    }

    override fun getViewModelStore(): ViewModelStore {
        if (vmStore == null)
            vmStore = ViewModelStore()
        return vmStore!!
    }
}

class VMFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.newInstance()
    }
}