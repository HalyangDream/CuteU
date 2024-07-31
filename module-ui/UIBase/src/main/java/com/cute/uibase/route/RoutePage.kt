package com.cute.uibase.route

object RoutePage {

    object Login {
        const val LOGIN_PAGE = "/login/LoginActivity"
        const val REGISTER_PAGE = "/login/RegisterActivity"
    }

    object Main {
        const val MAIN_PAGE = "/main/MainActivity"
    }


    object HOME {
        const val FEED_FRAGMENT = "/home/FeedFragment"
        const val FEED_VIDEO_FRAGMENT = "/home/FeedVideoFragment"
        const val FEED_EVENT_FRAGMENT = "/home/FeedEventFragment"
        const val FEED_MATCH_FRAGMENT = "/home/FeedMatchFragment"
        const val ANCHOR_DETAIL_ACTIVITY = "/home/AnchorDetailActivity"
    }

    object CHAT {
        const val CHAT_CONVERSATION = "/chat/ConversationFragment"
        const val CHAT_ACTIVITY = "/chat/ChatActivity"
    }

    object MINE {
        const val MINE_FRAGMENT = "/mine/MineFragment"
    }

    object STORE {
        const val COIN_STORE = "/store/CoinStoreActivity"
        const val VIP_STORE = "/store/VipStoreActivity"
    }

    object CALL {
    }


    object Provider {
        const val TELEPHONE_PROVIDER = "/service/telephone"
        const val PRODUCT_PROVIDER = "/product/Product"
        const val MINE_PROVIDER = "/mine/provider"
    }
}