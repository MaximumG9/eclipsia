package dev.osmii.shadow.enums

import org.bukkit.Material

enum class Foods(val material: () -> Material) {
    LEGACY_BREAD({ Material.LEGACY_BREAD }),
    BREAD({ Material.BREAD }),
    BAKED_POTATO({ Material.BAKED_POTATO }),
    COOKED_COD({ Material.COOKED_COD }),
    COOKED_RABBIT({ Material.COOKED_RABBIT }),
    COOKED_SALMON({ Material.COOKED_SALMON }),
    COOKED_CHICKEN({ Material.COOKED_CHICKEN }),
    RANDOM({ entries.random().material.invoke() });
}