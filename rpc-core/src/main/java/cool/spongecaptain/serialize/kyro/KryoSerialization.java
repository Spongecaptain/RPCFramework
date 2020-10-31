package cool.spongecaptain.serialize.kyro;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import cool.spongecaptain.serialize.Serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class KryoSerialization implements Serialization {
    //利用 Kryo 进行序列化的 ID 为 1
    final int  SERIALIZATION_ID = 1;

    //这里设私有的即可，因为通过 public 的 serialize 以及 deserialize 就能非常好的完成任务
    private static final ThreadLocal<Kryo> kryoLocal = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.setReferences(true);// 默认值为 true, 强调作用
            kryo.setRegistrationRequired(false);// 默认值为 false, 强调作用
            return kryo;
        }
    };


    @Override
    public byte[] serialize(Object object) {
        Kryo kryo = kryoLocal.get();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeClassAndObject(output, object);
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public int getSerializationId() {
        return SERIALIZATION_ID;
    }

    @Override
    public Object deserialize(byte[] bytes) {
        Kryo kryo = kryoLocal.get();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        input.close();
        return  kryo.readClassAndObject(input);
    }
}
