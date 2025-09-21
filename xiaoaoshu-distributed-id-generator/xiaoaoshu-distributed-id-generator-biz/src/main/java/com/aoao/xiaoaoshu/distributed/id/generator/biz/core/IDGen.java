package com.aoao.xiaoaoshu.distributed.id.generator.biz.core;


import com.aoao.xiaoaoshu.distributed.id.generator.biz.core.common.Result;

public interface IDGen {
    Result get(String key);
    boolean init();
}
