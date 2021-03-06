package org.codetome.zircon.internal.graphics

import org.assertj.core.api.Assertions.assertThat
import org.codetome.zircon.api.Position
import org.codetome.zircon.internal.behavior.impl.DefaultBoundable
import org.codetome.zircon.api.builder.TextCharacterBuilder
import org.codetome.zircon.api.Size
import org.codetome.zircon.internal.font.impl.FontSettings
import org.junit.Before
import org.junit.Test

class DefaultLayerTest {

    lateinit var target: DefaultLayer

    @Before
    fun setUp() {
        target = DefaultLayer(
                filler = TextCharacterBuilder.defaultCharacter(),
                size = SIZE,
                offset = OFFSET,
                initialFont = FontSettings.NO_FONT)
    }

    @Test
    fun shouldProperlySetOffset() {
        val expectedOffset = Position.defaultPosition()
        target.moveTo(expectedOffset)
        assertThat(target.getPosition())
                .isEqualTo(expectedOffset)
    }

    @Test
    fun shouldIntersectIntersectingBoundable() {
        assertThat(target.intersects(INTERSECTING_BOUNDABLE))
                .isTrue()
    }

    @Test
    fun shouldNotIntersectNonIntersectingBoundable() {
        assertThat(target.intersects(NON_INTERSECTING_BOUNDABLE))
                .isFalse()
    }

    @Test
    fun shouldContainContainedPosition() {
        assertThat(target.containsPosition(CONTAINED_POSITION))
                .isTrue()
    }

    @Test
    fun shouldNotContainNonContainedPosition() {
        assertThat(target.containsPosition(NON_CONTAINED_POSITION))
                .isFalse()
    }

    @Test
    fun shouldContainContainedBoundable() {
        assertThat(target.containsBoundable(target))
                .isTrue()
    }

    @Test
    fun shouldNotContainNonContainedBoundable() {
        assertThat(target.containsBoundable(NON_CONTAINED_BOUNDABLE))
                .isFalse()
    }

    @Test
    fun shouldProperlySetChar() {
        target.setCharacterAt(CONTAINED_POSITION, CHAR)
        assertThat(target.getCharacterAt(CONTAINED_POSITION).get())
                .isEqualTo(CHAR)
    }

    companion object {
        val CHAR = TextCharacterBuilder.newBuilder()
                .character('x')
                .build()
        val SIZE = Size.create(10, 10)
        val OFFSET = Position.create(5, 5)
        val INTERSECTING_BOUNDABLE = DefaultBoundable(Size.create(6, 6))
        val NON_INTERSECTING_BOUNDABLE = DefaultBoundable(Size.create(5, 5))
        val NON_CONTAINED_BOUNDABLE = DefaultBoundable(SIZE.withRelative(Size.one()))
        val CONTAINED_POSITION = OFFSET
        val NON_CONTAINED_POSITION = CONTAINED_POSITION
                .withRelativeX(-1)
                .withRelativeY(-1)
    }

}
