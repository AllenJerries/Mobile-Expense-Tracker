package com.jerries.expense.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExtensionsTest {

    @Test
    fun `toMinorUnitsOrNull converts valid decimal`() {
        assertEquals(1000L, "10.00".toMinorUnitsOrNull())
        assertEquals(100L, "1.00".toMinorUnitsOrNull())
        assertEquals(1L, "0.01".toMinorUnitsOrNull())
        assertEquals(0L, "0.00".toMinorUnitsOrNull())
    }

    @Test
    fun `toMinorUnitsOrNull rejects invalid input`() {
        assertNull("".toMinorUnitsOrNull())
        assertNull("abc".toMinorUnitsOrNull())
        assertNull("10.00.00".toMinorUnitsOrNull())
    }

    @Test
    fun `toMinorUnitsOrNull handles large values`() {
        assertEquals(99999999L, "999999.99".toMinorUnitsOrNull())
    }

    @Test
    fun `toMinorUnitsOrNull rounds to 2 decimal places`() {
        assertEquals(101L, "1.005".toMinorUnitsOrNull())
    }

    @Test
    fun `toMajorString converts correctly`() {
        assertEquals("10.00", 1000L.toMajorString())
        assertEquals("0.01", 1L.toMajorString())
        assertEquals("0.00", 0L.toMajorString())
    }
}
