package org.jetbrains.demo.thinkter

import jetbrains.demo.thinkter.model.Sale
import jetbrains.demo.thinkter.model.User
import kotlinx.coroutines.GlobalScope
import kotlinx.html.*
import kotlinx.html.js.*
import react.*
import react.dom.*
import runtime.wrappers.*
import kotlin.browser.*
import kotlinx.coroutines.launch

class ViewThoughtComponent : ReactDOMComponent<ViewThoughtComponent.Props, ReactComponentNoState>() {

    companion object : ReactComponentSpec<ViewThoughtComponent, Props, ReactComponentNoState>

    init {
        state = ReactComponentNoState()
    }

    override fun ReactDOMBuilder.render() {
        val userId = props.thought.userId
        val text = props.thought.text
        val date = props.thought.date

        div(classes = "pure-g") {
            div(classes = "pure-u-1 pure-u-md-1-3") {
                +"Username: $userId"
//                props.thought.replyTo?.let { id ->
//                    +" replies to $id"
//                }
            }
            div(classes = "pure-u-1 pure-u-md-2-3") {
                +date
            }
            div(classes = "pure-u-2 pure-u-md-1-1") {
                ReactMarkdownComponent {
                    source = text
                }
            }

            if (props.currentUser != null) {
                div(classes = "pure-u-3 pure-u-md-2-3") {
                    +""
                }
                div(classes = "pure-u-3 pure-u-md-1-3") {
                    val isOwner = props.currentUser!!.userId == props.thought.userId
                    if(isOwner) {
                        a(href = "javascript:void(0)") {
                            +"Delete"

                            onClickFunction = {
                                deleteSale()
                            }
                        }
                    }

                    span {
                        style = jsstyle { margin = "0 5px 0 5px" }
                        +" "
                    }

                    if (!isOwner) {
                        a(href = "javascript:void(0)") {
                            +"Offer my price"

                            onClickFunction = {
                                offerPrice()
                                //props.reply(props.thought)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun deleteSale() {
        if (window.confirm("Do you want to delete the sale?")) {
            GlobalScope.launch {
                val token = createSalePrepare()
                deleteSale(props.thought.id, token.date, token.code)
                props.leave()
            }
        }
    }

    private fun offerPrice() {
        GlobalScope.launch {
            offerPrice(props.thought.id, 10)
            props.leave()
        }
    }

    class Props(var thought: Sale, var currentUser: User? = null, var reply: (Sale) -> Unit = {}, var leave: () -> Unit = {}) : RProps()
}