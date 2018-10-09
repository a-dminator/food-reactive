package adev.kreactive.presentation.base

abstract class LifecyclePresenter<TView, TRouter> {

    protected var view: TView? = null
    protected var router: TRouter? = null

    open fun onCreateView(view: TView, router: TRouter) {
        this.view = view
        this.router = router
    }

    open fun onDestroyView() {
        this.view = null
        this.router = null
    }

    open fun onAppear() {}
    open fun onDisappear() {}
}