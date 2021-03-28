package cool.spongecaptain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//测试从 properties 文件中读取属性
public class TestLoadProperties {
    public static void main(String[] args) {
        Properties properties = new Properties();
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        InputStream in = TestLoadProperties.class.getClassLoader().getResourceAsStream("consumer.properties");
        // 使用properties对象加载输入流
        try {
            properties.load(in);
            //获取key对应的value值
            String loadbalance = properties.getProperty("loadbalance");
            System.out.println(loadbalance);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
