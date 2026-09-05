package com.example.models

data class District(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val divisionBn: String
)

object BangladeshDistricts {
    val allDistricts: List<District> = listOf(
        // Dhaka Division
        District("dhaka", "ঢাকা", "Dhaka", "ঢাকা"),
        District("gazipur", "গাজীপুর", "Gazipur", "ঢাকা"),
        District("narayanganj", "নারায়ণগঞ্জ", "Narayanganj", "ঢাকা"),
        District("tangail", "টাঙ্গাইল", "Tangail", "ঢাকা"),
        District("faridpur", "ফরিদপুর", "Faridpur", "ঢাকা"),
        District("manikganj", "মানিকগঞ্জ", "Manikganj", "ঢাকা"),
        District("munshiganj", "মুন্সীগঞ্জ", "Munshiganj", "ঢাকা"),
        District("narsingdi", "নরসিংদী", "Narsingdi", "ঢাকা"),
        District("gopalganj", "গোপালগঞ্জ", "Gopalganj", "ঢাকা"),
        District("madaripur", "মাদারীপুর", "Madaripur", "ঢাকা"),
        District("rajbari", "রাজবাড়ী", "Rajbari", "ঢাকা"),
        District("shariatpur", "শরীয়তপুর", "Shariatpur", "ঢাকা"),
        District("kishoreganj", "কিশোরগঞ্জ", "Kishoreganj", "ঢাকা"),

        // Chattogram Division
        District("chattogram", "চট্টগ্রাম", "Chattogram", "চট্টগ্রাম"),
        District("coxsbazar", "কক্সবাজার", "Cox's Bazar", "চট্টগ্রাম"),
        District("cumilla", "কুমিল্লা", "Cumilla", "চট্টগ্রাম"),
        District("feni", "ফেনী", "Feni", "চট্টগ্রাম"),
        District("brahmanbaria", "ব্রাহ্মণবাড়িয়া", "Brahmanbaria", "চট্টগ্রাম"),
        District("noakhali", "নোয়াখালী", "Noakhali", "চট্টগ্রাম"),
        District("chandpur", "চাঁদপুর", "Chandpur", "চট্টগ্রাম"),
        District("lakshmipur", "লক্ষ্মীপুর", "Lakshmipur", "চট্টগ্রাম"),
        District("rangamati", "রাঙ্গামাটি", "Rangamati", "চট্টগ্রাম"),
        District("khagrachhari", "খাগড়াছড়ি", "Khagrachhari", "চট্টগ্রাম"),
        District("bandarban", "বান্দরবান", "Bandarban", "চট্টগ্রাম"),

        // Sylhet Division
        District("sylhet", "সিলেট", "Sylhet", "সিলেট"),
        District("moulvibazar", "মৌলভীবাজার", "Moulvibazar", "সিলেট"),
        District("habiganj", "হবিগঞ্জ", "Habiganj", "সিলেট"),
        District("sunamganj", "সুনামগঞ্জ", "Sunamganj", "সিলেট"),

        // Rajshahi Division
        District("rajshahi", "রাজশাহী", "Rajshahi", "রাজশাহী"),
        District("bogra", "বগুড়া", "Bogura", "রাজশাহী"),
        District("pabna", "পাবনা", "Pabna", "রাজশাহী"),
        District("sirajganj", "সিরাজগঞ্জ", "Sirajganj", "রাজশাহী"),
        District("naogaon", "নওগাঁ", "Naogaon", "রাজশাহী"),
        District("natore", "নাটোর", "Natore", "রাজশাহী"),
        District("chapainawabganj", "চাঁপাইনবাবগঞ্জ", "Chapainawabganj", "রাজশাহী"),
        District("joypurhat", "জয়পুরহাট", "Joypurhat", "রাজশাহী"),

        // Khulna Division
        District("khulna", "খুলনা", "Khulna", "খুলনা"),
        District("jashore", "যশোর", "Jashore", "খুলনা"),
        District("kushtia", "কুষ্টিয়া", "Kushtia", "খুলনা"),
        District("satkhira", "সাতক্ষীরা", "Satkhira", "খুলনা"),
        District("bagerhat", "বাগেরহাট", "Bagerhat", "খুলনা"),
        District("jhenaidah", "ঝিনাইদহ", "Jhenaidah", "খুলনা"),
        District("chuadanga", "চুয়াডাঙ্গা", "Chuadanga", "খুলনা"),
        District("magura", "মাগুরা", "Magura", "খুলনা"),
        District("meherpur", "মেহেরপুর", "Meherpur", "খুলনা"),
        District("narail", "নড়াইল", "Narail", "খুলনা"),

        // Barishal Division
        District("barishal", "বরিশাল", "Barishal", "বরিশাল"),
        District("bhola", "ভোলা", "Bhola", "বরিশাল"),
        District("patuakhali", "পটুয়াখালী", "Patuakhali", "বরিশাল"),
        District("pirojpur", "পিরোজপুর", "Pirojpur", "বরিশাল"),
        District("jhalokati", "ঝালকাঠি", "Jhalokati", "বরিশাল"),
        District("barguna", "বরগুনা", "Barguna", "বরিশাল"),

        // Rangpur Division
        District("rangpur", "রংপুর", "Rangpur", "রংপুর"),
        District("dinajpur", "দিনাজপুর", "Dinajpur", "রংপুর"),
        District("kurigram", "কুড়িগ্রাম", "Kurigram", "রংপুর"),
        District("gaibandha", "গাইবান্ধা", "Gaibandha", "রংপুর"),
        District("nilphamari", "নীলফামারী", "Nilphamari", "রংপুর"),
        District("thakurgaon", "ঠাকুরগাঁও", "Thakurgaon", "রংপুর"),
        District("lalmonirhat", "লালমনিরহাট", "Lalmonirhat", "রংপুর"),
        District("panchagarh", "পঞ্চগড়", "Panchagarh", "রংপুর"),

        // Mymensingh Division
        District("mymensingh", "ময়মনসিংহ", "Mymensingh", "ময়মনসিংহ"),
        District("jamalpur", "জামালপুর", "Jamalpur", "ময়মনসিংহ"),
        District("netrokona", "নেত্রকোণা", "Netrokona", "ময়মনসিংহ"),
        District("sherpur", "শেরপুর", "Sherpur", "ময়মনসিংহ")
    )
}
