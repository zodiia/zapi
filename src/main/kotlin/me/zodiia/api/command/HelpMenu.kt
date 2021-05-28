package me.zodiia.api.command

import me.zodiia.api.util.translateColors
import org.bukkit.command.CommandSender
import kotlin.math.ceil

class HelpMenu(val name: String, val label: String) {
    private val commands = sortedMapOf<String, String>()
    private val pages = arrayListOf<List<String>>()
    var header: String? = null
    var footer: String? = null

    fun addCommand(command: String, description: String) {
        commands[command] = description
    }

    fun build(perPage: Int = DEFAULT_CMDS_PER_PAGE) {
        val totalPages = ceil(commands.size.toDouble() / perPage.toDouble())
        var currentPageIdx = 0
        var currentPage: ArrayList<String>? = null
        var idx = perPage

        pages.clear()
        commands.forEach {
            if (idx == perPage) {
                if (currentPage != null) {
                    footer?.let { footer -> currentPage!!.add(footer) }
                    pages.add(currentPage!!)
                }
                currentPage = arrayListOf()
                currentPageIdx++
                idx = 0
                currentPage!!.add("&7 -------[ &2Help: &e${name} &7(&2Page: &e${currentPageIdx}&7/&e${totalPages}&7) ]-------".translateColors())
                header?.let { header -> currentPage!!.add(header) }
            }
            idx++
            currentPage!!.add(" &8➜ &e/${label} ${it.key} &8&l- &3${it.value}")
        }
        idx++
        if (currentPage != null) {
            footer?.let { footer -> currentPage!!.add(footer) }
            pages.add(currentPage!!)
        }
    }

    fun getPage(page: Int) = pages[page]

    fun getTotalPages() = pages.size

    fun sendPage(page: Int, to: CommandSender) = to.sendMessage(pages[page].toTypedArray())

    companion object {
        private const val DEFAULT_CMDS_PER_PAGE = 6
    }
}
