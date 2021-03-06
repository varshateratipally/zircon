package org.codetome.zircon.api.input

import org.assertj.core.api.Assertions.assertThat
import org.codetome.zircon.internal.event.EventBus
import org.codetome.zircon.internal.event.Event
import org.junit.Test

class InputTypeTest {

    @Test
    fun test() {
        InputType.values().forEach {
            val inputs = mutableListOf<Input>()
            EventBus.subscribe<Event.Input> { (input) ->
                inputs.add(input)
            }
            EventBus.broadcast(Event.Input(KeyStroke(
                    character = ' ',
                    type = it)))
            assertThat(inputs.map { it.getInputType() }.first())
                    .isEqualTo(it)
        }
    }


}
