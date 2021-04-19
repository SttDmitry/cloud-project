package my.cloud.client.factory;

import my.cloud.client.service.NetworkService;
import my.cloud.client.service.impl.NettyClientService;

public class Factory {

    public static NetworkService getNetworkService() {
        return new NettyClientService();
    }

}
