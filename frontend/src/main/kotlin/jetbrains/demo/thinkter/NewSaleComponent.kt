package org.jetbrains.demo.thinkter

import jetbrains.demo.thinkter.model.Sale
import jetbrains.demo.thinkter.model.User
import kotlinx.coroutines.GlobalScope
import kotlinx.html.*
import kotlinx.html.js.*
import org.jetbrains.common.*
import react.*
import react.dom.*
import kotlinx.coroutines.launch

class NewSaleComponent : ReactDOMComponent<NewSaleComponent.Props, NewSaleComponent.State>() {
    companion object : ReactComponentSpec<NewSaleComponent, Props, State>

    init {
        state = State()
    }

    override fun ReactDOMBuilder.render() {
        form(classes = "pure-form pure-form-stacked") {
            legend {
                if (props.replyTo == null) {
                    +"New sale"
                } else {
                    +"Reply"
                }
            }

            props.replyTo?.let { replyTo ->
                div {
                    +"reply to ${replyTo.userId}"
                }
            }

            textArea(classes = "pure-input-1-2") {
                placeholder = "Description..."

                onChangeFunction = {
                    setState {
                        text = it.inputValue
                    }
                }
            }

            state.errorMessage?.let { message ->
                p { +message }
            }

            button(classes = "pure-button pure-button-primary") {
                +"Create"

                onClickFunction = {
                    it.preventDefault()
                    doCreateSale()
                }
            }
        }
    }

    private fun doCreateSale() {
        GlobalScope.launch {
            try {
                val token = createSalePrepare()
                onSubmitted(createSale(props.replyTo?.id, state.text, token))
            } catch (e: Exception) {
                onFailed(e)
            }
        }
    }

    private fun onSubmitted(sale: Sale) {
        setState {
            errorMessage = null
        }

        props.showThought(sale)
    }

    private fun onFailed(err: Throwable) {
        setState {
            errorMessage = err.message
        }
    }

    class State(var text: String = "", var errorMessage: String? = null) : RState
    class Props(var replyTo: Sale? = null, var replyToUser: User? = null, var showThought: (Sale) -> Unit = {}) :
        RProps()
}
