package com.ebensz.games.model;

/**
 * User: tosmart
 * Date: 11-10-18
 * Time: 下午3:19
 */
public enum Dir {

    Outside, Left, Right;

    public Dir shangJia() {

        switch (this) {

            case Outside:
                return Left;

            case Left:
                return Right;

            case Right:
                return Outside;
        }

        return null;
    }

    public Dir xiaJia() {

        switch (this) {

            case Outside:
                return Right;

            case Left:
                return Outside;

            case Right:
                return Left;
        }

        return null;
    }
}
