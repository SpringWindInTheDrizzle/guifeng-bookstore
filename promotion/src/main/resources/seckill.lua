    local buyNum = ARGV[1];
    local goodsKey = KEYS[1];
    local goodsNum = redis.call('get', goodsKey);
    goodsNum = tonumber(goodsNum);
    buyNum = tonumber(buyNum);
    if goodsNum >= buyNum then
        redis.call('DECRBY',goodsKey, buyNum);
        return 1;
    else
        return 2;
    end

    -- 所有抢单成功的用户
    -- redis.call('sadd',ARGV[#ARGV],KEYS[1]);

    -- return 1;
