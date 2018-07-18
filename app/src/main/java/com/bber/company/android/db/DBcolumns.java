package com.bber.company.android.db;

/**
 *
 */
public class DBcolumns {
    /**
     * 聊天消息信息表    聊天内容
     */
    public static final String TABLE_MSG = "table_msg";
    public static final String TABLE_BROADCAST_MSG = "table_broadcast_msg";
    public static final String MSG_ID = "id";
    public static final String FROM_ID = "from_id";
    public static final String TO_ID = "to_id";
    public static final String MSG = "msg";
    public static final String MSG_ISREADED = "msg_isreaded";
    public static final String SELLER_ID = "seller_id";
    public static final String ORDER_ID = "order_id";

    /** 主播聊天的内容*/
    public static final String BROADCAST_ID = "order_broadcast_id";
    public static final String BROADCAST_MAG = "order_broadcast_mag";
    public static final String BROADCAST_HEADM = "order_broadcast_headm";
    public static final String BROADCAST_TIME = "order_broadcast_time";
    public static final String BROADCAST_SELECT = "order_broadcast_select";

    /**
     * 聊天会话表  聊天框的列表
     */
    public static final String TABLE_SESSION = "table_session";
    public static final String TABLE_BROADCASD = "table_bradcasd_msg";
    public static final String SESSION_ID = "session_id";
    public static final String SESSION_FROM = "session_from";
    public static final String SESSION_HEAD = "session_head";
    public static final String SESSION_NAME = "session_name";
    public static final String SESSION_TYPE = "session_type";
    public static final String SESSION_TIME = "session_time";

    public static final String SESSION_CONTENT = "session_content";
    public static final String SESSION_TO = "session_to";// 登录人id
    public static final String SESSION_NOREADCOUNT = "session_noreadcount";
    public static final String SESSION_ISDISPOSE = "session_isdispose";
    /**
     * 收藏页面-我的最爱uSeller
     */
    public static final String TABLE_MATCH_HISTORY = "match_table_history";
    public static final String TABLE_FAVORITES = "table_favorites";

    public static final String SELLERUSER_ID = "selleruser_id";
    public static final String SELLERUSER_USELLER = "selleruser_useller";
    public static final String SELLERUSER_USNAME = "selleruser_usname";
    public static final String SELLERUSER_USHEIGHT = "selleruser_usheight";
    public static final String SELLERUSER_USBRASSIERE = "selleruser_usbrassiere";
    public static final String SELLERUSER_USDESCRIBE = "selleruser_usdescribe";
    public static final String SELLERUSER_IMAGES = "selleruser_images";
    public static final String SELLERUSER_USPHONE = "selleruser_usphone";
    public static final String SELLERUSER_USHEADM = "selleruser_usheadm";
    public static final String SELLERUSER_USHEADBIG = "selleruser_usheadbig";
    public static final String SELLERUSER_USSEX = "selleruser_ussex";
    public static final String SELLERUSER_SSSEX = "selleruser_sssex";
    public static final String SELLERUSER_USSTATE = "selleruser_usstate";
    public static final String SELLERUSER_USMONEY = "selleruser_usmoney";
    public static final String SELLERUSER_USGRADE = "selleruser_usgrade";
    public static final String SELLERUSER_LEVEL = "selleruser_level";
    public static final String SELLERUSER_ISACCEPTORDER = "selleruser_isacceptorder";
    public static final String SELLERUSER_TIME = "selleruser_time";

    /**
     * 创建表语句工厂
     *
     * @author brucs
     * @version [1.0.0.0, 2016-4-23]
     */
    public static final class CreateTableSQLFactory {

        /**
         * 主播聊天 sql
         *
         * @return
         * @author brucs
         */
        public static final String createBoasdcastTableMsg() {
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE IF NOT EXISTS ")
                    .append(TABLE_BROADCASD).append(" ( ")
                    .append(BROADCAST_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                    .append(BROADCAST_MAG).append(" TEXT, ")
                    .append(BROADCAST_SELECT).append(" TEXT, ")
                    .append(BROADCAST_HEADM).append(" TEXT, ")
                    .append(BROADCAST_TIME).append(" TEXT ")
                    .append(" );");
            return sb.toString();
        }


        public static final String createTableMsg() {
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_MSG).append(" ( ")
                    .append(MSG_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                    .append(FROM_ID).append(" TEXT, ")
                    .append(TO_ID).append(" TEXT, ")
                    .append(MSG).append(" TEXT, ")
                    .append(MSG_ISREADED).append(" TEXT, ")
                    .append(SELLER_ID).append(" TEXT, ")
                    .append(ORDER_ID).append(" TEXT ")
                    .append(" );");
            return sb.toString();
        }
        /**
         * 收藏SQL语句名
         *
         * @return
         * @author brucs
         */
        public static final String createTableFavorites() {
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_FAVORITES).append(" ( ")
                    .append(SELLERUSER_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                    .append(SELLERUSER_USELLER).append(" INTEGER, ")
                    .append(SELLERUSER_USNAME).append(" TEXT, ")
                    .append(SELLERUSER_USHEIGHT).append(" INTEGER, ")
                    .append(SELLERUSER_USBRASSIERE).append(" TEXT, ")
                    .append(SELLERUSER_USDESCRIBE).append(" TEXT, ")
                    .append(SELLERUSER_IMAGES).append(" TEXT, ")
                    .append(SELLERUSER_USPHONE).append(" TEXT, ")
                    .append(SELLERUSER_USHEADM).append(" TEXT, ")
                    .append(SELLERUSER_USHEADBIG).append(" TEXT, ")
                    .append(SELLERUSER_USSEX).append(" INTEGER, ")
                    .append(SELLERUSER_SSSEX).append(" INTEGER, ")
                    .append(SELLERUSER_USSTATE).append(" INTEGER, ")
                    .append(SELLERUSER_USMONEY).append(" TEXT, ")
                    .append(SELLERUSER_USGRADE).append(" TEXT, ")
                    .append(SELLERUSER_LEVEL).append(" INTEGER, ")
                    .append(SELLERUSER_ISACCEPTORDER).append(" INTEGER, ")
                    .append(SELLERUSER_TIME).append(" TEXT")
                    .append(" );");
            return sb.toString();
        }

        /**
         * 收藏SQL语句名
         *
         * @return
         * @author brucs
         */
        public static final String createTableMatchHistory() {
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_MATCH_HISTORY).append(" ( ")
                    .append(SELLERUSER_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                    .append(SELLERUSER_USELLER).append(" INTEGER, ")
                    .append(SELLERUSER_USNAME).append(" TEXT, ")
                    .append(SELLERUSER_USHEIGHT).append(" INTEGER, ")
                    .append(SELLERUSER_USBRASSIERE).append(" TEXT, ")
                    .append(SELLERUSER_USDESCRIBE).append(" TEXT, ")
                    .append(SELLERUSER_IMAGES).append(" TEXT, ")
                    .append(SELLERUSER_USPHONE).append(" TEXT, ")
                    .append(SELLERUSER_USHEADM).append(" TEXT, ")
                    .append(SELLERUSER_USHEADBIG).append(" TEXT, ")
                    .append(SELLERUSER_USSEX).append(" INTEGER, ")
                    .append(SELLERUSER_SSSEX).append(" INTEGER, ")
                    .append(SELLERUSER_USSTATE).append(" INTEGER, ")
                    .append(SELLERUSER_USMONEY).append(" TEXT, ")
                    .append(SELLERUSER_USGRADE).append(" TEXT, ")
                    .append(SELLERUSER_LEVEL).append(" INTEGER, ")
                    .append(SELLERUSER_ISACCEPTORDER).append(" INTEGER, ")
                    .append(SELLERUSER_TIME).append(" TEXT")
                    .append(" );");
            return sb.toString();
        }

        /**
         * 会话SQL语句
         *
         * @return
         * @author brucs
         */
        public static final String createSessionTable() {
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_SESSION).append(" ( ")
                    .append(SESSION_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                    .append(SESSION_FROM).append(" TEXT, ")
                    .append(SESSION_HEAD).append(" TEXT, ")
                    .append(SESSION_NAME).append(" TEXT, ")
                    .append(SESSION_TYPE).append(" TEXT, ")
                    .append(SESSION_TIME).append(" TEXT, ")
                    .append(SESSION_TO).append(" TEXT, ")
                    .append(SESSION_CONTENT).append(" TEXT, ")
                    .append(SELLER_ID).append(" TEXT, ")
                    .append(SESSION_NOREADCOUNT).append(" TEXT, ")
                    .append(SESSION_ISDISPOSE).append(" TEXT")
                    .append(" );");

            return sb.toString();
        }

    }
}
