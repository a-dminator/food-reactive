package adev.kreactive.view.base

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

abstract class ReactiveActivity<TView, TRouter> : AppCompatActivity() {

    abstract val component: LifecycleComponent<Context, TView, TRouter>

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.onCreateView(component as TView, component as TRouter)
    }

    override fun onDestroy() {
        super.onDestroy()
        component.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        component.onAppear()
    }

    override fun onPause() {
        super.onPause()
        component.onDisappear()
    }
}