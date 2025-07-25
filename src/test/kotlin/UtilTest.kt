import com.dpforge.easyraster.allUnorderedPairs
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UtilTest {

    @Test
    fun allUnorderedPairs() {
        val list = listOf("a", "b", "c")
        assertEquals(
            listOf("a" to "b", "a" to "c", "b" to "c"),
            list.allUnorderedPairs().toList()
        )
    }

    @Test
    fun `allUnorderedPairs - empty list`() {
        val list = listOf<String>()
        assertEquals(
            listOf<Pair<String, String>>(),
            list.allUnorderedPairs().toList()
        )
    }

    @Test
    fun `allUnorderedPairs - single item`() {
        val list = listOf("a")
        assertEquals(
            listOf<Pair<String, String>>(),
            list.allUnorderedPairs().toList()
        )
    }

    @Test
    fun `allUnorderedPairs - two items`() {
        val list = listOf("a", "b")
        assertEquals(
            listOf("a" to "b"),
            list.allUnorderedPairs().toList()
        )
    }

}