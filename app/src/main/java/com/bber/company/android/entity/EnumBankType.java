package com.bber.company.android.entity;

/**
 * Created by Vencent on 2017/2/20.
 * 关于银行对应的code的枚举
 */
public enum EnumBankType {

    ICBC("ICBC") {
        @Override
        public Integer getInt() {
            return 1;
        }
    },
    CMB("CMB") {
        @Override
        public Integer getInt() {
            return 2;
        }
    },
    CCB("CCB") {
        @Override
        public Integer getInt() {
            return 3;
        }
    },
    ABC("ABC") {
        @Override
        public Integer getInt() {
            return 4;
        }
    },
    BOC("BOC") {
        @Override
        public Integer getInt() {
            return 5;
        }
    },
    BCM("BCM") {
        @Override
        public Integer getInt() {
            return 6;
        }
    },
    CMBC("CMBC") {
        @Override
        public Integer getInt() {
            return 7;
        }
    },
    ECC("ECC") {
        @Override
        public Integer getInt() {
            return 8;
        }
    },
    SPDB("SPDB") {
        @Override
        public Integer getInt() {
            return 9;
        }
    },
    PSBC("PSBC") {
        @Override
        public Integer getInt() {
            return 10;
        }
    },
    CEB("CEB") {
        @Override
        public Integer getInt() {
            return 11;
        }
    },
    PINGAN("PINGAN") {
        @Override
        public Integer getInt() {
            return 12;
        }
    },
    CGB("CGB") {
        @Override
        public Integer getInt() {
            return 13;
        }
    },
    HXB("HXB") {
        @Override
        public Integer getInt() {
            return 14;
        }
    },
    CIB("CIB") {
        @Override
        public Integer getInt() {
            return 15;
        }
    };

    public String bank;

    EnumBankType(String bank) {
        this.bank = bank;
    }

    public static Integer getbankType(String bank) {
        if (bank.contains("中国工商银行")) {
            return ICBC.getInt();
        } else if (bank.contains("招商银行")) {
            return CMB.getInt();
        } else if (bank.contains("中国建设银行")) {
            return CCB.getInt();
        } else if (bank.contains("中国农业银行")) {
            return ABC.getInt();
        } else if (bank.contains("中国银行")) {
            return BOC.getInt();
        } else if (bank.contains("交通银行")) {
            return BCM.getInt();
        } else if (bank.contains("中国民生银行")) {
            return CMBC.getInt();
        } else if (bank.contains("中信银行")) {
            return ECC.getInt();
        } else if (bank.contains("上海浦东发展银行")) {
            return SPDB.getInt();
        } else if (bank.contains("邮政储汇")) {
            return PSBC.getInt();
        } else if (bank.contains("中国光大银行")) {
            return CEB.getInt();
        } else if (bank.contains("平安银行")) {
            return PINGAN.getInt();
        } else if (bank.contains("广发银行")) {
            return CGB.getInt();
        } else if (bank.contains("华夏银行")) {
            return HXB.getInt();
        } else if (bank.contains("福建兴业银行")) {
            return CIB.getInt();
        } else {
            return CCB.getInt();
        }
    }

    public abstract Integer getInt();

}
