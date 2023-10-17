package ski.mashiro

import com.github.yitter.contract.IdGeneratorOptions
import com.github.yitter.idgen.YitIdHelper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WsDemoApplication

fun main(args: Array<String>) {
    YitIdHelper.setIdGenerator(IdGeneratorOptions(32))
    runApplication<WsDemoApplication>(*args)
}
