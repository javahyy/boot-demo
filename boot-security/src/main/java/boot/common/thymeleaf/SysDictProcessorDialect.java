package boot.common.thymeleaf;

import boot.common.thymeleaf.processor.SelectElementTagProcessor;
import org.springframework.stereotype.Component;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * 字典 处理
 */
@Component
public class SysDictProcessorDialect extends AbstractProcessorDialect {

    /**
     * tab 名
     */
    private static final String DIALECT_NAME = "Dict Dialect";
    /**
     * 前缀
     */
    private static final String DIALECT_PREFIX = "x";
    private static final int PRECEDENCE = 1000;
    /**
     * 字典服务
     */
    private final SysDictFunc dictFunc;

    // 构造注入
    public SysDictProcessorDialect(SysDictFunc dictFunc) {
        super(DIALECT_NAME, DIALECT_PREFIX, PRECEDENCE);
        this.dictFunc = dictFunc;
    }

    @Override
    public Set<IProcessor> getProcessors(final String dialectPrefix) {
        final Set<IProcessor> processors = new HashSet<>(1);
        processors.add(new SelectElementTagProcessor(dialectPrefix, PRECEDENCE, dictFunc));
        return processors;
    }
}
