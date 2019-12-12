package org.jetbrains.demo.thinkter

import jetbrains.demo.thinkter.model.Sale
import kotlinx.html.*
import react.*
import react.dom.*
import kotlinx.coroutines.*

class HomeView : ReactDOMComponent<HomeView.Props, HomeView.State>() {
    companion object : ReactComponentSpec<HomeView, Props, State>

    init {
        state = State(emptyList(), emptyList(), true, Polling.NewMessages.None)
    }

    override fun componentWillMount() {
        super.componentWillMount()

        props.polling.listeners.add(pollerHandler)
        loadHome()
    }

    override fun componentWillUnmount() {
        super.componentWillUnmount()
        props.polling.listeners.remove(pollerHandler)
    }

    override fun ReactDOMBuilder.render() {
        div {
            h2 { +"Sales" }

            if (state.loading) {
                p { +"Loading..." }
            } else {
                h3 { +"Top" }
                ThoughtsListComponent {
                    thoughts = state.top
                    show = props.showThought
                }

                h3 { +"Recent" }
                ThoughtsListComponent {
                    thoughts = state.latest
                    show = props.showThought
                }
            }
        }
    }

    private fun loadHome() {
        CoroutineScope(Dispatchers.Default).launch {
            val r = index()
            props.polling.start()
            setState {
                loading = false
                top = r.top
                latest = r.latest
            }
        }
    }

    private val pollerHandler = { m : Polling.NewMessages ->
        val oldMessages = state.newMessages
        setState {
            newMessages = m
        }
        if (oldMessages != m && m == Polling.NewMessages.None) {
            loadHome()
        }
    }

    class State(var top: List<Sale>, var latest: List<Sale>, var loading: Boolean, var newMessages: Polling.NewMessages) : RState
    class Props(var polling: Polling, var showThought: (Sale) -> Unit = {}) : RProps()
}