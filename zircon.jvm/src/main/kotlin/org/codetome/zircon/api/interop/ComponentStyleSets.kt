package org.codetome.zircon.api.interop

import org.codetome.zircon.api.component.builder.ComponentStyleSetBuilder

object ComponentStyleSets {

    fun newBuilder() = ComponentStyleSetBuilder()

    val DEFAULT_COMPONENT_SYTLE_SET = ComponentStyleSetBuilder.newBuilder().build()
}
