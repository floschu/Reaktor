package com.tailoredapps.reaktor.android.koin

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import at.florianschuster.reaktor.Reactor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.definition.Definition
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * [Reactor] DSL extension to declare a [Reactor] in a Koin [Module].
 */
inline fun <reified R> Module.reactor(
    qualifier: Qualifier? = null,
    override: Boolean = false,
    noinline definition: Definition<R>
) where R : Reactor<*, *, *>, R : ViewModel = viewModel(qualifier, override, definition)

/**
 * Lazily gets a [Reactor] instance for a [LifecycleOwner].
 */
inline fun <L : LifecycleOwner, reified R> L.reactor(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<R> where R : Reactor<*, *, *>, R : ViewModel = viewModel(qualifier, parameters)

/**
 * Gets a [Reactor] instance for a [LifecycleOwner].
 */
inline fun <reified R> LifecycleOwner.getReactor(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): R where R : Reactor<*, *, *>, R : ViewModel = getViewModel(qualifier, parameters)