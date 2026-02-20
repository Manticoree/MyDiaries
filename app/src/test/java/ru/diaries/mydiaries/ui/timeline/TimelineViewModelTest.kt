package ru.diaries.mydiaries.ui.timeline

import android.app.Application
import app.cash.turbine.test
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.assertj.core.api.Assertions.assertThat
import ru.diaries.mydiaries.data.model.DiaryEntry
import ru.diaries.mydiaries.data.model.Expense
import ru.diaries.mydiaries.data.model.ExpenseCategory
import ru.diaries.mydiaries.data.repository.DiaryRepository
import ru.diaries.mydiaries.data.repository.ExpenseRepository
import ru.diaries.mydiaries.domain.usecase.timeline.GetTimelineItemsUseCase
import ru.diaries.mydiaries.domain.usecase.timeline.TimelineData
import ru.diaries.mydiaries.feature.food.data.model.FoodEntry
import ru.diaries.mydiaries.feature.food.data.repository.FoodRepository
import ru.diaries.mydiaries.feature.todo.data.model.Task
import ru.diaries.mydiaries.feature.todo.data.repository.TaskRepository
import ru.diaries.mydiaries.feature.track.data.model.DailyTrack
import ru.diaries.mydiaries.feature.track.data.repository.TrackRepository
import ru.diaries.mydiaries.feature.video.data.model.Video
import ru.diaries.mydiaries.feature.video.data.repository.VideoRepository
import ru.diaries.mydiaries.feature.workout.data.model.Workout
import ru.diaries.mydiaries.feature.workout.data.repository.WorkoutRepository
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class TimelineViewModelTest {

    private lateinit var viewModel: TimelineViewModel
    private val application: Application = mockk()
    private val getTimelineItemsUseCase: GetTimelineItemsUseCase = mockk()
    private val diaryRepository: DiaryRepository = mockk()
    private val expenseRepository: ExpenseRepository = mockk()
    private val taskRepository: TaskRepository = mockk()
    private val videoRepository: VideoRepository = mockk()
    private val foodRepository: FoodRepository = mockk()
    private val trackRepository: TrackRepository = mockk()
    private val workoutRepository: WorkoutRepository = mockk()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock StepCounterService and LocationTrackingService static properties
        mockkObject(ru.diaries.mydiaries.service.StepCounterService)
        mockkObject(ru.diaries.mydiaries.service.LocationTrackingService)

        every { ru.diaries.mydiaries.service.StepCounterService.todaySteps } returns MutableStateFlow(0)
        every { ru.diaries.mydiaries.service.StepCounterService.isRunning } returns MutableStateFlow(true) // Already running to prevent start
        every { ru.diaries.mydiaries.service.LocationTrackingService.isTracking } returns MutableStateFlow(false)

        every { application.applicationContext } returns application
        justRun { ru.diaries.mydiaries.service.StepCounterService.start(any()) }

        // Mock empty timeline data
        val emptyTimelineData = TimelineData(
            entries = emptyList(),
            expenses = emptyList(),
            tasks = emptyList(),
            videos = emptyList(),
            foodEntries = emptyList(),
            tracks = emptyList(),
            workouts = emptyList(),
            todayExpenses = emptyList(),
            todayTasks = emptyList(),
            todayVideos = emptyList(),
            todayFoodEntries = emptyList(),
            todayTrack = null,
            todayWorkouts = emptyList()
        )
        coEvery { getTimelineItemsUseCase() } returns flowOf(emptyTimelineData)

        // Mock delete operations
        coEvery { diaryRepository.deleteEntry(any()) } just Runs
        coEvery { expenseRepository.deleteExpense(any()) } just Runs
        coEvery { taskRepository.deleteTask(any()) } just Runs
        coEvery { taskRepository.toggleTaskCompletion(any(), any()) } just Runs

        viewModel = TimelineViewModel(
            application,
            getTimelineItemsUseCase,
            diaryRepository,
            expenseRepository,
            taskRepository,
            videoRepository,
            foodRepository,
            trackRepository,
            workoutRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state has correct default values`() {
        // Assert
        val initialState = viewModel.state.value
        assertThat(initialState.isLoading).isFalse()
        assertThat(initialState.entries).isEmpty()
        assertThat(initialState.groupedEntries).isEmpty()
        assertThat(initialState.searchQuery).isEmpty()
        assertThat(initialState.selectedFilterType).isEqualTo(FilterType.ALL)
        assertThat(initialState.showAddExpenseDialog).isFalse()
        assertThat(initialState.showAddTaskDialog).isFalse()
    }

    @Test
    fun `LoadEntries intent loads timeline data correctly`() = runTest {
        // Arrange
        val timelineData = TimelineData(
            entries = listOf(
                DiaryEntry(id = "1", title = "Entry 1", content = "Content", date = LocalDate.now())
            ),
            expenses = emptyList(),
            tasks = emptyList(),
            videos = emptyList(),
            foodEntries = emptyList(),
            tracks = emptyList(),
            workouts = emptyList(),
            todayExpenses = emptyList(),
            todayTasks = emptyList(),
            todayVideos = emptyList(),
            todayFoodEntries = emptyList(),
            todayTrack = null,
            todayWorkouts = emptyList()
        )

        coEvery { getTimelineItemsUseCase() } returns flowOf(timelineData)

        // Act
        viewModel.handleIntent(TimelineIntent.LoadEntries)

        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.entries).hasSize(1)
            assertThat(state.entries[0].title).isEqualTo("Entry 1")
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `DeleteEntry intent calls diaryRepository deleteEntry`() = runTest {
        // Arrange
        coEvery { diaryRepository.deleteEntry(any()) } just Runs

        // Act
        viewModel.handleIntent(TimelineIntent.DeleteEntry("entry-123"))

        // Assert
        coVerify(exactly = 1) { diaryRepository.deleteEntry("entry-123") }
    }

    @Test
    fun `DeleteExpense intent calls expenseRepository deleteExpense`() = runTest {
        // Arrange
        coEvery { expenseRepository.deleteExpense(any()) } just Runs

        // Act
        viewModel.handleIntent(TimelineIntent.DeleteExpense("expense-123"))

        // Assert
        coVerify(exactly = 1) { expenseRepository.deleteExpense("expense-123") }
    }

    @Test
    fun `ShowAddExpenseDialog updates state correctly`() {
        // Arrange
        val initialState = viewModel.state.value
        assertThat(initialState.showAddExpenseDialog).isFalse()

        // Act
        viewModel.handleIntent(TimelineIntent.ShowAddExpenseDialog)

        // Assert
        val newState = viewModel.state.value
        assertThat(newState.showAddExpenseDialog).isTrue()
    }

    @Test
    fun `HideAddExpenseDialog updates state correctly`() {
        // Arrange
        viewModel.handleIntent(TimelineIntent.ShowAddExpenseDialog)
        assertThat(viewModel.state.value.showAddExpenseDialog).isTrue()

        // Act
        viewModel.handleIntent(TimelineIntent.HideAddExpenseDialog)

        // Assert
        assertThat(viewModel.state.value.showAddExpenseDialog).isFalse()
    }

    @Test
    fun `ShowAddTaskDialog updates state correctly`() {
        // Arrange
        val initialState = viewModel.state.value
        assertThat(initialState.showAddTaskDialog).isFalse()

        // Act
        viewModel.handleIntent(TimelineIntent.ShowAddTaskDialog)

        // Assert
        val newState = viewModel.state.value
        assertThat(newState.showAddTaskDialog).isTrue()
    }

    @Test
    fun `HideAddTaskDialog updates state correctly`() {
        // Arrange
        viewModel.handleIntent(TimelineIntent.ShowAddTaskDialog)
        assertThat(viewModel.state.value.showAddTaskDialog).isTrue()

        // Act
        viewModel.handleIntent(TimelineIntent.HideAddTaskDialog)

        // Assert
        assertThat(viewModel.state.value.showAddTaskDialog).isFalse()
    }

    @Test
    fun `ToggleTaskCompletion calls taskRepository toggleTaskCompletion`() = runTest {
        // Arrange
        coEvery { taskRepository.toggleTaskCompletion(any(), any()) } just Runs

        // Act
        viewModel.handleIntent(TimelineIntent.ToggleTaskCompletion("task-123", true))

        // Assert
        coVerify(exactly = 1) { taskRepository.toggleTaskCompletion("task-123", true) }
    }

    @Test
    fun `DeleteTask intent calls taskRepository deleteTask`() = runTest {
        // Arrange
        coEvery { taskRepository.deleteTask(any()) } just Runs

        // Act
        viewModel.handleIntent(TimelineIntent.DeleteTask("task-123"))

        // Assert
        coVerify(exactly = 1) { taskRepository.deleteTask("task-123") }
    }

    @Test
    fun `UpdateSearchQuery updates search query in state`() {
        // Arrange
        val initialQuery = viewModel.state.value.searchQuery
        assertThat(initialQuery).isEmpty()

        // Act
        viewModel.handleIntent(TimelineIntent.UpdateSearchQuery("test query"))

        // Assert
        val newState = viewModel.state.value
        assertThat(newState.searchQuery).isEqualTo("test query")
    }

    @Test
    fun `SetFilterType updates filter type in state`() {
        // Arrange
        val initialFilter = viewModel.state.value.selectedFilterType
        assertThat(initialFilter).isEqualTo(FilterType.ALL)

        // Act
        viewModel.handleIntent(TimelineIntent.SetFilterType(FilterType.DIARIES))

        // Assert
        val newState = viewModel.state.value
        assertThat(newState.selectedFilterType).isEqualTo(FilterType.DIARIES)
    }

    @Test
    fun `ToggleSearchBar toggles showSearchBar in state`() {
        // Arrange
        val initialState = viewModel.state.value
        val initialShowSearchBar = initialState.showSearchBar

        // Act
        viewModel.handleIntent(TimelineIntent.ToggleSearchBar)

        // Assert
        val newState = viewModel.state.value
        assertThat(newState.showSearchBar).isNotEqualTo(initialShowSearchBar)
    }
}
