package com.example.myapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "food_app.db";
    private static final int DATABASE_VERSION = 7;

    public static final String TABLE_USERS = "users";
    public static final String TABLE_USER_PREFERENCES = "user_preferences";
    public static final String TABLE_RESTAURANTS = "restaurants";
    public static final String TABLE_RESTAURANT_MENU = "restaurant_menu";
    public static final String TABLE_RESTAURANT_REVIEWS = "restaurant_reviews";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String TABLE_USER_QUERIES = "user_queries";
    public static final String TABLE_USER_HISTORY = "user_history";
    public static final String TABLE_FAVORITE_RESTAURANTS = "favorite_restaurants";
    public static final String TABLE_FAVORITE_RECIPES = "favorite_recipes";
    public static final String TABLE_DICTIONARIES = "dictionaries";
    public static final String TABLE_APP_LOGS = "app_logs";

    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_USERNAME = "name";
    public static final String COLUMN_DATE_JOINED = "date_joined";
    public static final String COLUMN_IS_BLOCKED = "is_blocked";

    public static final String COLUMN_FAVORITE_CUISINES = "favorite_cuisines";
    public static final String COLUMN_DIETARY_RESTRICTIONS = "dietary_restrictions";
    public static final String COLUMN_ALLERGIES = "allergies";
    public static final String COLUMN_BUDGET = "budget";
    public static final String COLUMN_COOKING_TIME = "cooking_time";
    public static final String COLUMN_COOKING_SKILL_LEVEL = "cooking_skill_level";

    public static final String COLUMN_RESTAURANT_ID = "restaurant_id";
    public static final String COLUMN_REST_NAME = "name";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_CUISINE = "cuisine";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_PRICE_RANGE = "price_range";
    public static final String COLUMN_AVG_CHECK = "avg_check";
    public static final String COLUMN_DISTANCE_MIN = "distance_min";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CONTACT_INFO = "contact_info";
    public static final String COLUMN_OPENING_HOURS = "opening_hours";
    public static final String COLUMN_HAS_DELIVERY = "has_delivery";
    public static final String COLUMN_HAS_BOOKING = "has_booking";

    public static final String COLUMN_MENU_ID = "menu_id";
    public static final String COLUMN_DISH_NAME = "dish_name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IS_VEGETARIAN = "is_vegetarian";
    public static final String COLUMN_IS_VEGAN = "is_vegan";
    public static final String COLUMN_IS_GLUTEN_FREE = "is_gluten_free";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_INGREDIENTS_TEXT = "ingredients_text";
    public static final String COLUMN_ALLERGENS_TEXT = "allergens_text";

    public static final String COLUMN_REVIEW_ID = "review_id";
    public static final String COLUMN_REVIEW_TEXT = "review_text";
    public static final String COLUMN_CREATED_AT = "created_at";

    public static final String COLUMN_RECIPE_ID = "recipe_id";
    public static final String COLUMN_RECIPE_NAME = "name";
    public static final String COLUMN_COOK_TIME = "cooking_time";
    public static final String COLUMN_INGREDIENTS = "ingredients";
    public static final String COLUMN_INSTRUCTIONS = "instructions";
    public static final String COLUMN_MISSING_INGREDIENTS = "missing_ingredients";
    public static final String COLUMN_SHOPPING_LIST = "shopping_list";
    public static final String COLUMN_SOURCE = "source";

    public static final String COLUMN_INGREDIENT_ID = "ingredient_id";
    public static final String COLUMN_INGREDIENT_NAME = "name";
    public static final String COLUMN_CATEGORY = "category";

    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";

    public static final String COLUMN_QUERY_ID = "query_id";
    public static final String COLUMN_QUERY_TEXT = "query_text";
    public static final String COLUMN_RESULT = "result";

    public static final String COLUMN_HISTORY_ID = "history_id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DETAILS = "details";

    public static final String COLUMN_DICT_ID = "dict_id";
    public static final String COLUMN_DICT_TYPE = "dict_type";
    public static final String COLUMN_DICT_VALUE = "dict_value";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_DATE_JOINED + " TEXT DEFAULT (datetime('now')), " +
                COLUMN_IS_BLOCKED + " INTEGER DEFAULT 0" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_USER_PREFERENCES + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_FAVORITE_CUISINES + " TEXT, " +
                COLUMN_DIETARY_RESTRICTIONS + " TEXT, " +
                COLUMN_ALLERGIES + " TEXT, " +
                COLUMN_BUDGET + " INTEGER DEFAULT 1000, " +
                COLUMN_COOKING_TIME + " INTEGER DEFAULT 30, " +
                COLUMN_COOKING_SKILL_LEVEL + " INTEGER DEFAULT 1, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_RESTAURANTS + " (" +
                COLUMN_RESTAURANT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_REST_NAME + " TEXT NOT NULL, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_CUISINE + " TEXT, " +
                COLUMN_RATING + " REAL DEFAULT 0, " +
                COLUMN_PRICE_RANGE + " TEXT, " +
                COLUMN_AVG_CHECK + " INTEGER DEFAULT 0, " +
                COLUMN_DISTANCE_MIN + " INTEGER DEFAULT 0, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_CONTACT_INFO + " TEXT, " +
                COLUMN_OPENING_HOURS + " TEXT, " +
                COLUMN_HAS_DELIVERY + " INTEGER DEFAULT 1, " +
                COLUMN_HAS_BOOKING + " INTEGER DEFAULT 1" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_RESTAURANT_MENU + " (" +
                COLUMN_MENU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RESTAURANT_ID + " INTEGER NOT NULL, " +
                COLUMN_DISH_NAME + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_PRICE + " REAL DEFAULT 0, " +
                COLUMN_IS_VEGETARIAN + " INTEGER DEFAULT 0, " +
                COLUMN_IS_VEGAN + " INTEGER DEFAULT 0, " +
                COLUMN_IS_GLUTEN_FREE + " INTEGER DEFAULT 0, " +
                COLUMN_CALORIES + " INTEGER DEFAULT 0, " +
                COLUMN_INGREDIENTS_TEXT + " TEXT, " +
                COLUMN_ALLERGENS_TEXT + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_RESTAURANT_ID + ") REFERENCES " + TABLE_RESTAURANTS + "(" + COLUMN_RESTAURANT_ID + ") ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_RESTAURANT_REVIEWS + " (" +
                COLUMN_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RESTAURANT_ID + " INTEGER NOT NULL, " +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_REVIEW_TEXT + " TEXT, " +
                COLUMN_RATING + " REAL DEFAULT 0, " +
                COLUMN_CREATED_AT + " TEXT DEFAULT (datetime('now')), " +
                "FOREIGN KEY(" + COLUMN_RESTAURANT_ID + ") REFERENCES " + TABLE_RESTAURANTS + "(" + COLUMN_RESTAURANT_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE SET NULL" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_COOK_TIME + " INTEGER DEFAULT 0, " +
                COLUMN_CALORIES + " INTEGER DEFAULT 0, " +
                COLUMN_IS_VEGETARIAN + " INTEGER DEFAULT 0, " +
                COLUMN_IS_VEGAN + " INTEGER DEFAULT 0, " +
                COLUMN_IS_GLUTEN_FREE + " INTEGER DEFAULT 0, " +
                COLUMN_INGREDIENTS + " TEXT, " +
                COLUMN_MISSING_INGREDIENTS + " TEXT, " +
                COLUMN_SHOPPING_LIST + " TEXT, " +
                COLUMN_INSTRUCTIONS + " TEXT, " +
                COLUMN_SOURCE + " TEXT DEFAULT 'local', " +
                COLUMN_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_INGREDIENTS + " (" +
                COLUMN_INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_INGREDIENT_NAME + " TEXT NOT NULL UNIQUE, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_IS_VEGETARIAN + " INTEGER DEFAULT 1, " +
                COLUMN_IS_VEGAN + " INTEGER DEFAULT 1, " +
                COLUMN_IS_GLUTEN_FREE + " INTEGER DEFAULT 1" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COLUMN_RECIPE_ID + " INTEGER, " +
                COLUMN_INGREDIENT_ID + " INTEGER, " +
                COLUMN_QUANTITY + " REAL, " +
                COLUMN_UNIT + " TEXT, " +
                "PRIMARY KEY (" + COLUMN_RECIPE_ID + ", " + COLUMN_INGREDIENT_ID + "), " +
                "FOREIGN KEY(" + COLUMN_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + COLUMN_INGREDIENT_ID + ") REFERENCES " + TABLE_INGREDIENTS + "(" + COLUMN_INGREDIENT_ID + ") ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_USER_QUERIES + " (" +
                COLUMN_QUERY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_QUERY_TEXT + " TEXT, " +
                COLUMN_RESULT + " TEXT, " +
                COLUMN_CREATED_AT + " TEXT DEFAULT (datetime('now')), " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_USER_HISTORY + " (" +
                COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_TYPE + " TEXT NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_DETAILS + " TEXT, " +
                COLUMN_RESTAURANT_ID + " INTEGER, " +
                COLUMN_RECIPE_ID + " INTEGER, " +
                COLUMN_CREATED_AT + " TEXT DEFAULT (datetime('now')), " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + COLUMN_RESTAURANT_ID + ") REFERENCES " + TABLE_RESTAURANTS + "(" + COLUMN_RESTAURANT_ID + ") ON DELETE SET NULL, " +
                "FOREIGN KEY(" + COLUMN_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + ") ON DELETE SET NULL" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_FAVORITE_RESTAURANTS + " (" +
                COLUMN_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_RESTAURANT_ID + " INTEGER NOT NULL, " +
                COLUMN_CREATED_AT + " TEXT DEFAULT (datetime('now')), " +
                "PRIMARY KEY (" + COLUMN_USER_ID + ", " + COLUMN_RESTAURANT_ID + "), " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + COLUMN_RESTAURANT_ID + ") REFERENCES " + TABLE_RESTAURANTS + "(" + COLUMN_RESTAURANT_ID + ") ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_FAVORITE_RECIPES + " (" +
                COLUMN_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +
                COLUMN_CREATED_AT + " TEXT DEFAULT (datetime('now')), " +
                "PRIMARY KEY (" + COLUMN_USER_ID + ", " + COLUMN_RECIPE_ID + "), " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + COLUMN_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + ") ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_DICTIONARIES + " (" +
                COLUMN_DICT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DICT_TYPE + " TEXT NOT NULL, " +
                COLUMN_DICT_VALUE + " TEXT NOT NULL, " +
                "UNIQUE(" + COLUMN_DICT_TYPE + ", " + COLUMN_DICT_VALUE + ")" +
                ");");

        db.execSQL("CREATE TABLE " + TABLE_APP_LOGS + " (" +
                "log_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_DETAILS + " TEXT, " +
                COLUMN_CREATED_AT + " TEXT DEFAULT (datetime('now'))" +
                ");");

        seedDefaultData(db);
    }

    private void seedDefaultData(SQLiteDatabase db) {
        String[] dictionaries = new String[] {
                "cuisine|Итальянская", "cuisine|Японская", "cuisine|Русская", "cuisine|Грузинская",
                "cuisine|Китайская", "cuisine|Индийская", "cuisine|Мексиканская", "cuisine|Корейская",
                "cuisine|Европейская", "cuisine|Ближневосточная", "cuisine|Американская", "cuisine|Веганская",
                "cuisine|Здоровое питание", "cuisine|Морская", "diet|Без ограничений", "diet|Вегетарианство",
                "diet|Веганство", "diet|Кето", "diet|Без глютена", "diet|Без лактозы",
                "diet|Низкокалорийное", "diet|Высокобелковое", "allergen|нет аллергенов", "allergen|Орехи", "allergen|Глютен",
                "allergen|Молочные продукты", "allergen|Яйца", "allergen|Соевые продукты", "allergen|Морепродукты",
                "allergen|Пшеница", "allergen|Лактоза", "allergen|Рыба", "allergen|Кунжут",
                "allergen|Горчица", "allergen|Мёд", "product_category|Мясо", "product_category|Рыба",
                "product_category|Овощи", "product_category|Крупы", "product_category|Бобовые", "product_category|Молочные",
                "product_category|Фрукты", "product_category|Зелень", "product_category|Специи", "product_category|Соусы",
                "product_category|Выпечка", "product_category|Напитки"
        };
        for (String item : dictionaries) {
            String[] parts = item.split("\\|", 2);
            db.execSQL("INSERT OR IGNORE INTO " + TABLE_DICTIONARIES + "(" + COLUMN_DICT_TYPE + "," + COLUMN_DICT_VALUE + ") VALUES(?,?)", parts);
        }

        String[][] ingredients = new String[][] {
                {"Курица", "Мясо", "0", "0", "1"},
                {"Говядина", "Мясо", "0", "0", "1"},
                {"Свинина", "Мясо", "0", "0", "1"},
                {"Индейка", "Мясо", "0", "0", "1"},
                {"Лосось", "Рыба", "0", "0", "1"},
                {"Тунец", "Рыба", "0", "0", "1"},
                {"Креветки", "Рыба", "0", "0", "1"},
                {"Кальмар", "Рыба", "0", "0", "1"},
                {"Яйца", "Молочные", "1", "0", "1"},
                {"Картофель", "Овощи", "1", "1", "1"},
                {"Помидор", "Овощи", "1", "1", "1"},
                {"Огурец", "Овощи", "1", "1", "1"},
                {"Лук", "Овощи", "1", "1", "1"},
                {"Морковь", "Овощи", "1", "1", "1"},
                {"Перец болгарский", "Овощи", "1", "1", "1"},
                {"Кабачок", "Овощи", "1", "1", "1"},
                {"Баклажан", "Овощи", "1", "1", "1"},
                {"Шпинат", "Зелень", "1", "1", "1"},
                {"Базилик", "Зелень", "1", "1", "1"},
                {"Кинза", "Зелень", "1", "1", "1"},
                {"Рис", "Крупы", "1", "1", "1"},
                {"Гречка", "Крупы", "1", "1", "1"},
                {"Булгур", "Крупы", "1", "1", "0"},
                {"Киноа", "Крупы", "1", "1", "1"},
                {"Паста", "Крупы", "1", "1", "0"},
                {"Лапша", "Крупы", "1", "1", "0"},
                {"Нут", "Бобовые", "1", "1", "1"},
                {"Чечевица", "Бобовые", "1", "1", "1"},
                {"Фасоль", "Бобовые", "1", "1", "1"},
                {"Сыр", "Молочные", "1", "0", "1"},
                {"Моцарелла", "Молочные", "1", "0", "1"},
                {"Молоко", "Молочные", "1", "0", "1"},
                {"Творог", "Молочные", "1", "0", "1"},
                {"Сливки", "Молочные", "1", "0", "1"},
                {"Тофу", "Бобовые", "1", "1", "1"},
                {"Авокадо", "Фрукты", "1", "1", "1"},
                {"Яблоко", "Фрукты", "1", "1", "1"},
                {"Банан", "Фрукты", "1", "1", "1"},
                {"Лимон", "Фрукты", "1", "1", "1"},
                {"Манго", "Фрукты", "1", "1", "1"},
                {"Соль", "Специи", "1", "1", "1"},
                {"Перец", "Специи", "1", "1", "1"},
                {"Карри", "Специи", "1", "1", "1"},
                {"Паприка", "Специи", "1", "1", "1"},
                {"Соевый соус", "Соусы", "1", "1", "0"},
                {"Томатный соус", "Соусы", "1", "1", "1"},
                {"Сметана", "Молочные", "1", "0", "1"},
                {"Хлеб", "Выпечка", "1", "1", "0"}
        };
        for (String[] ing : ingredients) {
            db.execSQL("INSERT OR IGNORE INTO " + TABLE_INGREDIENTS + "(" + COLUMN_INGREDIENT_NAME + "," + COLUMN_CATEGORY + "," + COLUMN_IS_VEGETARIAN + "," + COLUMN_IS_VEGAN + "," + COLUMN_IS_GLUTEN_FREE + ") VALUES(?,?,?,?,?)", ing);
        }

        seedRecipeCatalog(db);

        // Увеличенная тестовая база: 32 ресторана, 192 позиции меню и 64 отзыва.
        insertRestaurant(db, "Паста & Базилик", "ул. Центральная, 12", "Итальянская", 4.8, "800–1600 ₽", 1250, 12, "Паста, ризотто и салаты. Подходит для спокойного ужина.", "+7 900 111-22-33", "10:00–23:00", 1, 1);
        insertRestaurant(db, "Траттория Север", "ул. Лесная, 21", "Итальянская", 4.6, "700–1500 ₽", 1150, 16, "Семейная траттория с пастой, пиццей и домашними десертами.", "+7 900 111-22-34", "11:00–23:00", 1, 1);
        insertRestaurant(db, "Пицца Мастер", "пр-т Победы, 5", "Итальянская", 4.4, "500–1100 ₽", 850, 9, "Быстрая пицца, паста и доставка до дома.", "+7 900 111-22-35", "10:00–01:00", 1, 0);
        insertRestaurant(db, "Сакура Рамен", "пр-т Мира, 8", "Японская", 4.6, "700–1400 ₽", 1100, 18, "Рамен, боулы, роллы и быстрый самовывоз.", "+7 900 222-33-44", "11:00–22:30", 1, 0);
        insertRestaurant(db, "Токио Боул", "ул. Гагарина, 14", "Японская", 4.7, "750–1500 ₽", 1180, 13, "Поке, суши, сашими и лёгкие японские блюда.", "+7 900 222-33-45", "10:30–23:00", 1, 1);
        insertRestaurant(db, "Суши Лайн", "ул. Молодёжная, 2", "Японская", 4.3, "600–1300 ₽", 980, 22, "Доступные роллы и наборы для доставки.", "+7 900 222-33-46", "10:00–00:00", 1, 0);
        insertRestaurant(db, "Домашняя Лавка", "ул. Советская, 3", "Русская", 4.5, "450–900 ₽", 650, 7, "Домашняя кухня, супы, каши и горячие блюда.", "+7 900 333-44-55", "08:00–21:00", 1, 1);
        insertRestaurant(db, "Борщ и Пироги", "ул. Октябрьская, 10", "Русская", 4.6, "500–1000 ₽", 760, 11, "Русская кухня с супами, пирогами и домашними обедами.", "+7 900 333-44-56", "09:00–22:00", 1, 1);
        insertRestaurant(db, "Северная Столовая", "пер. Студенческий, 4", "Русская", 4.2, "300–700 ₽", 520, 5, "Бюджетные комплексные обеды и простые горячие блюда.", "+7 900 333-44-57", "08:00–20:00", 0, 0);
        insertRestaurant(db, "Хинкали Град", "ул. Южная, 19", "Грузинская", 4.7, "900–1800 ₽", 1400, 20, "Хинкали, хачапури и мясные блюда.", "+7 900 444-55-66", "12:00–00:00", 1, 1);
        insertRestaurant(db, "Сулико", "ул. Театральная, 6", "Грузинская", 4.8, "850–1700 ₽", 1320, 15, "Грузинская классика, специи, зелень и домашнее вино в меню без алкоголя.", "+7 900 444-55-67", "11:00–23:30", 1, 1);
        insertRestaurant(db, "Мцване", "ул. Садовая, 30", "Грузинская", 4.5, "700–1500 ₽", 1100, 25, "Хачапури, овощи на гриле и блюда для компании.", "+7 900 444-55-68", "12:00–23:00", 1, 1);
        insertRestaurant(db, "Зелёная Тарелка", "ул. Парковая, 5", "Индийская", 4.4, "600–1200 ₽", 850, 10, "Вегетарианские карри, супы и блюда без мяса.", "+7 900 555-66-77", "10:00–22:00", 1, 1);
        insertRestaurant(db, "Дели Карри", "пр-т Индийский, 1", "Индийская", 4.6, "650–1300 ₽", 930, 17, "Карри, рис, лепёшки и острые блюда на выбор.", "+7 900 555-66-78", "11:00–23:00", 1, 0);
        insertRestaurant(db, "Масала Хаус", "ул. Восточная, 9", "Индийская", 4.7, "800–1600 ₽", 1200, 21, "Индийская кухня с большим выбором вегетарианских блюд.", "+7 900 555-66-79", "12:00–23:30", 1, 1);
        insertRestaurant(db, "Дракон Вок", "ул. Китайская, 7", "Китайская", 4.5, "550–1200 ₽", 870, 8, "Вок, рис, лапша и блюда в кисло-сладком соусе.", "+7 900 666-77-88", "10:00–23:00", 1, 0);
        insertRestaurant(db, "Лапша & Чай", "ул. Чайная, 11", "Китайская", 4.3, "450–1000 ₽", 720, 14, "Китайская лапша, супы и чайная карта.", "+7 900 666-77-89", "09:00–22:00", 1, 0);
        insertRestaurant(db, "Пекинский двор", "пр-т Востока, 18", "Китайская", 4.6, "700–1500 ₽", 1120, 19, "Классические китайские блюда и большие порции.", "+7 900 666-77-90", "11:00–23:30", 1, 1);
        insertRestaurant(db, "Тако Локо", "ул. Солнечная, 16", "Мексиканская", 4.4, "500–1100 ₽", 780, 12, "Тако, начос, буррито и острые соусы.", "+7 900 777-88-99", "10:00–23:00", 1, 0);
        insertRestaurant(db, "Буррито Бар", "ул. Спортивная, 20", "Мексиканская", 4.5, "600–1200 ₽", 890, 17, "Сытные буррито и быстрые комбо-наборы.", "+7 900 777-88-98", "11:00–00:00", 1, 0);
        insertRestaurant(db, "Кесадилья House", "ул. Молодёжная, 18", "Мексиканская", 4.2, "450–1000 ₽", 740, 23, "Кесадильи, тако и вегетарианские мексиканские блюда.", "+7 900 777-88-97", "10:00–22:30", 1, 1);
        insertRestaurant(db, "Сеул BBQ", "ул. Корейская, 4", "Корейская", 4.8, "900–1900 ₽", 1500, 20, "Корейское барбекю, кимчи и горячие сковородки.", "+7 900 888-99-00", "12:00–00:00", 1, 1);
        insertRestaurant(db, "Кимчи Лаб", "ул. Научная, 13", "Корейская", 4.5, "600–1300 ₽", 920, 11, "Боулы, кимчи, токпокки и острые супы.", "+7 900 888-99-01", "10:00–23:00", 1, 0);
        insertRestaurant(db, "Бибимбап Стрит", "пр-т Университетский, 3", "Корейская", 4.4, "550–1200 ₽", 850, 9, "Бибимбап, лапша и блюда навынос.", "+7 900 888-99-02", "09:30–22:30", 1, 0);
        insertRestaurant(db, "Fresh Fit", "ул. Здоровья, 2", "Здоровое питание", 4.7, "650–1400 ₽", 980, 6, "Боулы, салаты, белковые блюда и низкокалорийные варианты.", "+7 900 999-00-11", "08:00–22:00", 1, 1);
        insertRestaurant(db, "Vegan Space", "ул. Эко, 15", "Веганская", 4.8, "700–1500 ₽", 1050, 18, "Полностью веганское меню без молочных продуктов и яиц.", "+7 900 999-00-12", "10:00–23:00", 1, 1);
        insertRestaurant(db, "Шаурма Восток", "ул. Вокзальная, 1", "Ближневосточная", 4.1, "300–800 ₽", 550, 4, "Быстрая ближневосточная кухня, шаурма и фалафель.", "+7 900 101-10-10", "08:00–02:00", 1, 0);
        insertRestaurant(db, "Хумус & Фалафель", "ул. Рыночная, 24", "Ближневосточная", 4.6, "500–1100 ₽", 760, 13, "Хумус, фалафель, пита и веганские закуски.", "+7 900 101-10-11", "09:00–23:00", 1, 1);
        insertRestaurant(db, "Burger Craft", "ул. Промышленная, 8", "Американская", 4.3, "600–1300 ₽", 950, 16, "Бургеры, картофель и салаты с возможностью доставки.", "+7 900 202-20-20", "10:00–00:00", 1, 0);
        insertRestaurant(db, "Завтрак весь день", "ул. Утреняя, 6", "Европейская", 4.6, "550–1200 ₽", 840, 10, "Омлеты, каши, сырники и лёгкие завтраки весь день.", "+7 900 303-30-30", "07:30–21:00", 1, 1);
        insertRestaurant(db, "Морская Таверна", "наб. Речная, 22", "Морская", 4.7, "1000–2200 ₽", 1650, 24, "Рыба, морепродукты и блюда на гриле.", "+7 900 404-40-40", "12:00–23:30", 1, 1);
        insertRestaurant(db, "Кофе и Круассан", "ул. Книжная, 9", "Европейская", 4.4, "350–900 ₽", 620, 7, "Кофейня с выпечкой, салатами и лёгкими обедами.", "+7 900 505-50-50", "07:00–22:00", 1, 0);

        insertMenu(db, 1, "Паста с томатами", "Паста с томатным соусом, базиликом и сыром", 520, 1, 0, 0, 640, "Паста, помидор, базилик, сыр", "Глютен, Молочные продукты");
        insertMenu(db, 1, "Ризотто с овощами", "Рис с овощами и пармезаном", 590, 1, 0, 1, 560, "Рис, морковь, лук, сыр", "Молочные продукты");
        insertMenu(db, 1, "Пицца Маргарита", "Тесто, томаты, моцарелла и базилик", 650, 1, 0, 0, 780, "Тесто, томатный соус, моцарелла, базилик", "Глютен, Молочные продукты");
        insertMenu(db, 1, "Курица по-тоскански", "Курица в сливочном соусе со шпинатом", 720, 0, 0, 1, 690, "Курица, сливки, шпинат, сыр", "Молочные продукты");
        insertMenu(db, 1, "Салат Капрезе", "Томаты, моцарелла, базилик и оливковое масло", 430, 1, 0, 1, 360, "Помидор, моцарелла, базилик", "Молочные продукты");
        insertMenu(db, 1, "Минестроне", "Овощной итальянский суп", 390, 1, 1, 1, 310, "Картофель, морковь, фасоль, помидор", "");
        insertMenu(db, 2, "Паста с томатами", "Паста с томатным соусом, базиликом и сыром", 520, 1, 0, 0, 640, "Паста, помидор, базилик, сыр", "Глютен, Молочные продукты");
        insertMenu(db, 2, "Ризотто с овощами", "Рис с овощами и пармезаном", 590, 1, 0, 1, 560, "Рис, морковь, лук, сыр", "Молочные продукты");
        insertMenu(db, 2, "Пицца Маргарита", "Тесто, томаты, моцарелла и базилик", 650, 1, 0, 0, 780, "Тесто, томатный соус, моцарелла, базилик", "Глютен, Молочные продукты");
        insertMenu(db, 2, "Курица по-тоскански", "Курица в сливочном соусе со шпинатом", 720, 0, 0, 1, 690, "Курица, сливки, шпинат, сыр", "Молочные продукты");
        insertMenu(db, 2, "Салат Капрезе", "Томаты, моцарелла, базилик и оливковое масло", 430, 1, 0, 1, 360, "Помидор, моцарелла, базилик", "Молочные продукты");
        insertMenu(db, 2, "Минестроне", "Овощной итальянский суп", 390, 1, 1, 1, 310, "Картофель, морковь, фасоль, помидор", "");
        insertMenu(db, 3, "Паста с томатами", "Паста с томатным соусом, базиликом и сыром", 520, 1, 0, 0, 640, "Паста, помидор, базилик, сыр", "Глютен, Молочные продукты");
        insertMenu(db, 3, "Ризотто с овощами", "Рис с овощами и пармезаном", 590, 1, 0, 1, 560, "Рис, морковь, лук, сыр", "Молочные продукты");
        insertMenu(db, 3, "Пицца Маргарита", "Тесто, томаты, моцарелла и базилик", 650, 1, 0, 0, 780, "Тесто, томатный соус, моцарелла, базилик", "Глютен, Молочные продукты");
        insertMenu(db, 3, "Курица по-тоскански", "Курица в сливочном соусе со шпинатом", 720, 0, 0, 1, 690, "Курица, сливки, шпинат, сыр", "Молочные продукты");
        insertMenu(db, 3, "Салат Капрезе", "Томаты, моцарелла, базилик и оливковое масло", 430, 1, 0, 1, 360, "Помидор, моцарелла, базилик", "Молочные продукты");
        insertMenu(db, 3, "Минестроне", "Овощной итальянский суп", 390, 1, 1, 1, 310, "Картофель, морковь, фасоль, помидор", "");
        insertMenu(db, 4, "Куриный рамен", "Рамен с курицей, яйцом и зелёным луком", 610, 0, 0, 0, 720, "Курица, яйцо, лапша, лук", "Глютен, Яйца");
        insertMenu(db, 4, "Рисовый боул с овощами", "Боул на рисе с овощами и соевым соусом", 470, 1, 1, 1, 520, "Рис, огурец, морковь, помидор", "Соевые продукты");
        insertMenu(db, 4, "Ролл с лососем", "Рис, нори, лосось и огурец", 540, 0, 0, 1, 480, "Рис, лосось, огурец, нори", "Рыба");
        insertMenu(db, 4, "Тофу терияки", "Тофу с рисом и овощами", 520, 1, 1, 0, 560, "Тофу, рис, соевый соус, морковь", "Соевые продукты, Глютен");
        insertMenu(db, 4, "Мисо-суп", "Лёгкий суп с тофу и водорослями", 280, 1, 1, 1, 190, "Тофу, мисо, водоросли", "Соевые продукты");
        insertMenu(db, 4, "Поке с тунцом", "Рис, тунец, авокадо и овощи", 690, 0, 0, 1, 610, "Рис, тунец, авокадо, огурец", "Рыба");
        insertMenu(db, 5, "Куриный рамен", "Рамен с курицей, яйцом и зелёным луком", 610, 0, 0, 0, 720, "Курица, яйцо, лапша, лук", "Глютен, Яйца");
        insertMenu(db, 5, "Рисовый боул с овощами", "Боул на рисе с овощами и соевым соусом", 470, 1, 1, 1, 520, "Рис, огурец, морковь, помидор", "Соевые продукты");
        insertMenu(db, 5, "Ролл с лососем", "Рис, нори, лосось и огурец", 540, 0, 0, 1, 480, "Рис, лосось, огурец, нори", "Рыба");
        insertMenu(db, 5, "Тофу терияки", "Тофу с рисом и овощами", 520, 1, 1, 0, 560, "Тофу, рис, соевый соус, морковь", "Соевые продукты, Глютен");
        insertMenu(db, 5, "Мисо-суп", "Лёгкий суп с тофу и водорослями", 280, 1, 1, 1, 190, "Тофу, мисо, водоросли", "Соевые продукты");
        insertMenu(db, 5, "Поке с тунцом", "Рис, тунец, авокадо и овощи", 690, 0, 0, 1, 610, "Рис, тунец, авокадо, огурец", "Рыба");
        insertMenu(db, 6, "Куриный рамен", "Рамен с курицей, яйцом и зелёным луком", 610, 0, 0, 0, 720, "Курица, яйцо, лапша, лук", "Глютен, Яйца");
        insertMenu(db, 6, "Рисовый боул с овощами", "Боул на рисе с овощами и соевым соусом", 470, 1, 1, 1, 520, "Рис, огурец, морковь, помидор", "Соевые продукты");
        insertMenu(db, 6, "Ролл с лососем", "Рис, нори, лосось и огурец", 540, 0, 0, 1, 480, "Рис, лосось, огурец, нори", "Рыба");
        insertMenu(db, 6, "Тофу терияки", "Тофу с рисом и овощами", 520, 1, 1, 0, 560, "Тофу, рис, соевый соус, морковь", "Соевые продукты, Глютен");
        insertMenu(db, 6, "Мисо-суп", "Лёгкий суп с тофу и водорослями", 280, 1, 1, 1, 190, "Тофу, мисо, водоросли", "Соевые продукты");
        insertMenu(db, 6, "Поке с тунцом", "Рис, тунец, авокадо и овощи", 690, 0, 0, 1, 610, "Рис, тунец, авокадо, огурец", "Рыба");
        insertMenu(db, 7, "Борщ со сметаной", "Классический борщ с овощами и сметаной", 340, 1, 0, 1, 390, "Свёкла, капуста, картофель, сметана", "Молочные продукты");
        insertMenu(db, 7, "Гречка с грибами", "Гречка с луком и грибами", 330, 1, 1, 1, 420, "Гречка, лук, морковь", "");
        insertMenu(db, 7, "Курица с картофелем", "Домашняя курица с картофелем", 450, 0, 0, 1, 690, "Курица, картофель, морковь", "");
        insertMenu(db, 7, "Сырники", "Творожные сырники со сметаной", 390, 1, 0, 0, 510, "Творог, яйца, мука, сметана", "Молочные продукты, Яйца, Глютен");
        insertMenu(db, 7, "Оливье с индейкой", "Салат с индейкой и овощами", 410, 0, 0, 1, 540, "Индейка, картофель, морковь, яйцо", "Яйца");
        insertMenu(db, 7, "Щи постные", "Капустный суп без мяса", 290, 1, 1, 1, 260, "Капуста, картофель, морковь, лук", "");
        insertMenu(db, 8, "Борщ со сметаной", "Классический борщ с овощами и сметаной", 340, 1, 0, 1, 390, "Свёкла, капуста, картофель, сметана", "Молочные продукты");
        insertMenu(db, 8, "Гречка с грибами", "Гречка с луком и грибами", 330, 1, 1, 1, 420, "Гречка, лук, морковь", "");
        insertMenu(db, 8, "Курица с картофелем", "Домашняя курица с картофелем", 450, 0, 0, 1, 690, "Курица, картофель, морковь", "");
        insertMenu(db, 8, "Сырники", "Творожные сырники со сметаной", 390, 1, 0, 0, 510, "Творог, яйца, мука, сметана", "Молочные продукты, Яйца, Глютен");
        insertMenu(db, 8, "Оливье с индейкой", "Салат с индейкой и овощами", 410, 0, 0, 1, 540, "Индейка, картофель, морковь, яйцо", "Яйца");
        insertMenu(db, 8, "Щи постные", "Капустный суп без мяса", 290, 1, 1, 1, 260, "Капуста, картофель, морковь, лук", "");
        insertMenu(db, 9, "Борщ со сметаной", "Классический борщ с овощами и сметаной", 340, 1, 0, 1, 390, "Свёкла, капуста, картофель, сметана", "Молочные продукты");
        insertMenu(db, 9, "Гречка с грибами", "Гречка с луком и грибами", 330, 1, 1, 1, 420, "Гречка, лук, морковь", "");
        insertMenu(db, 9, "Курица с картофелем", "Домашняя курица с картофелем", 450, 0, 0, 1, 690, "Курица, картофель, морковь", "");
        insertMenu(db, 9, "Сырники", "Творожные сырники со сметаной", 390, 1, 0, 0, 510, "Творог, яйца, мука, сметана", "Молочные продукты, Яйца, Глютен");
        insertMenu(db, 9, "Оливье с индейкой", "Салат с индейкой и овощами", 410, 0, 0, 1, 540, "Индейка, картофель, морковь, яйцо", "Яйца");
        insertMenu(db, 9, "Щи постные", "Капустный суп без мяса", 290, 1, 1, 1, 260, "Капуста, картофель, морковь, лук", "");
        insertMenu(db, 10, "Хачапури по-аджарски", "Лодочка с сыром и яйцом", 620, 1, 0, 0, 850, "Тесто, сыр, яйцо", "Глютен, Молочные продукты, Яйца");
        insertMenu(db, 10, "Хинкали с говядиной", "Хинкали с мясной начинкой", 690, 0, 0, 0, 760, "Говядина, тесто, лук", "Глютен");
        insertMenu(db, 10, "Овощной салат по-грузински", "Свежие овощи с зеленью", 390, 1, 1, 1, 310, "Помидор, огурец, лук, кинза", "Орехи");
        insertMenu(db, 10, "Лобио", "Фасоль со специями и зеленью", 420, 1, 1, 1, 440, "Фасоль, лук, кинза, специи", "");
        insertMenu(db, 10, "Чахохбили", "Курица в томатном соусе", 580, 0, 0, 1, 610, "Курица, помидор, лук, кинза", "");
        insertMenu(db, 10, "Аджапсандали", "Овощное рагу с баклажаном", 460, 1, 1, 1, 370, "Баклажан, помидор, перец, лук", "");
        insertMenu(db, 11, "Хачапури по-аджарски", "Лодочка с сыром и яйцом", 620, 1, 0, 0, 850, "Тесто, сыр, яйцо", "Глютен, Молочные продукты, Яйца");
        insertMenu(db, 11, "Хинкали с говядиной", "Хинкали с мясной начинкой", 690, 0, 0, 0, 760, "Говядина, тесто, лук", "Глютен");
        insertMenu(db, 11, "Овощной салат по-грузински", "Свежие овощи с зеленью", 390, 1, 1, 1, 310, "Помидор, огурец, лук, кинза", "Орехи");
        insertMenu(db, 11, "Лобио", "Фасоль со специями и зеленью", 420, 1, 1, 1, 440, "Фасоль, лук, кинза, специи", "");
        insertMenu(db, 11, "Чахохбили", "Курица в томатном соусе", 580, 0, 0, 1, 610, "Курица, помидор, лук, кинза", "");
        insertMenu(db, 11, "Аджапсандали", "Овощное рагу с баклажаном", 460, 1, 1, 1, 370, "Баклажан, помидор, перец, лук", "");
        insertMenu(db, 12, "Хачапури по-аджарски", "Лодочка с сыром и яйцом", 620, 1, 0, 0, 850, "Тесто, сыр, яйцо", "Глютен, Молочные продукты, Яйца");
        insertMenu(db, 12, "Хинкали с говядиной", "Хинкали с мясной начинкой", 690, 0, 0, 0, 760, "Говядина, тесто, лук", "Глютен");
        insertMenu(db, 12, "Овощной салат по-грузински", "Свежие овощи с зеленью", 390, 1, 1, 1, 310, "Помидор, огурец, лук, кинза", "Орехи");
        insertMenu(db, 12, "Лобио", "Фасоль со специями и зеленью", 420, 1, 1, 1, 440, "Фасоль, лук, кинза, специи", "");
        insertMenu(db, 12, "Чахохбили", "Курица в томатном соусе", 580, 0, 0, 1, 610, "Курица, помидор, лук, кинза", "");
        insertMenu(db, 12, "Аджапсандали", "Овощное рагу с баклажаном", 460, 1, 1, 1, 370, "Баклажан, помидор, перец, лук", "");
        insertMenu(db, 13, "Овощное карри с рисом", "Пряное карри без мяса", 490, 1, 1, 1, 580, "Рис, морковь, лук, карри", "");
        insertMenu(db, 13, "Чечевичный дал", "Дал из чечевицы со специями", 430, 1, 1, 1, 420, "Чечевица, лук, карри", "");
        insertMenu(db, 13, "Курица тикка масала", "Курица в пряном сливочном соусе", 690, 0, 0, 1, 720, "Курица, сливки, карри", "Молочные продукты");
        insertMenu(db, 13, "Палак панир", "Шпинат с сыром панир", 560, 1, 0, 1, 540, "Шпинат, сыр, специи", "Молочные продукты");
        insertMenu(db, 13, "Нут масала", "Нут в томатном соусе", 470, 1, 1, 1, 510, "Нут, помидор, лук, карри", "");
        insertMenu(db, 13, "Овощной бирьяни", "Рис со специями и овощами", 530, 1, 1, 1, 620, "Рис, морковь, перец, специи", "");
        insertMenu(db, 14, "Овощное карри с рисом", "Пряное карри без мяса", 490, 1, 1, 1, 580, "Рис, морковь, лук, карри", "");
        insertMenu(db, 14, "Чечевичный дал", "Дал из чечевицы со специями", 430, 1, 1, 1, 420, "Чечевица, лук, карри", "");
        insertMenu(db, 14, "Курица тикка масала", "Курица в пряном сливочном соусе", 690, 0, 0, 1, 720, "Курица, сливки, карри", "Молочные продукты");
        insertMenu(db, 14, "Палак панир", "Шпинат с сыром панир", 560, 1, 0, 1, 540, "Шпинат, сыр, специи", "Молочные продукты");
        insertMenu(db, 14, "Нут масала", "Нут в томатном соусе", 470, 1, 1, 1, 510, "Нут, помидор, лук, карри", "");
        insertMenu(db, 14, "Овощной бирьяни", "Рис со специями и овощами", 530, 1, 1, 1, 620, "Рис, морковь, перец, специи", "");
        insertMenu(db, 15, "Овощное карри с рисом", "Пряное карри без мяса", 490, 1, 1, 1, 580, "Рис, морковь, лук, карри", "");
        insertMenu(db, 15, "Чечевичный дал", "Дал из чечевицы со специями", 430, 1, 1, 1, 420, "Чечевица, лук, карри", "");
        insertMenu(db, 15, "Курица тикка масала", "Курица в пряном сливочном соусе", 690, 0, 0, 1, 720, "Курица, сливки, карри", "Молочные продукты");
        insertMenu(db, 15, "Палак панир", "Шпинат с сыром панир", 560, 1, 0, 1, 540, "Шпинат, сыр, специи", "Молочные продукты");
        insertMenu(db, 15, "Нут масала", "Нут в томатном соусе", 470, 1, 1, 1, 510, "Нут, помидор, лук, карри", "");
        insertMenu(db, 15, "Овощной бирьяни", "Рис со специями и овощами", 530, 1, 1, 1, 620, "Рис, морковь, перец, специи", "");
        insertMenu(db, 16, "Вок с курицей", "Лапша с курицей и овощами", 520, 0, 0, 0, 650, "Курица, лапша, морковь, соевый соус", "Глютен, Соевые продукты");
        insertMenu(db, 16, "Рис с овощами", "Жареный рис с овощами", 410, 1, 1, 1, 520, "Рис, морковь, лук, перец", "");
        insertMenu(db, 16, "Говядина в кисло-сладком соусе", "Говядина с овощами", 690, 0, 0, 0, 740, "Говядина, перец, соевый соус", "Соевые продукты, Глютен");
        insertMenu(db, 16, "Тофу с овощами", "Тофу в пряном соусе", 470, 1, 1, 0, 500, "Тофу, перец, соевый соус", "Соевые продукты, Глютен");
        insertMenu(db, 16, "Суп с лапшой", "Лёгкий китайский суп", 390, 1, 1, 0, 430, "Лапша, морковь, лук", "Глютен");
        insertMenu(db, 16, "Курица с кешью", "Курица с овощами и орехами", 640, 0, 0, 1, 680, "Курица, перец, орехи", "Орехи");
        insertMenu(db, 17, "Вок с курицей", "Лапша с курицей и овощами", 520, 0, 0, 0, 650, "Курица, лапша, морковь, соевый соус", "Глютен, Соевые продукты");
        insertMenu(db, 17, "Рис с овощами", "Жареный рис с овощами", 410, 1, 1, 1, 520, "Рис, морковь, лук, перец", "");
        insertMenu(db, 17, "Говядина в кисло-сладком соусе", "Говядина с овощами", 690, 0, 0, 0, 740, "Говядина, перец, соевый соус", "Соевые продукты, Глютен");
        insertMenu(db, 17, "Тофу с овощами", "Тофу в пряном соусе", 470, 1, 1, 0, 500, "Тофу, перец, соевый соус", "Соевые продукты, Глютен");
        insertMenu(db, 17, "Суп с лапшой", "Лёгкий китайский суп", 390, 1, 1, 0, 430, "Лапша, морковь, лук", "Глютен");
        insertMenu(db, 17, "Курица с кешью", "Курица с овощами и орехами", 640, 0, 0, 1, 680, "Курица, перец, орехи", "Орехи");
        insertMenu(db, 18, "Вок с курицей", "Лапша с курицей и овощами", 520, 0, 0, 0, 650, "Курица, лапша, морковь, соевый соус", "Глютен, Соевые продукты");
        insertMenu(db, 18, "Рис с овощами", "Жареный рис с овощами", 410, 1, 1, 1, 520, "Рис, морковь, лук, перец", "");
        insertMenu(db, 18, "Говядина в кисло-сладком соусе", "Говядина с овощами", 690, 0, 0, 0, 740, "Говядина, перец, соевый соус", "Соевые продукты, Глютен");
        insertMenu(db, 18, "Тофу с овощами", "Тофу в пряном соусе", 470, 1, 1, 0, 500, "Тофу, перец, соевый соус", "Соевые продукты, Глютен");
        insertMenu(db, 18, "Суп с лапшой", "Лёгкий китайский суп", 390, 1, 1, 0, 430, "Лапша, морковь, лук", "Глютен");
        insertMenu(db, 18, "Курица с кешью", "Курица с овощами и орехами", 640, 0, 0, 1, 680, "Курица, перец, орехи", "Орехи");
        insertMenu(db, 19, "Тако с говядиной", "Тако с мясом, овощами и соусом", 430, 0, 0, 0, 520, "Говядина, тортилья, помидор, лук", "Глютен");
        insertMenu(db, 19, "Буррито с курицей", "Большой буррито с рисом и курицей", 590, 0, 0, 0, 760, "Курица, рис, тортилья, фасоль", "Глютен");
        insertMenu(db, 19, "Кесадилья с сыром", "Тортилья с сыром и овощами", 470, 1, 0, 0, 620, "Тортилья, сыр, перец", "Глютен, Молочные продукты");
        insertMenu(db, 19, "Веган тако", "Тако с фасолью и овощами", 390, 1, 1, 0, 430, "Фасоль, тортилья, помидор, лук", "Глютен");
        insertMenu(db, 19, "Начос с сальсой", "Кукурузные чипсы и томатная сальса", 360, 1, 1, 1, 450, "Кукуруза, помидор, лук", "");
        insertMenu(db, 19, "Боул мексиканский", "Рис, фасоль, овощи и авокадо", 520, 1, 1, 1, 610, "Рис, фасоль, авокадо, перец", "");
        insertMenu(db, 20, "Тако с говядиной", "Тако с мясом, овощами и соусом", 430, 0, 0, 0, 520, "Говядина, тортилья, помидор, лук", "Глютен");
        insertMenu(db, 20, "Буррито с курицей", "Большой буррито с рисом и курицей", 590, 0, 0, 0, 760, "Курица, рис, тортилья, фасоль", "Глютен");
        insertMenu(db, 20, "Кесадилья с сыром", "Тортилья с сыром и овощами", 470, 1, 0, 0, 620, "Тортилья, сыр, перец", "Глютен, Молочные продукты");
        insertMenu(db, 20, "Веган тако", "Тако с фасолью и овощами", 390, 1, 1, 0, 430, "Фасоль, тортилья, помидор, лук", "Глютен");
        insertMenu(db, 20, "Начос с сальсой", "Кукурузные чипсы и томатная сальса", 360, 1, 1, 1, 450, "Кукуруза, помидор, лук", "");
        insertMenu(db, 20, "Боул мексиканский", "Рис, фасоль, овощи и авокадо", 520, 1, 1, 1, 610, "Рис, фасоль, авокадо, перец", "");
        insertMenu(db, 21, "Тако с говядиной", "Тако с мясом, овощами и соусом", 430, 0, 0, 0, 520, "Говядина, тортилья, помидор, лук", "Глютен");
        insertMenu(db, 21, "Буррито с курицей", "Большой буррито с рисом и курицей", 590, 0, 0, 0, 760, "Курица, рис, тортилья, фасоль", "Глютен");
        insertMenu(db, 21, "Кесадилья с сыром", "Тортилья с сыром и овощами", 470, 1, 0, 0, 620, "Тортилья, сыр, перец", "Глютен, Молочные продукты");
        insertMenu(db, 21, "Веган тако", "Тако с фасолью и овощами", 390, 1, 1, 0, 430, "Фасоль, тортилья, помидор, лук", "Глютен");
        insertMenu(db, 21, "Начос с сальсой", "Кукурузные чипсы и томатная сальса", 360, 1, 1, 1, 450, "Кукуруза, помидор, лук", "");
        insertMenu(db, 21, "Боул мексиканский", "Рис, фасоль, овощи и авокадо", 520, 1, 1, 1, 610, "Рис, фасоль, авокадо, перец", "");
        insertMenu(db, 22, "Бибимбап с говядиной", "Рис с овощами, мясом и яйцом", 680, 0, 0, 1, 760, "Рис, говядина, яйцо, морковь", "Яйца");
        insertMenu(db, 22, "Кимчи рамен", "Острый рамен с кимчи", 560, 1, 1, 0, 610, "Лапша, кимчи, лук", "Глютен");
        insertMenu(db, 22, "Токпокки", "Рисовые клёцки в остром соусе", 430, 1, 1, 1, 520, "Рисовые клёцки, соус, лук", "");
        insertMenu(db, 22, "Корейская курица", "Хрустящая курица в соусе", 620, 0, 0, 0, 820, "Курица, соевый соус, мука", "Глютен, Соевые продукты");
        insertMenu(db, 22, "Кимчи боул", "Рис, кимчи, овощи и тофу", 490, 1, 1, 1, 540, "Рис, кимчи, тофу, огурец", "Соевые продукты");
        insertMenu(db, 22, "Суп кукси", "Холодный суп с лапшой", 470, 0, 0, 0, 560, "Лапша, говядина, огурец", "Глютен");
        insertMenu(db, 23, "Бибимбап с говядиной", "Рис с овощами, мясом и яйцом", 680, 0, 0, 1, 760, "Рис, говядина, яйцо, морковь", "Яйца");
        insertMenu(db, 23, "Кимчи рамен", "Острый рамен с кимчи", 560, 1, 1, 0, 610, "Лапша, кимчи, лук", "Глютен");
        insertMenu(db, 23, "Токпокки", "Рисовые клёцки в остром соусе", 430, 1, 1, 1, 520, "Рисовые клёцки, соус, лук", "");
        insertMenu(db, 23, "Корейская курица", "Хрустящая курица в соусе", 620, 0, 0, 0, 820, "Курица, соевый соус, мука", "Глютен, Соевые продукты");
        insertMenu(db, 23, "Кимчи боул", "Рис, кимчи, овощи и тофу", 490, 1, 1, 1, 540, "Рис, кимчи, тофу, огурец", "Соевые продукты");
        insertMenu(db, 23, "Суп кукси", "Холодный суп с лапшой", 470, 0, 0, 0, 560, "Лапша, говядина, огурец", "Глютен");
        insertMenu(db, 24, "Бибимбап с говядиной", "Рис с овощами, мясом и яйцом", 680, 0, 0, 1, 760, "Рис, говядина, яйцо, морковь", "Яйца");
        insertMenu(db, 24, "Кимчи рамен", "Острый рамен с кимчи", 560, 1, 1, 0, 610, "Лапша, кимчи, лук", "Глютен");
        insertMenu(db, 24, "Токпокки", "Рисовые клёцки в остром соусе", 430, 1, 1, 1, 520, "Рисовые клёцки, соус, лук", "");
        insertMenu(db, 24, "Корейская курица", "Хрустящая курица в соусе", 620, 0, 0, 0, 820, "Курица, соевый соус, мука", "Глютен, Соевые продукты");
        insertMenu(db, 24, "Кимчи боул", "Рис, кимчи, овощи и тофу", 490, 1, 1, 1, 540, "Рис, кимчи, тофу, огурец", "Соевые продукты");
        insertMenu(db, 24, "Суп кукси", "Холодный суп с лапшой", 470, 0, 0, 0, 560, "Лапша, говядина, огурец", "Глютен");
        insertMenu(db, 25, "Боул с киноа", "Киноа, овощи, авокадо и зелень", 590, 1, 1, 1, 520, "Киноа, авокадо, огурец, шпинат", "");
        insertMenu(db, 25, "Индейка с овощами", "Индейка на гриле с овощами", 690, 0, 0, 1, 610, "Индейка, кабачок, перец", "");
        insertMenu(db, 25, "Салат с тунцом", "Лёгкий салат с тунцом и яйцом", 580, 0, 0, 1, 430, "Тунец, яйцо, огурец, шпинат", "Рыба, Яйца");
        insertMenu(db, 25, "Суп из чечевицы", "Белковый суп с чечевицей", 390, 1, 1, 1, 360, "Чечевица, морковь, лук", "");
        insertMenu(db, 25, "Кето-тарелка", "Курица, авокадо и зелёные овощи", 740, 0, 0, 1, 650, "Курица, авокадо, шпинат", "");
        insertMenu(db, 25, "Овощи на гриле", "Кабачок, перец и баклажан", 420, 1, 1, 1, 300, "Кабачок, перец, баклажан", "");
        insertMenu(db, 26, "Фалафель боул", "Нутовые шарики, овощи и соус", 540, 1, 1, 1, 560, "Нут, огурец, помидор, тахини", "Кунжут");
        insertMenu(db, 26, "Тофу с киноа", "Тофу, киноа и овощи", 620, 1, 1, 1, 590, "Тофу, киноа, перец, шпинат", "Соевые продукты");
        insertMenu(db, 26, "Веган паста", "Паста с томатным соусом без сыра", 480, 1, 1, 0, 610, "Паста, помидор, базилик", "Глютен");
        insertMenu(db, 26, "Крем-суп из тыквы", "Постный крем-суп", 390, 1, 1, 1, 330, "Тыква, морковь, лук", "");
        insertMenu(db, 26, "Салат с авокадо", "Авокадо, овощи и зелень", 520, 1, 1, 1, 440, "Авокадо, огурец, шпинат", "");
        insertMenu(db, 26, "Чили син карне", "Фасоль, томаты и специи", 500, 1, 1, 1, 520, "Фасоль, помидор, лук, паприка", "");
        insertMenu(db, 27, "Шаурма с курицей", "Пита с курицей, овощами и соусом", 360, 0, 0, 0, 620, "Курица, пита, огурец, соус", "Глютен, Молочные продукты");
        insertMenu(db, 27, "Фалафель в пите", "Фалафель, овощи и тахини", 330, 1, 1, 0, 520, "Нут, пита, огурец, тахини", "Глютен, Кунжут");
        insertMenu(db, 27, "Хумус с овощами", "Хумус, овощи и лепёшка", 390, 1, 1, 0, 480, "Нут, кунжут, огурец, хлеб", "Кунжут, Глютен");
        insertMenu(db, 27, "Кебаб с рисом", "Мясной кебаб с рисом", 640, 0, 0, 1, 780, "Говядина, рис, лук", "");
        insertMenu(db, 27, "Табуле", "Салат с булгуром и зеленью", 350, 1, 1, 0, 360, "Булгур, помидор, кинза", "Глютен");
        insertMenu(db, 27, "Чечевичный суп", "Суп из красной чечевицы", 310, 1, 1, 1, 330, "Чечевица, морковь, лук", "");
        insertMenu(db, 28, "Шаурма с курицей", "Пита с курицей, овощами и соусом", 360, 0, 0, 0, 620, "Курица, пита, огурец, соус", "Глютен, Молочные продукты");
        insertMenu(db, 28, "Фалафель в пите", "Фалафель, овощи и тахини", 330, 1, 1, 0, 520, "Нут, пита, огурец, тахини", "Глютен, Кунжут");
        insertMenu(db, 28, "Хумус с овощами", "Хумус, овощи и лепёшка", 390, 1, 1, 0, 480, "Нут, кунжут, огурец, хлеб", "Кунжут, Глютен");
        insertMenu(db, 28, "Кебаб с рисом", "Мясной кебаб с рисом", 640, 0, 0, 1, 780, "Говядина, рис, лук", "");
        insertMenu(db, 28, "Табуле", "Салат с булгуром и зеленью", 350, 1, 1, 0, 360, "Булгур, помидор, кинза", "Глютен");
        insertMenu(db, 28, "Чечевичный суп", "Суп из красной чечевицы", 310, 1, 1, 1, 330, "Чечевица, морковь, лук", "");
        insertMenu(db, 29, "Классический бургер", "Говяжья котлета, булочка и овощи", 590, 0, 0, 0, 850, "Говядина, хлеб, сыр, огурец", "Глютен, Молочные продукты");
        insertMenu(db, 29, "Куриный бургер", "Курица, булочка и соус", 540, 0, 0, 0, 780, "Курица, хлеб, соус", "Глютен, Яйца");
        insertMenu(db, 29, "Вегги бургер", "Котлета из фасоли и овощи", 520, 1, 1, 0, 690, "Фасоль, хлеб, помидор", "Глютен");
        insertMenu(db, 29, "Картофель по-деревенски", "Запечённый картофель со специями", 260, 1, 1, 1, 390, "Картофель, паприка", "");
        insertMenu(db, 29, "Салат коул-слоу", "Капуста, морковь и лёгкий соус", 290, 1, 0, 1, 310, "Капуста, морковь, соус", "Яйца");
        insertMenu(db, 29, "Стейк с овощами", "Говяжий стейк и овощи гриль", 950, 0, 0, 1, 720, "Говядина, кабачок, перец", "");
        insertMenu(db, 30, "Омлет с сыром", "Омлет с сыром и зеленью", 320, 1, 0, 1, 430, "Яйца, сыр, зелень", "Яйца, Молочные продукты");
        insertMenu(db, 30, "Сырники с ягодами", "Творожные сырники", 390, 1, 0, 0, 520, "Творог, яйца, мука", "Молочные продукты, Яйца, Глютен");
        insertMenu(db, 30, "Круассан с индейкой", "Круассан с индейкой и овощами", 430, 0, 0, 0, 560, "Хлеб, индейка, сыр", "Глютен, Молочные продукты");
        insertMenu(db, 30, "Греческий салат", "Овощи, сыр и оливки", 480, 1, 0, 1, 410, "Огурец, помидор, сыр", "Молочные продукты");
        insertMenu(db, 30, "Куриный суп", "Лёгкий суп с курицей", 350, 0, 0, 1, 330, "Курица, морковь, лук", "");
        insertMenu(db, 30, "Панкейки", "Панкейки с бананом", 360, 1, 0, 0, 590, "Мука, яйца, банан", "Глютен, Яйца");
        insertMenu(db, 31, "Лосось на гриле", "Лосось с овощами", 980, 0, 0, 1, 620, "Лосось, кабачок, лимон", "Рыба");
        insertMenu(db, 31, "Паста с креветками", "Паста с креветками и сливочным соусом", 790, 0, 0, 0, 740, "Паста, креветки, сливки", "Глютен, Морепродукты, Молочные продукты");
        insertMenu(db, 31, "Салат с тунцом", "Тунец, овощи и зелень", 650, 0, 0, 1, 420, "Тунец, огурец, шпинат", "Рыба");
        insertMenu(db, 31, "Кальмар с рисом", "Кальмар, рис и овощи", 720, 0, 0, 1, 560, "Кальмар, рис, перец", "Морепродукты");
        insertMenu(db, 31, "Рыбный суп", "Суп с рыбой и овощами", 540, 0, 0, 1, 380, "Рыба, картофель, морковь", "Рыба");
        insertMenu(db, 31, "Овощной гарнир", "Овощи на гриле", 390, 1, 1, 1, 290, "Кабачок, баклажан, перец", "");
        insertMenu(db, 32, "Омлет с сыром", "Омлет с сыром и зеленью", 320, 1, 0, 1, 430, "Яйца, сыр, зелень", "Яйца, Молочные продукты");
        insertMenu(db, 32, "Сырники с ягодами", "Творожные сырники", 390, 1, 0, 0, 520, "Творог, яйца, мука", "Молочные продукты, Яйца, Глютен");
        insertMenu(db, 32, "Круассан с индейкой", "Круассан с индейкой и овощами", 430, 0, 0, 0, 560, "Хлеб, индейка, сыр", "Глютен, Молочные продукты");
        insertMenu(db, 32, "Греческий салат", "Овощи, сыр и оливки", 480, 1, 0, 1, 410, "Огурец, помидор, сыр", "Молочные продукты");
        insertMenu(db, 32, "Куриный суп", "Лёгкий суп с курицей", 350, 0, 0, 1, 330, "Курица, морковь, лук", "");
        insertMenu(db, 32, "Панкейки", "Панкейки с бананом", 360, 1, 0, 0, 590, "Мука, яйца, банан", "Глютен, Яйца");

        insertReview(db, 1, "Хорошее место: Паста, ризотто и салаты. Подходит для спокойного ужина.", 4.7);
        insertReview(db, 1, "Понравилось меню и скорость обслуживания. Средний чек около 1250 ₽.", 4.8);
        insertReview(db, 2, "Хорошее место: Семейная траттория с пастой, пиццей и домашними десертами.", 4.5);
        insertReview(db, 2, "Понравилось меню и скорость обслуживания. Средний чек около 1150 ₽.", 4.6);
        insertReview(db, 3, "Хорошее место: Быстрая пицца, паста и доставка до дома.", 4.3);
        insertReview(db, 3, "Понравилось меню и скорость обслуживания. Средний чек около 850 ₽.", 4.4);
        insertReview(db, 4, "Хорошее место: Рамен, боулы, роллы и быстрый самовывоз.", 4.5);
        insertReview(db, 4, "Понравилось меню и скорость обслуживания. Средний чек около 1100 ₽.", 4.6);
        insertReview(db, 5, "Хорошее место: Поке, суши, сашими и лёгкие японские блюда.", 4.6);
        insertReview(db, 5, "Понравилось меню и скорость обслуживания. Средний чек около 1180 ₽.", 4.7);
        insertReview(db, 6, "Хорошее место: Доступные роллы и наборы для доставки.", 4.2);
        insertReview(db, 6, "Понравилось меню и скорость обслуживания. Средний чек около 980 ₽.", 4.3);
        insertReview(db, 7, "Хорошее место: Домашняя кухня, супы, каши и горячие блюда.", 4.4);
        insertReview(db, 7, "Понравилось меню и скорость обслуживания. Средний чек около 650 ₽.", 4.5);
        insertReview(db, 8, "Хорошее место: Русская кухня с супами, пирогами и домашними обедами.", 4.5);
        insertReview(db, 8, "Понравилось меню и скорость обслуживания. Средний чек около 760 ₽.", 4.6);
        insertReview(db, 9, "Хорошее место: Бюджетные комплексные обеды и простые горячие блюда.", 4.1);
        insertReview(db, 9, "Понравилось меню и скорость обслуживания. Средний чек около 520 ₽.", 4.2);
        insertReview(db, 10, "Хорошее место: Хинкали, хачапури и мясные блюда.", 4.6);
        insertReview(db, 10, "Понравилось меню и скорость обслуживания. Средний чек около 1400 ₽.", 4.7);
        insertReview(db, 11, "Хорошее место: Грузинская классика, специи, зелень и домашнее вино в меню без алкоголя.", 4.7);
        insertReview(db, 11, "Понравилось меню и скорость обслуживания. Средний чек около 1320 ₽.", 4.8);
        insertReview(db, 12, "Хорошее место: Хачапури, овощи на гриле и блюда для компании.", 4.4);
        insertReview(db, 12, "Понравилось меню и скорость обслуживания. Средний чек около 1100 ₽.", 4.5);
        insertReview(db, 13, "Хорошее место: Вегетарианские карри, супы и блюда без мяса.", 4.3);
        insertReview(db, 13, "Понравилось меню и скорость обслуживания. Средний чек около 850 ₽.", 4.4);
        insertReview(db, 14, "Хорошее место: Карри, рис, лепёшки и острые блюда на выбор.", 4.5);
        insertReview(db, 14, "Понравилось меню и скорость обслуживания. Средний чек около 930 ₽.", 4.6);
        insertReview(db, 15, "Хорошее место: Индийская кухня с большим выбором вегетарианских блюд.", 4.6);
        insertReview(db, 15, "Понравилось меню и скорость обслуживания. Средний чек около 1200 ₽.", 4.7);
        insertReview(db, 16, "Хорошее место: Вок, рис, лапша и блюда в кисло-сладком соусе.", 4.4);
        insertReview(db, 16, "Понравилось меню и скорость обслуживания. Средний чек около 870 ₽.", 4.5);
        insertReview(db, 17, "Хорошее место: Китайская лапша, супы и чайная карта.", 4.2);
        insertReview(db, 17, "Понравилось меню и скорость обслуживания. Средний чек около 720 ₽.", 4.3);
        insertReview(db, 18, "Хорошее место: Классические китайские блюда и большие порции.", 4.5);
        insertReview(db, 18, "Понравилось меню и скорость обслуживания. Средний чек около 1120 ₽.", 4.6);
        insertReview(db, 19, "Хорошее место: Тако, начос, буррито и острые соусы.", 4.3);
        insertReview(db, 19, "Понравилось меню и скорость обслуживания. Средний чек около 780 ₽.", 4.4);
        insertReview(db, 20, "Хорошее место: Сытные буррито и быстрые комбо-наборы.", 4.4);
        insertReview(db, 20, "Понравилось меню и скорость обслуживания. Средний чек около 890 ₽.", 4.5);
        insertReview(db, 21, "Хорошее место: Кесадильи, тако и вегетарианские мексиканские блюда.", 4.1);
        insertReview(db, 21, "Понравилось меню и скорость обслуживания. Средний чек около 740 ₽.", 4.2);
        insertReview(db, 22, "Хорошее место: Корейское барбекю, кимчи и горячие сковородки.", 4.7);
        insertReview(db, 22, "Понравилось меню и скорость обслуживания. Средний чек около 1500 ₽.", 4.8);
        insertReview(db, 23, "Хорошее место: Боулы, кимчи, токпокки и острые супы.", 4.4);
        insertReview(db, 23, "Понравилось меню и скорость обслуживания. Средний чек около 920 ₽.", 4.5);
        insertReview(db, 24, "Хорошее место: Бибимбап, лапша и блюда навынос.", 4.3);
        insertReview(db, 24, "Понравилось меню и скорость обслуживания. Средний чек около 850 ₽.", 4.4);
        insertReview(db, 25, "Хорошее место: Боулы, салаты, белковые блюда и низкокалорийные варианты.", 4.6);
        insertReview(db, 25, "Понравилось меню и скорость обслуживания. Средний чек около 980 ₽.", 4.7);
        insertReview(db, 26, "Хорошее место: Полностью веганское меню без молочных продуктов и яиц.", 4.7);
        insertReview(db, 26, "Понравилось меню и скорость обслуживания. Средний чек около 1050 ₽.", 4.8);
        insertReview(db, 27, "Хорошее место: Быстрая ближневосточная кухня, шаурма и фалафель.", 4.0);
        insertReview(db, 27, "Понравилось меню и скорость обслуживания. Средний чек около 550 ₽.", 4.1);
        insertReview(db, 28, "Хорошее место: Хумус, фалафель, пита и веганские закуски.", 4.5);
        insertReview(db, 28, "Понравилось меню и скорость обслуживания. Средний чек около 760 ₽.", 4.6);
        insertReview(db, 29, "Хорошее место: Бургеры, картофель и салаты с возможностью доставки.", 4.2);
        insertReview(db, 29, "Понравилось меню и скорость обслуживания. Средний чек около 950 ₽.", 4.3);
        insertReview(db, 30, "Хорошее место: Омлеты, каши, сырники и лёгкие завтраки весь день.", 4.5);
        insertReview(db, 30, "Понравилось меню и скорость обслуживания. Средний чек около 840 ₽.", 4.6);
        insertReview(db, 31, "Хорошее место: Рыба, морепродукты и блюда на гриле.", 4.6);
        insertReview(db, 31, "Понравилось меню и скорость обслуживания. Средний чек около 1650 ₽.", 4.7);
        insertReview(db, 32, "Хорошее место: Кофейня с выпечкой, салатами и лёгкими обедами.", 4.3);
        insertReview(db, 32, "Понравилось меню и скорость обслуживания. Средний чек около 620 ₽.", 4.4);
    }

    private void insertRestaurant(SQLiteDatabase db, String name, String location, String cuisine, double rating, String priceRange, int avgCheck, int distanceMin, String description, String contact, String hours, int delivery, int booking) {
        db.execSQL("INSERT INTO " + TABLE_RESTAURANTS + "(" + COLUMN_REST_NAME + "," + COLUMN_LOCATION + "," + COLUMN_CUISINE + "," + COLUMN_RATING + "," + COLUMN_PRICE_RANGE + "," + COLUMN_AVG_CHECK + "," + COLUMN_DISTANCE_MIN + "," + COLUMN_DESCRIPTION + "," + COLUMN_CONTACT_INFO + "," + COLUMN_OPENING_HOURS + "," + COLUMN_HAS_DELIVERY + "," + COLUMN_HAS_BOOKING + ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{name, location, cuisine, rating, priceRange, avgCheck, distanceMin, description, contact, hours, delivery, booking});
    }

    private void insertMenu(SQLiteDatabase db, int restaurantId, String dish, String description, double price, int vegetarian, int vegan, int glutenFree, int calories, String ingredients, String allergens) {
        db.execSQL("INSERT INTO " + TABLE_RESTAURANT_MENU + "(" + COLUMN_RESTAURANT_ID + "," + COLUMN_DISH_NAME + "," + COLUMN_DESCRIPTION + "," + COLUMN_PRICE + "," + COLUMN_IS_VEGETARIAN + "," + COLUMN_IS_VEGAN + "," + COLUMN_IS_GLUTEN_FREE + "," + COLUMN_CALORIES + "," + COLUMN_INGREDIENTS_TEXT + "," + COLUMN_ALLERGENS_TEXT + ") VALUES(?,?,?,?,?,?,?,?,?,?)",
                new Object[]{restaurantId, dish, description, price, vegetarian, vegan, glutenFree, calories, ingredients, allergens});
    }

    private void insertReview(SQLiteDatabase db, int restaurantId, String text, double rating) {
        db.execSQL("INSERT INTO " + TABLE_RESTAURANT_REVIEWS + "(" + COLUMN_RESTAURANT_ID + "," + COLUMN_REVIEW_TEXT + "," + COLUMN_RATING + ") VALUES(?,?,?)",
                new Object[]{restaurantId, text, rating});
    }

    private void seedRecipeCatalog(SQLiteDatabase db) {
        String[] formats = new String[] {
                "Боул", "Паста", "Суп", "Салат", "Рагу", "Запеканка", "Сковорода", "Карри", "Ролл", "Тёплая тарелка"
        };
        String[] bases = new String[] {
                "рисом", "гречкой", "картофелем", "пастой", "лапшой", "булгуром", "киноа", "нутом", "фасолью", "овощами"
        };
        String[] baseIngredients = new String[] {
                "Рис", "Гречка", "Картофель", "Паста", "Лапша", "Булгур", "Киноа", "Нут", "Фасоль", "Овощи"
        };
        String[] proteins = new String[] {
                "курицей", "индейкой", "говядиной", "рыбой", "яйцом", "тофу", "нутом", "сыром", "фасолью", "овощами"
        };
        String[] proteinIngredients = new String[] {
                "Курица", "Индейка", "Говядина", "Рыба", "Яйца", "Тофу", "Нут", "Сыр", "Фасоль", "Овощи"
        };
        String[] vegetables = new String[] {
                "морковью", "помидорами", "огурцом", "луком", "кабачком", "баклажаном", "шпинатом", "перцем", "авокадо", "зеленью"
        };
        String[] vegetableIngredients = new String[] {
                "Морковь", "Помидор", "Огурец", "Лук", "Кабачок", "Баклажан", "Шпинат", "Перец болгарский", "Авокадо", "Зелень"
        };
        String[] sauces = new String[] {
                "томатным соусом", "сметанной заправкой", "соевым соусом", "лимонной заправкой", "карри", "паприкой", "чесночным соусом", "йогуртовой заправкой", "базиликом", "зелёным соусом"
        };
        String[] sauceIngredients = new String[] {
                "Томатный соус", "Сметана", "Соевый соус", "Лимон", "Карри", "Паприка", "Чеснок", "Йогурт", "Базилик", "Зелень"
        };

        int count = 0;
        for (int f = 0; f < formats.length && count < 500; f++) {
            for (int b = 0; b < bases.length && count < 500; b++) {
                for (int pr = 0; pr < proteins.length && count < 500; pr++) {
                    int v = (count + f + b + pr) % vegetables.length;
                    int sc = (count + b + pr) % sauces.length;
                    count++;
                    String name = formats[f] + " с " + bases[b] + ", " + proteins[pr] + " и " + vegetables[v];
                    String description = "Быстрый вариант для ежедневного питания №" + count + ".";
                    int cookTime = 15 + (count % 8) * 5;
                    int calories = 280 + (count % 10) * 45;
                    String ingredients = baseIngredients[b] + ", " + proteinIngredients[pr] + ", " + vegetableIngredients[v] + ", " + sauceIngredients[sc] + ", Соль, Перец";
                    String instructions = "1. Подготовьте и нарежьте ингредиенты.\n" +
                            "2. Приготовьте основу до мягкости.\n" +
                            "3. Добавьте белковый ингредиент, овощи и соус.\n" +
                            "4. Тушите или обжарьте до готовности, затем подавайте горячим.";
                    int vegetarian = (pr >= 4) ? 1 : 0;
                    int vegan = (pr == 5 || pr == 6 || pr == 8 || pr == 9) ? 1 : 0;
                    if (sc == 1 || sc == 7) vegan = 0;
                    int glutenFree = (b == 3 || b == 4 || b == 5 || sc == 2) ? 0 : 1;
                    insertCatalogRecipe(db, name, description, cookTime, calories, vegetarian, vegan, glutenFree, ingredients, instructions);
                }
            }
        }
    }

    private void insertCatalogRecipe(SQLiteDatabase db, String name, String description, int cookTime, int calories, int vegetarian, int vegan, int glutenFree, String ingredients, String instructions) {
        db.execSQL("INSERT INTO " + TABLE_RECIPES + "(" +
                        COLUMN_RECIPE_NAME + "," + COLUMN_DESCRIPTION + "," + COLUMN_COOK_TIME + "," + COLUMN_CALORIES + "," +
                        COLUMN_IS_VEGETARIAN + "," + COLUMN_IS_VEGAN + "," + COLUMN_IS_GLUTEN_FREE + "," +
                        COLUMN_INGREDIENTS + "," + COLUMN_MISSING_INGREDIENTS + "," + COLUMN_SHOPPING_LIST + "," +
                        COLUMN_INSTRUCTIONS + "," + COLUMN_SOURCE + ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{name, description, cookTime, calories, vegetarian, vegan, glutenFree, ingredients, "", "", instructions, "catalog"});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("Database", "Upgrading database from " + oldVersion + " to " + newVersion + "; data will be recreated for prototype build");
        dropAllTables(db);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void dropAllTables(SQLiteDatabase db) {
        String[] tables = {
                TABLE_APP_LOGS,
                TABLE_DICTIONARIES,
                TABLE_FAVORITE_RECIPES,
                TABLE_FAVORITE_RESTAURANTS,
                TABLE_USER_HISTORY,
                TABLE_USER_QUERIES,
                TABLE_RECIPE_INGREDIENTS,
                TABLE_INGREDIENTS,
                TABLE_RECIPES,
                TABLE_RESTAURANT_REVIEWS,
                TABLE_RESTAURANT_MENU,
                TABLE_RESTAURANTS,
                TABLE_USER_PREFERENCES,
                TABLE_USERS
        };
        for (String table : tables) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
    }
}
