package org.codetome.zircon.api.builder

import org.codetome.zircon.api.Modifier
import org.codetome.zircon.api.TextCharacter
import org.codetome.zircon.api.color.TextColor
import org.codetome.zircon.platform.factory.TextColorFactory
import org.codetome.zircon.api.graphics.StyleSet
import org.codetome.zircon.api.graphics.builder.StyleSetBuilder

/**
 * Builds [TextCharacter]s.
 * Defaults:
 * - Default character is a space
 * - Default modifiers is an empty set
 * also
 * @see TextColorFactory to check default colors
 */
data class TextCharacterBuilder(
        private var character: Char = ' ',
        private var styleSet: StyleSet = StyleSetBuilder.defaultStyle(),
        private var tags: Set<String> = setOf()) : Builder<TextCharacter> {

    fun character(character: Char) = also {
        this.character = character
    }

    /**
     * Sets the styles (colors and modifiers) from the given
     * `styleSet`.
     */
    fun styleSet(styleSet: StyleSet) = also {
        this.styleSet = styleSet
    }

    fun foregroundColor(foregroundColor: TextColor) = also {
        this.styleSet = styleSet.withForegroundColor(foregroundColor)
    }

    fun backgroundColor(backgroundColor: TextColor) = also {
        this.styleSet = styleSet.withBackgroundColor(backgroundColor)
    }

    fun modifiers(modifiers: Set<Modifier>) = also {
        this.styleSet = styleSet.withModifiers(modifiers)
    }

    fun modifiers(vararg modifiers: Modifier): TextCharacterBuilder = modifiers(modifiers.toSet())

    fun tag(vararg tags: String) = also {
        this.tags = tags.toSet()
    }

    fun tags(tags: Set<String>) = also {
        this.tags = tags
    }

    override fun build(): TextCharacter = TextCharacter.create(character, styleSet, tags)

    override fun createCopy() = copy()

    companion object {
        /**
         * Creates a new [TextCharacterBuilder] for creating [TextCharacter]s.
         */
        fun newBuilder() = TextCharacterBuilder()

        /**
         * Shorthand for the default character which is:
         * - a space character
         * - with default foreground
         * - and default background
         * - and no modifiers.
         */
        fun defaultCharacter() = TextCharacterBuilder.newBuilder().build()

        /**
         * Shorthand for an empty character which is:
         * - a space character
         * - with transparent foreground
         * - and transparent background
         * - and no modifiers.
         */
        fun empty() = TextCharacterBuilder.newBuilder()
                .backgroundColor(TextColorFactory.transparent())
                .foregroundColor(TextColorFactory.transparent())
                .character(' ')
                .build()
    }
}
