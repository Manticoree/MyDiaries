package ru.diaries.mydiaries.data.repository

import ru.diaries.mydiaries.data.model.Achievement
import ru.diaries.mydiaries.data.local.entity.AchievementCategory

object AchievementDefinitions {
    fun getAllAchievements(): List<Achievement> = listOf(
        // === DAILY STEPS ACHIEVEMENTS ===
        Achievement(
            id = "daily_1000",
            name = "Первые шаги",
            description = "Сделай 1000 шагов за один день",
            icon = "👣",
            category = AchievementCategory.STEPS,
            requiredSteps = 1000
        ),
        Achievement(
            id = "daily_3000",
            name = "Разминка",
            description = "Сделай 3000 шагов за один день",
            icon = "🚶",
            category = AchievementCategory.STEPS,
            requiredSteps = 3000
        ),
        Achievement(
            id = "daily_5000",
            name = "На верном пути",
            description = "Сделай 5000 шагов за один день",
            icon = "🏃",
            category = AchievementCategory.STEPS,
            requiredSteps = 5000
        ),
        Achievement(
            id = "daily_7000",
            name = "Почти у цели",
            description = "Сделай 7000 шагов за один день",
            icon = "💪",
            category = AchievementCategory.STEPS,
            requiredSteps = 7000
        ),
        Achievement(
            id = "daily_10000",
            name = "Цель достигнута!",
            description = "Сделай 10000 шагов за один день",
            icon = "🎯",
            category = AchievementCategory.STEPS,
            requiredSteps = 10000
        ),
        Achievement(
            id = "daily_12000",
            name = "Неплохо!",
            description = "Сделай 12000 шагов за один день",
            icon = "🌟",
            category = AchievementCategory.STEPS,
            requiredSteps = 12000
        ),
        Achievement(
            id = "daily_15000",
            name = "Марафонец",
            description = "Сделай 15000 шагов за один день",
            icon = "🏆",
            category = AchievementCategory.STEPS,
            requiredSteps = 15000
        ),
        Achievement(
            id = "daily_20000",
            name = "Машина!",
            description = "Сделай 20000 шагов за один день",
            icon = "🚀",
            category = AchievementCategory.STEPS,
            requiredSteps = 20000
        ),
        Achievement(
            id = "daily_25000",
            name = "Невероятно!",
            description = "Сделай 25000 шагов за один день",
            icon = "⭐",
            category = AchievementCategory.STEPS,
            requiredSteps = 25000
        ),
        Achievement(
            id = "daily_30000",
            name = "Легенда",
            description = "Сделай 30000 шагов за один день",
            icon = "👑",
            category = AchievementCategory.STEPS,
            requiredSteps = 30000
        ),

        // === STREAK ACHIEVEMENTS ===
        Achievement(
            id = "streak_3",
            name = "Начало пути",
            description = "Выполняй цель по шагам 3 дня подряд",
            icon = "🔥",
            category = AchievementCategory.STREAK,
            requiredStreak = 3
        ),
        Achievement(
            id = "streak_7",
            name = "Неделя успехов",
            description = "Выполняй цель по шагам 7 дней подряд",
            icon = "🌅",
            category = AchievementCategory.STREAK,
            requiredStreak = 7
        ),
        Achievement(
            id = "streak_14",
            name = "Две недели",
            description = "Выполняй цель по шагам 14 дней подряд",
            icon = "📅",
            category = AchievementCategory.STREAK,
            requiredStreak = 14
        ),
        Achievement(
            id = "streak_21",
            name = "Три недели",
            description = "Выполняй цель по шагам 21 день подряд",
            icon = "🎖️",
            category = AchievementCategory.STREAK,
            requiredStreak = 21
        ),
        Achievement(
            id = "streak_30",
            name = "Целый месяц!",
            description = "Выполняй цель по шагам 30 дней подряд",
            icon = "🏅",
            category = AchievementCategory.STREAK,
            requiredStreak = 30
        ),
        Achievement(
            id = "streak_60",
            name = "Два месяца",
            description = "Выполняй цель по шагам 60 дней подряд",
            icon = "💎",
            category = AchievementCategory.STREAK,
            requiredStreak = 60
        ),
        Achievement(
            id = "streak_90",
            name = "Три месяца",
            description = "Выполняй цель по шагам 90 дней подряд",
            icon = "🌈",
            category = AchievementCategory.STREAK,
            requiredStreak = 90
        ),
        Achievement(
            id = "streak_100",
            name = "Сто дней!",
            description = "Выполняй цель по шагам 100 дней подряд",
            icon = "🎉",
            category = AchievementCategory.STREAK,
            requiredStreak = 100
        ),
        Achievement(
            id = "streak_365",
            name = "Год успеха",
            description = "Выполняй цель по шагам 365 дней подряд",
            icon = "🎊",
            category = AchievementCategory.STREAK,
            requiredStreak = 365
        ),

        // === TOTAL STEPS MILESTONES ===
        Achievement(
            id = "total_10000",
            name = "Десятка",
            description = "Пройди 10000 шагов за всё время",
            icon = "1️⃣",
            category = AchievementCategory.TOTAL,
            requiredSteps = 10000
        ),
        Achievement(
            id = "total_50000",
            name = "Половина сотки",
            description = "Пройди 50000 шагов за всё время",
            icon = "5️⃣",
            category = AchievementCategory.TOTAL,
            requiredSteps = 50000
        ),
        Achievement(
            id = "total_100000",
            name = "Сто тысяч!",
            description = "Пройди 100000 шагов за всё время",
            icon = "🎯",
            category = AchievementCategory.TOTAL,
            requiredSteps = 100000
        ),
        Achievement(
            id = "total_250000",
            name = "Четверть миллиона",
            description = "Пройди 250000 шагов за всё время",
            icon = "🌟",
            category = AchievementCategory.TOTAL,
            requiredSteps = 250000
        ),
        Achievement(
            id = "total_500000",
            name = "Полмиллиона!",
            description = "Пройди 500000 шагов за всё время",
            icon = "💫",
            category = AchievementCategory.TOTAL,
            requiredSteps = 500000
        ),
        Achievement(
            id = "total_750000",
            name = "Подходим к миллиону",
            description = "Пройди 750000 шагов за всё время",
            icon = "✨",
            category = AchievementCategory.TOTAL,
            requiredSteps = 750000
        ),
        Achievement(
            id = "total_1000000",
            name = "Миллион шагов!",
            description = "Пройди 1000000 шагов за всё время",
            icon = "🏆",
            category = AchievementCategory.TOTAL,
            requiredSteps = 1000000
        ),
        Achievement(
            id = "total_2000000",
            name = "Два миллиона",
            description = "Пройди 2000000 шагов за всё время",
            icon = "🚀",
            category = AchievementCategory.TOTAL,
            requiredSteps = 2000000
        ),
        Achievement(
            id = "total_5000000",
            name = "Пять миллионов",
            description = "Пройди 5000000 шагов за всё время",
            icon = "🌌",
            category = AchievementCategory.TOTAL,
            requiredSteps = 5000000
        ),
        Achievement(
            id = "total_10000000",
            name = "Десять миллионов!",
            description = "Пройди 10000000 шагов за всё время",
            icon = "👑",
            category = AchievementCategory.TOTAL,
            requiredSteps = 10000000
        ),

        // === SPECIAL ACHIEVEMENTS ===
        Achievement(
            id = "early_bird",
            name = "Ранняя пташка",
            description = "Сделай 5000 шагов до 9:00 утра",
            icon = "🐦",
            category = AchievementCategory.SPECIAL,
            requiredSteps = 5000
        ),
        Achievement(
            id = "night_owl",
            name = "Сова",
            description = "Сделай 5000 шагов после 22:00",
            icon = "🦉",
            category = AchievementCategory.SPECIAL,
            requiredSteps = 5000
        ),
        Achievement(
            id = "weekend_warrior",
            name = "Воин выходного дня",
            description = "Сделай 15000 шагов в выходной",
            icon = "⚔️",
            category = AchievementCategory.SPECIAL,
            requiredSteps = 15000
        ),
        Achievement(
            id = "perfect_day",
            name = "Идеальный день",
            description = "Сделай 20000 шагов за один день",
            icon = "💎",
            category = AchievementCategory.SPECIAL,
            requiredSteps = 20000
        ),
        Achievement(
            id = "consistent_walker",
            name = "Постоянный ходок",
            description = "Достигай 10000 шагов 5 дней подряд",
            icon = "🔄",
            category = AchievementCategory.SPECIAL,
            requiredStreak = 5
        ),
        Achievement(
            id = "dedication_master",
            name = "Мастер преданности",
            description = "Достигай 10000 шагов 10 дней подряд",
            icon = "🎯",
            category = AchievementCategory.SPECIAL,
            requiredStreak = 10
        ),
        Achievement(
            id = "step_master",
            name = "Мастер шагов",
            description = "Достигай 15000 шагов 7 дней подряд",
            icon = "🏅",
            category = AchievementCategory.SPECIAL,
            requiredStreak = 7
        ),
        Achievement(
            id = "ultimate_walker",
            name = "Верховный ходок",
            description = "Достигай 20000 шагов 5 дней подряд",
            icon = "👑",
            category = AchievementCategory.SPECIAL,
            requiredStreak = 5
        ),

        // === WEEKLY ACHIEVEMENTS ===
        Achievement(
            id = "weekly_50000",
            name = "Начало недели",
            description = "Пройди 50000 шагов за неделю",
            icon = "📆",
            category = AchievementCategory.WEEKLY,
            requiredSteps = 50000
        ),
        Achievement(
            id = "weekly_70000",
            name = "Хорошая неделя",
            description = "Пройди 70000 шагов за неделю",
            icon = "📅",
            category = AchievementCategory.WEEKLY,
            requiredSteps = 70000
        ),
        Achievement(
            id = "weekly_100000",
            name = "Отличная неделя!",
            description = "Пройди 100000 шагов за неделю",
            icon = "🌟",
            category = AchievementCategory.WEEKLY,
            requiredSteps = 100000
        ),

        // === MONTHLY ACHIEVEMENTS ===
        Achievement(
            id = "monthly_200000",
            name = "Хороший месяц",
            description = "Пройди 200000 шагов за месяц",
            icon = "📊",
            category = AchievementCategory.MONTHLY,
            requiredSteps = 200000
        ),
        Achievement(
            id = "monthly_300000",
            name = "Отличный месяц",
            description = "Пройди 300000 шагов за месяц",
            icon = "📈",
            category = AchievementCategory.MONTHLY,
            requiredSteps = 300000
        ),
        Achievement(
            id = "monthly_500000",
            name = "Превосходный месяц!",
            description = "Пройди 500000 шагов за месяц",
            icon = "🏆",
            category = AchievementCategory.MONTHLY,
            requiredSteps = 500000
        ),

        // === MILESTONE ACHIEVEMENTS ===
        Achievement(
            id = "first_achievement",
            name = "Первое достижение",
            description = "Разблокируй своё первое достижение",
            icon = "🎖️",
            category = AchievementCategory.MILESTONE,
            requiredSteps = 0
        ),
        Achievement(
            id = "five_achievements",
            name = "Пять достижений",
            description = "Разблокируй 5 достижений",
            icon = "⭐",
            category = AchievementCategory.MILESTONE,
            requiredSteps = 0
        ),
        Achievement(
            id = "ten_achievements",
            name = "Десять достижений",
            description = "Разблокируй 10 достижений",
            icon = "🌟",
            category = AchievementCategory.MILESTONE,
            requiredSteps = 0
        ),
        Achievement(
            id = "twenty_achievements",
            name = "Двадцать достижений",
            description = "Разблокируй 20 достижений",
            icon = "✨",
            category = AchievementCategory.MILESTONE,
            requiredSteps = 0
        ),
        Achievement(
            id = "thirty_achievements",
            name = "Тридцать достижений",
            description = "Разблокируй 30 достижений",
            icon = "💫",
            category = AchievementCategory.MILESTONE,
            requiredSteps = 0
        ),
        Achievement(
            id = "fifty_achievements",
            name = "Полсотни достижений!",
            description = "Разблокируй 50 достижений",
            icon = "🎊",
            category = AchievementCategory.MILESTONE,
            requiredSteps = 0
        ),
        Achievement(
            id = "hundred_achievements",
            name = "Сто достижений!",
            description = "Разблокируй 100 достижений",
            icon = "🎉",
            category = AchievementCategory.MILESTONE,
            requiredSteps = 0
        ),

        // === DISTANCE-BASED ACHIEVEMENTS ===
        Achievement(
            id = "distance_5km",
            name = "Пятерка",
            description = "Пройди 5 км за один день",
            icon = "🏃",
            category = AchievementCategory.DAILY,
            requiredSteps = 6000
        ),
        Achievement(
            id = "distance_10km",
            name = "Десятка",
            description = "Пройди 10 км за один день",
            icon = "🏅",
            category = AchievementCategory.DAILY,
            requiredSteps = 12000
        ),
        Achievement(
            id = "distance_15km",
            name = "Половина марафона",
            description = "Пройди 15 км за один день",
            icon = "🎖️",
            category = AchievementCategory.DAILY,
            requiredSteps = 18000
        ),
        Achievement(
            id = "distance_20km",
            name = "Длинная дистанция",
            description = "Пройди 20 км за один день",
            icon = "🌟",
            category = AchievementCategory.DAILY,
            requiredSteps = 24000
        ),
        Achievement(
            id = "distance_42km",
            name = "Марафонец!",
            description = "Пройди марафон (42 км) за один день",
            icon = "🏆",
            category = AchievementCategory.DAILY,
            requiredSteps = 50000
        ),

        // === CALORIES-BASED (Approximation) ===
        Achievement(
            id = "active_day_1",
            name = "Активный день",
            description = "Сожги 200 калорий ходьбой",
            icon = "🔥",
            category = AchievementCategory.DAILY,
            requiredSteps = 4000
        ),
        Achievement(
            id = "active_day_2",
            name = "Очень активный день",
            description = "Сожги 400 калорий ходьбой",
            icon = "🔥🔥",
            category = AchievementCategory.DAILY,
            requiredSteps = 8000
        ),
        Achievement(
            id = "active_day_3",
            name = "Супер активный день",
            description = "Сожги 600 калорий ходьбой",
            icon = "🔥🔥🔥",
            category = AchievementCategory.DAILY,
            requiredSteps = 12000
        )
    )
}
