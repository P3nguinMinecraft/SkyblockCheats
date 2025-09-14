package config

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.SortingBehavior
import net.minecraft.client.MinecraftClient
import com.sbc.data.Constants
import com.sbc.util.ChatUtils
import java.io.File
import java.util.Comparator

val client : MinecraftClient = MinecraftClient.getInstance()

object Config : Vigilant(
    File(Constants.CONFIG_FILE_PATH),
    "SBC Config",
    sortingBehavior = ConfigSorting
) {
    fun openGui() {
        ChatUtils.sendMessage("opening gui")
        ChatUtils.sendMessage(gui() ?: "null")
        client.execute { client.setScreen(gui()) }
    }

    fun init() {
        initialize()
        markDirty()
    }

    // examples

    @Property(
        type = PropertyType.SWITCH, name = "Example Toggle",
        description = "This is the description",
        category = "Category Name",
        subcategory = "Subcategory Name"
    )
    var example = true

    private object ConfigSorting : SortingBehavior() {
        override fun getCategoryComparator(): Comparator<in Category> = Comparator { o1, o2 ->
            //if (o1.name == "General") return@Comparator -1
            //if (o2.name == "General") return@Comparator 1
            //else
                compareValuesBy(o1.name, o2.name)
        }
    }
}