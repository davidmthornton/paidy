package forex.config

import scala.concurrent.duration.FiniteDuration

case class ApplicationConfig(
    http: HttpConfig,
    oneFrameConfig: OneFrameConfig
)

case class HttpConfig(
    protocol: String,
    host: String,
    port: Int,
    timeout: FiniteDuration
)

case class OneFrameConfig(
    cacheFetchInterval: FiniteDuration,
    http: HttpConfig,
    token: String
)
