package me.zodiia.api.config.decoders

import com.sksamuel.hoplite.ArrayNode
import com.sksamuel.hoplite.ConfigFailure
import com.sksamuel.hoplite.ConfigResult
import com.sksamuel.hoplite.DecoderContext
import com.sksamuel.hoplite.MapNode
import com.sksamuel.hoplite.Node
import com.sksamuel.hoplite.StringNode
import com.sksamuel.hoplite.decoder.NullHandlingDecoder
import com.sksamuel.hoplite.decoder.StringDecoder
import com.sksamuel.hoplite.fp.invalid
import com.sksamuel.hoplite.fp.valid
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

class FlatStringMapDecoder: NullHandlingDecoder<Map<String, Array<String>>> {
    override fun supports(type: KType): Boolean =
        type.isSubtypeOf(Map::class.starProjectedType) ||
            type.isSubtypeOf(Map::class.starProjectedType.withNullability(true))

    override fun safeDecode(
        node: Node,
        type: KType,
        context: DecoderContext
    ): ConfigResult<Map<String, Array<String>>> {
        require(type.arguments.size == 2)

        val sType = type.arguments[0].type!!
        val sDecoder = context.decoder(sType).getUnsafe() as StringDecoder

        fun decodeFromMap(node: MapNode, prefix: String, context: DecoderContext): ConfigResult<Map<String, Array<String>>> {
            val map = hashMapOf<String, Array<String>>()

            node.map.entries.forEach { (k, v) ->
                val decodedKey = sDecoder.decode(StringNode(k, node.pos), sType, context)
                if (decodedKey.isInvalid()) throw IllegalStateException()
                val key = "${prefix}${decodedKey.getUnsafe()}"

                when (v) {
                    is MapNode -> {
                        val res = decodeFromMap(v, "${prefix}${if (prefix.isNotEmpty()) "." else ""}${key}", context)
                        if (res.isInvalid()) throw IllegalStateException()
                        res.getUnsafe().forEach { map[it.key] = it.value }
                    }
                    is ArrayNode -> {
                        map[key] = v.elements.map {
                            val decodedValue = sDecoder.decode(it, sType, context)
                            if (decodedValue.isInvalid()) throw IllegalStateException()
                            decodedValue.getUnsafe()
                        }.toTypedArray()
                    }
                    is StringNode -> {
                        val decodedValue = sDecoder.decode(v, sType, context)
                        if (decodedValue.isInvalid()) throw IllegalStateException()
                        map[key] = arrayOf(decodedValue.getUnsafe())
                    }
                    else -> ConfigFailure.UnsupportedCollectionType(node, "Map").invalid()
                }
            }
            return map.valid()
        }

        return when (node) {
            is MapNode -> decodeFromMap(node, "", context)
            else -> ConfigFailure.UnsupportedCollectionType(node, "Map").invalid()
        }
    }
}
