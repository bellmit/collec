package com.unicom.storage.exception;

import com.unicom.common.exception.BaseException;

/**
 * @author : smalljop
 * @description : 异常
 * @create : 2020-11-10 10:49
 **/
public class StorageException extends BaseException {
    public StorageException(String msg) {
        super(msg);
    }

    public StorageException(String msg, Throwable e) {
        super(msg, e);
    }
}
