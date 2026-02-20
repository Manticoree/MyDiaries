package ru.diaries.mydiaries.data.repository

import app.cash.turbine.test
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.assertj.core.api.Assertions.assertThat
import ru.diaries.mydiaries.data.local.dao.DiaryDao
import ru.diaries.mydiaries.data.local.dao.PhotoDao
import ru.diaries.mydiaries.data.local.entity.DiaryEntryEntity
import ru.diaries.mydiaries.data.local.entity.PhotoEntity
import ru.diaries.mydiaries.data.local.mapper.toDomainList
import ru.diaries.mydiaries.data.local.mapper.toEntity
import ru.diaries.mydiaries.data.model.DiaryEntry
import ru.diaries.mydiaries.data.model.Photo
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryRepositoryTest {

    private lateinit var diaryRepository: DiaryRepository
    private val diaryDao: DiaryDao = mockk()
    private val photoDao: PhotoDao = mockk()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        diaryRepository = RoomDiaryRepository(diaryDao, photoDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getEntries returns list of diary entries with photos`() = runTest {
        // Arrange
        val entryEntities = listOf(
            DiaryEntryEntity(id = "1", title = "Entry 1", content = "Content 1", date = 1000L),
            DiaryEntryEntity(id = "2", title = "Entry 2", content = "Content 2", date = 2000L)
        )
        val photoEntities1 = listOf(
            PhotoEntity(id = "photo1", uri = "uri1", position = 0, entryId = "1")
        )
        val photoEntities2 = emptyList<PhotoEntity>()

        every { diaryDao.getAllEntries() } returns flowOf(entryEntities)
        coEvery { photoDao.getPhotosForEntrySync("1") } returns photoEntities1
        coEvery { photoDao.getPhotosForEntrySync("2") } returns photoEntities2

        // Act & Assert
        diaryRepository.getEntries().test {
            val result = awaitItem()
            assertThat(result).hasSize(2)
            assertThat(result[0].id).isEqualTo("1")
            assertThat(result[0].photos).hasSize(1)
            assertThat(result[1].id).isEqualTo("2")
            assertThat(result[1].photos).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `getEntries returns empty list when no entries exist`() = runTest {
        // Arrange
        every { diaryDao.getAllEntries() } returns flowOf(emptyList())

        // Act & Assert
        diaryRepository.getEntries().test {
            val result = awaitItem()
            assertThat(result).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `getEntry returns diary entry when exists`() = runTest {
        // Arrange
        val entryEntity = DiaryEntryEntity(
            id = "test-id",
            title = "Test Entry",
            content = "Test Content",
            date = 5000L
        )
        val photoEntities = listOf(
            PhotoEntity(id = "photo1", uri = "photo1", position = 0, entryId = "test-id"),
            PhotoEntity(id = "photo2", uri = "photo2", position = 1, entryId = "test-id")
        )

        coEvery { diaryDao.getEntryById("test-id") } returns entryEntity
        coEvery { photoDao.getPhotosForEntrySync("test-id") } returns photoEntities

        // Act
        val result = diaryRepository.getEntry("test-id")

        // Assert
        assertThat(result).isNotNull
        assertThat(result!!.id).isEqualTo("test-id")
        assertThat(result.title).isEqualTo("Test Entry")
        assertThat(result.photos).hasSize(2)
    }

    @Test
    fun `getEntry returns null when entry does not exist`() = runTest {
        // Arrange
        coEvery { diaryDao.getEntryById("non-existent") } returns null

        // Act
        val result = diaryRepository.getEntry("non-existent")

        // Assert
        assertThat(result).isNull()
        coVerify(exactly = 0) { photoDao.getPhotosForEntrySync(any()) }
    }

    @Test
    fun `saveEntry inserts new entry without photos`() = runTest {
        // Arrange
        val entry = DiaryEntry(
            id = "new-id",
            title = "New Entry",
            content = "New Content",
            date = LocalDate.now(),
            photos = emptyList()
        )

        coEvery { diaryDao.insertEntry(any()) } just Runs
        coEvery { photoDao.deletePhotosForEntry("new-id") } just Runs

        // Act
        diaryRepository.saveEntry(entry)

        // Assert
        coVerify(exactly = 1) {
            diaryDao.insertEntry(entry.toEntity())
            photoDao.deletePhotosForEntry("new-id")
        }
        coVerify(exactly = 0) { photoDao.insertPhotos(any()) }
    }

    @Test
    fun `saveEntry inserts entry with photos`() = runTest {
        // Arrange
        val photos = listOf(
            Photo(id = "photo1", uri = "uri1", position = 0),
            Photo(id = "photo2", uri = "uri2", position = 1)
        )
        val entry = DiaryEntry(
            id = "entry-with-photos",
            title = "Entry with Photos",
            content = "Content",
            date = LocalDate.now(),
            photos = photos
        )

        coEvery { diaryDao.insertEntry(any()) } just Runs
        coEvery { photoDao.deletePhotosForEntry(any()) } just Runs
        coEvery { photoDao.insertPhotos(any()) } just Runs

        // Act
        diaryRepository.saveEntry(entry)

        // Assert
        coVerify(exactly = 1) {
            diaryDao.insertEntry(entry.toEntity())
            photoDao.deletePhotosForEntry("entry-with-photos")
            photoDao.insertPhotos(match { it.size == 2 })
        }
    }

    @Test
    fun `saveEntry deletes old photos when updating entry`() = runTest {
        // Arrange
        val entry = DiaryEntry(
            id = "update-id",
            title = "Updated Entry",
            content = "Updated Content",
            date = LocalDate.now(),
            photos = emptyList() // Empty photos means delete old ones
        )

        coEvery { diaryDao.insertEntry(any()) } just Runs
        coEvery { photoDao.deletePhotosForEntry("update-id") } just Runs

        // Act
        diaryRepository.saveEntry(entry)

        // Assert
        coVerify(exactly = 1) { photoDao.deletePhotosForEntry("update-id") }
        coVerify(exactly = 0) { photoDao.insertPhotos(any()) }
    }

    @Test
    fun `deleteEntry calls dao deleteEntry`() = runTest {
        // Arrange
        coEvery { diaryDao.deleteEntry(any()) } just Runs

        // Act
        diaryRepository.deleteEntry("delete-id")

        // Assert
        coVerify(exactly = 1) { diaryDao.deleteEntry("delete-id") }
    }
}
