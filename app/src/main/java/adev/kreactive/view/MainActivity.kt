package adev.kreactive.view

import adev.kreactive.di
import adev.kreactive.presentation.MainPresenter
import adev.kreactive.presentation.MainRouter
import adev.kreactive.presentation.MainView
import adev.kreactive.view.base.LifecycleComponent
import adev.kreactive.view.base.ReactiveActivity
import android.content.Context
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.verticalLayout
import org.kodein.di.erased.instance

class MainActivity : ReactiveActivity<MainView, MainRouter>() {
    override val component = MainUI()
}

class MainUI : LifecycleComponent<Context, MainView, MainRouter>() {
    override val presenter: MainPresenter by di.instance()

    override fun createView(ui: AnkoContext<Context>) = with(ui) {
        verticalLayout {

        }
    }
}