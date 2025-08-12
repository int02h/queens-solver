import com.dpforge.easyraster.Color
import com.dpforge.easyraster.FieldCodec
import com.dpforge.easyraster.Position
import kotlin.test.Test
import kotlin.test.assertEquals

class FieldCodecTest {

    private val expectedColorRegions = mapOf(
        Color.LIGHT_BLUE to setOf(
            Position(row = 0, col = 0),
            Position(row = 0, col = 1),
            Position(row = 1, col = 0),
            Position(row = 1, col = 1),
            Position(row = 2, col = 1),
            Position(row = 2, col = 2),
            Position(row = 3, col = 1),
            Position(row = 3, col = 2),
            Position(row = 4, col = 1),
            Position(row = 4, col = 2),
            Position(row = 5, col = 2),
            Position(row = 5, col = 3),
            Position(row = 6, col = 2),
            Position(row = 6, col = 3),
            Position(row = 6, col = 4),
            Position(row = 6, col = 5),
            Position(row = 7, col = 2),
            Position(row = 7, col = 3),
            Position(row = 7, col = 4),
            Position(row = 7, col = 5),
            Position(row = 8, col = 2),
            Position(row = 8, col = 3),
            Position(row = 8, col = 4),
            Position(row = 8, col = 5),
            Position(row = 9, col = 2),
            Position(row = 9, col = 3),
            Position(row = 9, col = 4),
            Position(row = 9, col = 5)
        ),
        Color.ORANGE to setOf(
            Position(row = 0, col = 2),
            Position(row = 1, col = 2)
        ),
        Color.VIOLET to setOf(
            Position(row = 0, col = 3),
            Position(row = 0, col = 4),
            Position(row = 0, col = 5),
            Position(row = 1, col = 3),
            Position(row = 2, col = 3),
            Position(row = 3, col = 3),
            Position(row = 4, col = 3)
        ),
        Color.PURPLE to setOf(
            Position(row = 0, col = 6),
            Position(row = 0, col = 7),
            Position(row = 1, col = 6),
            Position(row = 2, col = 6),
            Position(row = 2, col = 7),
            Position(row = 2, col = 8),
            Position(row = 3, col = 7),
            Position(row = 3, col = 8),
            Position(row = 4, col = 7),
            Position(row = 4, col = 8),
            Position(row = 4, col = 9),
            Position(row = 5, col = 7),
            Position(row = 5, col = 8),
            Position(row = 5, col = 9),
            Position(row = 6, col = 7),
            Position(row = 6, col = 8),
            Position(row = 6, col = 9),
            Position(row = 7, col = 8),
            Position(row = 7, col = 9),
            Position(row = 8, col = 9),
            Position(row = 9, col = 9)
        ),
        Color.WHITE to setOf(
            Position(row = 0, col = 8),
            Position(row = 0, col = 9)
        ),
        Color.DARK_GRAY to setOf(
            Position(row = 1, col = 4),
            Position(row = 1, col = 5),
            Position(row = 2, col = 4),
            Position(row = 3, col = 4),
            Position(row = 4, col = 4),
            Position(row = 5, col = 4)
        ),
        Color.YELLOW to setOf(
            Position(row = 1, col = 7),
            Position(row = 1, col = 8),
            Position(row = 1, col = 9),
            Position(row = 2, col = 9),
            Position(row = 3, col = 9)
        ),
        Color.RED to setOf(
            Position(row = 2, col = 0),
            Position(row = 3, col = 0),
            Position(row = 4, col = 0)
        ),
        Color.BLUE to setOf(
            Position(row = 2, col = 5),
            Position(row = 3, col = 5),
            Position(row = 3, col = 6),
            Position(row = 4, col = 5),
            Position(row = 4, col = 6),
            Position(row = 5, col = 5),
            Position(row = 5, col = 6),
            Position(row = 6, col = 6),
            Position(row = 7, col = 6),
            Position(row = 7, col = 7),
            Position(row = 8, col = 6),
            Position(row = 8, col = 7),
            Position(row = 8, col = 8),
            Position(row = 9, col = 6),
            Position(row = 9, col = 7),
            Position(row = 9, col = 8)
        ),
        Color.GREEN to setOf(
            Position(row = 5, col = 0),
            Position(row = 5, col = 1),
            Position(row = 6, col = 0),
            Position(row = 6, col = 1),
            Position(row = 7, col = 0),
            Position(row = 7, col = 1),
            Position(row = 8, col = 0),
            Position(row = 8, col = 1),
            Position(row = 9, col = 0),
            Position(row = 9, col = 1)
        )
    )

    @Test
    fun `decode - encode`() {
        val encoded =
            "llovvvppwwllovddpyyyrllvdbpppyrllvdbbppyrllvdbbpppgglldbbpppggllllbpppggllllbbppggllllbbbpggllllbbbp"
        val field = FieldCodec.decodeFromString(encoded)
        assertEquals(10, field.size)
        assertEquals(expectedColorRegions, field.colorRegions)
        assertEquals(encoded, FieldCodec.encodeToString(field))
    }

    @Test
    fun `compressed decode - compressed encode`() {
        val encoded = "3b2y2o2dlr2wy2vo2dlr2w2yvo2dl4r2vo2dl5rvo3d4r2v2o2d3r2gv3pd3r2g4pd6r3pd10r"
        val field = FieldCodec.decodeFromCompressedString(encoded)
        assertEquals(10, field.size)
        assertEquals(
            mapOf(
                Color.BLUE to setOf(Position(row = 0, col = 0), Position(row = 0, col = 1), Position(row = 0, col = 2)),
                Color.YELLOW to setOf(
                    Position(row = 0, col = 3),
                    Position(row = 0, col = 4),
                    Position(row = 1, col = 3),
                    Position(row = 2, col = 3),
                    Position(row = 2, col = 4)
                ),
                Color.ORANGE to setOf(
                    Position(row = 0, col = 5),
                    Position(row = 0, col = 6),
                    Position(row = 1, col = 6),
                    Position(row = 2, col = 6),
                    Position(row = 3, col = 6),
                    Position(row = 4, col = 6),
                    Position(row = 5, col = 6),
                    Position(row = 5, col = 7)
                ),
                Color.DARK_GRAY to setOf(
                    Position(row = 0, col = 7),
                    Position(row = 0, col = 8),
                    Position(row = 1, col = 7),
                    Position(row = 1, col = 8),
                    Position(row = 2, col = 7),
                    Position(row = 2, col = 8),
                    Position(row = 3, col = 7),
                    Position(row = 3, col = 8),
                    Position(row = 4, col = 7),
                    Position(row = 4, col = 8),
                    Position(row = 4, col = 9),
                    Position(row = 5, col = 8),
                    Position(row = 5, col = 9),
                    Position(row = 6, col = 9),
                    Position(row = 7, col = 9),
                    Position(row = 8, col = 9)
                ),
                Color.LIGHT_BLUE to setOf(
                    Position(row = 0, col = 9),
                    Position(row = 1, col = 9),
                    Position(row = 2, col = 9),
                    Position(row = 3, col = 9)
                ),
                Color.RED to setOf(
                    Position(row = 1, col = 0),
                    Position(row = 2, col = 0),
                    Position(row = 3, col = 0),
                    Position(row = 3, col = 1),
                    Position(row = 3, col = 2),
                    Position(row = 3, col = 3),
                    Position(row = 4, col = 0),
                    Position(row = 4, col = 1),
                    Position(row = 4, col = 2),
                    Position(row = 4, col = 3),
                    Position(row = 4, col = 4),
                    Position(row = 5, col = 0),
                    Position(row = 5, col = 1),
                    Position(row = 5, col = 2),
                    Position(row = 5, col = 3),
                    Position(row = 6, col = 0),
                    Position(row = 6, col = 1),
                    Position(row = 6, col = 2),
                    Position(row = 7, col = 0),
                    Position(row = 7, col = 1),
                    Position(row = 7, col = 2),
                    Position(row = 8, col = 0),
                    Position(row = 8, col = 1),
                    Position(row = 8, col = 2),
                    Position(row = 8, col = 3),
                    Position(row = 8, col = 4),
                    Position(row = 8, col = 5),
                    Position(row = 9, col = 0),
                    Position(row = 9, col = 1),
                    Position(row = 9, col = 2),
                    Position(row = 9, col = 3),
                    Position(row = 9, col = 4),
                    Position(row = 9, col = 5),
                    Position(row = 9, col = 6),
                    Position(row = 9, col = 7),
                    Position(row = 9, col = 8),
                    Position(row = 9, col = 9)
                ),
                Color.WHITE to setOf(
                    Position(row = 1, col = 1),
                    Position(row = 1, col = 2),
                    Position(row = 2, col = 1),
                    Position(row = 2, col = 2)
                ),
                Color.VIOLET to setOf(
                    Position(row = 1, col = 4),
                    Position(row = 1, col = 5),
                    Position(row = 2, col = 5),
                    Position(row = 3, col = 4),
                    Position(row = 3, col = 5),
                    Position(row = 4, col = 5),
                    Position(row = 5, col = 4),
                    Position(row = 5, col = 5),
                    Position(row = 6, col = 5)
                ),
                Color.GREEN to setOf(
                    Position(row = 6, col = 3),
                    Position(row = 6, col = 4),
                    Position(row = 7, col = 3),
                    Position(row = 7, col = 4)
                ),
                Color.PURPLE to setOf(
                    Position(row = 6, col = 6),
                    Position(row = 6, col = 7),
                    Position(row = 6, col = 8),
                    Position(row = 7, col = 5),
                    Position(row = 7, col = 6),
                    Position(row = 7, col = 7),
                    Position(row = 7, col = 8),
                    Position(row = 8, col = 6),
                    Position(row = 8, col = 7),
                    Position(row = 8, col = 8)
                )
            ), field.colorRegions
        )
        assertEquals(encoded, FieldCodec.encodeToCompressedString(field))
    }

    @Test
    fun `human text decode - human text encode`() {
        val encoded = listOf(
            "llovvvppww",
            "llovddpyyy",
            "rllvdbpppy",
            "rllvdbbppy",
            "rllvdbbppp",
            "gglldbbppp",
            "ggllllbppp",
            "ggllllbbpp",
            "ggllllbbbp",
            "ggllllbbbp",
        )
        val field = FieldCodec.decodeFromHumanText(encoded)
        assertEquals(10, field.size)
        assertEquals(expectedColorRegions, field.colorRegions)
        assertEquals(encoded, FieldCodec.encodeToHumanText(field))
    }

}