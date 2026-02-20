package ru.diaries.mydiaries.data.local.mapper

import org.junit.Test
import org.assertj.core.api.Assertions.assertThat
import ru.diaries.mydiaries.data.local.entity.DiaryEntryEntity
import ru.diaries.mydiaries.data.model.DiaryEntry
import ru.diaries.mydiaries.data.model.Photo
import java.time.LocalDate

class DiaryEntryMapperTest {

    @Test
    fun `toDomain converts DiaryEntryEntity to DiaryEntry correctly`() {
        // Arrange
        val entity = DiaryEntryEntity(
            id = "1",
            title = "Test Title",
            content = "Test Content",
            date = LocalDate.of(2024, 1, 15).toEpochDay()
        )
        val photos = listOf(
            Photo(id = "1", uri = "content://photo1", position = 0),
            Photo(id = "2", uri = "content://photo2", position = 1)
        )

        // Act
        val domain = entity.toDomain(photos)

        // Assert
        assertThat(domain.id).isEqualTo("1")
        assertThat(domain.title).isEqualTo("Test Title")
        assertThat(domain.content).isEqualTo("Test Content")
        assertThat(domain.date).isEqualTo(LocalDate.of(2024, 1, 15))
        assertThat(domain.photos).hasSize(2)
        assertThat(domain.photos[0].uri).isEqualTo("content://photo1")
    }

    @Test
    fun `toDomain with empty photos returns DiaryEntry with empty photos list`() {
        // Arrange
        val entity = DiaryEntryEntity(
            id = "1",
            title = "Test",
            content = "Content",
            date = LocalDate.now().toEpochDay()
        )

        // Act
        val domain = entity.toDomain()

        // Assert
        assertThat(domain.photos).isEmpty()
    }

    @Test
    fun `toEntity converts DiaryEntry to DiaryEntryEntity correctly`() {
        // Arrange
        val domain = DiaryEntry(
            id = "1",
            title = "Test Title",
            content = "Test Content",
            date = LocalDate.of(2024, 12, 25),
            photos = emptyList()
        )

        // Act
        val entity = domain.toEntity()

        // Assert
        assertThat(entity.id).isEqualTo("1")
        assertThat(entity.title).isEqualTo("Test Title")
        assertThat(entity.content).isEqualTo("Test Content")
        assertThat(entity.date).isEqualTo(LocalDate.of(2024, 12, 25).toEpochDay())
    }

    @Test
    fun `toEntity handles date conversion for epoch day 0`() {
        // Arrange
        val domain = DiaryEntry(
            id = "0",
            title = "Epoch Test",
            content = "Testing epoch day 0",
            date = LocalDate.ofEpochDay(0),
            photos = emptyList()
        )

        // Act
        val entity = domain.toEntity()

        // Assert
        assertThat(entity.date).isEqualTo(0L)
        assertThat(entity.date).isEqualTo(LocalDate.of(1970, 1, 1).toEpochDay())
    }

    @Test
    fun `toDomainList converts list of DiaryEntryEntity correctly`() {
        // Arrange
        val entities = listOf(
            DiaryEntryEntity(id = "1", title = "First", content = "Content 1", date = 1000L),
            DiaryEntryEntity(id = "2", title = "Second", content = "Content 2", date = 2000L),
            DiaryEntryEntity(id = "3", title = "Third", content = "Content 3", date = 3000L)
        )

        // Act
        val result = entities.toDomainList()

        // Assert
        assertThat(result).hasSize(3)
        assertThat(result[0].title).isEqualTo("First")
        assertThat(result[1].title).isEqualTo("Second")
        assertThat(result[2].title).isEqualTo("Third")
    }

    @Test
    fun `toDomainList handles empty list`() {
        // Arrange
        val entities = emptyList<DiaryEntryEntity>()

        // Act
        val result = entities.toDomainList()

        // Assert
        assertThat(result).isEmpty()
    }
}
