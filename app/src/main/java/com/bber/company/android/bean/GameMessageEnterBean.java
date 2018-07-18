package com.bber.company.android.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;

/**
 * id：游戏id
 * gameName：游戏名称
 * gameCode：游戏编码
 * gameTableNumber：游戏桌台号
 * gameBetLimit：游戏可下注金额
 * gameMaxNumber：最大下注号码数量
 */
public class GameMessageEnterBean extends BaseObservable {
    public String id;
    public String gameName;
    public String gameCode;
    public String gameTableNumber;
    public String gameBetLimit;
    public String gameMaxNumber;
    public String watching;
    public String userRatio;
    public String jackpotRatio;
    public String profitRatio;
    public String jackpotWinRatio;

    public Double jackpotBalance;
    public String notOpenImg;


    public String getGameBetLimit() {
        return gameBetLimit;
    }

    public void setGameBetLimit(String gameBetLimit) {
        this.gameBetLimit = gameBetLimit;
    }

    public int getBetLimitG() {
        if (gameBetLimit.length() == 1) {
            return Integer.parseInt(gameBetLimit);
        } else if (gameBetLimit.length() == 2) {
            return Integer.parseInt(gameBetLimit.substring(1, 2));
        } else if (gameBetLimit.length() == 3) {
            return Integer.parseInt(gameBetLimit.substring(2, 3));
        }
        return 99;
    }

    public int getBetLimitS() {
        if (gameBetLimit.length() == 2) {

            return Integer.parseInt(gameBetLimit.substring(0, 1));
        } else if (gameBetLimit.length() == 3) {
            return Integer.parseInt(gameBetLimit.substring(1, 2));
        }
        return 99;
    }

    public int getBetModel() {
        if (Integer.parseInt(gameBetLimit) < 6) {
            return 1;
        } else if (Integer.parseInt(gameBetLimit) >= 6 && Integer.parseInt(gameBetLimit) < 20) {
            return 2;
        } else if (Integer.parseInt(gameBetLimit) >= 21 && Integer.parseInt(gameBetLimit) < 1000) {
            return 3;
        }
        return 0;
    }


    public int getBetLimitB() {
        if (gameBetLimit.length() == 3) {
            return Integer.parseInt(gameBetLimit.substring(0, 1));
        }
        return 99;
    }


    @Bindable
    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
        notifyPropertyChanged(BR.gameCode);
    }

    @Bindable
    public String getGameTableNumber() {
        return gameTableNumber;
    }

    public void setGameTableNumber(String gameTableNumber) {
        this.gameTableNumber = gameTableNumber;
        notifyPropertyChanged(BR.gameTableNumber);
    }

    @Bindable
    public String getWatching() {
        return watching;
    }

    public void setWatching(String watching) {
        this.watching = watching;
        notifyPropertyChanged(BR.watching);
    }

    @Bindable
    public Double getJackpotBalance() {
        return jackpotBalance;
    }

    public void setJackpotBalance(Double jackpotBalance) {
        this.jackpotBalance = jackpotBalance;
        notifyPropertyChanged(BR.jackpotBalance);
    }


}
