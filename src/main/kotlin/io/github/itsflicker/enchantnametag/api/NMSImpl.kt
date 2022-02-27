package io.github.itsflicker.enchantnametag.api

import io.github.itsflicker.enchantnametag.module.display.Scoreboard
import net.minecraft.server.v1_16_R3.*
import org.bukkit.entity.Player
import taboolib.common.reflect.Reflex.Companion.invokeMethod
import taboolib.common.reflect.Reflex.Companion.setProperty
import taboolib.module.nms.Packet
import java.util.*

/**
 * @author wlys
 * @since 2022/2/24 22:36
 */
class NMSImpl : NMS() {

    override fun processPlayerInfo(packet: Packet) {

    }

    override fun processScoreboardTeam(player: Player, packet: Packet) {
        val name = packet.read<String>("name")!!
        if (!Scoreboard.teams.containsKey(name)) {
            return
        }
        val method = packet.read<Int>("method")!!
        if (method != 0 && method != 2) {
            return
        }
        val optional = packet.read<Optional<*>>("parameters")!!
        if (!optional.isPresent) {
            return
        }
        val team = Scoreboard.teams[name]!!
        val parameters = optional.get()
        parameters.setProperty("playerPrefix", classChatSerializer.invokeMethod("fromJson", team.prefix, fixed = true))
        parameters.setProperty("playerSuffix", classChatSerializer.invokeMethod("fromJson", team.suffix, fixed = true))
        parameters.setProperty("color", EnumChatFormat.values()[team.color.ordinal])
    }

    override fun getMetaEntityBoolean(index: Int, value: Boolean): Any {
        return DataWatcher.Item(DataWatcherObject(index, DataWatcherRegistry.i), value)
    }

    override fun getMetaEntityChatBaseComponent(index: Int, json: String): Any {
        return DataWatcher.Item<Optional<IChatBaseComponent>>(
                DataWatcherObject(index, DataWatcherRegistry.f),
                Optional.ofNullable(classChatSerializer.invokeMethod("fromJson", json, fixed = true))
            )
    }
}