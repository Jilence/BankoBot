/*
 * MIT License
 *
 * Copyright (c) 2021 BankoBot Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package de.nycode.bankobot.command

import de.nycode.bankobot.command.permissions.PermissionHandler
import de.nycode.bankobot.utils.Embeds
import de.nycode.bankobot.utils.Embeds.respondEmbed
import dev.kord.x.commands.kord.model.context.KordCommandEvent
import dev.kord.x.commands.model.command.Command
import dev.kord.x.commands.model.command.CommandBuilder
import dev.kord.x.commands.model.command.CommandEvent
import dev.kord.x.commands.model.metadata.Metadata
import dev.kord.x.commands.model.precondition.Precondition
import dev.kord.x.commands.model.processor.ProcessorContext

enum class PermissionLevel {
    ALL,
    MODERATOR,
    ADMIN,
    BOT_OWNER
}

private object PermissionData : Metadata.Key<PermissionLevel>

/**
 * The permission of a command.
 *
 * @see PermissionLevel
 */
val Command<*>.permission: PermissionLevel
    get() = data.metadata[PermissionData] ?: PermissionLevel.ALL

/**
 * Sets the permission of a command to [permission].
 * @see Command.permission
 */
fun <S, A, COMMANDCONTEXT : CommandEvent> CommandBuilder<S, A, COMMANDCONTEXT>.permission(permission: PermissionLevel): Unit =
    metaData.set(PermissionData, permission)

abstract class AbstractPermissionHandler : AbstractKordPrecondition(), PermissionHandler {
    override suspend fun invoke(event: KordCommandEvent): Boolean {
        val member = event.event.member ?: error("Missing member")
        val command = event.command
        val permission = command.permission
        if (isCovered(member, permission)) return true

        event.respondEmbed(
            Embeds.error(
                "Keine Berechtigung",
                "Du benötigst die Berechtigung $permission um diesen Befehl benutzen zu können"
            )
        )
        return false
    }

}
