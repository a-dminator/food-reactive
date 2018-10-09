package adev.kreactive.view

import adev.kreactive.entities.Product
import adev.kreactive.view.base.ReactiveComponent
import android.content.Context
import android.view.Gravity.END
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.textView
import org.jetbrains.anko.wrapContent

class ProductUI : ReactiveComponent<Context>() {

    var product: Product? by lateinitListenable()

    private var title by ::product.map(Product::title)
    private var price by ::product.map(Product::price).map { it.toString() }

    override fun createView(ui: AnkoContext<Context>) = with(ui) {
        frameLayout {
            textView {
                ::text listen ::title
            }
            textView {
                ::text listen ::price
                // I also can write `::title.emitIn(::setText)` but it pretty ugly
            }.lparams {
                width = wrapContent
                height = wrapContent
                gravity = END
            }
        }
    }
}