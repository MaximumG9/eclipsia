package dev.osmii.shadow.game.start

import dev.osmii.shadow.Shadow
import dev.osmii.shadow.enums.GamePhase
import dev.osmii.shadow.enums.PlayableRole
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.ItemDisplay
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.random.Random

const val ENDER_EYE_OVERWORLD_COUNT = 8
const val ENDER_EYE_STRONGHOLD_COUNT = 0
const val ENDER_EYE_NETHER_COUNT = 8
const val ENDER_EYE_NETHER_ROOF_COUNT = 8

class P3SpawnEnderEyes(private val shadow: Shadow) {
    fun spawnEnderEyes() {
        val overworld = shadow.overworld
        val nether = shadow.server.worlds[1]

        // Spawn Overworld Surface Ender Eyes

        for (i in 1..ENDER_EYE_OVERWORLD_COUNT) {
            val x = Random.nextInt(
                (-WORLD_BORDER_SIZE).toInt(), WORLD_BORDER_SIZE.toInt()
            ) + overworld.spawnLocation.x.toInt()
            val z = Random.nextInt(
                (-WORLD_BORDER_SIZE).toInt(), WORLD_BORDER_SIZE.toInt()
            ) + overworld.spawnLocation.z.toInt()
            val loc = overworld.getHighestBlockAt(x, z).location
            loc.add(0.0, 1.0, 0.0)
            createEnderEye(loc)
        }

        /*

        // Spawn Stronghold Ender Eyes

        /* We replace the target spaces for the ender eyes with structure blocks,
         * then replace them back (with a similar pattern).
         * We then get the blocks changed and pick a few on which to spawn ender eyes.
         */

        val session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(overworld))


        /* This mask searches for this pattern
         *  ________
         * |        |
         * |   Air  |
         * |________|
         *  ________
         * |        |
         * |   Air  |
         * |________|
         *  ________
         * | Stone  |
         * | Bricks |
         * |________|
         * NOTE: Stone bricks or equivalents like cracked & mossy stone bricks.
         */

        val strongholdMask = MaskIntersection()

        // Creates a mask that checks for this
        strongholdMask.add(
            OffsetMask(
                Masks.negate(ExistingBlockMask(session)), BlockVector3.at(0, 1, 0)
            )
        ) // FAWE only has support for OffsetMask, not OffsetsMasks (WTF)
        strongholdMask.add(
            OffsetMask(
                Masks.negate(ExistingBlockMask(session)), BlockVector3.at(0, 2, 0)
            )
        )

        val stoneBrickMask = BlockTypeMask(
            session, BlockTypes.STONE_BRICKS, BlockTypes.CRACKED_STONE_BRICKS, BlockTypes.MOSSY_STONE_BRICKS
        )
        strongholdMask.add(stoneBrickMask)

        val worldBorderBoundingBox = BoundingBox(
            overworld.spawnLocation.x + WORLD_BORDER_SIZE,
            -64.0,
            overworld.spawnLocation.z + WORLD_BORDER_SIZE,
            overworld.spawnLocation.x - WORLD_BORDER_SIZE,
            315.0,
            overworld.spawnLocation.z - WORLD_BORDER_SIZE
        )

        if (shadow.strongholdBoundingBox == null) {
            shadow.server.broadcast(
                MiniMessage.miniMessage().deserialize(
                    "<red>Failed to spawn ender eyes, couldn't re-find the stronghold</red>"
                )
            )
        }

        val strongholdBoundingBox: BoundingBox = worldBorderBoundingBox.intersection(shadow.strongholdBoundingBox!!)

        val region = CuboidRegion(
            BlockVector3.at(strongholdBoundingBox.minX, strongholdBoundingBox.minY, strongholdBoundingBox.minZ),
            BlockVector3.at(strongholdBoundingBox.maxX, strongholdBoundingBox.maxY, strongholdBoundingBox.maxZ)
        )

        session.replaceBlocks(region, strongholdMask, BlockTypes.STRUCTURE_BLOCK!!.defaultState)

        session.close()

        val session2 = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(overworld))

        val stoneBrickPattern = RandomPattern()
        stoneBrickPattern.add(BlockTypes.STONE_BRICKS!!.defaultState, 0.97)
        stoneBrickPattern.add(BlockTypes.CRACKED_STONE_BRICKS!!.defaultState, 0.02)
        stoneBrickPattern.add(BlockTypes.MOSSY_STONE_BRICKS!!.defaultState, 0.01)

        session2.replaceBlocks(
            region, BlockTypes.STRUCTURE_BLOCK!!.allStates.map { it.toBaseBlock() }.toSet(), stoneBrickPattern
        )

        session2.close()

        val possiblePositions = ArrayList<Vector3>()

        val iter: Iterator<Change> = session.changeSet.backwardIterator()
        var posGetter : ((Change) -> Vector3)? = null

        while (iter.hasNext()) {
            val change = iter.next()
            if(posGetter == null) {
                if(change is BlockChange) {
                    posGetter = posGet@{ changed : Change ->
                        return@posGet (changed as BlockChange).position.toVector3()
                    }
                } else { // We're using FAWE and this is a MutableBlockChange
                    posGetter = posGet@{ changed : Change ->
                        val xField = changed.javaClass.getDeclaredField("x")
                        val yField = changed.javaClass.getDeclaredField("y")
                        val zField = changed.javaClass.getDeclaredField("z")

                        val x = xField.getInt(changed)
                        val y = yField.getInt(changed)
                        val z = zField.getInt(changed)

                        return@posGet Vector3.at(x.toDouble(),y.toDouble(),z.toDouble())
                    }
                }
            }
            possiblePositions.add(posGetter.invoke(change))
        }

        if (possiblePositions.size > ENDER_EYE_STRONGHOLD_COUNT) {
            for (i in 1..ENDER_EYE_STRONGHOLD_COUNT) {
                val chosenVector = possiblePositions[Random.nextInt(0, possiblePositions.size - 1)]
                val loc = Location(overworld, chosenVector.x, chosenVector.y + 1, chosenVector.z)
                createEnderEye(loc)
            }
        } else {
            shadow.server.broadcast(
                MiniMessage.miniMessage().deserialize("<red> not enough space to spawn stronghold ender eyes </red>")
            )
        }

        */


        // Spawn Nether Ender Eyes
        val minY = 32
        val maxY = 110

        for (i in 1..ENDER_EYE_NETHER_COUNT) {
            var eyePosition: Location?

            do {
                val x = Random.nextInt(
                    (overworld.spawnLocation.x / 8 - WORLD_BORDER_SIZE).toInt(),
                    (overworld.spawnLocation.x / 8 + WORLD_BORDER_SIZE).toInt()
                )
                val z = Random.nextInt(
                    (overworld.spawnLocation.z / 8 - WORLD_BORDER_SIZE).toInt(),
                    (overworld.spawnLocation.z / 8 + WORLD_BORDER_SIZE).toInt()
                )
                val y = Random.nextInt(minY, maxY)

                eyePosition = Location(nether, x.toDouble(), y.toDouble(), z.toDouble())

                if (!eyePosition.block.isEmpty) {
                    eyePosition = null
                    continue
                }

                while (eyePosition.block.isEmpty) {
                    eyePosition.add(0.0, -1.0, 0.0)
                }

            } while (eyePosition == null || eyePosition.block.type == Material.LAVA || eyePosition.block.type == Material.FIRE)

            eyePosition.add(0.0, 1.0, 0.0)

            createEnderEye(eyePosition)
        }


        // Spawn Nether Roof Ender Eyes

        for (i in 1..ENDER_EYE_NETHER_ROOF_COUNT) {
            val x = Random.nextInt(
                (-WORLD_BORDER_SIZE).toInt(), WORLD_BORDER_SIZE.toInt()
            ) + overworld.spawnLocation.x.toInt() / 8
            val z = Random.nextInt(
                (-WORLD_BORDER_SIZE).toInt(), WORLD_BORDER_SIZE.toInt()
            ) + overworld.spawnLocation.z.toInt() / 8
            val loc = Location(nether, x.toDouble(), 128.0, z.toDouble())

            createEnderEye(loc)
        }

        // Finish phase
        shadow.gameState.startTick = shadow.server.currentTick
        shadow.gameState.currentPhase = GamePhase.GAME_IN_PROGRESS
    }

    private fun createEnderEye(loc: Location): Item {

        loc.chunk.load()
        val eyeLoc = loc.add(0.5, 1.0, 0.5)

        val e = eyeLoc.world.spawnEntity(eyeLoc, EntityType.DROPPED_ITEM) as Item
        e.itemStack = ItemStack(Material.ENDER_EYE, 1)
        e.setWillAge(false)
        e.setCanMobPickup(false)
        e.velocity = Vector(0,0,0)
        e.isInvulnerable = true
        e.isUnlimitedLifetime = true

        val display = eyeLoc.world.spawnEntity(eyeLoc, EntityType.ITEM_DISPLAY) as ItemDisplay
        display.itemStack = e.itemStack
        display.displayHeight = 3.0F
        display.displayWidth = 3.0F

        val lookerArmorStand = eyeLoc.world.spawnEntity(eyeLoc, EntityType.ARMOR_STAND) as ArmorStand
        lookerArmorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING)
        lookerArmorStand.setItem(EquipmentSlot.HEAD,e.itemStack)
        lookerArmorStand.isMarker = true
        lookerArmorStand.isInvisible = true
        lookerArmorStand.isGlowing = true

        shadow.gameState.currentRoles.forEach { (uuid, role) ->
            val player = shadow.server.getPlayer(uuid)
            if(role != PlayableRole.LOOKER) {
                player?.hideEntity(shadow, lookerArmorStand)
            }
        }

        shadow.eyes[e.uniqueId] = Pair(display.uniqueId,lookerArmorStand.uniqueId)

        return e
    }

}