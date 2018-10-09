package adev.kreactive.view.base

import adev.kreactive.presentation.base.LifecyclePresenter

abstract class LifecycleComponent<TOwner, TView, TRouter> : ReactiveComponent<TOwner>() {

    abstract val presenter: LifecyclePresenter<TView, TRouter>

    fun onCreateView(view: TView, router: TRouter) {
        presenter.onCreateView(view, router)
    }

    fun onDestroyView() {
        presenter.onDestroyView()
    }

    fun onAppear() {
        presenter.onAppear()
    }

    fun onDisappear() {
        presenter.onDisappear()
    }
}