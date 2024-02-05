package services.demoInfoProvider

import runner.Runner
import services.runnerRegistry.RunnerRegistry
import skadistats.clarity.processor.reader.OnMessage
import skadistats.clarity.wire.shared.demo.proto.Demo.CDemoFileInfo

class DemoInfoProvider(runnerRegistry: RunnerRegistry) : Runner(runnerRegistry) {
    var demoFileInfo: CDemoFileInfo? = null

    @OnMessage(CDemoFileInfo::class)
    fun onFileInfo(info: CDemoFileInfo) {
        demoFileInfo = info
    }
}