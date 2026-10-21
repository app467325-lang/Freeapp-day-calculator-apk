package com.example.model

import androidx.compose.ui.graphics.Color

data class PortraitStyle(
    val bgTop: Color,
    val bgBottom: Color,
    val headwearColor: Color,
    val headwearShade: Color,
    val robeColor: Color,
    val skinLight: Color,
    val skinDark: Color,
    val beardColor: Color,
    val hasAgal: Boolean
)

data class Reciter(
    val id: String,
    val name: String,
    val riwayah: String,
    val server: String,
    val isNew: Boolean,
    val photoUrl: String? = null,
    val portraitStyle: PortraitStyle
)

data class Surah(
    val num: Int,
    val code: String,
    val en: String,
    val ar: String,
    val verses: Int,
    val tag: String // "fav" or "focus"
)

enum class AppTab(val index: Int, val id: String, val labelAr: String) {
    HOME(0, "home", "الرئيسية"),
    RECITERS(1, "reciters", "القراء"),
    PLAYLISTS(2, "playlists", "قوائم التشغيل"),
    SETTINGS(3, "settings", "الإعدادات"),
    SEARCH(4, "search", "بحث");

    companion object {
        fun fromIndex(idx: Int): AppTab = entries.getOrElse(idx.coerceIn(0, entries.size - 1)) { HOME }
    }
}

enum class PlaylistFilter(val id: String, val titleAr: String) {
    ALL("all", "تلاوات المفضلة والتركيز"),
    FAV("fav", "قائمة المفضلة ⭐"),
    FOCUS("focus", "قائمة التركيز والعمل 🎯")
}

object QuranDataRepository {
    val reciters = listOf(
        Reciter(
            id = "zawawi",
            name = "عبد الرحمن الزواوي",
            riwayah = "مرتل - تلاوة خاشعة",
            server = "https://server8.mp3quran.net/afs/",
            isNew = true,
            photoUrl = null,
            portraitStyle = PortraitStyle(
                bgTop = Color(0xFFFFFFFF),
                bgBottom = Color(0xFFE2E8F0),
                headwearColor = Color(0xFFFFFFFF),
                headwearShade = Color(0xFFE2E8F0),
                robeColor = Color(0xFFF8FAFC),
                skinLight = Color(0xFFE8BFA2),
                skinDark = Color(0xFFD4A383),
                beardColor = Color(0xFF141416),
                hasAgal = false
            )
        ),
        Reciter(
            id = "afasy",
            name = "مشاري راشد العفاسي",
            riwayah = "حفص عن عاصم",
            server = "https://server8.mp3quran.net/afs/",
            isNew = false,
            photoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2c/Mishary_Rashid_Alafasy.jpg/400px-Mishary_Rashid_Alafasy.jpg",
            portraitStyle = PortraitStyle(
                bgTop = Color(0xFF1E293B),
                bgBottom = Color(0xFF0F172A),
                headwearColor = Color(0xFFF8FAFC),
                headwearShade = Color(0xFFCBD5E1),
                robeColor = Color(0xFF1E293B),
                skinLight = Color(0xFFE5B899),
                skinDark = Color(0xFFC99878),
                beardColor = Color(0xFF1C1917),
                hasAgal = true
            )
        ),
        Reciter(
            id = "dosari",
            name = "ياسر الدوسري",
            riwayah = "حفص عن عاصم",
            server = "https://server11.mp3quran.net/yasser/",
            isNew = true,
            photoUrl = null,
            portraitStyle = PortraitStyle(
                bgTop = Color(0xFF1E3A8A),
                bgBottom = Color(0xFF0F172A),
                headwearColor = Color(0xFFFEF2F2),
                headwearShade = Color(0xFFFECACA),
                robeColor = Color(0xFFFFFFFF),
                skinLight = Color(0xFFE2B496),
                skinDark = Color(0xFFC69372),
                beardColor = Color(0xFF18181B),
                hasAgal = true
            )
        ),
        Reciter(
            id = "muaiqly",
            name = "ماهر المعيقلي",
            riwayah = "حفص عن عاصم",
            server = "https://server12.mp3quran.net/maher/",
            isNew = false,
            photoUrl = null,
            portraitStyle = PortraitStyle(
                bgTop = Color(0xFF312E81),
                bgBottom = Color(0xFF0F172A),
                headwearColor = Color(0xFFFFF1F2),
                headwearShade = Color(0xFFFDA4AF),
                robeColor = Color(0xFFF8FAFC),
                skinLight = Color(0xFFE6B89C),
                skinDark = Color(0xFFCA987A),
                beardColor = Color(0xFF1F2937),
                hasAgal = true
            )
        ),
        Reciter(
            id = "qatami",
            name = "ناصر القطامي",
            riwayah = "حفص عن عاصم",
            server = "https://server6.mp3quran.net/qtm/",
            isNew = true,
            photoUrl = null,
            portraitStyle = PortraitStyle(
                bgTop = Color(0xFF064E3B),
                bgBottom = Color(0xFF022C22),
                headwearColor = Color(0xFFFFFFFF),
                headwearShade = Color(0xFFE2E8F0),
                robeColor = Color(0xFF1E293B),
                skinLight = Color(0xFFE2B699),
                skinDark = Color(0xFFC59678),
                beardColor = Color(0xFF171717),
                hasAgal = true
            )
        ),
        Reciter(
            id = "basit",
            name = "عبد الباسط عبد الصمد",
            riwayah = "المصحف المرتل",
            server = "https://server7.mp3quran.net/basit/",
            isNew = false,
            photoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/Abdul_Basit_Abdul_Samad.jpg/400px-Abdul_Basit_Abdul_Samad.jpg",
            portraitStyle = PortraitStyle(
                bgTop = Color(0xFF3F3F46),
                bgBottom = Color(0xFF18181B),
                headwearColor = Color(0xFFFEFEFE),
                headwearShade = Color(0xFFD4D4D8),
                robeColor = Color(0xFF27272A),
                skinLight = Color(0xFFDDB092),
                skinDark = Color(0xFFBF8F70),
                beardColor = Color(0xFF27272A),
                hasAgal = false
            )
        )
    )

    val surahs = listOf(
        Surah(num = 1, code = "001", en = "1. Al-Fatihah", ar = "سورة الفاتحة", verses = 7, tag = "fav"),
        Surah(num = 2, code = "002", en = "2. Al-Baqarah", ar = "سورة البقرة", verses = 286, tag = "focus"),
        Surah(num = 18, code = "018", en = "18. Al-Kahf", ar = "سورة الكهف", verses = 110, tag = "fav"),
        Surah(num = 36, code = "036", en = "36. Ya-Sin", ar = "سورة يس", verses = 83, tag = "fav"),
        Surah(num = 55, code = "055", en = "55. Ar-Rahman", ar = "سورة الرحمن", verses = 78, tag = "focus"),
        Surah(num = 56, code = "056", en = "56. Al-Waqiah", ar = "سورة الواقعة", verses = 96, tag = "focus"),
        Surah(num = 67, code = "067", en = "67. Al-Mulk", ar = "سورة الملك", verses = 30, tag = "fav"),
        Surah(num = 112, code = "112", en = "112. Al-Ikhlas", ar = "سورة الإخلاص", verses = 4, tag = "fav"),
        Surah(num = 113, code = "113", en = "113. Al-Falaq", ar = "سورة الفلق", verses = 5, tag = "focus"),
        Surah(num = 114, code = "114", en = "114. An-Nas", ar = "سورة الناس", verses = 6, tag = "fav")
    )

    val initialLikedSurahNums = setOf(1, 18, 36, 67)
}
