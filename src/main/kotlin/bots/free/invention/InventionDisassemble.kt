package bots.free.invention

import bots.free.invention.ui.InventionUI
import quantum.api.game.Invention
import quantum.api.ui.extenders.UIExtender
import quantum.api.ui.managers.BreakManager
import quantum.api.ui.managers.ListenerManager
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory
import com.runemate.game.api.hybrid.region.Players
import com.runemate.game.api.script.Execution
import com.runemate.game.api.script.framework.LoopingBot

class InventionDisassemble : LoopingBot() {

    private val listenerManager = ListenerManager()
    private val breakHandler = BreakManager()

    var item: String = ""
    var isDestroyingEverything: Boolean = false
    var start: Boolean = false

    override fun onStart(vararg args: String) {
        embeddableUI = UIExtender(this, InventionUI(this), "com/bots/free/invention/ui/invention_ui.fxml", listenerManager, breakHandler)
        setLoopDelay(1000, 1500)
    }

    override fun onLoop() {
        if (start && (isDestroyingEverything || item.isNotBlank())) {
            if (Inventory.isEmpty() || (item.isNotBlank() && !Inventory.contains(item))) {
                stop("No more items left!")
            } else if (!breakHandler.isBreaking) {
                Invention.getProgressWindowContainer().let {
                    if (it == null || !it.isVisible) {
                        val itemToDestroy = if (isDestroyingEverything) Inventory.newQuery().results().first() else Inventory.newQuery().names(item).results().first()
                        if (Invention.disassemble(itemToDestroy)) {
                            Execution.delayUntil({ Players.getLocal()?.animationId == -1 }, { Players.getLocal()?.animationId != -1 }, 4600, 5200)
                        }
                    }
                }
            }
        }
    }
}
