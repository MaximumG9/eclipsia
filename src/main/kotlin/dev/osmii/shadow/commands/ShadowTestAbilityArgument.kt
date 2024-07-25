package dev.osmii.shadow.commands

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.osmii.shadow.enums.ShadowTestAbilities
import net.minecraft.commands.CommandSourceStack
import java.util.concurrent.CompletableFuture
import net.minecraft.network.chat.Component

class ShadowTestAbilityArgument private constructor() : ArgumentType<ShadowTestAbilities>  {



    override fun parse(reader: StringReader?): ShadowTestAbilities {
        try {
            return ShadowTestAbilities.valueOf(reader!!.readString())
        } catch (e : IllegalArgumentException) {
            throw NONEXISTANT_ABILITY_EXCEPTION.createWithContext(reader)
        }
    }

    override fun getExamples(): MutableCollection<String> {
        return ShadowTestAbilities.entries.map {
                obj: ShadowTestAbilities -> obj.name
        }.toMutableList()
    }

    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>?,
        builder: SuggestionsBuilder?
    ): CompletableFuture<Suggestions> {
        builder!!

        ShadowTestAbilities.entries.forEach {
            ability: ShadowTestAbilities -> builder.suggest(ability.name)
        }

        return builder.buildFuture()
    }

    companion object {
        val NONEXISTANT_ABILITY_EXCEPTION = SimpleCommandExceptionType(Component.literal("That ability does not exist"))

        fun testAbility() : ShadowTestAbilityArgument {
            return ShadowTestAbilityArgument()
        }

        fun getAbility(context : CommandContext<CommandSourceStack>, name : String) : ShadowTestAbilities {
            return context.getArgument(name,ShadowTestAbilities::class.java)
        }
    }
}