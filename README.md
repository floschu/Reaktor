# Reaktor

![Reaktor Version](https://img.shields.io/badge/Reaktor-1.0.0-red.svg) ![minSdk](https://img.shields.io/badge/minSdk-14-green.svg)

Reaktor is a framework for a reactive and unidirectional Kotlin application architecture.  
It is a Kotlin port of the [ReaktorKit](https://github.com/ReactorKit/ReactorKit/) Swift concept.

<p align="center">
  <img alt="flow" src="https://github.com/floschu/Reaktor/blob/master/reactor_diagram.png">
</p>

## Installation

```groovy
allprojects {
    repositories {
        jcenter()
    }
}

dependencies {
    implementation 'at.florianschuster.reaktor:core:1.0.0'
    
    /**
     * Android (AAC) Extensions for Reactor. See: Reaktor-Android
     */
    implementation 'at.florianschuster.reaktor:android:1.0.0'
    
    /**
     * Android (AAC) Koin Extensions for Reactor. See: Reaktor-Android-Koin
     */
    implementation 'at.florianschuster.reaktor:androidkoin:1.0.0'
}
```

## What should I know before I try this?

* Kotlin
* RxJava2
* MVI Architecture Pattern

## Tell me more

### General Concept and Unidirectional Data Flow

For this you should hit up the [ReactorKit Repo Readme](https://github.com/ReactorKit/ReactorKit/blob/master/README.md). It is very extensive and since Swift 4 and Kotlin are much alike you will feel right at home! They also have nice graphics.

### Reaktor

A Reactor has to implement the `Reactor<Action, Mutation, State>` interface. Do not forget to clear the `CompositeDisposable` in the `Reactor` after you are done with it. A View that binds to a Reactor has to implement the interface `ReactorView`.

The `DefaultReactor` is a default implementation for a `Reactor` that handles creation for all variables but not the clearing of the `CompositeDisposable`.

By default, a `Reactor` catches and ignores all errors emitted in `fun mutate()` and `fun reduce()` in release builds to keep the `state` stream going. You can change this behavior for debug builds and also attach an error handler with `Reaktor.handleErrorsWith(...)`.  

### Reaktor-Android

When binding the Reactor to an Activity or a Fragment, their life cycles have to be taken into account.  
All views have to be laid out before the bind happens, so you should not call `fun bind(Reactor)` before:

* Activity: after `setContentView(Int)` in `fun onCreate(Bundle)`
* Fragment: `fun onViewCreated(View, Bundle)`

Also do not forget to dispose the View's `CompositeDisposable`. I propose to do this in: 

* Activity: `fun onDestroy()`
* Fragment: `fun onDestroyView()`

The `ViewModelReactor` is a default implementation for a `Reactor` that uses the [Android Architecture ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) and thus handles the clearing of the `CompositeDisposable` in `fun onCleared()`.

When binding a `State` stream to a View, the `fun bind(...)` extension function can come in handy since it observes on the `AndroidSchedulers.mainThread()` and also logs errors.

### Reaktor-Android-Koin

[Koin](https://github.com/InsertKoinIO/koin) is a lightweight dependency injection framework for Kotlin.

The `androidkoin` module contains simple extension functions for the `ViewModelReactor`. They are just renamed extension functions that can be used for more clarity when developing with the framework.

## Examples

* [Counter](https://github.com/floschu/Reaktor/tree/master/counterexample): Most Basic Counter Example. It uses `ViewModelReactor` for an Activity.
* [Github Search](https://github.com/floschu/Reaktor/tree/master/githubexample): Github Repository Search. It uses `ViewModelReactor` for a Fragment.
* [Saved State Example](https://github.com/floschu/Reaktor/tree/master/savedstateexample): Like the Counter. Basic but it uses `onSaveInstanceState` to preserve the `Reactor`'s state on process death.
* [Watchables](https://github.com/floschu/Watchables): A Movie and TV Show Watchlist Application. It uses [Koin](https://github.com/InsertKoinIO/koin) as DI Framework to inject dependencies into a `Reactor`.

## Author

Visit my [Website](https://florianschuster.at/).

## AboutLibraries

``` xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="define_reaktor"></string>
    <string name="library_reaktor_author">Florian Schuster</string>
    <string name="library_reaktor_authorWebsite">https://florianschuster.at</string>
    <string name="library_reaktor_libraryName">Reaktor</string>
    <string name="library_reaktor_libraryDescription">Reaktor is a framework for a reactive and unidirectional application architecture.</string>
    <string name="library_reaktor_libraryWebsite">https://github.com/floschu/Reaktor</string>
    <string name="library_reaktor_libraryVersion">1.0.0</string>
    <string name="library_reaktor_isOpenSource">true</string>
    <string name="library_reaktor_repositoryLink">https://github.com/floschu/Reaktor</string>
    <string name="library_reaktor_classPath">at.florianschuster.reaktor</string>
    <string name="library_reaktor_licenseId">apache_2_0</string>
</resources>
```

## License

```
Copyright 2019 Florian Schuster.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
