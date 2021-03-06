package org.jetbrains.demo.thinkter

import jetbrains.demo.thinkter.model.Sale
import kotlinx.html.*
import kotlinx.html.js.*
import react.*
import react.dom.*

class ThoughtsListComponent : ReactDOMComponent<ThoughtsListComponent.Props, ReactComponentNoState>() {
    companion object : ReactComponentSpec<ThoughtsListComponent, Props, ReactComponentNoState>

    init {
        state = ReactComponentNoState()
    }

    override fun ReactDOMBuilder.render() {
        fun UL.thoughtLi(t: Sale) {
            li {
                section(classes = "post") {
                    header(classes = "post-header") {
                        p(classes = "post-meta") {
                            a(href = "javascript:void(0)") {
                                +t.date
                                +" by "
                                +t.userId

                                onClickFunction = {
                                    props.show(t)
                                }
                            }
                        }
                    }
                    div(classes = "post-description") {
                        ReactMarkdownComponent {
                            source = t.text
                        }
                    }
                }
            }
        }

        div {
            ul {
                if (props.thoughts.isEmpty()) {
                    li { +"There are no sales yet" }
                } else {
                    for (t in props.thoughts) {
                        thoughtLi(t)
                    }
                }
            }
        }
    }

    class Props(var thoughts: List<Sale> = emptyList(), var show: (Sale) -> Unit = {}) : RProps()
}