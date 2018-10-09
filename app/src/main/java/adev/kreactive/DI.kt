package adev.kreactive

import adev.kreactive.presentation.MainPresenter
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton

val di = Kodein {
    bind<MainPresenter>() with singleton { MainPresenter() }
}