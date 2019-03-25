package at.florianschuster.reaktor.android

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import at.florianschuster.reaktor.Reactor

/**
 * Lazily gets or creates the ViewModel Reactor for a FragmentActivity scope.
 */
inline fun <reified R> FragmentActivity.viewModelReactor(): Lazy<R> where R : Reactor<*, *, *>, R : ViewModel =
    lazy { ViewModelProviders.of(this).get(R::class.java) }

/**
 * Lazily gets or creates the ViewModel Reactor for a FragmentActivity scope with a provided initialization lambda.
 */
inline fun <reified R> FragmentActivity.viewModelReactor(crossinline factory: () -> R): Lazy<R> where R : Reactor<*, *, *>, R : ViewModel =
    lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = factory() as T
        }).get(R::class.java)
    }

/**
 * Lazily gets or creates the ViewModel Reactor for a Fragment scope.
 */
inline fun <reified R> Fragment.viewModelReactor(): Lazy<R> where R : Reactor<*, *, *>, R : ViewModel =
    lazy { ViewModelProviders.of(this).get(R::class.java) }

/**
 * Lazily gets or creates the ViewModel Reactor for a Fragment scope with a provided initialization lambda.
 */
inline fun <reified R> Fragment.viewModelReactor(crossinline factory: () -> R): Lazy<R> where R : Reactor<*, *, *>, R : ViewModel =
    lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = factory() as T
        }).get(R::class.java)
    }