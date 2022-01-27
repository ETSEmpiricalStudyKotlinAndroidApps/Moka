package io.github.tonnyl.moka.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

/**
 * Returns an existing [ViewModel] or creates a new one in the given owner (usually, a fragment or
 * an activity), defaulting to the owner provided by [LocalViewModelStoreOwner].
 *
 * The created [ViewModel] is associated with the given [viewModelStoreOwner] and will be retained
 * as long as the owner is alive (e.g. if it is an activity, until it is
 * finished or process is killed).
 *
 * @param viewModelStoreOwner The owner of the [ViewModel] that controls the scope and lifetime
 * of the returned [ViewModel]. Defaults to using [LocalViewModelStoreOwner].
 * @param key The key to use to identify the [ViewModel].
 * @param factory The [ViewModelProvider.Factory] that should be used to create the [ViewModel]
 * or null if you would like to use the default factory from the [LocalViewModelStoreOwner]
 * @return A [ViewModel] that is an instance of the given [VM] type.
 */
@Suppress("MissingJvmstatic")
@Composable
public inline fun <reified VM : ViewModel> viewModel(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    key: String? = null,
    factory: ViewModelProvider.Factory? = null,
    defaultCreationExtras: CreationExtras = CreationExtras.Empty
): VM = viewModel(VM::class.java, viewModelStoreOwner, key, factory, defaultCreationExtras)

/**
 * Returns an existing [ViewModel] or creates a new one in the scope (usually, a fragment or
 * an activity)
 *
 * The created [ViewModel] is associated with the given [viewModelStoreOwner] and will be retained
 * as long as the scope is alive (e.g. if it is an activity, until it is
 * finished or process is killed).
 *
 * @param modelClass The class of the [ViewModel] to create an instance of it if it is not
 * present.
 * @param viewModelStoreOwner The scope that the created [ViewModel] should be associated with.
 * @param key The key to use to identify the [ViewModel].
 * @return A [ViewModel] that is an instance of the given [VM] type.
 */
@Suppress("MissingJvmstatic")
@Composable
public fun <VM : ViewModel> viewModel(
    modelClass: Class<VM>,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    key: String? = null,
    factory: ViewModelProvider.Factory? = null,
    defaultCreationExtras: CreationExtras = CreationExtras.Empty
): VM = viewModelStoreOwner.get(modelClass, key, factory, defaultCreationExtras)

private fun <VM : ViewModel> ViewModelStoreOwner.get(
    javaClass: Class<VM>,
    key: String? = null,
    factory: ViewModelProvider.Factory? = null,
    defaultCreationExtras: CreationExtras = CreationExtras.Empty
): VM {
    val provider = if (factory != null) {
        ViewModelProvider(this.viewModelStore, factory, defaultCreationExtras)
    } else {
        ViewModelProvider(this)
    }
    return if (key != null) {
        provider[key, javaClass]
    } else {
        provider[javaClass]
    }
}