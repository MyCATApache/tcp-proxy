/**
 * Copyright (C) <2021>  <chen junwen>
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with this program.  If
 * not, see <http://www.gnu.org/licenses/>.
 */
package io.mycat.config;

import lombok.SneakyThrows;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ZooMap implements Map<String, String>, Closeable {
    private final String root;
    private final CuratorFramework client;

    public static ZooMap newMap(CuratorFramework client, String root) throws Exception {
        Stat stat = client.checkExists().forPath(root);
        if (stat == null) {
            client.createContainers(root);
        }
        return new ZooMap(client, root);
    }

    private ZooMap(CuratorFramework client, String root) {
        this.client = client;
        this.root = root;
    }

    @Override
    @SneakyThrows
    public int size() {
        return  getKeys().size();
    }

    @Override
    @SneakyThrows
    public boolean isEmpty() {
        return  getKeys().isEmpty();
    }

    @Override
    @SneakyThrows
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            return  client.checkExists().forPath(keyPath((String) key)) != null;
        }
        throw new IllegalArgumentException("key must be of type String");
    }

    private List<String> getKeys() throws Exception {
        return client.getChildren().forPath(emptyToSlash(root));
    }

    @Override
    @SneakyThrows
    public boolean containsValue(Object value) {
        if (value instanceof String) {
            return getKeys().stream().map(this::getValue).anyMatch(value::equals);
        }
        throw new IllegalArgumentException("value must be of type String");
    }

    @SneakyThrows
    private String getValue(String key) {
        final byte[] bytes = client.getData().forPath(keyPath(key));
        return bytes != null ? new String(bytes, StandardCharsets.UTF_8) : null;
    }

    private String keyPath(String key) {
        return root + "/" + key;
    }

    private static String emptyToSlash(String path) {
        return path.isEmpty() ? "/" : path;
    }

    @Override
    public String get(Object key) {
        if (key instanceof String) {
            if (!containsKey(key)) {
                return null;
            }
            return getValue((String) key);
        }

        throw new IllegalArgumentException("key must be of type String");
    }

    @Override
    @SneakyThrows
    public String put(String key, String value) {
        if (key != null && !key.isEmpty()) {
            String previousValue = get(key);
            client.createContainers(keyPath(key));
            client.setData().forPath(keyPath(key), value != null ? value.getBytes(StandardCharsets.UTF_8) : null);
            return previousValue;
        }
        throw new IllegalArgumentException("Key should not be empty nor null (" + key + ")");
    }

    @Override
    @SneakyThrows
    public String remove(Object key) {
        if (key instanceof String) {
            String previousValue = get(key);
            if (previousValue != null) {
                client.delete().forPath(keyPath((String) key));
            }
            return previousValue;
        } else {
            throw new IllegalArgumentException("key must be of type String");
        }
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        m.forEach(this::put);
    }

    @Override
    @SneakyThrows
    public void clear() {
        client.delete().deletingChildrenIfNeeded().forPath(emptyToSlash(root));
        client.create().forPath(emptyToSlash(root));
    }

    @Override
    @SneakyThrows
    public Set<String> keySet() {
        return new HashSet<>(client.getChildren().forPath(emptyToSlash(root)));
    }

    @Override
    @SneakyThrows
    public Collection<String> values() {
        return  getKeys().stream().map(this::getValue).map(String::new).collect(Collectors.toList());
    }

    @Override
    @SneakyThrows
    public Set<Entry<String, String>> entrySet() {
        return getKeys().stream().map(entryFromKey()).collect(Collectors.toSet());
    }

    @SneakyThrows
    private Function<String, Entry<String, String>> entryFromKey() {
        return k -> new AbstractMap.SimpleEntry<>(k, getValue(k));
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof ZooMap))
            return false;

        ZooMap otherZooMap = (ZooMap) o;
        return root.equals(otherZooMap.root) && this.client.equals(otherZooMap.client);
    }

    @Override
    public final int hashCode() {
        return this.client.hashCode() + this.root.hashCode();
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super String, ? extends String> function) {
        throw new UnsupportedOperationException();
    }

    public CuratorFramework getClient() {
        return client;
    }

    @Override
    public void close() {
        client.close();//check
    }

    public String getRoot() {
        return root;
    }

}

