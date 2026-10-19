package com.example

import com.example.ai.SpiritualInterpreter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testFallbackSpiritualInterpretation_recognizesDreamAndSymbols() {
        val dreamText = "J'ai rêvé que je marchais près d'un grand fleuve avec un aigle au-dessus de ma maison."
        val result = SpiritualInterpreter.fallbackSpiritualInterpretation(dreamText)

        assertNotNull(result)
        assertEquals("REVE", result.category)
        assertTrue(result.title.isNotBlank())
        assertTrue(result.biblicalInterpretation.contains("Écritures"))
        assertTrue(result.africanPerspective.contains("africaines"))
        assertTrue(result.keySymbols.isNotEmpty())
    }

    @Test
    fun testFallbackSpiritualInterpretation_recognizesProphecy() {
        val prophecyText = "Voici la vision prophétique que l'Éternel a révélée pour cette saison."
        val result = SpiritualInterpreter.fallbackSpiritualInterpretation(prophecyText)

        assertEquals("PROPHETIE", result.category)
        assertTrue(result.subCategory.contains("Prophétie"))
    }
}
