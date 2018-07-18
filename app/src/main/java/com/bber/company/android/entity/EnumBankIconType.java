package com.bber.company.android.entity;

import android.graphics.drawable.Drawable;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;

/**
 * Created by Vencent on 2017/2/20.
 * 关于银行对应的code的枚举
 */
public enum EnumBankIconType {

    ICBC("ICBC") {
        @Override
        public Drawable getDrawble() {
            return getRes(R.mipmap.icbc3);
        }
    },
    CMB("CMB") {
        @Override
        public Drawable getDrawble() {
            return getRes(R.mipmap.cmb3);
        }
    },
    CCB("CCB") {
        @Override
        public Drawable getDrawble() {
            return getRes(R.mipmap.ccb3);
        }
    },
    ABC("ABC") {
        @Override
        public Drawable getDrawble() {
            return getRes(R.mipmap.abc3);
        }
    },
    BOC("BOC") {
        @Override
        public Drawable getDrawble() {
            return getRes(R.mipmap.boc3);
        }
    },
    BCM("BCM") {
        @Override
        public Drawable getDrawble() {
            return getRes(R.mipmap.bcm3);
        }
    },
    CMBC("CMBC") {
        @Override
        public Drawable getDrawble() {
            return getRes(R.mipmap.cmbc3);
        }
    },
    ECC("ECC") {
        @Override
        public Drawable getDrawble() {
            return getRes(R.mipmap.ecc3x);
        }
    },
    SPDB("SPDB") {
        @Override
        public Drawable getDrawble() {
            return getRes(R.mipmap.spdb3);
        }
    },
    PSBC("PSBC") {
        @Override
        public Drawable getDrawble() {
            return getRes(R.mipmap.psbc3);
        }
    },
    CEB("CEB") {
        @Override
        public Drawable getDrawble() {
            return getRes(R.mipmap.ceb3);
        }
    },
    PINGAN("PINGAN") {
        @Override
        public Drawable getDrawble() {
            return getRes(R.mipmap.pingan3);
        }
    },
    CGB("CGB") {
        @Override
        public Drawable getDrawble() {
            return getRes(R.mipmap.cgb3);
        }
    },
    HXB("HXB") {
        @Override
        public Drawable getDrawble() {
            return getRes(R.mipmap.hxb3);
        }
    },
    CIB("CIB") {
        @Override
        public Drawable getDrawble() {
            return getRes(R.mipmap.cib3);
        }
    };

    public String bank;

    EnumBankIconType(String bank) {
        this.bank = bank;
    }

    public static Drawable getbankType(int bankid) {
        if (bankid == 1) {
            return ICBC.getDrawble();
        } else if (bankid == 2) {
            return CMB.getDrawble();
        } else if (bankid == 3) {
            return CCB.getDrawble();
        } else if (bankid == 4) {
            return ABC.getDrawble();
        } else if (bankid == 5) {
            return BOC.getDrawble();
        } else if (bankid == 6) {
            return BCM.getDrawble();
        } else if (bankid == 7) {
            return CMBC.getDrawble();
        } else if (bankid == 8) {
            return ECC.getDrawble();
        } else if (bankid == 9) {
            return SPDB.getDrawble();
        } else if (bankid == 10) {
            return PSBC.getDrawble();
        } else if (bankid == 11) {
            return CEB.getDrawble();
        } else if (bankid == 12) {
            return PINGAN.getDrawble();
        } else if (bankid == 13) {
            return CGB.getDrawble();
        } else if (bankid == 14) {
            return HXB.getDrawble();
        } else if (bankid == 15) {
            return CIB.getDrawble();
        } else {
            return CCB.getDrawble();
        }
    }

    public abstract Drawable getDrawble();


    Drawable getRes(int image) {
        return MyApplication.getContext().getResources().getDrawable(image);
    }
}
