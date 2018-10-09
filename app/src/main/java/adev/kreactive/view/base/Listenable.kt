package adev.kreactive.view.base

import android.view.ViewGroup
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

interface Listenable<out T> : ReadOnlyProperty<ReactiveComponent<*>, T> {
    fun emitIn(postImmediately: Boolean = true, onChange: (T) -> Unit)
    fun emitIn(onChange: (T) -> Unit) = emitIn(postImmediately = true, onChange = onChange)
}

interface MutableListenable<T> : Listenable<T>, ReadWriteProperty<ReactiveComponent<*>, T>

open class ListenableImpl<T>() : Listenable<T>, MutableListenable<T> {

    private var isInit = false
    private var _value: T? = null
    var value: T
        set(value) {
            isInit = true
            _value = value
        }
        get() = run {
            if (!isInit) throw UninitializedPropertyAccessException()
            _value as T
        }

    constructor(initialValue: T) : this() {
        isInit = true
        value = initialValue
    }

    private val listeners = mutableListOf<(T) -> Unit>()
    override fun emitIn(postImmediately: Boolean, onChange: (T) -> Unit) {
        listeners.add(onChange)
        if (postImmediately) {
            onChange(value)
        }
    }

    override fun getValue(thisRef: ReactiveComponent<*>, property: KProperty<*>) = value

    override fun setValue(thisRef: ReactiveComponent<*>, property: KProperty<*>, value: T) {
        set(value)
    }

    fun set(value: T) {
        isInit = true
        if (value != this.value) {
            this.value = value
            listeners.forEach { it.invoke(value) }
        }
    }
}

abstract class ReactiveComponent<TOwner> : AnkoComponent<TOwner> {

    fun <T> KProperty0<T>.listenable() = this.listenableImpl()!! as Listenable<T>
    fun <T> KProperty0<T>.mutableListenable() = this.listenableImpl()!! as MutableListenable<T>

    private fun <T> KProperty0<T>.listenableImpl() = this.apply { isAccessible = true }.getDelegate() as? ListenableImpl<T>

    fun <T> KProperty0<T>.emitIn(setter: (T) -> Unit) = listenable().emitIn(setter)

    infix fun <T> KMutableProperty0<T>.listen(listenable: Listenable<T>) =
        listenable.emitIn(postImmediately = true, onChange = ::setIfNeeded)

    infix fun <T> KMutableProperty0<T>.listen(bindableProperty: KProperty0<T>) =
        listen(bindableProperty.listenable())

    infix fun <T> ((T) -> Unit).listen(bindableProperty: KProperty0<T>) =
        bindableProperty.listenable().emitIn(postImmediately = true, onChange = this)

    infix fun <T> ((T) -> Unit).listen(listenable: Listenable<T>) =
        listenable.emitIn(postImmediately = true, onChange = this)

    fun <T, TBindable> KMutableProperty0<T>.listen(
        listenable: Listenable<TBindable>,
        action: (TBindable) -> T
    ) = listenable.emitIn {
        this.setIfNeeded(action(it))
    }

    fun <T, TBindable> KMutableProperty0<T>.listen(
        bindableProperty: KProperty0<TBindable>,
        action: (TBindable) -> T
    ) = listen(
        bindableProperty.listenable(),
        action
    )

    fun <T> listenable(
        initialValue: T,
        postImmediately: Boolean = false,
        onChange: ((T) -> Unit)? = null
    ): MutableListenable<T> = ListenableImpl(initialValue)
        .also {
            if (onChange != null) {
                it.emitIn(postImmediately, onChange = onChange)
            }
        }

    fun <T> lateinitListenable(
        onChange: ((T) -> Unit)? = null
    ): MutableListenable<T> = ListenableImpl<T>()
        .also {
            if (onChange != null) {
                it.emitIn(postImmediately = false, onChange = onChange)
            }
        }

    fun <T, R> KMutableProperty0<T>.map(transform: (T) -> R) =
        listenable().map(transform)

    @JvmName("mapNullable")
    fun <T, R> KMutableProperty0<T?>.map(transform: (T) -> R?) =
        listenable().map(transform)

    fun <T, R> KMutableProperty0<T?>.mapNotNull(transform: (T) -> R) =
        listenable().mapNotNull(transform)

    fun <T, R> Listenable<T>.map(
        transform: (T) -> R
    ): MutableListenable<R> = listenable(
        initialValue = transform((this as ListenableImpl<T>).value),
        postImmediately = true
    ).also { mappedListenable ->
        emitIn { value ->
            (mappedListenable as ListenableImpl<R>).set(transform(value))
        }
    }

    fun <T, R> Listenable<T?>.mapNotNull(
        transform: (T) -> R,
        onChange: ((R) -> Unit)? = null
    ): MutableListenable<R> = lateinitListenable(
        onChange
    ).also { mappedListenable ->
        emitIn { value ->
            if (value != null) {
                (mappedListenable as ListenableImpl<R>).set(transform(value))
            }
        }
    }

    @JvmName("mapNullable")
    fun <T, R> Listenable<T?>.map(
        transform: (T) -> R?
    ): MutableListenable<R?> = listenable(
        initialValue = (this as ListenableImpl<T?>).value?.let(transform),
        postImmediately = true
    ).also { mappedListenable ->
        emitIn { value ->
            (mappedListenable as ListenableImpl<R?>).set(value?.let(transform))
        }
    }
}

private fun <T> KMutableProperty0<T>.setIfNeeded(value: T) {
    val needSet = try {
        get()
    } catch (e: Exception) {
        true
    }
    if (needSet != value) {
        set(value)
    }
}

fun <TComponent : AnkoComponent<ViewGroup>> ViewGroup.mount(
    component: TComponent,
    init: TComponent.() -> Unit = {}
) = run {
    val delegate = AnkoContext.createDelegate(this)
    val view = component.createView(delegate)
    init(component)
    view
}