import com.dpforge.easyraster.Color
import com.dpforge.easyraster.Pattern
import com.dpforge.easyraster.Position
import org.junit.jupiter.api.Assertions.assertFalse
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PatternTest {

    private val fieldSize = 7
    private val blue = Color.BLUE

    private lateinit var cells: Array<Array<Color?>>

    @BeforeTest
    fun setUp() {
        cells = Array(fieldSize) { Array(fieldSize) { null } }
    }

    @Test
    fun `canApply - success`() {
        assertTrue(Pattern.TShape().canApply(cells, Position(1, 2)))
    }

    @Test
    fun `canApply - cell is occupied`() {
        cells[2][3] = Color.RED
        assertFalse(Pattern.TShape().canApply(cells, Position(1, 2)))
    }

    @Test
    fun `canApply - pattern out of field`() {
        assertFalse(Pattern.TShape().canApply(cells, Position(5, 0)))
        assertFalse(Pattern.TShape().canApply(cells, Position(-5, 0)))

        assertFalse(Pattern.TShape().canApply(cells, Position(0, 5)))
        assertFalse(Pattern.TShape().canApply(cells, Position(0, -5)))
    }

    @Test
    fun apply() {
        val pattern = Pattern.Bowl()
        pattern.apply(cells, Position(1, 2), blue)
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[0])
        assertContentEquals(arrayOf(null, null, blue, null, null, blue, null), cells[1])
        assertContentEquals(arrayOf(null, null, blue, blue, blue, blue, null), cells[2])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[3])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[4])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[5])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[6])
    }

    @Test
    fun `setTransformation - FLIP_VERTICALLY`() {
        val pattern = Pattern.Bowl()
        pattern.setTransformation(Pattern.Transformation.FLIP_VERTICALLY)
        pattern.apply(cells, Position(1, 2), blue)
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[0])
        assertContentEquals(arrayOf(null, null, blue, blue, blue, blue, null), cells[1])
        assertContentEquals(arrayOf(null, null, blue, null, null, blue, null), cells[2])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[3])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[4])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[5])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[6])
    }

    @Test
    fun `setTransformation - FLIP_HORIZONTALLY`() {
        val pattern = Pattern.Ladder()
        pattern.setTransformation(Pattern.Transformation.FLIP_HORIZONTALLY)
        pattern.apply(cells, Position(1, 2), blue)
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[0])
        assertContentEquals(arrayOf(null, null, null, null, blue, null, null), cells[1])
        assertContentEquals(arrayOf(null, null, null, blue, blue, null, null), cells[2])
        assertContentEquals(arrayOf(null, null, blue, blue, blue, null, null), cells[3])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[4])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[5])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[6])
    }

    @Test
    fun `setTransformation - ROTATE_90_CW`() {
        val pattern = Pattern.Bowl()
        pattern.setTransformation(Pattern.Transformation.ROTATE_90_CW)
        pattern.apply(cells, Position(1, 2), blue)
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[0])
        assertContentEquals(arrayOf(null, null, blue, blue, null, null, null), cells[1])
        assertContentEquals(arrayOf(null, null, blue, null, null, null, null), cells[2])
        assertContentEquals(arrayOf(null, null, blue, null, null, null, null), cells[3])
        assertContentEquals(arrayOf(null, null, blue, blue, null, null, null), cells[4])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[5])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[6])
    }

    @Test
    fun `setTransformation - ROTATE_90_CCW`() {
        val pattern = Pattern.Bowl()
        pattern.setTransformation(Pattern.Transformation.ROTATE_90_CCW)
        pattern.apply(cells, Position(1, 2), blue)
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[0])
        assertContentEquals(arrayOf(null, null, blue, blue, null, null, null), cells[1])
        assertContentEquals(arrayOf(null, null, null, blue, null, null, null), cells[2])
        assertContentEquals(arrayOf(null, null, null, blue, null, null, null), cells[3])
        assertContentEquals(arrayOf(null, null, blue, blue, null, null, null), cells[4])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[5])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[6])
    }

    @Test
    fun `setTransformation - ROTATE_180`() {
        val pattern = Pattern.Ladder()
        pattern.setTransformation(Pattern.Transformation.ROTATE_180)
        pattern.apply(cells, Position(1, 2), blue)
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[0])
        assertContentEquals(arrayOf(null, null, blue, blue, blue, null, null), cells[1])
        assertContentEquals(arrayOf(null, null, null, blue, blue, null, null), cells[2])
        assertContentEquals(arrayOf(null, null, null, null, blue, null, null), cells[3])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[4])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[5])
        assertContentEquals(arrayOf(null, null, null, null, null, null, null), cells[6])
    }

    @Test
    fun `setTransformation - dimensions change`() {
        val pattern = Pattern.Bowl()
        assertEquals(4, pattern.width)
        assertEquals(2, pattern.height)

        pattern.setTransformation(Pattern.Transformation.FLIP_VERTICALLY)
        assertEquals(4, pattern.width)
        assertEquals(2, pattern.height)

        pattern.setTransformation(Pattern.Transformation.FLIP_HORIZONTALLY)
        assertEquals(4, pattern.width)
        assertEquals(2, pattern.height)

        pattern.setTransformation(Pattern.Transformation.ROTATE_180)
        assertEquals(4, pattern.width)
        assertEquals(2, pattern.height)

        pattern.setTransformation(Pattern.Transformation.ROTATE_90_CW)
        assertEquals(2, pattern.width)
        assertEquals(4, pattern.height)

        pattern.setTransformation(Pattern.Transformation.ROTATE_90_CCW)
        assertEquals(2, pattern.width)
        assertEquals(4, pattern.height)
    }
}