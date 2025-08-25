import com.dpforge.easyraster.FieldCodec
import com.dpforge.easyraster.FieldDB
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FieldDBTest {

    @field:TempDir
    lateinit var tempFolder: File

    lateinit var fieldDB: FieldDB

    @BeforeTest
    fun setUp() {
        fieldDB = FieldDB(tempFolder.resolve("test.fdb"))
    }

    @Test
    fun addField() {
        val field1 = FieldCodec.decodeFromCompressedString(
            "gl2y5v2dg3lvw3v2dg3lvw3vdcg3lvw3v2d5gwo2v2d2g3r2o2p2d2g3ro3p2d7g2p2d8gp2d14gb7g"
        )
        val field2 = FieldCodec.decodeFromCompressedString(
            "rc4yg2wobrcyp2yg2wobrc4yg3wb2r3y2gd2wb2r3y2gd3w2r2y4d3w3r5d3w2r2vd2vdv2w2r7v2w2rv2rl3v2w9r2w"
        )
        val field3 = FieldCodec.decodeFromCompressedString(
            "7w2bcgw5pd2b2cw5p2db2c6p3d2c2pl3p3dycp4vp3dyc2p5vd2yc3po2v2r2yc3pov3r2yc5p5rc7p3rc"
        )
        fieldDB.addField(field1)
        fieldDB.addField(field2)
        fieldDB.addField(field3)

        assertEquals(3, fieldDB.getFieldCount())
        assertEquals(field1, fieldDB.getField(0))
        assertEquals(field2, fieldDB.getField(1))
        assertEquals(field3, fieldDB.getField(2))
    }

}