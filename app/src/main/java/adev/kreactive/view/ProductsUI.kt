package adev.kreactive.view

import adev.kreactive.entities.Product
import adev.kreactive.view.base.Listenable
import adev.kreactive.view.base.ReactiveComponent
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.recyclerview.v7.recyclerView

class ProductsUI(
    private val productsListenable: Listenable<List<Product>>
) : ReactiveComponent<ViewGroup>() {

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        recyclerView {
            adapter = ProductsAdapter(context).apply {
                ::products listen productsListenable
            }
            layoutManager = LinearLayoutManager(context)
        }
    }
}

class ProductsAdapter(
    context: Context
) : RecyclerView.Adapter<ProductViewHolder>() {

    var products = emptyList<Product>()

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.ui.product = products[position]
    }

    private val ankoContext = AnkoContext.createReusable(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = run {
        val ui = ProductUI()
        val view = ui.createView(ankoContext)
        ProductViewHolder(ui, view)
    }

    override fun getItemCount() = products.size
}

class ProductViewHolder(val ui: ProductUI, view: View) : RecyclerView.ViewHolder(view)