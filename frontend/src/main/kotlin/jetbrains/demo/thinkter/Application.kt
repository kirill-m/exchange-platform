package org.jetbrains.demo.thinkter

import jetbrains.demo.thinkter.model.Sale
import jetbrains.demo.thinkter.model.User
import kotlinx.html.*
import kotlinx.html.js.*
import react.*
import react.dom.*
import kotlin.browser.*
import kotlinx.coroutines.*

fun main(args: Array<String>) {
    runtime.wrappers.require("pure-blog.css")

    ReactDOM.render(document.getElementById("content")) {
        div {
            Application {}
        }
    }
}

class Application : ReactDOMComponent<ReactComponentNoProps, ApplicationPageState>() {
    companion object : ReactComponentSpec<Application, ReactComponentNoProps, ApplicationPageState>

    val polling = Polling()

    init {
        state = ApplicationPageState(MainView.Home)
        checkUserSession()
    }

    override fun componentWillUnmount() {
        polling.stop()
        super.componentWillUnmount()
    }

    override fun ReactDOMBuilder.render() {
        div("pure-g") {
            div("sidebar pure-u-1 pure-u-md-1-4") {
                div("header") {
                    div("brand-title") {
                        +"Exchanger"

                        if (state.selected != MainView.Loading) {
                            onClickFunction = { mainViewSelected() }
                        }
                    }
                    nav("nav") {
                        if (state.selected != MainView.Loading) {
                            NavBarComponent {
                                user = state.currentUser
                                handler = { navBarSelected(it) }
                                logoutHandler = { onLoggedOut() }
                                poller = this@Application.polling
                            }
                        }
                    }
                }
            }

            div("content pure-u-1 pure-u-md-3-4") {
                when (state.selected) {
                    MainView.Loading -> h1 { +"Loading..." }
                    MainView.Home -> HomeView {
                        showThought = { t -> onShowThought(t) }
                        polling = this@Application.polling
                    }
                    MainView.Login -> LoginComponent {
                        userAssigned = { onUserAssigned(it) }
                    }
                    MainView.Register -> RegisterComponent {
                        userAssigned = { onUserAssigned(it) }
                    }
                    MainView.PostSale -> NewSaleComponent {
                        showThought = { t -> onShowThought(t) }
                        replyTo = state.replyTo
                    }
                    MainView.User -> {
                    }
                    MainView.Sale -> ViewThoughtComponent {
                        thought = state.currentThought ?: Sale(0, "?", "?", "?", null)
                        currentUser = state.currentUser
                        reply = { onReplyTo(it) }
                        leave = { mainViewSelected() }
                    }
                }
            }

            div("footer") {
                +"Exchanger kotlin frontend + react + ktor example"
            }
        }
    }

    private fun onReplyTo(t: Sale) {
        setState {
            replyTo = t
            selected = MainView.PostSale
        }
    }

    private fun onLoggedOut() {
        val oldSelected = state.selected

        setState {
            currentUser = null
            selected = when (oldSelected) {
                MainView.Home, MainView.Sale, MainView.Login, MainView.Register -> oldSelected
                else -> MainView.Home
            }
        }
    }

    private fun onShowThought(t: Sale) {
        setState {
            currentThought = t
            selected = MainView.Sale
        }
    }

    private fun navBarSelected(newSelected: MainView) {
        setState {
            selected = newSelected
        }
    }

    private fun onUserAssigned(user: User) {
        setState {
            currentUser = user
            selected = MainView.Home
        }
    }

    private fun mainViewSelected() {
        setState {
            selected = MainView.Home
        }
    }

    private fun checkUserSession() {
        GlobalScope.launch {
            try {
                val user = checkSession()
                onUserAssigned(user)
            } catch (e: Exception) {
                setState {
                    selected = org.jetbrains.demo.thinkter.MainView.Home
                }
            }
        }
    }
}

enum class MainView {
    Loading,
    Register,
    Login,
    User,
    PostSale,
    Sale,
    Home
}

class ApplicationPageState(
    var selected: MainView,
    var currentUser: User? = null,
    var currentThought: Sale? = null,
    var replyTo: Sale? = null
) : RState

class UserProps : RProps() {
    var userAssigned: (User) -> Unit = {}
}
