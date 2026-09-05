package com.example.core.localization

/**
 * Bangla-first localization manager and string repository for Amar Khata.
 * Supports complete Bangla UI with English fallback and Bangla number formatting.
 */
object LocaleStrings {

    // App Branding
    const val APP_NAME_BN = "আমার খাতা"
    const val APP_NAME_EN = "Amar Khata"
    const val APP_TAGLINE_BN = "আপনার বিশ্বস্ত ডিজিটাল হিসাবের খাতা"
    const val APP_TAGLINE_EN = "Your Trusted Digital Ledger & Accounting"

    // Splash & Onboarding
    const val ONBOARDING_TITLE_1 = "সহজ ও নির্ভুল ডিজিটাল হিসাব"
    const val ONBOARDING_DESC_1 = "খাতা-কলমের ঝামেলা ভুলে সব বেচাকেনা ও লেনদেনের সঠিক হিসাব রাখুন এক ক্লিকে।"

    const val ONBOARDING_TITLE_2 = "দ্রুত বকেয়া ও তাগাদা এসএমএস"
    const val ONBOARDING_DESC_2 = "কাস্টমারের বকেয়া ট্র্যাক করুন এবং সহজে ফ্রি তাগাদা মেসেজ পাঠিয়ে টাকা আদায় করুন।"

    const val ONBOARDING_TITLE_3 = "১০০% নিরাপদ ও সুরক্ষিত ক্লাউড ব্যাকআপ"
    const val ONBOARDING_DESC_3 = "ফোন হারালেও হিসাব হারাবে না। ফায়ারবেস সিকিউরিটিতে আপনার ডাটা থাকবে সুরক্ষিত।"

    const val GET_STARTED = "শুরু করুন"
    const val NEXT = "পরবর্তী"
    const val SKIP = "এড়িয়ে যান"

    // Authentication - Login
    const val LOGIN_TITLE = "লগইন করুন"
    const val LOGIN_SUBTITLE = "আপনার অ্যাকাউন্টে প্রবেশ করে ব্যবসার হিসাব দেখুন"
    const val PHONE_OR_EMAIL_LABEL = "মোবাইল নম্বর / Email"
    const val PHONE_OR_EMAIL_HINT = "017XXXXXXXX অথবা your@email.com"
    const val PASSWORD_LABEL = "পাসওয়ার্ড"
    const val PASSWORD_HINT = "কমপক্ষে ৬ অক্ষরের পাসওয়ার্ড"
    const val FORGOT_PASSWORD = "পাসওয়ার্ড ভুলে গেছেন?"
    const val LOGIN_BTN = "লগইন"
    const val QUICK_DEMO_LOGIN = "⚡ ডেমো অ্যাকাউন্ট দিয়ে দেখুন"
    const val DONT_HAVE_ACCOUNT = "নতুন ব্যবহারকারী? "
    const val REGISTER_LINK = "রেজিস্ট্রেশন করুন"

    // Authentication - Registration
    const val REGISTER_TITLE = "নতুন অ্যাকাউন্ট তৈরি"
    const val REGISTER_SUBTITLE = "বিনামূল্যে খাতা খুলুন এবং ব্যবসা পরিচালনা করুন"
    const val NAME_LABEL = "আপনার নাম"
    const val NAME_HINT = "উদাঃ মোঃ রফিকুল ইসলাম"
    const val PHONE_LABEL = "মোবাইল নম্বর"
    const val PHONE_HINT = "01XXXXXXXXX"
    const val EMAIL_LABEL = "ইমেইল (ঐচ্ছিক)"
    const val EMAIL_HINT = "example@domain.com"
    const val SHOP_NAME_LABEL = "ব্যবসা / দোকানের নাম"
    const val SHOP_NAME_HINT = "উদাঃ ভাই ভাই স্টোর"
    const val DISTRICT_LABEL = "জেলা"
    const val DISTRICT_HINT = "আপনার জেলা নির্বাচন করুন"
    const val CONFIRM_PASSWORD_LABEL = "কনফার্ম পাসওয়ার্ড"
    const val CONFIRM_PASSWORD_HINT = "পাসওয়ার্ড পুনরায় লিখুন"
    const val TERMS_AGREEMENT = "আমি সেবা শর্তাবলী ও গোপনীয়তা নীতি মেনে নিচ্ছি"
    const val REGISTER_BTN = "রেজিস্ট্রেশন সম্পন্ন করুন"
    const val ALREADY_HAVE_ACCOUNT = "ইতিমধ্যে অ্যাকাউন্ট আছে? "

    // Forgot Password
    const val FORGOT_PASSWORD_TITLE = "পাসওয়ার্ড রিসেট"
    const val FORGOT_PASSWORD_SUBTITLE = "আপনার ইমেইল বা মোবাইল নম্বর দিন, আমরা পাসওয়ার্ড রিসেট লিংক পাঠাবো"
    const val RESET_PASSWORD_BTN = "রিসেট লিংক পাঠান"
    const val BACK_TO_LOGIN = "লগইনে ফিরে যান"
    const val RESET_LINK_SENT = "পাসওয়ার্ড রিসেট ইমেইল সফলভাবে পাঠানো হয়েছে!"

    // Bottom Navigation
    const val NAV_HOME = "হোম"
    const val NAV_TRANSACTIONS = "লেনদেন"
    const val NAV_CUSTOMERS = "কাস্টমার"
    const val NAV_REPORTS = "রিপোর্ট"
    const val NAV_MORE = "আরও"

    // Dashboard / Home
    const val TODAY_SUMMARY = "আজকের হিসাব"
    const val TODAY_SALE = "আজকের বিক্রি"
    const val TOTAL_DUE = "মোট বকেয়া (পাবো)"
    const val TOTAL_PAYABLE = "মোট দেনা (দেবো)"
    const val NET_CASH = "নগদ ক্যাশ"
    const val QUICK_ACTIONS = "দ্রুত সেবা"
    const val ADD_CUSTOMER = "নতুন কাস্টমার"
    const val CASH_IN = "ক্যাশ ইন (+)"
    const val CASH_OUT = "ক্যাশ আউট (-)"
    const val TAGADA_SMS = "তাগাদা মেসেজ"
    const val RECENT_TRANSACTIONS = "সাম্প্রতিক লেনদেন"
    const val VIEW_ALL = "সব দেখুন"
    const val NO_TRANSACTIONS_TODAY = "আজকে এখনও কোনো লেনদেন যোগ করা হয়নি"
    const val START_RECORDING = "লেনদেন যোগ করে হিসাব শুরু করুন"

    // Customers Screen
    const val CUSTOMER_HEADER = "কাস্টমার ও বাকীর খাতা"
    const val SEARCH_CUSTOMER = "নাম বা মোবাইল নম্বর দিয়ে খুঁজুন..."
    const val TAB_ALL_CUSTOMERS = "সব"
    const val TAB_DUE_CUSTOMERS = "বাকীদার"
    const val TAB_PAYABLE_CUSTOMERS = "পাওনাদার"
    const val EMPTY_CUSTOMERS_TITLE = "কোনো কাস্টমার পাওয়া যায়নি"
    const val EMPTY_CUSTOMERS_DESC = "নতুন কাস্টমার যোগ করে তাদের বাকি ও জমার ডিজিটাল হিসাব রাখুন।"
    const val ADD_NEW_CUSTOMER_BTN = "নতুন কাস্টমার যোগ করুন"

    // Transactions Screen
    const val TRANSACTION_HEADER = "দৈনিক লেনদেন খাতা"
    const val ALL_DATES = "সকল তারিখ"
    const val TODAY = "আজ"
    const val THIS_WEEK = "চলতি সপ্তাহ"
    const val THIS_MONTH = "চলতি মাস"
    const val EMPTY_TRANSACTIONS_TITLE = "লেনদেনের তালিকা খালি"
    const val EMPTY_TRANSACTIONS_DESC = "বেচাকেনা, খরচ বা জমার তথ্য যুক্ত করুন। ফেজ ১ শেষে সম্পূর্ণ হিসাব দেখতে পাবেন।"
    const val NEW_TRANSACTION_BTN = "নতুন লেনদেন যোগ করুন"

    // Reports Screen
    const val REPORTS_HEADER = "ব্যবসার রিপোর্ট ও বিশ্লেষণ"
    const val REPORT_DAILY_LEDGER = "দৈনিক খতিয়ান"
    const val REPORT_CUSTOMER_LEDGER = "কাস্টমার বাকি রিপোর্ট"
    const val REPORT_PROFIT_LOSS = "লাভ-ক্ষতির সারসংক্ষেপ"
    const val REPORT_CASH_BOOK = "ক্যাশ বুক"
    const val REPORT_DOWNLOAD_PDF = "PDF রিপোর্ট ডাউনলোড"
    const val REPORT_SHARE = "শেয়ার করুন"
    const val REPORT_PHASE_NOTICE = "ফেজ ১ ফাউন্ডেশন: ফেজ ২-তে সম্পূর্ণ স্বয়ংক্রিয় খতিয়ান ও ডাউনলোড সুবিধা কার্যকর হবে।"

    // Settings / More Screen
    const val SETTINGS_HEADER = "দোকান ও সেটিংস"
    const val SHOP_PROFILE = "দোকানের প্রোফাইল"
    const val APP_THEME = "অ্যাপ থিম"
    const val THEME_SYSTEM = "সিস্টেম ডিফল্ট"
    const val THEME_LIGHT = "লাইট মোড"
    const val THEME_DARK = "ডার্ক মোড"
    const val LANGUAGE = "ভাষা / Language"
    const val LANG_BANGLA = "বাংলা (Bangla)"
    const val LANG_ENGLISH = "English"
    const val SECURITY = "নিরাপত্তা ও পিন লক"
    const val BACKUP_SYNC = "ফায়ারবেস ক্লাউড স্ট্যাটাস"
    const val HELP_SUPPORT = "সহায়তা ও হেল্পলাইন"
    const val PRIVACY_POLICY = "প্রাইভেসি পলিসি"
    const val APP_VERSION = "ভার্সন ১.০.০ (ফেজ ১ ফাউন্ডেশন)"
    const val LOGOUT = "লগআউট"
    const val LOGOUT_CONFIRM_TITLE = "লগআউট করতে চান?"
    const val LOGOUT_CONFIRM_DESC = "আপনার অ্যাকাউন্ট থেকে লগআউট করা হবে। আপনি যেকোনো সময় আবার লগইন করতে পারবেন।"
    const val CANCEL = "বাতিল"
    const val CONFIRM = "হ্যাঁ, লগআউট"

    // Common
    const val CURRENCY_SYMBOL = "৳"
    const val LOADING = "অনুগ্রহ করে অপেক্ষা করুন..."
    const val ERROR_GENERIC = "একটি সমস্যা হয়েছে। অনুগ্রহ করে আবার চেষ্টা করুন।"
    const val ERROR_INVALID_CREDENTIALS = "মোবাইল নম্বর/ইমেইল অথবা পাসওয়ার্ড সঠিক নয়।"
    const val ERROR_INVALID_INPUT = "সবগুলো প্রয়োজনীয় ঘর সঠিকভাবে পূরণ করুন।"
    const val ERROR_PASSWORD_MISMATCH = "পাসওয়ার্ড দুটি মেলেনি।"
    const val ERROR_PASSWORD_LENGTH = "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে।"
    const val ERROR_INVALID_PHONE = "সঠিক ১১ সংখ্যার মোবাইল নম্বর দিন (উদাঃ 017XXXXXXXX)"
    const val ERROR_REQUIRED_FIELD = "এই তথ্যটি পূরণ করা আবশ্যক"
    const val SUCCESS_REGISTER = "অ্যাকাউন্ট সফলভাবে তৈরি হয়েছে!"
    const val SUCCESS_LOGIN = "স্বাগতম! সফলভাবে লগইন হয়েছে।"

    /**
     * Converts western digits to Bengali numerals: 123 -> ১২৩
     */
    fun toBanglaDigits(input: String): String {
        val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val sb = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(banglaDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Formats amount with Taka symbol in Bengali numerals: 15000 -> ৳১৫,০০০
     */
    fun formatTaka(amount: Number, showSymbol: Boolean = true): String {
        val formatted = "%,.0f".format(amount.toDouble())
        val banglaFormatted = toBanglaDigits(formatted)
        return if (showSymbol) "$CURRENCY_SYMBOL $banglaFormatted" else banglaFormatted
    }
}
