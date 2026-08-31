package com.project.staynest.auth.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    private Node master;
    private Node replica;

    public Node getMaster() {
        return master;
    }
    public void setMaster(Node master) {
        this.master = master;
    }

    public Node getReplica() {
        return replica;
    }
    public void setReplica(Node replica) {
        this.replica = replica;
    }

    public static class Node{
        private String host;
        private int port;
        private String password;

        public String getHost() {
            return host;
        }
        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }
        public void setPort(int port) {
            this.port = port;
        }

        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }
}
