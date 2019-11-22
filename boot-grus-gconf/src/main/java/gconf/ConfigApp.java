package gconf;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Description TODO
 * @Date 2019/11/22 17:49
 * @Author huangyangyang
 */
public class ConfigApp implements Serializable {
    public static final ConfigApp NOOP_INSTANCE = new ConfigApp();

    private String configCollectionId;

    //应用名称
    private String name;

    public ConfigApp(String configCollectionId) {
        this.configCollectionId = configCollectionId;
    }

    public ConfigApp() {
    }

    public String getConfigCollectionId() {
        return configCollectionId;
    }

    public void setConfigCollectionId(String configCollectionId) {
        this.configCollectionId = configCollectionId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfigApp)) {
            return false;
        }
        ConfigApp configApp = (ConfigApp) o;
        return Objects.equals(configCollectionId, configApp.configCollectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configCollectionId);
    }

    @Override
    public String toString() {
        return "ConfigApp{" +
                "configCollectionId='" + configCollectionId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


}
