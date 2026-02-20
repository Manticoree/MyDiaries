package ru.diaries.mydiaries.data.local.converter

import org.junit.Test
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDate

class DateConverterTest {

    private val converter = DateConverter()

    @Test
    fun `fromTimestamp converts Long to LocalDate correctly`() {
        // Arrange
        val epochDay = LocalDate.of(2024, 3, 15).toEpochDay()

        // Act
        val result = converter.fromTimestamp(epochDay)

        // Assert
        assertThat(result).isEqualTo(LocalDate.of(2024, 3, 15))
    }

    @Test
    fun `fromTimestamp handles epoch day 0`() {
        // Act
        val result = converter.fromTimestamp(0L)

        // Assert
        assertThat(result).isEqualTo(LocalDate.of(1970, 1, 1))
    }

    @Test
    fun `fromTimestamp returns null for null input`() {
        // Act
        val result = converter.fromTimestamp(null)

        // Assert
        assertThat(result).isNull()
    }

    @Test
    fun `dateToTimestamp converts LocalDate to Long correctly`() {
        // Arrange
        val date = LocalDate.of(2024, 12, 31)

        // Act
        val result = converter.dateToTimestamp(date)

        // Assert
        assertThat(result).isEqualTo(LocalDate.of(2024, 12, 31).toEpochDay())
    }

    @Test
    fun `dateToTimestamp returns null for null input`() {
        // Act
        val result = converter.dateToTimestamp(null)

        // Assert
        assertThat(result).isNull()
    }

    @Test
    fun `round-trip conversion maintains date integrity`() {
        // Arrange
        val originalDate = LocalDate.of(2024, 6, 15)

        // Act
        val timestamp = converter.dateToTimestamp(originalDate)
        val backToDate = converter.fromTimestamp(timestamp)

        // Assert
        assertThat(backToDate).isEqualTo(originalDate)
    }

    @Test
    fun `round-trip conversion for multiple dates`() {
        val testDates = listOf(
            LocalDate.of(1970, 1, 1),
            LocalDate.of(2000, 1, 1),
            LocalDate.of(2020, 2, 29), // Leap year
            LocalDate.of(2024, 12, 31),
            LocalDate.now()
        )

        testDates.forEach { originalDate ->
            val timestamp = converter.dateToTimestamp(originalDate)
            val backToDate = converter.fromTimestamp(timestamp)

            assertThat(backToDate).isEqualTo(originalDate)
        }
    }

    @Test
    fun `fromTimestamp handles negative epoch days (dates before 1970)`() {
        // Arrange - 100 days before epoch
        val epochDay = -100L

        // Act
        val result = converter.fromTimestamp(epochDay)

        // Assert
        assertThat(result).isEqualTo(LocalDate.of(1969, 9, 23))
    }
}
