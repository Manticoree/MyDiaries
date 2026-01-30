package ru.diaries.mydiaries.feature.food.ml

import android.util.Log

/**
 * Database mapping food labels to approximate calories per 100g.
 * Supports both Food-101 labels and Google AIY Vision Food model labels.
 */
object CalorieDatabase {

    /**
     * Get calories per 100g for a food label.
     * Returns estimated value based on food type if exact match not found.
     */
    fun getCaloriesPer100g(label: String): Int {
        val normalizedLabel = label.lowercase().trim()

        // Check exact match first
        calorieMap[normalizedLabel]?.let { return it }

        // Check partial matches
        for ((key, calories) in calorieMap) {
            if (normalizedLabel.contains(key) || key.contains(normalizedLabel)) {
                return calories
            }
        }

        // Estimate based on food category keywords
        return estimateCaloriesByCategory(normalizedLabel)
    }

    /**
     * Get display name for a food label (Russian).
     */
    fun getDisplayName(label: String): String {
        val normalizedLabel = label.lowercase().trim()
        Log.d("CalorieDatabase", "Looking up: '$normalizedLabel'")

        // Check exact match
        displayNames[normalizedLabel]?.let {
            Log.d("CalorieDatabase", "Found exact match: $it")
            return it
        }

        // Try with underscores replaced by spaces
        val withSpaces = normalizedLabel.replace("_", " ")
        displayNames[withSpaces]?.let {
            Log.d("CalorieDatabase", "Found with spaces: $it")
            return it
        }

        // Try with spaces replaced by underscores
        val withUnderscores = normalizedLabel.replace(" ", "_")
        displayNames[withUnderscores]?.let {
            Log.d("CalorieDatabase", "Found with underscores: $it")
            return it
        }

        // Check partial matches (both directions)
        for ((key, name) in displayNames) {
            if (normalizedLabel.contains(key) || key.contains(normalizedLabel)) {
                Log.d("CalorieDatabase", "Found partial match: $key -> $name")
                return name
            }
            // Also check with underscore/space variants
            if (withSpaces.contains(key) || key.contains(withSpaces)) {
                Log.d("CalorieDatabase", "Found partial (spaces): $key -> $name")
                return name
            }
            if (withUnderscores.contains(key) || key.contains(withUnderscores)) {
                Log.d("CalorieDatabase", "Found partial (underscores): $key -> $name")
                return name
            }
        }

        // Format label as display name (capitalize words)
        val formatted = formatLabelForDisplay(label)
        Log.d("CalorieDatabase", "No match found, formatting: $formatted")
        return formatted
    }

    private fun estimateCaloriesByCategory(label: String): Int {
        return when {
            // High calorie foods
            label.contains("cake") || label.contains("pie") || label.contains("торт") -> 350
            label.contains("cookie") || label.contains("печенье") -> 450
            label.contains("chocolate") || label.contains("шоколад") -> 540
            label.contains("donut") || label.contains("пончик") -> 420
            label.contains("ice cream") || label.contains("мороженое") -> 210
            label.contains("candy") || label.contains("конфет") -> 400
            label.contains("cheese") || label.contains("сыр") -> 350
            label.contains("butter") || label.contains("масло") -> 720
            label.contains("fried") || label.contains("жарен") -> 280
            label.contains("chips") || label.contains("чипсы") -> 540
            label.contains("nuts") || label.contains("орех") -> 600

            // Medium-high calorie
            label.contains("bread") || label.contains("хлеб") -> 265
            label.contains("pasta") || label.contains("макарон") || label.contains("спагетти") -> 160
            label.contains("rice") || label.contains("рис") -> 130
            label.contains("pizza") || label.contains("пицца") -> 270
            label.contains("burger") || label.contains("бургер") || label.contains("гамбургер") -> 260
            label.contains("sandwich") || label.contains("сэндвич") || label.contains("бутерброд") -> 250
            label.contains("meat") || label.contains("мясо") -> 250
            label.contains("beef") || label.contains("говядин") -> 250
            label.contains("pork") || label.contains("свинин") -> 240
            label.contains("chicken") || label.contains("курин") || label.contains("курица") -> 190
            label.contains("steak") || label.contains("стейк") -> 270
            label.contains("sausage") || label.contains("колбас") || label.contains("сосиск") -> 300

            // Medium calorie
            label.contains("fish") || label.contains("рыб") -> 140
            label.contains("salmon") || label.contains("лосось") || label.contains("сёмга") -> 180
            label.contains("shrimp") || label.contains("креветк") -> 100
            label.contains("egg") || label.contains("яйц") || label.contains("яичн") -> 150
            label.contains("soup") || label.contains("суп") -> 60
            label.contains("noodle") || label.contains("лапша") -> 140
            label.contains("dumpling") || label.contains("пельмен") || label.contains("вареник") -> 190
            label.contains("sushi") || label.contains("суши") || label.contains("ролл") -> 150
            label.contains("ramen") || label.contains("рамен") -> 90

            // Low-medium calorie
            label.contains("salad") || label.contains("салат") -> 100
            label.contains("vegetable") || label.contains("овощ") -> 50
            label.contains("fruit") || label.contains("фрукт") -> 55
            label.contains("apple") || label.contains("яблок") -> 52
            label.contains("banana") || label.contains("банан") -> 90
            label.contains("orange") || label.contains("апельсин") -> 45
            label.contains("berry") || label.contains("ягод") -> 45
            label.contains("tomato") || label.contains("помидор") -> 20
            label.contains("potato") || label.contains("картоф") -> 80
            label.contains("carrot") || label.contains("морков") -> 40

            // Drinks
            label.contains("juice") || label.contains("сок") -> 45
            label.contains("milk") || label.contains("молок") -> 60
            label.contains("coffee") || label.contains("кофе") -> 2
            label.contains("tea") || label.contains("чай") -> 1
            label.contains("smoothie") || label.contains("смузи") -> 70
            label.contains("beer") || label.contains("пиво") -> 45
            label.contains("wine") || label.contains("вино") -> 85

            // Default
            else -> 150
        }
    }

    private fun formatLabelForDisplay(label: String): String {
        return label.replace("_", " ")
            .replace("-", " ")
            .split(" ")
            .joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercase() }
            }
    }

    /**
     * Calorie values per 100g for common foods.
     * Includes all 101 Food-101 classes with underscore format.
     */
    private val calorieMap: Map<String, Int> = mapOf(
        // ===== Food-101 classes (underscore format) =====
        "apple_pie" to 237,
        "baby_back_ribs" to 292,
        "baklava" to 428,
        "beef_carpaccio" to 170,
        "beef_tartare" to 180,
        "beet_salad" to 90,
        "beignets" to 350,
        "bibimbap" to 170,
        "bread_pudding" to 240,
        "breakfast_burrito" to 200,
        "bruschetta" to 150,
        "caesar_salad" to 127,
        "cannoli" to 369,
        "caprese_salad" to 160,
        "carrot_cake" to 326,
        "ceviche" to 84,
        "cheesecake" to 321,
        "cheese_plate" to 350,
        "chicken_curry" to 160,
        "chicken_quesadilla" to 220,
        "chicken_wings" to 203,
        "chocolate_cake" to 367,
        "chocolate_mousse" to 195,
        "churros" to 364,
        "clam_chowder" to 87,
        "club_sandwich" to 234,
        "crab_cakes" to 200,
        "creme_brulee" to 276,
        "croque_madame" to 320,
        "cup_cakes" to 305,
        "deviled_eggs" to 200,
        "donuts" to 421,
        "dumplings" to 189,
        "edamame" to 120,
        "eggs_benedict" to 254,
        "escargots" to 90,
        "falafel" to 333,
        "filet_mignon" to 280,
        "fish_and_chips" to 247,
        "foie_gras" to 460,
        "french_fries" to 312,
        "french_onion_soup" to 51,
        "french_toast" to 229,
        "fried_calamari" to 175,
        "fried_rice" to 163,
        "frozen_yogurt" to 159,
        "garlic_bread" to 350,
        "gnocchi" to 133,
        "greek_salad" to 99,
        "grilled_cheese_sandwich" to 349,
        "grilled_salmon" to 182,
        "guacamole" to 157,
        "gyoza" to 210,
        "hamburger" to 264,
        "hot_and_sour_soup" to 45,
        "hot_dog" to 290,
        "huevos_rancheros" to 170,
        "hummus" to 166,
        "ice_cream" to 207,
        "lasagna" to 135,
        "lobster_bisque" to 89,
        "lobster_roll_sandwich" to 230,
        "macaroni_and_cheese" to 164,
        "macarons" to 393,
        "miso_soup" to 40,
        "mussels" to 86,
        "nachos" to 306,
        "omelette" to 154,
        "onion_rings" to 350,
        "oysters" to 69,
        "pad_thai" to 165,
        "paella" to 149,
        "pancakes" to 227,
        "panna_cotta" to 232,
        "peking_duck" to 250,
        "pho" to 52,
        "pizza" to 266,
        "pork_chop" to 231,
        "poutine" to 290,
        "prime_rib" to 280,
        "pulled_pork_sandwich" to 230,
        "ramen" to 89,
        "ravioli" to 176,
        "red_velvet_cake" to 367,
        "risotto" to 166,
        "samosa" to 308,
        "sashimi" to 110,
        "scallops" to 111,
        "seaweed_salad" to 70,
        "shrimp_and_grits" to 220,
        "spaghetti_bolognese" to 160,
        "spaghetti_carbonara" to 180,
        "spring_rolls" to 165,
        "steak" to 271,
        "strawberry_shortcake" to 250,
        "sushi" to 150,
        "tacos" to 226,
        "takoyaki" to 180,
        "tiramisu" to 283,
        "tuna_tartare" to 130,
        "waffles" to 291,

        // ===== Additional common foods (space format for compatibility) =====
        "cheeseburger" to 280,
        "fried chicken" to 250,
        "chicken nuggets" to 280,
        "grilled chicken" to 165,
        "roast chicken" to 190,
        "noodles" to 140,
        "pasta" to 160,
        "spaghetti" to 160,
        "taco" to 226,
        "quesadilla" to 213,
        "enchilada" to 170,
        "fajita" to 150,
        "curry" to 145,
        "tikka masala" to 160,
        "biryani" to 180,
        "egg rolls" to 190,
        "kebab" to 180,
        "shawarma" to 190,

        // Soups
        "soup" to 60,
        "chicken soup" to 55,
        "tomato soup" to 45,
        "pumpkin soup" to 50,
        "mushroom soup" to 55,
        "onion soup" to 51,
        "borscht" to 45,

        // Salads
        "salad" to 100,
        "caprese" to 160,
        "coleslaw" to 80,
        "potato salad" to 140,

        // Breakfast
        "scrambled eggs" to 150,
        "fried eggs" to 200,
        "bacon" to 540,
        "sausage" to 300,
        "toast" to 265,
        "cereal" to 370,
        "oatmeal" to 70,
        "granola" to 470,
        "yogurt" to 100,
        "croissant" to 406,
        "bagel" to 250,
        "muffin" to 340,

        // Sandwiches
        "sandwich" to 250,
        "grilled cheese" to 349,
        "blt" to 280,
        "pb&j" to 350,
        "wrap" to 200,
        "panini" to 280,

        // Seafood
        "fish" to 140,
        "salmon" to 182,
        "tuna" to 130,
        "shrimp" to 100,
        "lobster" to 90,
        "crab" to 90,
        "calamari" to 175,

        // Meat
        "beef" to 250,
        "pork" to 240,
        "lamb" to 280,
        "ribs" to 292,
        "meatballs" to 210,
        "meatloaf" to 180,
        "roast beef" to 180,
        "pulled pork" to 195,

        // Desserts
        "cake" to 350,
        "cupcake" to 305,
        "brownie" to 400,
        "cookie" to 450,
        "pie" to 250,
        "gelato" to 180,
        "pudding" to 150,
        "mousse" to 195,
        "donut" to 421,
        "doughnut" to 421,
        "eclair" to 260,
        "macaron" to 393,
        "waffle" to 291,
        "red velvet" to 367,

        // Snacks
        "chips" to 540,
        "popcorn" to 380,
        "pretzels" to 380,
        "crackers" to 420,
        "nuts" to 600,
        "almonds" to 575,
        "peanuts" to 570,
        "trail mix" to 480,

        // Bread & Baked
        "bread" to 265,
        "baguette" to 270,
        "focaccia" to 280,
        "naan" to 290,
        "pita" to 275,
        "tortilla" to 300,
        "pizza dough" to 250,

        // Fruits
        "apple" to 52,
        "banana" to 90,
        "orange" to 45,
        "strawberry" to 32,
        "blueberry" to 57,
        "grape" to 70,
        "watermelon" to 30,
        "mango" to 60,
        "pineapple" to 50,
        "peach" to 39,
        "pear" to 57,
        "kiwi" to 61,
        "avocado" to 160,

        // Vegetables
        "broccoli" to 35,
        "carrot" to 40,
        "tomato" to 20,
        "cucumber" to 15,
        "lettuce" to 15,
        "spinach" to 23,
        "corn" to 85,
        "potato" to 80,
        "sweet potato" to 86,
        "onion" to 40,
        "mushroom" to 22,
        "pepper" to 25,
        "zucchini" to 17,
        "eggplant" to 25,
        "asparagus" to 20,
        "green beans" to 30,

        // Dairy
        "cheese" to 350,
        "milk" to 60,
        "butter" to 720,
        "cream" to 340,
        "sour cream" to 200,

        // Condiments & Sauces
        "ketchup" to 100,
        "mayonnaise" to 680,
        "mustard" to 65,
        "salsa" to 35,
        "pesto" to 450,

        // Drinks (per 100ml)
        "coffee" to 2,
        "tea" to 1,
        "juice" to 45,
        "orange juice" to 45,
        "apple juice" to 46,
        "smoothie" to 70,
        "milkshake" to 150,
        "soda" to 40,
        "cola" to 42,
        "beer" to 45,
        "wine" to 85,
        "cocktail" to 150
    )

    /**
     * Display names in Russian for food labels.
     * Includes all 101 Food-101 classes with underscore format.
     */
    private val displayNames: Map<String, String> = mapOf(
        // ===== Food-101 classes (underscore format) =====
        "apple_pie" to "Яблочный пирог",
        "baby_back_ribs" to "Свиные рёбрышки",
        "baklava" to "Пахлава",
        "beef_carpaccio" to "Карпаччо из говядины",
        "beef_tartare" to "Тартар из говядины",
        "beet_salad" to "Свекольный салат",
        "beignets" to "Бенье",
        "bibimbap" to "Пибимпап",
        "bread_pudding" to "Хлебный пудинг",
        "breakfast_burrito" to "Буррито на завтрак",
        "bruschetta" to "Брускетта",
        "caesar_salad" to "Салат Цезарь",
        "cannoli" to "Канноли",
        "caprese_salad" to "Капрезе",
        "carrot_cake" to "Морковный торт",
        "ceviche" to "Севиче",
        "cheesecake" to "Чизкейк",
        "cheese_plate" to "Сырная тарелка",
        "chicken_curry" to "Куриное карри",
        "chicken_quesadilla" to "Куриная кесадилья",
        "chicken_wings" to "Куриные крылышки",
        "chocolate_cake" to "Шоколадный торт",
        "chocolate_mousse" to "Шоколадный мусс",
        "churros" to "Чуррос",
        "clam_chowder" to "Похлёбка из моллюсков",
        "club_sandwich" to "Клаб-сэндвич",
        "crab_cakes" to "Крабовые котлеты",
        "creme_brulee" to "Крем-брюле",
        "croque_madame" to "Крок-мадам",
        "cup_cakes" to "Капкейки",
        "deviled_eggs" to "Фаршированные яйца",
        "donuts" to "Пончики",
        "dumplings" to "Пельмени",
        "edamame" to "Эдамаме",
        "eggs_benedict" to "Яйца Бенедикт",
        "escargots" to "Эскарго (улитки)",
        "falafel" to "Фалафель",
        "filet_mignon" to "Филе-миньон",
        "fish_and_chips" to "Рыба с картошкой",
        "foie_gras" to "Фуа-гра",
        "french_fries" to "Картофель фри",
        "french_onion_soup" to "Луковый суп",
        "french_toast" to "Французские тосты",
        "fried_calamari" to "Жареные кальмары",
        "fried_rice" to "Жареный рис",
        "frozen_yogurt" to "Замороженный йогурт",
        "garlic_bread" to "Чесночный хлеб",
        "gnocchi" to "Ньокки",
        "greek_salad" to "Греческий салат",
        "grilled_cheese_sandwich" to "Горячий сэндвич с сыром",
        "grilled_salmon" to "Лосось на гриле",
        "guacamole" to "Гуакамоле",
        "gyoza" to "Гёдза",
        "hamburger" to "Гамбургер",
        "hot_and_sour_soup" to "Кисло-острый суп",
        "hot_dog" to "Хот-дог",
        "huevos_rancheros" to "Уэвос ранчерос",
        "hummus" to "Хумус",
        "ice_cream" to "Мороженое",
        "lasagna" to "Лазанья",
        "lobster_bisque" to "Биск из лобстера",
        "lobster_roll_sandwich" to "Лобстер-ролл",
        "macaroni_and_cheese" to "Макароны с сыром",
        "macarons" to "Макаронс",
        "miso_soup" to "Мисо-суп",
        "mussels" to "Мидии",
        "nachos" to "Начос",
        "omelette" to "Омлет",
        "onion_rings" to "Луковые кольца",
        "oysters" to "Устрицы",
        "pad_thai" to "Пад тай",
        "paella" to "Паэлья",
        "pancakes" to "Блинчики",
        "panna_cotta" to "Панна-котта",
        "peking_duck" to "Утка по-пекински",
        "pho" to "Фо",
        "pizza" to "Пицца",
        "pork_chop" to "Свиная отбивная",
        "poutine" to "Путин",
        "prime_rib" to "Прайм риб",
        "pulled_pork_sandwich" to "Сэндвич со свининой",
        "ramen" to "Рамен",
        "ravioli" to "Равиоли",
        "red_velvet_cake" to "Красный бархат",
        "risotto" to "Ризотто",
        "samosa" to "Самоса",
        "sashimi" to "Сашими",
        "scallops" to "Гребешки",
        "seaweed_salad" to "Салат из водорослей",
        "shrimp_and_grits" to "Креветки с кашей",
        "spaghetti_bolognese" to "Спагетти болоньезе",
        "spaghetti_carbonara" to "Спагетти карбонара",
        "spring_rolls" to "Спринг-роллы",
        "steak" to "Стейк",
        "strawberry_shortcake" to "Клубничный торт",
        "sushi" to "Суши",
        "tacos" to "Тако",
        "takoyaki" to "Такояки",
        "tiramisu" to "Тирамису",
        "tuna_tartare" to "Тартар из тунца",
        "waffles" to "Вафли",

        // ===== Additional common foods (space format for compatibility) =====
        "cheeseburger" to "Чизбургер",
        "fried chicken" to "Жареная курица",
        "chicken nuggets" to "Куриные наггетсы",
        "grilled chicken" to "Курица на гриле",
        "roast chicken" to "Жареная курица",
        "noodles" to "Лапша",
        "pasta" to "Паста",
        "spaghetti" to "Спагетти",
        "taco" to "Тако",
        "quesadilla" to "Кесадилья",
        "burrito" to "Буррито",
        "enchilada" to "Энчилада",
        "fajita" to "Фахитас",
        "curry" to "Карри",
        "tikka masala" to "Тикка масала",
        "biryani" to "Бирьяни",
        "egg rolls" to "Яичные роллы",
        "kebab" to "Кебаб",
        "shawarma" to "Шаурма",

        // Soups
        "soup" to "Суп",
        "chicken soup" to "Куриный суп",
        "tomato soup" to "Томатный суп",
        "pumpkin soup" to "Тыквенный суп",
        "mushroom soup" to "Грибной суп",
        "onion soup" to "Луковый суп",
        "borscht" to "Борщ",

        // Salads
        "salad" to "Салат",
        "caprese" to "Капрезе",
        "coleslaw" to "Коулслоу",
        "potato salad" to "Картофельный салат",

        // Breakfast
        "scrambled eggs" to "Яичница-болтунья",
        "fried eggs" to "Яичница",
        "bacon" to "Бекон",
        "sausage" to "Сосиски",
        "toast" to "Тост",
        "cereal" to "Хлопья",
        "oatmeal" to "Овсянка",
        "granola" to "Гранола",
        "yogurt" to "Йогурт",
        "croissant" to "Круассан",
        "bagel" to "Бейгл",
        "muffin" to "Маффин",

        // Sandwiches
        "sandwich" to "Сэндвич",
        "grilled cheese" to "Горячий сэндвич с сыром",
        "blt" to "Сэндвич BLT",
        "pb&j" to "Сэндвич с арахисовым маслом",
        "wrap" to "Ролл",
        "panini" to "Панини",

        // Seafood
        "fish" to "Рыба",
        "salmon" to "Лосось",
        "tuna" to "Тунец",
        "shrimp" to "Креветки",
        "lobster" to "Лобстер",
        "crab" to "Краб",
        "calamari" to "Кальмары",

        // Meat
        "beef" to "Говядина",
        "pork" to "Свинина",
        "lamb" to "Баранина",
        "ribs" to "Рёбрышки",
        "meatballs" to "Фрикадельки",
        "meatloaf" to "Мясной рулет",
        "roast beef" to "Ростбиф",
        "pulled pork" to "Рваная свинина",

        // Desserts
        "cake" to "Торт",
        "cupcake" to "Капкейк",
        "brownie" to "Брауни",
        "cookie" to "Печенье",
        "cookies" to "Печенье",
        "pie" to "Пирог",
        "gelato" to "Джелато",
        "pudding" to "Пудинг",
        "mousse" to "Мусс",
        "donut" to "Пончик",
        "doughnut" to "Пончик",
        "eclair" to "Эклер",
        "macaron" to "Макарон",
        "waffle" to "Вафли",
        "red velvet" to "Красный бархат",

        // Snacks
        "chips" to "Чипсы",
        "popcorn" to "Попкорн",
        "pretzels" to "Крендельки",
        "crackers" to "Крекеры",
        "nuts" to "Орехи",
        "almonds" to "Миндаль",
        "peanuts" to "Арахис",
        "trail mix" to "Смесь орехов",

        // Bread
        "bread" to "Хлеб",
        "baguette" to "Багет",
        "focaccia" to "Фокачча",
        "naan" to "Наан",
        "pita" to "Пита",
        "tortilla" to "Тортилья",

        // Fruits
        "apple" to "Яблоко",
        "banana" to "Банан",
        "orange" to "Апельсин",
        "strawberry" to "Клубника",
        "blueberry" to "Черника",
        "grape" to "Виноград",
        "grapes" to "Виноград",
        "watermelon" to "Арбуз",
        "mango" to "Манго",
        "pineapple" to "Ананас",
        "peach" to "Персик",
        "pear" to "Груша",
        "kiwi" to "Киви",
        "avocado" to "Авокадо",

        // Vegetables
        "broccoli" to "Брокколи",
        "carrot" to "Морковь",
        "tomato" to "Помидор",
        "cucumber" to "Огурец",
        "lettuce" to "Салат",
        "spinach" to "Шпинат",
        "corn" to "Кукуруза",
        "potato" to "Картофель",
        "sweet potato" to "Батат",
        "onion" to "Лук",
        "mushroom" to "Грибы",
        "pepper" to "Перец",
        "zucchini" to "Кабачок",
        "eggplant" to "Баклажан",
        "asparagus" to "Спаржа",
        "green beans" to "Стручковая фасоль",

        // Dairy
        "cheese" to "Сыр",
        "milk" to "Молоко",
        "butter" to "Масло",
        "cream" to "Сливки",
        "sour cream" to "Сметана",

        // Condiments
        "ketchup" to "Кетчуп",
        "mayonnaise" to "Майонез",
        "mustard" to "Горчица",
        "salsa" to "Сальса",
        "pesto" to "Песто",

        // Drinks
        "coffee" to "Кофе",
        "tea" to "Чай",
        "juice" to "Сок",
        "orange juice" to "Апельсиновый сок",
        "apple juice" to "Яблочный сок",
        "smoothie" to "Смузи",
        "milkshake" to "Милкшейк",
        "soda" to "Газировка",
        "cola" to "Кола",
        "beer" to "Пиво",
        "wine" to "Вино",
        "cocktail" to "Коктейль"
    )
}
